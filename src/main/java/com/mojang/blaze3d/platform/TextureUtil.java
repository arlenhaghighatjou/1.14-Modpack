package com.mojang.blaze3d.platform;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class TextureUtil {
    public static final int MIN_MIPMAP_LEVEL = 0;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int DEFAULT_IMAGE_BUFFER_SIZE = 8192;

    public static int generateTextureId() {
        return GlStateManager.genTexture();
    }

    public static void releaseTextureId(int p_releaseTextureId_0_) {
        GlStateManager.deleteTexture(p_releaseTextureId_0_);
    }

    public static void prepareImage(int p_prepareImage_0_, int p_prepareImage_1_, int p_prepareImage_2_) {
        prepareImage(NativeImage.PixelFormatGLCode.RGBA, p_prepareImage_0_, 0, p_prepareImage_1_, p_prepareImage_2_);
    }

    public static void prepareImage(NativeImage.PixelFormatGLCode p_prepareImage_0_, int p_prepareImage_1_, int p_prepareImage_2_, int p_prepareImage_3_) {
        prepareImage(p_prepareImage_0_, p_prepareImage_1_, 0, p_prepareImage_2_, p_prepareImage_3_);
    }

    public static void prepareImage(int p_prepareImage_0_, int p_prepareImage_1_, int p_prepareImage_2_, int p_prepareImage_3_) {
        prepareImage(NativeImage.PixelFormatGLCode.RGBA, p_prepareImage_0_, p_prepareImage_1_, p_prepareImage_2_, p_prepareImage_3_);
    }

    public static void prepareImage(NativeImage.PixelFormatGLCode p_prepareImage_0_, int p_prepareImage_1_, int p_prepareImage_2_, int p_prepareImage_3_, int p_prepareImage_4_) {
        bind(p_prepareImage_1_);
        if (p_prepareImage_2_ >= 0) {
            GlStateManager.texParameter(3553, 33085, p_prepareImage_2_);
            GlStateManager.texParameter(3553, 33082, 0);
            GlStateManager.texParameter(3553, 33083, p_prepareImage_2_);
        }

        for (int i = 0; i <= p_prepareImage_2_; ++i) {
            GlStateManager.texImage2D(3553, i, p_prepareImage_0_.getGlFormat(), p_prepareImage_3_ >> i, p_prepareImage_4_ >> i, 0, 6408, 5121, null);
        }

    }

    private static void bind(int p_bind_0_) {
        GlStateManager.bindTexture(p_bind_0_);
    }

}
