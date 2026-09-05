package net.minecraft.client.resources;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraft.client.renderer.DownloadImageBuffer;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.DownloadingTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class SkinManager {
    private final TextureManager textureManager;
    private final VFile2 skinCacheDir;
    private final MinecraftSessionService sessionService;

    public SkinManager(TextureManager textureManagerInstance, VFile2 skinCacheDirectory, MinecraftSessionService sessionService) {
        this.textureManager = textureManagerInstance;
        this.skinCacheDir = skinCacheDirectory;
        this.sessionService = sessionService;
    }

    public ResourceLocation loadSkin(MinecraftProfileTexture profileTexture, Type textureType) {
        return this.loadSkin(profileTexture, textureType, (SkinManager.ISkinAvailableCallback) null);
    }

    public ResourceLocation loadSkin(final MinecraftProfileTexture profileTexture, final Type textureType, final SkinManager.ISkinAvailableCallback skinAvailableCallback) {
        String s = Integer.toHexString(profileTexture.getHash().hashCode());
        final ResourceLocation resourcelocation = new ResourceLocation("skins/" + s);
        ITextureObject itextureobject = this.textureManager.getTexture(resourcelocation);
        if (itextureobject != null) {
            if (skinAvailableCallback != null) {
                skinAvailableCallback.onSkinTextureAvailable(textureType, resourcelocation, profileTexture);
            }
        } else {
            VFile2 file1 = new VFile2(this.skinCacheDir, s.length() > 2 ? s.substring(0, 2) : "xx");
            VFile2 file2 = new VFile2(file1, s);
            final IImageBuffer iimagebuffer = textureType == Type.SKIN ? new DownloadImageBuffer() : null;
            DownloadingTexture downloadingtexture = new DownloadingTexture(file2, profileTexture.getUrl(), DefaultPlayerSkin.getDefaultSkinLegacy(), new IImageBuffer() {
                public NativeImage parseUserSkin(NativeImage nativeImageIn) {
                    return iimagebuffer != null ? iimagebuffer.parseUserSkin(nativeImageIn) : nativeImageIn;
                }

                public void skinAvailable() {
                    if (iimagebuffer != null) {
                        iimagebuffer.skinAvailable();
                    }

                    if (skinAvailableCallback != null) {
                        skinAvailableCallback.onSkinTextureAvailable(textureType, resourcelocation, profileTexture);
                    }

                }
            });
            this.textureManager.loadTexture(resourcelocation, downloadingtexture);
        }

        return resourcelocation;
    }

    public Map<Type, MinecraftProfileTexture> loadSkinFromCache(GameProfile profile) {
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public interface ISkinAvailableCallback {
        void onSkinTextureAvailable(Type p_onSkinTextureAvailable_1_, ResourceLocation p_onSkinTextureAvailable_2_, MinecraftProfileTexture p_onSkinTextureAvailable_3_);
    }
}
