package net.p3pp3rf1y.sophisticatedbackpacks.upgrades.battery;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.Slot;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackScreen;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.UpgradeSettingsTab;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.Dimension;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.GuiHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.Position;

import java.util.List;

import static net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.TranslationHelper.translUpgrade;
import static net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.TranslationHelper.translUpgradeTooltip;

public class BatteryUpgradeTab extends UpgradeSettingsTab<BatteryUpgradeContainer> {
	public BatteryUpgradeTab(BatteryUpgradeContainer upgradeContainer, Position position, BackpackScreen screen) {
		super(upgradeContainer, position, screen, translUpgrade("battery"), translUpgradeTooltip("battery"));
		openTabDimension = new Dimension(48, 48);
	}

	@Override
	protected void renderBg(Minecraft minecraft, int mouseX, int mouseY) {
		super.renderBg(minecraft, mouseX, mouseY);
		if (getContainer().isOpen()) {
			GuiHelper.renderSlotsBackground(minecraft, x + 3, y + 24, 1, 1);
			GuiHelper.renderSlotsBackground(minecraft, x + 24, y + 24, 1, 1);
		}
	}

	@Override
	protected void moveSlotsToTab() {
		List<Slot> slots = getContainer().getSlots();
		positionSlot(inventorySlots.get(BatteryUpgradeWrapper.INPUT_SLOT), screen.getGuiLeft(), screen.getGuiTop(), 4);
		positionSlot(inventorySlots.get(BatteryUpgradeWrapper.OUTPUT_SLOT), screen.getGuiLeft(), screen.getGuiTop(), 25);
	}

	private void positionSlot(Slot slot, int screenGuiLeft, int screenGuiTop, int xOffset) {
		slot.xPos = x - screenGuiLeft + xOffset;
		slot.yPos = y - screenGuiTop + 25;
	}
}
