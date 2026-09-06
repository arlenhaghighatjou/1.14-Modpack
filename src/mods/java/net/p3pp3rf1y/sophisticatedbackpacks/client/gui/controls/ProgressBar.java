package net.p3pp3rf1y.sophisticatedbackpacks.client.gui.controls;

import net.minecraft.client.Minecraft;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.TextureBlitData;

import java.util.function.Supplier;

public class ProgressBar extends BackpackWidget {
	private final TextureBlitData progressTexture;
	private final Supplier<Float> getProgress;
	private final ProgressDirection dir;

	public ProgressBar(Position position, TextureBlitData progressTexture, Supplier<Float> getProgress, ProgressDirection dir) {
		super(position);
		this.progressTexture = progressTexture;
		this.getProgress = getProgress;
		this.dir = dir;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY) {
		//noop
	}

	@Override
	protected void renderWidget(int mouseX, int mouseY, float partialTicks) {
		int height = progressTexture.getHeight();
		int width = progressTexture.getWidth();
		float progress = getProgress.get();
		if (progress <= 0) {
			return;
		}
		int yOffset = 0;
		if (dir == ProgressDirection.BOTTOM_UP) {
			yOffset = (int) (height * progress);
			height -= yOffset;
		} else if (dir == ProgressDirection.LEFT_RIGHT) {
			width = (int) (width * progress);
		}
		minecraft.getTextureManager().bindTexture(progressTexture.getTextureName());
		blit(x, y + yOffset, progressTexture.getU(), (float) progressTexture.getV() + yOffset, width, height, progressTexture.getTextureWidth(), progressTexture.getTextureHeight());
	}

	@Override
	public int getWidth() {
		return progressTexture.getWidth();
	}

	@Override
	public int getHeight() {
		return progressTexture.getHeight();
	}

	public enum ProgressDirection {
		LEFT_RIGHT,
		BOTTOM_UP
	}
}
