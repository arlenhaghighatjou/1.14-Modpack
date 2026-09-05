package net.p3pp3rf1y.sophisticatedbackpacks.client.gui.controls;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.Dimension;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.GuiHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.Position;

import java.util.function.IntConsumer;

public class ItemButton extends ButtonBase {
	private final ItemStack stack;

	public ItemButton(Position position, IntConsumer onClick, ItemStack stack) {
		super(position, Dimension.SQUARE_16, onClick);
		this.stack = stack;
	}

	@Override
	protected void renderBg(Minecraft minecraft, int mouseX, int mouseY) {
		//noop
	}

	@Override
	protected void renderWidget(int mouseX, int mouseY, float partialTicks) {
		GuiHelper.renderItemInGUI(minecraft, stack, x, y);
	}
}
