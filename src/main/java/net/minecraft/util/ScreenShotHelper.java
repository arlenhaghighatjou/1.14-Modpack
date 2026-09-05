package net.minecraft.util;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.resources.SimpleResource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ScreenShotHelper {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

   public static void saveScreenshot(VFile2 gameDirectory, int width, int height, Framebuffer buffer, Consumer<ITextComponent> p_148260_4_) {
      saveScreenshot(gameDirectory, (String)null, width, height, buffer, p_148260_4_);
   }

   public static void saveScreenshot(VFile2 gameDirectory,  String screenshotName, int width, int height, Framebuffer buffer, Consumer<ITextComponent> p_148259_5_) {
      SimpleResource.RESOURCE_IO_EXECUTOR.execute(() -> {
         try {
            String name = net.lax1dude.eaglercraft.internal.PlatformApplication.saveScreenshot();
            ITextComponent itextcomponent = (new StringTextComponent(name)).applyTextStyle(TextFormatting.UNDERLINE);
            p_148259_5_.accept(new TranslationTextComponent("screenshot.success", itextcomponent));
         } catch (Exception exception) {
            LOGGER.warn("Couldn't save screenshot", (Throwable)exception);
            p_148259_5_.accept(new TranslationTextComponent("screenshot.failure", exception.getMessage()));
         }
      });
   }

   public static NativeImage createScreenshot(int width, int height, Framebuffer framebufferIn) {
      if (GLX.isUsingFBOs()) {
         width = framebufferIn.framebufferTextureWidth;
         height = framebufferIn.framebufferTextureHeight;
      }

      NativeImage nativeimage = new NativeImage(width, height, false);
      framebufferIn.bindFramebuffer(true);
      nativeimage.downloadFromFramebuffer(true);

      nativeimage.flip();
      return nativeimage;
   }

   private static VFile2 getTimestampedPNGFileForDirectory(VFile2 gameDirectory) {
      String s = DATE_FORMAT.format(new Date());
      int i = 1;

      while(true) {
         VFile2 file1 = new VFile2(gameDirectory, s + (i == 1 ? "" : "_" + i) + ".png");
         if (!file1.exists()) {
            return file1;
         }

         ++i;
      }
   }
}
