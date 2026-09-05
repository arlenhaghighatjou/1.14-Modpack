package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import net.lax1dude.eaglercraft.internal.EnumCursorType;
import net.lax1dude.eaglercraft.internal.PlatformInput;
import net.lax1dude.eaglercraft.sp.gui.ScreenLANInfo;
import net.lax1dude.eaglercraft.sp.gui.ScreenLANNotSupported;
import net.lax1dude.eaglercraft.sp.gui.ScreenRelay;
import net.lax1dude.eaglercraft.sp.lan.LANServerController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;

public class NetworkSettingsButton extends Widget {

    private final Screen screen;
    private final String text;
    private final Minecraft mc;

    public NetworkSettingsButton(Screen screen) {
        super(0, 0, 0, 0, "");
        this.screen = screen;
        this.text = I18n.format("directConnect.lanWorldRelay");
        this.mc = Minecraft.getInstance();
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.scalef(0.75f, 0.75f, 0.75f);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);

        int w = mc.fontRenderer.getStringWidth(text);
        boolean hover = mouseX > 1 && mouseY > 1 && mouseX < (w * 3 / 4) + 7 && mouseY < 12;
        if(hover) {
        }

        mc.fontRenderer.drawString(TextFormatting.UNDERLINE + text, 5, 5, hover ? 0xFFEEEE22 : 0xFFCCCCCC);

        GlStateManager.popMatrix();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int w = mc.fontRenderer.getStringWidth(text);
        if(mouseX > 2 && mouseY > 2 && mouseX < (w * 3 / 4) + 5 && mouseY < 12) {
            if(LANServerController.supported()) {
                mc.displayGuiScreen(new ScreenRelay(screen));
            }else {
                mc.displayGuiScreen(new ScreenLANNotSupported(screen));
            }
            this.mc.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }
        return false;
    }
}
