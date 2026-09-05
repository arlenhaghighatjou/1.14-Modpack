package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import net.lax1dude.eaglercraft.internal.buffer.IntBuffer;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.CRC32;
import java.util.EnumSet;
import java.util.Set;
import net.lax1dude.eaglercraft.EaglerZLIB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.lax1dude.eaglercraft.IOUtils;
import net.lax1dude.eaglercraft.opengl.ImageData;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.EagRuntime;
import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.*;

@OnlyIn(Dist.CLIENT)
public final class NativeImage implements AutoCloseable {
   private final NativeImage.PixelFormat pixelFormat;
   private final int width;
   private final int height;
   private final boolean stbiPointer;
   private long imagePointer;
   private final int size;
   public ImageData imageData;

   public NativeImage(int widthIn, int heightIn, boolean clear) {
      this(NativeImage.PixelFormat.RGBA, widthIn, heightIn, clear);
   }

   public NativeImage(NativeImage.PixelFormat pixelFormatIn, int widthIn, int heightIn, boolean initialize) {
      this.pixelFormat = pixelFormatIn;
      this.width = widthIn;
      this.height = heightIn;
      this.size = widthIn * heightIn * pixelFormatIn.getPixelSize();
      this.stbiPointer = false;
      this.imagePointer = 0L; 
      this.imageData = new ImageData(widthIn, heightIn, initialize);
      if (initialize) {
         this.imageData.fillAlpha();
      }
   }

   private NativeImage(NativeImage.PixelFormat pixelFormatIn, int widthIn, int heightIn, boolean stbiPointerIn, long pointer) {
      this.pixelFormat = pixelFormatIn;
      this.width = widthIn;
      this.height = heightIn;
      this.stbiPointer = stbiPointerIn;
      this.imagePointer = pointer;
      this.size = widthIn * heightIn * pixelFormatIn.getPixelSize();
      this.imageData = new ImageData(widthIn, heightIn, true);
   }

   public String toString() {
      return "NativeImage[" + this.pixelFormat + " " + this.width + "x" + this.height + "@" + this.imagePointer + (this.stbiPointer ? "S" : "N") + "]";
   }

   public static NativeImage read(InputStream inputStreamIn) throws IOException {
      return read(NativeImage.PixelFormat.RGBA, inputStreamIn);
   }

   public static NativeImage read(NativeImage.PixelFormat pixelFormatIn, InputStream inputStreamIn) throws IOException {
      try {
         ImageData imgData = ImageData.loadImageFile(inputStreamIn);
         if (imgData == null) {
            throw new IOException("Failed to load image");
         }
         NativeImage nativeImage = new NativeImage(pixelFormatIn, imgData.width, imgData.height, false);
         nativeImage.imageData = imgData;
         return nativeImage;
      } finally {
         IOUtils.closeQuietly(inputStreamIn);
      }
   }

   public static NativeImage read(ByteBuffer byteBufferIn) throws IOException {
      return read(NativeImage.PixelFormat.RGBA, byteBufferIn);
   }

   public static NativeImage read(NativeImage.PixelFormat pixelFormatIn, ByteBuffer byteBufferIn) throws IOException {
      if (pixelFormatIn != null && !pixelFormatIn.isSerializable()) {
         throw new UnsupportedOperationException("Don't know how to read format " + pixelFormatIn);
      } else if (byteBufferIn == null || !byteBufferIn.hasRemaining()) {
         throw new IllegalArgumentException("Invalid buffer");
      } else {
         byte[] data = new byte[byteBufferIn.remaining()];
         byteBufferIn.get(data);
         ImageData imgData = ImageData.loadImageFile(data);
         if (imgData == null) {
            throw new IOException("Failed to load image");
         }
         NativeImage nativeImage = new NativeImage(pixelFormatIn, imgData.width, imgData.height, false);
         nativeImage.imageData = imgData;
         return nativeImage;
      }
   }

   private static void setWrapST(boolean clamp) {
      if (clamp) {
         GlStateManager.texParameter(3553, 10242, 33071);
         GlStateManager.texParameter(3553, 10243, 33071);
      } else {
         GlStateManager.texParameter(3553, 10242, 10497);
         GlStateManager.texParameter(3553, 10243, 10497);
      }
   }

   private static void setMinMagFilters(boolean linear, boolean mipmap) {
      if (mipmap) {
         GlStateManager.texParameter(3553, 10241, linear ? 9987 : 9986);
         GlStateManager.texParameter(3553, 10240, linear ? 9729 : 9728);
      } else {
         GlStateManager.texParameter(3553, 10241, linear ? 9729 : 9728);
         GlStateManager.texParameter(3553, 10240, linear ? 9729 : 9728);
      }
   }

   private void checkImage() {
      if (this.imageData == null) {
         throw new IllegalStateException("Image is not initialized");
      }
   }

   public void close() {
      if (this.imageData != null) {
         this.imageData = null;
      }
      this.imagePointer = 0L;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public NativeImage.PixelFormat getFormat() {
      return this.pixelFormat;
   }

   public int getPixelRGBA(int x, int y) {
      this.checkImage();
      if (x >= 0 && x < this.width && y >= 0 && y < this.height) {
         return this.imageData.pixels[y * this.width + x];
      } else {
         throw new IllegalArgumentException("Invalid coordinates");
      }
   }

   public void setPixelRGBA(int x, int y, int value) {
      this.checkImage();
      if (x >= 0 && x < this.width && y >= 0 && y < this.height) {
         this.imageData.pixels[y * this.width + x] = value;
      } else {
         throw new IllegalArgumentException("Invalid coordinates");
      }
   }

   public byte getPixelLuminanceOrAlpha(int x, int y) {
      this.checkImage();
      if (x >= 0 && x < this.width && y >= 0 && y < this.height) {
         int pixel = this.imageData.pixels[y * this.width + x];
         return (byte)((pixel >> this.pixelFormat.getOffsetAlphaBits()) & 255);
      } else {
         throw new IllegalArgumentException("Invalid coordinates");
      }
   }

   public void blendPixel(int xIn, int yIn, int colIn) {
      this.checkImage();
      if (xIn >= 0 && xIn < this.width && yIn >= 0 && yIn < this.height) {
         int currentPixel = this.imageData.pixels[yIn * this.width + xIn];
         int alpha = (colIn >> 24) & 255;
         if (alpha == 255) {
            this.imageData.pixels[yIn * this.width + xIn] = colIn;
         } else if (alpha > 0) {
            int r1 = (currentPixel >> 16) & 255;
            int g1 = (currentPixel >> 8) & 255;
            int b1 = currentPixel & 255;
            int a1 = (currentPixel >> 24) & 255;
            int r2 = (colIn >> 16) & 255;
            int g2 = (colIn >> 8) & 255;
            int b2 = colIn & 255;
            int newAlpha = 255 - ((255 - alpha) * (255 - a1) / 255);
            int newR = (r2 * alpha + r1 * (255 - alpha) * a1 / 255) / newAlpha;
            int newG = (g2 * alpha + g1 * (255 - alpha) * a1 / 255) / newAlpha;
            int newB = (b2 * alpha + b1 * (255 - alpha) * a1 / 255) / newAlpha;
            this.imageData.pixels[yIn * this.width + xIn] = (newAlpha << 24) | (newR << 16) | (newG << 8) | newB;
         }
      }
   }

   @Deprecated
   public int[] makePixelArray() {
      this.checkImage();
      int[] result = new int[this.width * this.height];
      System.arraycopy(this.imageData.pixels, 0, result, 0, result.length);
      return result;
   }

   public void uploadTextureSub(int level, int xOffset, int yOffset, boolean mipmap) {
      this.uploadTextureSub(level, xOffset, yOffset, 0, 0, this.width, this.height, mipmap);
   }

   public void uploadTextureSub(int level, int xOffset, int yOffset, int unpackSkipPixels, int unpackSkipRows, int widthIn, int heightIn, boolean mipmap) {
      this.uploadTextureSub(level, xOffset, yOffset, unpackSkipPixels, unpackSkipRows, widthIn, heightIn, false, false, mipmap);
   }

   public void uploadTextureSub(int level, int xOffset, int yOffset, int unpackSkipPixels, int unpackSkipRows, int widthIn, int heightIn, boolean blur, boolean clamp, boolean mipmap) {
      this.checkImage();

      int[] subPixels = new int[widthIn * heightIn];
      for (int row = 0; row < heightIn; row++) {
         int srcRow = unpackSkipRows + row;
         if (srcRow < this.height) {
            int srcOffset = srcRow * this.width + unpackSkipPixels;
            int dstOffset = row * widthIn;
            int copyWidth = Math.min(widthIn, this.width - unpackSkipPixels);
            if (copyWidth > 0) {
               System.arraycopy(this.imageData.pixels, srcOffset, subPixels, dstOffset, copyWidth);
            }
         }
      }

      IntBuffer pixelBuffer = EagRuntime.allocateIntBuffer(subPixels.length);
      try {
         pixelBuffer.put(subPixels);
         pixelBuffer.flip();
         EaglercraftGPU.glTexSubImage2D(GL_TEXTURE_2D, level, xOffset, yOffset, widthIn, heightIn, GL_RGBA, GL_UNSIGNED_BYTE, pixelBuffer);
      } finally {
         EagRuntime.freeIntBuffer(pixelBuffer);
      }

      if (clamp) {
         setWrapST(true);
      }

      setMinMagFilters(blur, mipmap);
   }

   public void downloadFromTexture(int level, boolean opaque) {
      this.checkImage();
   }

   public void downloadFromFramebuffer(boolean opaque) {
      this.checkImage();
      int w = this.width;
      int h = this.height;
      net.lax1dude.eaglercraft.internal.buffer.ByteBuffer buffer = EagRuntime.allocateByteBuffer(w * h * 4);
      EaglercraftGPU.glReadPixels(0, 0, w, h, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
      int[] pixels = this.imageData.pixels;
      int idx = 0;
      for (int y = 0; y < h; y++) {
         for (int x = 0; x < w; x++) {
            int r = buffer.get(idx++) & 0xFF;
            int g = buffer.get(idx++) & 0xFF;
            int b = buffer.get(idx++) & 0xFF;
            int a = buffer.get(idx++) & 0xFF;
            if (opaque) a = 255;
            pixels[y * w + x] = (a << 24) | (b << 16) | (g << 8) | r;
         }
      }
      EagRuntime.freeByteBuffer(buffer);
   }

   public void func_216510_a(String p_216510_1_) throws IOException {
      this.write(new VFile2(p_216510_1_));
   }

   public void write(VFile2 fileIn) throws IOException {
      this.checkImage();
      fileIn.setAllBytes(this.encodePNG());
   }

   private byte[] encodePNG() throws IOException {
      int w = this.width;
      int h = this.height;
      int[] px = this.imageData.pixels;
      byte[] raw = new byte[h * (1 + w * 4)];
      int p = 0;
      for (int y = 0; y < h; ++y) {
         raw[p++] = 0;
         int row = y * w;
         for (int x = 0; x < w; ++x) {
            int c = px[row + x];
            raw[p++] = (byte) (c & 0xFF);          
            raw[p++] = (byte) ((c >>> 8) & 0xFF);  
            raw[p++] = (byte) ((c >>> 16) & 0xFF); 
            raw[p++] = (byte) ((c >>> 24) & 0xFF); 
         }
      }
      ByteArrayOutputStream idat = new ByteArrayOutputStream();
      OutputStream def = EaglerZLIB.newDeflaterOutputStream(idat);
      def.write(raw);
      def.close();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      out.write(new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A }); 
      ByteArrayOutputStream ihdr = new ByteArrayOutputStream();
      writeIntBE(ihdr, w);
      writeIntBE(ihdr, h);
      ihdr.write(8); 
      ihdr.write(6); 
      ihdr.write(0); 
      ihdr.write(0); 
      ihdr.write(0); 
      writeChunk(out, "IHDR", ihdr.toByteArray());
      writeChunk(out, "IDAT", idat.toByteArray());
      writeChunk(out, "IEND", new byte[0]);
      return out.toByteArray();
   }

   private static void writeIntBE(ByteArrayOutputStream os, int v) {
      os.write((v >>> 24) & 0xFF);
      os.write((v >>> 16) & 0xFF);
      os.write((v >>> 8) & 0xFF);
      os.write(v & 0xFF);
   }

   private static void writeChunk(ByteArrayOutputStream os, String type, byte[] data) throws IOException {
      writeIntBE(os, data.length);
      byte[] typeBytes = new byte[] { (byte) type.charAt(0), (byte) type.charAt(1), (byte) type.charAt(2), (byte) type.charAt(3) };
      os.write(typeBytes);
      os.write(data);
      CRC32 crc = new CRC32();
      crc.update(typeBytes);
      crc.update(data);
      writeIntBE(os, (int) crc.getValue());
   }

   public void renderGlyph(Object info, int glyphIndex, int widthIn, int heightIn, float scaleX, float scaleY, float shiftX, float shiftY, int x, int y) {
      this.checkImage();
   }

   public void copyImageData(NativeImage from) {
      this.checkImage();
      from.checkImage();
      if (from.width != this.width || from.height != this.height) {
         throw new IllegalArgumentException("Source image dimensions don't match");
      }
      System.arraycopy(from.imageData.pixels, 0, this.imageData.pixels, 0, this.imageData.pixels.length);
   }

   public void fillAreaRGBA(int x, int y, int widthIn, int heightIn, int value) {
      this.checkImage();
      for (int dy = 0; dy < heightIn; ++dy) {
         for (int dx = 0; dx < widthIn; ++dx) {
            int px = x + dx;
            int py = y + dy;
            if (px >= 0 && px < this.width && py >= 0 && py < this.height) {
               this.imageData.pixels[py * this.width + px] = value;
            }
         }
      }
   }

   public void copyAreaRGBA(int xFrom, int yFrom, int xToDelta, int yToDelta, int widthIn, int heightIn, boolean mirrorX, boolean mirrorY) {
      this.checkImage();
   }

   public void flip() {
      this.checkImage();
      for (int y = 0; y < this.height / 2; ++y) {
         for (int x = 0; x < this.width; ++x) {
            int topIndex = y * this.width + x;
            int bottomIndex = (this.height - 1 - y) * this.width + x;
            int temp = this.imageData.pixels[topIndex];
            this.imageData.pixels[topIndex] = this.imageData.pixels[bottomIndex];
            this.imageData.pixels[bottomIndex] = temp;
         }
      }
   }

   public void resizeSubRectTo(int xIn, int yIn, int widthIn, int heightIn, NativeImage imageIn) {
      this.checkImage();
      imageIn.checkImage();

      for (int dy = 0; dy < imageIn.height; ++dy) {
         for (int dx = 0; dx < imageIn.width; ++dx) {
            int srcX = xIn + (dx * widthIn) / imageIn.width;
            int srcY = yIn + (dy * heightIn) / imageIn.height;

            if (srcX >= 0 && srcX < this.width && srcY >= 0 && srcY < this.height) {
               imageIn.imageData.pixels[dy * imageIn.width + dx] = this.imageData.pixels[srcY * this.width + srcX];
            }
         }
      }
   }

   public void resizeSubRectToBilinear(int xIn, int yIn, int widthIn, int heightIn, NativeImage imageIn) {
      this.checkImage();
      imageIn.checkImage();

      for (int dy = 0; dy < imageIn.height; ++dy) {
         for (int dx = 0; dx < imageIn.width; ++dx) {
            float srcXf = xIn + ((float) dx / (float) imageIn.width) * widthIn;
            float srcYf = yIn + ((float) dy / (float) imageIn.height) * heightIn;

            int sx = (int) srcXf;
            int sy = (int) srcYf;
            float fracX = srcXf - sx;
            float fracY = srcYf - sy;

            if (sx >= 0 && sx < this.width && sy >= 0 && sy < this.height) {
               int sx1 = Math.min(sx + 1, this.width - 1);
               int sy1 = Math.min(sy + 1, this.height - 1);

               int c00 = this.imageData.pixels[sy * this.width + sx];
               int c10 = this.imageData.pixels[sy * this.width + sx1];
               int c01 = this.imageData.pixels[sy1 * this.width + sx];
               int c11 = this.imageData.pixels[sy1 * this.width + sx1];

               int a00 = (c00 >> 24) & 0xFF;
               int b00 = (c00 >> 16) & 0xFF;
               int g00 = (c00 >> 8) & 0xFF;
               int r00 = c00 & 0xFF;

               int a10 = (c10 >> 24) & 0xFF;
               int b10 = (c10 >> 16) & 0xFF;
               int g10 = (c10 >> 8) & 0xFF;
               int r10 = c10 & 0xFF;

               int a01 = (c01 >> 24) & 0xFF;
               int b01 = (c01 >> 16) & 0xFF;
               int g01 = (c01 >> 8) & 0xFF;
               int r01 = c01 & 0xFF;

               int a11 = (c11 >> 24) & 0xFF;
               int b11 = (c11 >> 16) & 0xFF;
               int g11 = (c11 >> 8) & 0xFF;
               int r11 = c11 & 0xFF;

               float r0 = r00 + (r10 - r00) * fracX;
               float g0 = g00 + (g10 - g00) * fracX;
               float b0 = b00 + (b10 - b00) * fracX;
               float a0 = a00 + (a10 - a00) * fracX;

               float r1 = r01 + (r11 - r01) * fracX;
               float g1 = g01 + (g11 - g01) * fracX;
               float b1 = b01 + (b11 - b01) * fracX;
               float a1 = a01 + (a11 - a01) * fracX;

               int r = Math.min(255, Math.max(0, (int) (r0 + (r1 - r0) * fracY)));
               int g = Math.min(255, Math.max(0, (int) (g0 + (g1 - g0) * fracY)));
               int b = Math.min(255, Math.max(0, (int) (b0 + (b1 - b0) * fracY)));
               int a = Math.min(255, Math.max(0, (int) (a0 + (a1 - a0) * fracY)));

               imageIn.imageData.pixels[dy * imageIn.width + dx] = (a << 24) | (b << 16) | (g << 8) | r;
            }
         }
      }
   }

   public void untrack() {
   }

   public static NativeImage func_216511_b(String p_216511_0_) throws IOException {
      return read(new net.lax1dude.eaglercraft.internal.vfs2.VFile2(p_216511_0_).getInputStream());
   }

   @OnlyIn(Dist.CLIENT)
   public static enum PixelFormat {
      RGBA(4, 6408, true, true, true, false, true, 0, 8, 16, 255, 24, true),
      RGB(3, 6407, true, true, true, false, false, 0, 8, 16, 255, 255, true),
      LUMINANCE_ALPHA(2, 6410, false, false, false, true, true, 255, 255, 255, 0, 8, true),
      LUMINANCE(1, 6409, false, false, false, true, false, 0, 0, 0, 0, 255, true);

      private final int pixelSize;
      private final int glFormat;
      private final boolean red;
      private final boolean green;
      private final boolean blue;
      private final boolean hasLuminance;
      private final boolean hasAlpha;
      private final int offsetRed;
      private final int offsetGreen;
      private final int offsetBlue;
      private final int offsetLuminance;
      private final int offsetAlpha;
      private final boolean serializable;

      private PixelFormat(int channelsIn, int glFormatIn, boolean redIn, boolean greenIn, boolean blueIn, boolean luminanceIn, boolean alphaIn, int offsetRedIn, int offsetGreenIn, int offsetBlueIn, int offsetLuminanceIn, int offsetAlphaIn, boolean standardIn) {
         this.pixelSize = channelsIn;
         this.glFormat = glFormatIn;
         this.red = redIn;
         this.green = greenIn;
         this.blue = blueIn;
         this.hasLuminance = luminanceIn;
         this.hasAlpha = alphaIn;
         this.offsetRed = offsetRedIn;
         this.offsetGreen = offsetGreenIn;
         this.offsetBlue = offsetBlueIn;
         this.offsetLuminance = offsetLuminanceIn;
         this.offsetAlpha = offsetAlphaIn;
         this.serializable = standardIn;
      }

      public int getPixelSize() {
         return this.pixelSize;
      }
      public int getGlFormat() {
         return this.glFormat;
      }

      public boolean hasAlpha() {
         return this.hasAlpha;
      }

      public int getOffsetAlpha() {
         return this.offsetAlpha;
      }

      public boolean hasLuminanceOrAlpha() {
         return this.hasLuminance || this.hasAlpha;
      }

      public int getOffsetAlphaBits() {
         return this.hasLuminance ? this.offsetLuminance : this.offsetAlpha;
      }

      public boolean isSerializable() {
         return this.serializable;
      }

      private static NativeImage.PixelFormat fromChannelCount(int channelsIn) {
         switch(channelsIn) {
         case 1:
            return LUMINANCE;
         case 2:
            return LUMINANCE_ALPHA;
         case 3:
            return RGB;
         case 4:
         default:
            return RGBA;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum PixelFormatGLCode {
      RGBA(6408),
      RGB(6407),
      LUMINANCE_ALPHA(6410),
      LUMINANCE(6409),
      INTENSITY(32841);

      private final int glConstant;

      private PixelFormatGLCode(int glFormatIn) {
         this.glConstant = glFormatIn;
      }

      public int getGlFormat() {
         return this.glConstant;
      }
   }

}
