package net.minecraft.client.gui.toasts;

import com.mojang.blaze3d.platform.GlStateManager;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class FriendToast implements IToast {

    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("textures/gui/friends/toast_background.png");

    private static final int TOAST_WIDTH = 160;
    private static final int TOAST_TEX_H = 32;
    private static final int BORDER = 4;
    private static final int FACE_SIZE = 20;
    private static final int TEXT_LEFT_WITH_FACE = 30;
    private static final int TEXT_LEFT_NO_FACE = 7;
    private static final int PADDING_TOP = 7;
    private static final int PADDING_BOTTOM = 3;
    private static final int LINE_SPACING = 11;
    private static final long DISPLAY_TIME_MS = 5000L;

    @Nullable
    private final String skinLocation;
    private final String username;
    private final List<String> messageLines;
    private long firstDrawTime;
    private boolean newDisplay;

    public FriendToast(String message, @Nullable String skinLocation, @Nullable String username) {
        this.skinLocation = skinLocation;
        this.username = username;
        this.newDisplay = true;

        Minecraft mc = Minecraft.getInstance();
        int textLeft = hasFace() ? TEXT_LEFT_WITH_FACE : TEXT_LEFT_NO_FACE;

        if (mc != null) {
            this.messageLines = mc.fontRenderer.listFormattedStringToWidth(message, TOAST_WIDTH - textLeft - 4);
        } else {
            this.messageLines = Collections.singletonList(message);
        }
    }

    private boolean hasFace() {
        return (skinLocation != null && !skinLocation.isEmpty())
                || (username != null && !username.isEmpty());
    }

    private int getToastHeight() {
        int contentHeight = Math.max(this.messageLines.size(), 2) * LINE_SPACING;
        return PADDING_TOP + contentHeight + PADDING_BOTTOM;
    }

    @Override
    public IToast.Visibility draw(ToastGui toastGui, long delta) {
        if (this.newDisplay) {
            this.firstDrawTime = delta;
            this.newDisplay = false;
        }

        Minecraft mc = toastGui.getMinecraft();
        int toastHeight = getToastHeight();
        int midH = toastHeight - BORDER * 2; 

        mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        GlStateManager.color3f(1.0F, 1.0F, 1.0F);

        AbstractGui.blit(0, 0, TOAST_WIDTH, BORDER,
                0.0F, 0.0F, TOAST_WIDTH, BORDER,
                TOAST_WIDTH, TOAST_TEX_H);

        AbstractGui.blit(0, BORDER, TOAST_WIDTH, midH,
                0.0F, (float) BORDER, TOAST_WIDTH, TOAST_TEX_H - BORDER * 2,
                TOAST_WIDTH, TOAST_TEX_H);

        AbstractGui.blit(0, BORDER + midH, TOAST_WIDTH, BORDER,
                0.0F, (float) (TOAST_TEX_H - BORDER), TOAST_WIDTH, BORDER,
                TOAST_WIDTH, TOAST_TEX_H);

        int textLeft = TEXT_LEFT_NO_FACE;
        if (hasFace()) {
            ResourceLocation skinLoc;
            if (skinLocation != null && !skinLocation.isEmpty()) {
                skinLoc = new ResourceLocation(skinLocation);
            } else {
                skinLoc = DefaultPlayerSkin.getDefaultSkin(
                        EaglercraftUUID.nameUUIDFromBytes(username.getBytes()));
            }
            mc.getTextureManager().bindTexture(skinLoc);
            GlStateManager.color3f(1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            AbstractGui.blit(6, 6, FACE_SIZE, FACE_SIZE, 8.0F, 8.0F, 8, 8, 64, 64);
            AbstractGui.blit(6, 6, FACE_SIZE, FACE_SIZE, 40.0F, 8.0F, 8, 8, 64, 64);
            textLeft = TEXT_LEFT_WITH_FACE;
        }

        int contentHeight = Math.max(this.messageLines.size(), 2) * LINE_SPACING;
        int totalTextHeight = this.messageLines.size() * LINE_SPACING;
        int textTop = PADDING_TOP + (contentHeight - totalTextHeight) / 2;

        for (int i = 0; i < this.messageLines.size(); i++) {
            mc.fontRenderer.drawString(this.messageLines.get(i),
                    (float) textLeft, (float) (textTop + i * LINE_SPACING), -1);
        }

        return delta - this.firstDrawTime < DISPLAY_TIME_MS
                ? IToast.Visibility.SHOW : IToast.Visibility.HIDE;
    }

    public static void add(ToastGui toastGui, String message,
                           @Nullable String skinLocation, @Nullable String username) {
        toastGui.add(new FriendToast(message, skinLocation, username));
    }

    public static void showInviteSent(Minecraft mc, String targetName,
                                      @Nullable String skinLocation) {
        add(mc.getToastGui(), "§6§lInvite Sent\n§rInvited " + targetName,
                skinLocation, targetName);
    }

    public static void showInviteReceived(Minecraft mc, String fromName,
                                          @Nullable String skinLocation) {
        add(mc.getToastGui(), "§a§lInvite!\n§r" + fromName + " invited you",
                skinLocation, fromName);
    }

    public static void showRequestSent(Minecraft mc, String targetName,
                                       @Nullable String skinLocation) {
        add(mc.getToastGui(), "§a§lRequest Sent\n§rRequest sent to " + targetName,
                skinLocation, targetName);
    }

    public static void showJoinRequestReceived(Minecraft mc, String fromName,
                                               String worldName,
                                               @Nullable String skinLocation) {
        add(mc.getToastGui(), "§6§lJoin request\n§r" + fromName + " - " + worldName,
                skinLocation, fromName);
    }
}
