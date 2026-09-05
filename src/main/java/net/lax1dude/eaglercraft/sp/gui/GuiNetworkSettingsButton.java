package net.lax1dude.eaglercraft.sp.gui;

import net.lax1dude.eaglercraft.Mouse;
import net.lax1dude.eaglercraft.internal.EnumCursorType;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.lax1dude.eaglercraft.sp.lan.LANServerController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GuiNetworkSettingsButton extends AbstractGui {

	private final Screen screen;
	private final String text;
	private final Minecraft mc;

	public GuiNetworkSettingsButton(Screen screen) {
		this.screen = screen;
		this.text = I18n.format("directConnect.lanWorldRelay");
		this.mc = Minecraft.getInstance();
	}

	public void drawScreen(int xx, int yy) {
		GlStateManager.pushMatrix();
		GlStateManager.scale(0.75f, 0.75f, 0.75f);
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

		int w = this.mc.fontRenderer.getStringWidth(text);
		boolean hover = xx > 1 && yy > 1 && xx < (w * 3 / 4) + 7 && yy < 12;
		if(hover) {
			Mouse.showCursor(EnumCursorType.HAND);
		}

		drawString(this.mc.fontRenderer, TextFormatting.UNDERLINE + text, 5, 5, hover ? 0xFFEEEE22 : 0xFFCCCCCC);

		GlStateManager.popMatrix();
	}

	public void mouseClicked(double xx, double yy, int btn) {
		int w = this.mc.fontRenderer.getStringWidth(text);
		if(xx > 2 && yy > 2 && xx < (w * 3 / 4) + 5 && yy < 12) {
			if(LANServerController.supported()) {
				this.mc.displayGuiScreen(ScreenLANInfo.showLANInfoScreen(new ScreenRelay(screen)));
			} else {
				this.mc.displayGuiScreen(new ScreenLANNotSupported(screen));
			}
		}
	}

}