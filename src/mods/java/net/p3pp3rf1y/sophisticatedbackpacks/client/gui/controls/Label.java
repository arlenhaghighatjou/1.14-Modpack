package net.p3pp3rf1y.sophisticatedbackpacks.client.gui.controls;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.Position;

public class Label extends BackpackWidget {
	private static final int DEFAULT_GUI_TEXT_COLOR = 4210752;
	private final ITextComponent labelText;
	private final int color;

	public Label(Position position, ITextComponent labelText) {
		this(position, labelText, DEFAULT_GUI_TEXT_COLOR);
	}

	public Label(Position position, ITextComponent labelText, int color) {
		super(position);
		this.labelText = labelText;
		this.color = color;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY) {
		//noop
	}

	@Override
	protected void renderWidget(int mouseX, int mouseY, float partialTicks) {
		minecraft.fontRenderer.drawString(labelText, x, y, color);
	}

	@Override
	public int getWidth() {
		return minecraft.fontRenderer.getStringWidth(labelText);
	}

	@Override
	public int getHeight() {
		return 8;
	}
}
