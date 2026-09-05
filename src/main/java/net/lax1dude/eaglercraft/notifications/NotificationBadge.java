package net.lax1dude.eaglercraft.notifications;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.lax1dude.eaglercraft.profanity_filter.ProfanityFilter;
import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketNotifBadgeShowV4EAG.EnumBadgePriority;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;

public class NotificationBadge {

    public final ServerNotificationManager mgr;
    public final EaglercraftUUID badgeUUID;
    public final ITextComponent bodyComponent;
    public final ITextComponent titleComponent;
    public final ITextComponent sourceComponent;
    public final long clientTimestamp;
    public final long serverTimestamp;
    public final boolean silent;
    public final EnumBadgePriority priority;
    public final NotificationIcon mainIcon;
    public final NotificationIcon titleIcon;
    public final int hideAfterSec;
    public final int expireAfterSec;
    public final int backgroundColor;
    public final int bodyTxtColor;
    public final int titleTxtColor;
    public final int sourceTxtColor;
    protected ITextComponent bodyComponentProfanityFilter;
    protected ITextComponent titleComponentProfanityFilter;
    protected ITextComponent sourceComponentProfanityFilter;
    protected CachedNotifBadgeTexture currentCacheGLTexture = null;
    protected int currentCacheScaleFac = -1;
    protected boolean currentCacheXButton = false;
    protected boolean currentCacheProfanityFilter = false;
    protected long hideAtMillis = -1l;
    protected boolean unreadFlag = true;
    protected boolean unreadFlagRender = true;

    protected NotificationBadge(ServerNotificationManager mgr, EaglercraftUUID badgeUUID, ITextComponent bodyComponent,
                                ITextComponent titleComponent, ITextComponent sourceComponent, long clientTimestamp, long serverTimestamp,
                                boolean silent, EnumBadgePriority priority, NotificationIcon mainIcon, NotificationIcon titleIcon,
                                int hideAfterSec, int expireAfterSec, int backgroundColor, int bodyTxtColor, int titleTxtColor,
                                int sourceTxtColor) {
        this.mgr = mgr;
        this.badgeUUID = badgeUUID;
        this.bodyComponent = bodyComponent;
        this.titleComponent = titleComponent;
        this.sourceComponent = sourceComponent;
        this.clientTimestamp = clientTimestamp;
        this.serverTimestamp = serverTimestamp;
        this.silent = silent;
        this.priority = priority;
        this.mainIcon = mainIcon;
        this.titleIcon = titleIcon;
        this.hideAfterSec = hideAfterSec;
        this.expireAfterSec = expireAfterSec;
        this.backgroundColor = backgroundColor;
        this.bodyTxtColor = bodyTxtColor;
        this.titleTxtColor = titleTxtColor;
        this.sourceTxtColor = sourceTxtColor;
    }

    protected void incrIconRefcounts() {
        if (mainIcon != null) {
            mainIcon.retain();
        }
        if (titleIcon != null) {
            titleIcon.retain();
        }
    }

    protected void decrIconRefcounts() {
        deleteGLTexture();
        if (mainIcon != null) {
            mainIcon.release();
        }
        if (titleIcon != null) {
            titleIcon.release();
        }
    }

    protected CachedNotifBadgeTexture getGLTexture(ServerNotificationRenderer renderer, int scaleFactor, boolean showXButton) {
        boolean profanityFilter = Minecraft.getInstance().isEnableProfanityFilter();
        if (currentCacheGLTexture == null || currentCacheScaleFac != scaleFactor || currentCacheXButton != showXButton || currentCacheProfanityFilter != profanityFilter) {
            deleteGLTexture();
            currentCacheGLTexture = renderer.renderBadge(this, scaleFactor, showXButton);
            currentCacheScaleFac = scaleFactor;
            currentCacheXButton = showXButton;
            currentCacheProfanityFilter = profanityFilter;
        }
        return currentCacheGLTexture;
    }

    protected void deleteGLTexture() {
        if (currentCacheGLTexture != null) {
            GlStateManager.deleteTexture(currentCacheGLTexture.glTexture);
            currentCacheGLTexture = null;
        }
    }

    public void hideNotif() {
        if (hideAtMillis == -1l) {
            markRead();
            unreadFlagRender = false;
            hideAtMillis = EagRuntime.steadyTimeMillis();
        }
    }

    public void removeNotif() {
        mgr.removeNotifFromActiveList(badgeUUID);
    }

    public void markRead() {
        if (unreadFlag) {
            unreadFlag = false;
            --mgr.unreadCounter;
        }
    }

    public ITextComponent getBodyProfanityFilter() {
        if (Minecraft.getInstance().isEnableProfanityFilter()) {
            if (bodyComponentProfanityFilter == null && bodyComponent != null) {
                bodyComponentProfanityFilter = ProfanityFilter.getInstance().profanityFilterChatComponent(bodyComponent);
            }
            return bodyComponentProfanityFilter;
        } else {
            return bodyComponent;
        }
    }

    public ITextComponent getTitleProfanityFilter() {
        if (Minecraft.getInstance().isEnableProfanityFilter()) {
            if (titleComponentProfanityFilter == null && titleComponent != null) {
                titleComponentProfanityFilter = ProfanityFilter.getInstance().profanityFilterChatComponent(titleComponent);
            }
            return titleComponentProfanityFilter;
        } else {
            return titleComponent;
        }
    }

    public ITextComponent getSourceProfanityFilter() {
        if (Minecraft.getInstance().isEnableProfanityFilter()) {
            if (sourceComponentProfanityFilter == null && sourceComponent != null) {
                sourceComponentProfanityFilter = ProfanityFilter.getInstance().profanityFilterChatComponent(sourceComponent);
            }
            return sourceComponentProfanityFilter;
        } else {
            return sourceComponent;
        }
    }

}
