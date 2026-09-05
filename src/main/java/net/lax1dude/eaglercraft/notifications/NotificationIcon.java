package net.lax1dude.eaglercraft.notifications;

import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.lax1dude.eaglercraft.profile.EaglerSkinTexture;
import net.minecraft.util.ResourceLocation;

public class NotificationIcon {

    private static int notifIconTmpId = 0;
    public final EaglercraftUUID iconUUID;
    public final EaglerSkinTexture texture;
    public final ResourceLocation resource;
    protected int refCount = 0;
    protected boolean serverRegistered = true;

    protected NotificationIcon(EaglercraftUUID iconUUID, EaglerSkinTexture texture) {
        this.iconUUID = iconUUID;
        this.texture = texture;
        this.resource = new ResourceLocation("eagler:gui/server/notifs/tex_" + notifIconTmpId++);
    }

    public void retain() {
        ++refCount;
    }

    public void release() {
        --refCount;
    }

    public boolean isValid() {
        return serverRegistered || refCount > 0;
    }

}
