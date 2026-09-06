package net.p3pp3rf1y.sophisticatedbackpacks.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.GuiHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.SettingsContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.network.BackpackOpenMessage;
import net.p3pp3rf1y.sophisticatedbackpacks.network.PacketHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.settings.BackpackSettingsTabControl;

import javax.annotation.Nullable;
import java.util.Optional;

public class SettingsScreen extends ContainerScreen<SettingsContainer> {
	private BackpackSettingsTabControl settingsTabControl;

	public SettingsScreen(SettingsContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		ySize = 114 + getContainer().getNumberOfRows() * 18;
		xSize = getContainer().getBackpackBackgroundProperties().getSlotsOnLine() * 18 + 14;
		playerInventoryTitleY = ySize - 94;
		playerInventoryTitleX = 8 + getContainer().getBackpackBackgroundProperties().getPlayerInventoryXOffset();
	}

	@Override
	protected void init() {
		super.init();

		settingsTabControl = new BackpackSettingsTabControl(this, new Position(guiLeft + xSize, guiTop + 4));
		children.add(settingsTabControl);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int x, int y) {
		BackpackBackgroundProperties backpackBackgroundProperties = getContainer().getBackpackBackgroundProperties();
		BackpackGuiHelper.renderBackpackBackground(new Position((width - xSize) / 2, (height - ySize) / 2), getContainer().getBackpackInventorySlots().size(), getContainer().getSlotsOnLine(), backpackBackgroundProperties.getTextureName(), xSize, mc, container.getNumberOfRows());
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		container.detectSettingsChangeAndReload();
		renderBackground();
		settingsTabControl.render(mouseX, mouseY, partialTicks);
		GlStateManager.translated(0, 0, 200);
		super.render(mouseX, mouseY, partialTicks);
		settingsTabControl.afterScreenRender(mouseX, mouseY, partialTicks);
		renderTooltip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.renderLabels(mouseX, mouseY);
		for (int slotId = 0; slotId < container.ghostSlots.size(); ++slotId) {
			Slot slot = container.ghostSlots.get(slotId);
			drawSlot(slot);

			settingsTabControl.renderSlotOverlays(slot, this::renderSlotOverlay);

			if (isSlotSelected(slot, mouseX, mouseY) && slot.isEnabled()) {
				hoveredSlot = slot;
				renderSlotOverlay(slot, getSlotColor(slotId));
			}
		}
	}

	@Override
	protected void drawSlot(Slot slot) {
		Optional<ItemStack> memorizedStack = getContainer().getMemorizedStackInSlot(slot.getSlotIndex());
		ItemStack itemstack = slot.getStack();
		if (memorizedStack.isPresent()) {
			itemstack = memorizedStack.get();
		}

		blitOffset = 100;
		itemRenderer.zLevel = 100.0F;

		GlStateManager.enableDepthTest();
		GlStateManager.pushMatrix();
		settingsTabControl.renderGuiItem(itemRenderer, itemstack, slot);
		GlStateManager.popMatrix();
		itemRenderer.zLevel = 0.0F;
		blitOffset = 0;

		if (memorizedStack.isPresent()) {
			drawMemorizedStackOverlay(slot.xPos, slot.yPos);
		}
	}

	private void drawMemorizedStackOverlay(int x, int y) {
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableDepthTest();
		mc.getTextureManager().bindTexture(GuiHelper.GUI_CONTROLS);
		blit(x, y, 77, 0, 16, 16);
		GlStateManager.enableDepthTest();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	@SuppressWarnings("java:S2589") // slot can actually be null despite being marked non null
	@Override
	protected void handleMouseClick(Slot slot, int slotId, int mouseButton, ClickType type) {
		//noinspection ConstantConditions
		if (slot != null) {
			settingsTabControl.handleSlotClick(slot, mouseButton);
		}
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		Slot slot = getSelectedSlot(mouseX, mouseY);
		if (slot != null) {
			settingsTabControl.handleSlotClick(slot, button);
		}
		return true;
	}

	@Nullable
	@Override
	protected Slot getSelectedSlot(double mouseX, double mouseY) {
		for (int i = 0; i < container.ghostSlots.size(); ++i) {
			Slot slot = container.ghostSlots.get(i);
			if (isSlotSelected(slot, mouseX, mouseY) && slot.isEnabled()) {
				return slot;
			}
		}

		return null;
	}

	@Override
	protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton) {
		return super.hasClickedOutside(mouseX, mouseY, guiLeftIn, guiTopIn, mouseButton) && hasClickedOutsideOfSettings(mouseX, mouseY);
	}

	private boolean hasClickedOutsideOfSettings(double mouseX, double mouseY) {
		return settingsTabControl.getTabRectangles().stream().noneMatch(r -> r.contains((int) mouseX, (int) mouseY));
	}

	private void renderSlotOverlay(Slot slot, int slotColor) {
		renderSlotOverlay(slot.xPos, slot.yPos, 16, slotColor);
	}

	private void renderSlotOverlay(int xPos, int yPos, int height, int slotColor) {
		GlStateManager.disableDepthTest();
		GlStateManager.colorMask(true, true, true, false);
		fillGradient(xPos, yPos, xPos + 16, yPos + height, slotColor, slotColor);
		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.enableDepthTest();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			PacketHandler.sendToServer(new BackpackOpenMessage());
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	protected void renderTooltip(int x, int y) {
		super.renderTooltip(x, y);
		GuiHelper.renderTooltip(mc, x, y);
	}

	public static SettingsScreen constructScreen(SettingsContainer settingsContainer, PlayerInventory playerInventory, ITextComponent title) {
		return new SettingsScreen(settingsContainer, playerInventory, title);
	}

	public BackpackSettingsTabControl getSettingsTabControl() {
		return settingsTabControl;
	}
}
