package net.minecraft.client.renderer;

import net.lax1dude.eaglercraft.EagRuntime;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.lax1dude.eaglercraft.internal.buffer.ByteBuffer;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WorldVertexBufferUploader {
   private ByteBuffer uploadBuffer;
   private final byte[] copyBuffer = new byte[12];

   public void draw(BufferBuilder bufferBuilderIn) {
      if (bufferBuilderIn.getVertexCount() > 0) {
         VertexFormat fmt = bufferBuilderIn.getVertexFormat();
         ByteBuffer src = bufferBuilderIn.getByteBuffer();
         int vertexCount = bufferBuilderIn.getVertexCount();
         int totalBytes = vertexCount * fmt.getSize();

         if (fmt.isEaglerCompatible()) {
            src.limit(totalBytes).position(0);
            EaglercraftGPU.renderBuffer(src, fmt.getEaglerAttribBits(), bufferBuilderIn.getDrawMode(), vertexCount);
         } else {
            int stride = fmt.getSize();
            int attribs = 0;
            int colorOffset = fmt.hasColor() ? fmt.getColorOffset() : -1;
            int texOffset = fmt.hasUv(0) ? fmt.getUvOffsetById(0) : -1;
            int lightmapOffset = getlightmapoffset(fmt, colorOffset, texOffset);
            int normalOffset = fmt.hasNormal() ? fmt.getNormalOffset() : -1;

            if (colorOffset >= 0) attribs |= EaglercraftGPU.ATTRIB_COLOR;
            if (texOffset >= 0) attribs |= EaglercraftGPU.ATTRIB_TEXTURE;
            if (normalOffset >= 0) attribs |= EaglercraftGPU.ATTRIB_NORMAL;
            if (lightmapOffset >= 0) attribs |= EaglercraftGPU.ATTRIB_LIGHTMAP;

            int dstStride = 12;
            if ((attribs & EaglercraftGPU.ATTRIB_COLOR) != 0) dstStride += 4;
            if ((attribs & EaglercraftGPU.ATTRIB_TEXTURE) != 0) dstStride += 8;
            if ((attribs & EaglercraftGPU.ATTRIB_NORMAL) != 0) dstStride += 4;
            if ((attribs & EaglercraftGPU.ATTRIB_LIGHTMAP) != 0) dstStride += 4;

            int dstSize = dstStride * vertexCount;
            if (uploadBuffer == null || uploadBuffer.capacity() < dstSize) {
               ByteBuffer oldBuffer = uploadBuffer;
               uploadBuffer = GLAllocation.createDirectByteBuffer(dstSize);
               if (oldBuffer != null) {
                  EagRuntime.freeByteBuffer(oldBuffer);
               }
            }
            uploadBuffer.clear();

            byte[] tmpBuf = this.copyBuffer;
            int pos = src.position();
            for (int i = 0; i < vertexCount; ++i) {
               int base = i * stride;

               src.position(base);
               src.get(tmpBuf, 0, 12);
               uploadBuffer.put(tmpBuf, 0, 12);

               if ((attribs & EaglercraftGPU.ATTRIB_COLOR) != 0) {
                  src.position(base + colorOffset);
                  src.get(tmpBuf, 0, 4);
                  uploadBuffer.put(tmpBuf, 0, 4);
               }
               if ((attribs & EaglercraftGPU.ATTRIB_TEXTURE) != 0) {
                  src.position(base + texOffset);
                  src.get(tmpBuf, 0, 8);
                  uploadBuffer.put(tmpBuf, 0, 8);
               }
               if ((attribs & EaglercraftGPU.ATTRIB_NORMAL) != 0) {
                  src.position(base + normalOffset);
                  src.get(tmpBuf, 0, 4);
                  uploadBuffer.put(tmpBuf, 0, 4);
               }
               if ((attribs & EaglercraftGPU.ATTRIB_LIGHTMAP) != 0) {
                  src.position(base + lightmapOffset);
                  src.get(tmpBuf, 0, 4);
                  uploadBuffer.put(tmpBuf, 0, 4);
               }
            }
            src.position(pos).limit(totalBytes);
            uploadBuffer.flip();
            EaglercraftGPU.renderBuffer(uploadBuffer, attribs, bufferBuilderIn.getDrawMode(), vertexCount);
         }
         bufferBuilderIn.reset();
      }
   }

   private static int getlightmapoffset(VertexFormat fmt, int colorOff, int texOff) {
      if (fmt.hasUv(1)) return fmt.getUvOffsetById(1);
      int stride = fmt.getSize();
      int normalOff = fmt.hasNormal() ? fmt.getNormalOffset() : -1;
      if (stride == 28 && colorOff == 12 && texOff == 16 && normalOff < 0) return 24;
      if (stride == 32 && colorOff == 12 && texOff == 16 && normalOff == 28) return 24;
      return -1;
   }
}
