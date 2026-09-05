package net.p3pp3rf1y.sophisticatedbackpacks.client.gui.controls;

import net.minecraft.client.Minecraft;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.Dimension;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.GuiHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.TextureBlitData;

import java.util.function.IntConsumer;

public class ImageButton extends ButtonBase {
	private final TextureBlitData texture;

	public ImageButton(Position position, Dimension dimension, TextureBlitData texture, IntConsumer onClick) {
		super(position, dimension, onClick);
		this.texture = texture;
	}

	@Override
	protected void renderBg(Minecraft minecraft, int mouseX, int mouseY) {
		//noop
	}

	@Override
	protected void renderWidget(int mouseX, int mouseY, float partialTicks) {
		GuiHelper.blit(minecraft, matrixStack, x, y, texture);
	}
}
