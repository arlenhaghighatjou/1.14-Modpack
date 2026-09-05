package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
public class DynamicTexture extends Texture implements AutoCloseable {
    private NativeImage dynamicTextureData;

    public DynamicTexture(NativeImage nativeImageIn) {
        this.dynamicTextureData = nativeImageIn;
        TextureUtil.prepareImage(this.getGlTextureId(), this.dynamicTextureData.getWidth(), this.dynamicTextureData.getHeight());
        this.updateDynamicTexture();
    }

    public DynamicTexture(int widthIn, int heightIn, boolean clearIn) {
        this.dynamicTextureData = new NativeImage(widthIn, heightIn, clearIn);
        TextureUtil.prepareImage(this.getGlTextureId(), this.dynamicTextureData.getWidth(), this.dynamicTextureData.getHeight());
    }

    public void loadTexture(IResourceManager manager) throws IOException {
    }

    public void updateDynamicTexture() {
        this.bindTexture();
        this.dynamicTextureData.uploadTextureSub(0, 0, 0, false);
        this.setBlurMipmap(false, false);
    }

    public NativeImage getTextureData() {
        return this.dynamicTextureData;
    }

    public void setTextureData(NativeImage nativeImageIn) throws Exception {
        this.dynamicTextureData.close();
        this.dynamicTextureData = nativeImageIn;
    }

    public void close() {
        this.dynamicTextureData.close();
        this.deleteGlTexture();
        this.dynamicTextureData = null;
    }
}
