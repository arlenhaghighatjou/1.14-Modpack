package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@OnlyIn(Dist.CLIENT)
public class DownloadingTexture extends SimpleTexture {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final AtomicInteger TEXTURE_DOWNLOADER_THREAD_ID = new AtomicInteger(0);

    private final VFile2 cacheFile;
    private final String imageUrl;

    private final IImageBuffer imageBuffer;

    private Thread imageThread;
    private volatile boolean textureUploaded;

    public DownloadingTexture(VFile2 cacheFileIn, String imageUrlIn, ResourceLocation textureResourceLocation, IImageBuffer imageBufferIn) {
        super(textureResourceLocation);
        this.cacheFile = cacheFileIn;
        this.imageUrl = imageUrlIn;
        this.imageBuffer = imageBufferIn;
    }

    private void uploadImage(NativeImage nativeImageIn) {
        TextureUtil.prepareImage(this.getGlTextureId(), nativeImageIn.getWidth(), nativeImageIn.getHeight());
        nativeImageIn.uploadTextureSub(0, 0, 0, false);
    }

    public void setImage(NativeImage nativeImageIn) {
        if (this.imageBuffer != null) {
            this.imageBuffer.skinAvailable();
        }

        synchronized (this) {
            this.uploadImage(nativeImageIn);
            this.textureUploaded = true;
        }
    }

    public void loadTexture(IResourceManager manager) throws IOException {
        if (!this.textureUploaded) {
            synchronized (this) {
                super.loadTexture(manager);
                this.textureUploaded = true;
            }
        }

        if (this.imageThread == null) {
            if (this.cacheFile != null && this.cacheFile.exists()) {
                LOGGER.debug("Loading http texture from local cache ({})", (Object) this.cacheFile);
                NativeImage nativeimage = null;

                try {
                    nativeimage = NativeImage.read(this.cacheFile.getInputStream());
                    if (this.imageBuffer != null) {
                        nativeimage = this.imageBuffer.parseUserSkin(nativeimage);
                    }

                    this.setImage(nativeimage);
                } catch (IOException ioexception) {
                    LOGGER.error("Couldn't load skin {}", this.cacheFile, ioexception);
                    this.loadTextureFromServer();
                } finally {
                    if (nativeimage != null) {
                        nativeimage.close();
                    }

                }
            } else {
                this.loadTextureFromServer();
            }
        }

    }

    protected void loadTextureFromServer() {
    }
}
