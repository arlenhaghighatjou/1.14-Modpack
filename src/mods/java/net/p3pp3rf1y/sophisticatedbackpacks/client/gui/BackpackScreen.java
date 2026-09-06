package net.p3pp3rf1y.sophisticatedbackpacks.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.client.ClientProxy;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.controls.Button;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.controls.ButtonDefinitions;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.controls.ToggleButton;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.GuiHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackInventorySlot;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.SortBy;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.UpgradeContainerBase;
import net.p3pp3rf1y.sophisticatedbackpacks.network.BackpackOpenMessage;
import net.p3pp3rf1y.sophisticatedbackpacks.network.PacketHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.network.TransferFullSlotMessage;
import net.p3pp3rf1y.sophisticatedbackpacks.network.WindowClickMessage;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.crafting.ICraftingUIPart;
import net.p3pp3rf1y.sophisticatedbackpacks.util.CountAbbreviator;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.GuiHelper.GUI_CONTROLS;

public class BackpackScreen extends ContainerScreen<BackpackContainer> {
	private static final int DISABLED_SLOT_COLOR = -1072689136;
	private static final int UPGRADE_TOP_HEIGHT = 7;
	private static final int UPGRADE_SLOT_HEIGHT = 18;
	private static final int UPGRADE_SPACE_BETWEEN_SLOTS = 4;
	private static final int UPGRADE_BOTTOM_HEIGHT = 7;
	private static final int TOTAL_UPGRADE_GUI_HEIGHT = 252;
	public static final int UPGRADE_INVENTORY_OFFSET = 26;
	public static final int DISABLED_SLOT_X_POS = -1000;
	static final int SLOTS_Y_OFFSET = 17;
	static final int SLOTS_X_OFFSET = 7;
	private static IButtonReplacer buttonReplacer = new IButtonReplacer() {};

	public static void setButtonReplacer(IButtonReplacer replacer) {
		buttonReplacer = replacer;
	}

	public static BackpackScreen constructScreen(BackpackContainer screenContainer, PlayerInventory inv, ITextComponent title) {
		return new BackpackScreen(screenContainer, inv, title);
	}

	private UpgradeSettingsTabControl settingsTabControl;
	private final int numberOfUpgradeSlots;
	@Nullable
	private Button sortButton = null;
	@Nullable
	private ToggleButton<SortBy> sortByButton = null;
	private final Set<ToggleButton<Boolean>> upgradeSwitches = new HashSet<>();

	private final Map<Integer, UpgradeInventoryPartBase<?>> inventoryParts = new LinkedHashMap<>();

	private static ICraftingUIPart craftingUIPart = ICraftingUIPart.NOOP;

	public static void setCraftingUIPart(ICraftingUIPart part) {
		craftingUIPart = part;
	}

	public BackpackScreen(BackpackContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		ySize = 114 + getContainer().getNumberOfRows() * 18;
		xSize = getContainer().getBackpackBackgroundProperties().getSlotsOnLine() * 18 + 14;
		playerInventoryTitleY = ySize - 94;
		playerInventoryTitleX = 8 + getContainer().getBackpackBackgroundProperties().getPlayerInventoryXOffset();
		numberOfUpgradeSlots = getContainer().getNumberOfUpgradeSlots();
		passEvents = true;
	}

	public ICraftingUIPart getCraftingUIAddition() {
		return craftingUIPart;
	}

	@Override
	protected void init() {
		super.init();
		craftingUIPart.setBackpackScreen(this);
		initUpgradeSettingsControl();
		initUpgradeInventoryParts();
		addUpgradeSwitches();
		getContainer().setUpgradeChangeListener(c -> {
			children.remove(settingsTabControl);
			craftingUIPart.onCraftingSlotsHidden();
			initUpgradeSettingsControl();
			initUpgradeInventoryParts();
			addUpgradeSwitches();
		});
		addSortButtons();
	}

	@Override
	protected <T extends Widget> T addButton(T widget) {
		if (!(widget instanceof net.minecraft.client.gui.widget.button.Button)) {
			return widget;
		}
		net.minecraft.client.gui.widget.button.Button button = (net.minecraft.client.gui.widget.button.Button) widget;

		if (buttonReplacer.shouldReplace(this, button)) {
			return super.addButton((T) buttonReplacer.replace(this, button));
		}

		return super.addButton(widget);
	}

	private void initUpgradeInventoryParts() {
		inventoryParts.clear();
		if (getContainer().getColumnsTaken() == 0) {
			return;
		}

		AtomicReference<Position> pos = new AtomicReference<>(new Position(SLOTS_X_OFFSET + container.getSlotsOnLine() * 18, SLOTS_Y_OFFSET));
		int height = container.getNumberOfRows() * 18;
		for (Map.Entry<Integer, UpgradeContainerBase<?, ?>> entry : getContainer().getUpgradeContainers().entrySet()) {
			UpgradeContainerBase<?, ?> container = entry.getValue();
			UpgradeGuiManager.getInventoryPart(entry.getKey(), container, pos.get(), height, this).ifPresent(part -> {
				inventoryParts.put(entry.getKey(), part);
				pos.set(new Position(pos.get().getX() + 36, pos.get().getY()));
			});
		}
	}

	private void addUpgradeSwitches() {
		upgradeSwitches.clear();
		int switchTop = guiTop + getUpgradeTop() + 10;
		for (int slot = 0; slot < numberOfUpgradeSlots; slot++) {
			if (container.canDisableUpgrade(slot)) {
				int finalSlot = slot;
				ToggleButton<Boolean> upgradeSwitch = new ToggleButton<>(new Position(guiLeft - 22, switchTop), ButtonDefinitions.UPGRADE_SWITCH,
						button -> getContainer().setUpgradeEnabled(finalSlot, !getContainer().getUpgradeEnabled(finalSlot)), () -> getContainer().getUpgradeEnabled(finalSlot));
				addWidget(upgradeSwitch);
				upgradeSwitches.add(upgradeSwitch);
			}
			switchTop += 22;
		}
	}

	private void addSortButtons() {
		SortButtonsPosition sortButtonsPosition = Config.CLIENT.sortButtonsPosition;
		if (sortButtonsPosition == SortButtonsPosition.HIDDEN) {
			return;
		}

		Position pos = getSortButtonsPosition(sortButtonsPosition);

		sortButton = new Button(new Position(pos.getX(), pos.getY()), ButtonDefinitions.SORT, button -> {
			if (button == 0) {
				getContainer().sort();
				Minecraft.getInstance().player.sendStatusMessage(new StringTextComponent("Sorted"), true);
			}
		});
		addWidget(sortButton);
		sortByButton = new ToggleButton<>(new Position(pos.getX() + 14, pos.getY()), ButtonDefinitions.SORT_BY, button -> {
			if (button == 0) {
				getContainer().setSortBy(getContainer().getSortBy().next());
			}
		}, () -> getContainer().getSortBy());
		addWidget(sortByButton);

	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256 || ClientProxy.BACKPACK_OPEN_KEYBIND.isActiveAndMatches(InputMappings.getKey(keyCode, scanCode)) && mouseNotOverBackpack()) {
			if (getContainer().isFirstLevelBackpack() && getContainer().getBackpackContext().wasOpenFromInventory()) {
				getMinecraft().player.closeContainer();
				getMinecraft().setScreen(new InventoryScreen(getMinecraft().player));
				return true;
			} else if (!getContainer().isFirstLevelBackpack()) {
				PacketHandler.sendToServer(new BackpackOpenMessage());
				return true;
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	private boolean mouseNotOverBackpack() {
		Slot selectedSlot = getSlotUnderMouse();
		return selectedSlot == null || !(selectedSlot.getStack().getItem() instanceof BackpackItem);
	}

	private Position getSortButtonsPosition(SortButtonsPosition sortButtonsPosition) {
		switch (sortButtonsPosition) {
			case ABOVE_UPGRADES:
				return new Position(guiLeft - UPGRADE_INVENTORY_OFFSET - 2, guiTop + getUpgradeTop() - 14);
			case BELOW_UPGRADES:
				return new Position(guiLeft - UPGRADE_INVENTORY_OFFSET - 2, guiTop + getUpgradeTop() + getUpgradeHeightWithoutBottom() + UPGRADE_BOTTOM_HEIGHT + 2);
			case BELOW_UPGRADE_TABS:
				return settingsTabControl == null ? new Position(0, 0) : new Position(settingsTabControl.getX() + 2, settingsTabControl.getY() + Math.max(0, settingsTabControl.getHeight() + 2));
			case TITLE_LINE_RIGHT:
			default:
				return new Position(guiLeft + xSize - 34, guiTop + 4);
		}
	}

	public Optional<Rectangle2d> getSortButtonsRectangle() {
		return sortButton == null || sortByButton == null ? Optional.empty() : Optional.of(new Rectangle2d(sortButton.getX(), sortButton.getY(),
				sortByButton.getX() + sortByButton.getWidth() - sortButton.getX(), sortByButton.getY() + sortByButton.getHeight() - sortButton.getY()));
	}

	private void initUpgradeSettingsControl() {
		settingsTabControl = new UpgradeSettingsTabControl(new Position(guiLeft + xSize, guiTop + 4), this);
		addWidget(settingsTabControl);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		container.detectSettingsChangeAndReload();
		renderBackground();
		settingsTabControl.render(mouseX, mouseY, partialTicks);
		GlStateManager.translated(0, 0, 200);
		super.render(mouseX, mouseY, partialTicks);
		settingsTabControl.afterScreenRender(mouseX, mouseY, partialTicks);
		if (sortButton != null && sortByButton != null) {
			sortButton.render(mouseX, mouseY, partialTicks);
			sortByButton.render(mouseX, mouseY, partialTicks);
		}
		upgradeSwitches.forEach(us -> us.render(mouseX, mouseY, partialTicks));
		renderErrorOverlay();
		renderTooltip(mouseX, mouseY);
	}

	@Override
	protected void renderLabels(int mouseX, int mouseY) {
		super.renderLabels(mouseX, mouseY);
		renderUpgradeInventoryParts(mouseX, mouseY);
		renderUpgradeSlots(mouseX, mouseY);
		renderRealInventorySlots(mouseX, mouseY);
	}

	private void renderUpgradeInventoryParts(int mouseX, int mouseY) {
		inventoryParts.values().forEach(ip -> ip.render(mouseX, mouseY));
	}

	private void renderRealInventorySlots(int mouseX, int mouseY) {
		for (int slotId = 0; slotId < menu.realInventorySlots.size(); ++slotId) {
			Slot slot = menu.realInventorySlots.get(slotId);
			renderSlot(slot);

			if (isHovering(slot, mouseX, mouseY) && slot.isEnabled()) {
				hoveredSlot = slot;
				renderSlotOverlay(slot, getSlotColor(slotId));
			}
		}
	}

	private void renderUpgradeSlots(int mouseX, int mouseY) {
		for (int slotId = 0; slotId < menu.upgradeSlots.size(); ++slotId) {
			Slot slot = menu.upgradeSlots.get(slotId);
			if (slot.xPos != DISABLED_SLOT_X_POS) {
				renderSlot(slot);
				if (!slot.isEnabled()) {
					renderSlotOverlay(slot, DISABLED_SLOT_COLOR);
				}
			}

			if (isHovering(slot, mouseX, mouseY) && slot.isEnabled()) {
				hoveredSlot = slot;
				renderSlotOverlay(slot, getSlotColor(slotId));
			}
		}
	}

	@Override
	protected void renderSlot(Slot slot) {
		int i = slot.xPos;
		int j = slot.yPos;
		ItemStack itemstack = slot.getStack();
		boolean flag = false;
		boolean rightClickDragging = slot == clickedSlot && !draggingItem.isEmpty() && !isSplittingStack;
		//noinspection ConstantConditions - player is not null at this point for sure
		ItemStack itemstack1 = minecraft.player.inventory.getItemStack();
		String stackCountText = null;
		if (slot == clickedSlot && !draggingItem.isEmpty() && isSplittingStack && !itemstack.isEmpty()) {
			itemstack = itemstack.copy();
			itemstack.setCount(itemstack.getCount() / 2);
		} else if (isQuickCrafting && dragSlots.contains(slot) && !itemstack1.isEmpty()) {
			if (dragSlots.size() == 1) {
				return;
			}

			if (BackpackContainer.canMergeItemToSlot(slot, itemstack1) && container.canDragTo(slot)) {
				itemstack = itemstack1.copy();
				flag = true;
				Container.getQuickCraftSlotCount(dragSlots, quickCraftingType, itemstack, slot.getStack().isEmpty() ? 0 : slot.getStack().getCount());
				int slotLimit = slot.getSlotStackLimit(itemstack);
				if (itemstack.getCount() > slotLimit) {
					stackCountText = TextFormatting.YELLOW + CountAbbreviator.abbreviate(slotLimit);
					itemstack.setCount(slotLimit);
				}
			} else {
				dragSlots.remove(slot);
				recalculateQuickCraftRemaining();
			}
		}

		setBlitOffset(100);
		itemRenderer.zLevel = 100.0F;
		if (itemstack.isEmpty() && slot.isEnabled()) {
			renderSlotBackground(slot, i, j);
		} else if (!rightClickDragging) {
			renderStack(i, j, itemstack, flag, stackCountText);
		}

		itemRenderer.zLevel = 0.0F;
		setBlitOffset(0);
	}

	private void renderStack(int i, int j, ItemStack itemstack, boolean flag, @Nullable String stackCountText) {
		if (flag) {
			fill(i, j, i + 16, j + 16, -2130706433);
		}

		GlStateManager.enableDepthTest();
		itemRenderer.renderItemAndEffectIntoGUI(mc.player, itemstack, i, j);
		if (shouldUseSpecialCountRender(itemstack)) {
			itemRenderer.renderItemOverlayIntoGUI(font, itemstack, i, j, "");
			if (stackCountText == null) {
				stackCountText = CountAbbreviator.abbreviate(itemstack.getCount());
			}
			renderStackCount(stackCountText, i, j);
		} else {
			itemRenderer.renderItemOverlayIntoGUI(font, itemstack, i, j, stackCountText);
		}

	}

	private void renderSlotBackground(Slot slot, int i, int j) {
		Optional<ItemStack> memorizedStack = getContainer().getMemorizedStackInSlot(slot.slotNumber);
		if (memorizedStack.isPresent()) {
			itemRenderer.renderItemAndEffectIntoGUI(mc.player, memorizedStack.get(), i, j);
			drawMemorizedStackOverlay(i, j);
		} else {
			Pair<ResourceLocation, ResourceLocation> pair = slot.getBackgroundLocation();
			if (pair != null) {
				TextureAtlasSprite textureatlassprite = mc.getTextureAtlas(pair.getFirst()).apply(pair.getSecond());
				mc.getTextureManager().bind(textureatlassprite.atlas().location());
				blit(i, j, getBlitOffset(), 16, 16, textureatlassprite);
			}
		}
	}

	private void drawMemorizedStackOverlay(int x, int y) {
		GlStateManager.pushMatrix();
		GlStateManager._enableBlend();
		GlStateManager._disableDepthTest();
		mc.getTextureManager().bindTexture(GuiHelper.GUI_CONTROLS);
		blit(x, y, 77, 0, 16, 16);
		GlStateManager._enableDepthTest();
		GlStateManager._disableBlend();
		GlStateManager.popMatrix();
	}

	private boolean shouldUseSpecialCountRender(ItemStack itemstack) {
		return itemstack.getCount() > 99;
	}

	private void renderSlotOverlay(Slot slot, int slotColor) {
		renderSlotOverlay(slot, slotColor, 0, 16);
	}

	private void renderSlotOverlay(Slot slot, int slotColor, int yOffset, int height) {
		renderOverlay(slotColor, slot.xPos, slot.yPos + yOffset, 16, height);
	}

	public void renderOverlay(int slotColor, int xPos, int yPos, int width, int height) {
		GlStateManager.disableDepthTest();
		GlStateManager.colorMask(true, true, true, false);
		fillGradient(xPos, yPos, xPos + width, yPos + height, slotColor, slotColor);
		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.enableDepthTest();
	}

	protected void renderBg(float partialTicks, int x, int y) {
		drawInventoryBackground();
		drawUpgradeBackground();
	}

	@Override
	protected void renderTooltip(int x, int y) {
		if (minecraft.player.inventory.getItemStack().isEmpty() && hoveredSlot != null) {
			if (hoveredSlot.getHasStack()) {
				renderTooltip(hoveredSlot.getStack(), x, y);
			} else if (hoveredSlot instanceof INameableEmptySlot) {
				INameableEmptySlot emptySlot = (INameableEmptySlot) hoveredSlot;
				if (emptySlot.hasEmptyTooltip()) {
					renderWrappedToolTip(Collections.singletonList(emptySlot.getEmptyTooltip()), x, y, font);
				}
			}
		}
		GuiHelper.renderTooltip(mc, x, y);
	}

	@Override
	public List<ITextComponent> getTooltipFromItem(ItemStack itemStack) {
		List<ITextComponent> ret = super.getTooltipFromItem(itemStack);
		if (itemStack.getCount() > 999) {
			ret.add(new TranslationTextComponent("gui.sophisticatedbackpacks.tooltip.stack_count",
					new StringTextComponent(NumberFormat.getNumberInstance().format(itemStack.getCount())).applyTextStyle(TextFormatting.DARK_AQUA))
					.applyTextStyle(TextFormatting.GRAY)
			);
		}
		return ret;
	}

	private void drawInventoryBackground() {
		BackpackBackgroundProperties backpackBackgroundProperties = getContainer().getBackpackBackgroundProperties();
		BackpackGuiHelper.renderBackpackBackground(new Position((width - xSize) / 2, (height - ySize) / 2), getContainer().getNumberOfSlots(), getContainer().getSlotsOnLine(), backpackBackgroundProperties.getTextureName(), xSize, mc, container.getNumberOfRows());

		GlStateManager.pushMatrix();
		GlStateManager.translatef(getGuiLeft(), (float) getGuiTop(), 0.0F);
		for (int slotNumber = 0; slotNumber < container.getNumberOfSlots(); slotNumber++) {
			List<Integer> colors = container.getSlotOverlayColors(slotNumber);
			if (!colors.isEmpty()) {
				int stripeHeight = 16 / colors.size();
				int i = 0;
				for (int slotColor : colors) {
					int yOffset = i * stripeHeight;
					renderSlotOverlay(container.getSlot(slotNumber), slotColor | (80 << 24), yOffset, i == colors.size() - 1 ? 16 - yOffset : stripeHeight);
					i++;
				}
			}
		}
		GlStateManager.popMatrix();
	}

	private void drawUpgradeBackground() {
		if (numberOfUpgradeSlots == 0) {
			return;
		}

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI_CONTROLS);

		int firstHalfHeight = getUpgradeHeightWithoutBottom();

		blit(guiLeft - UPGRADE_INVENTORY_OFFSET, guiTop + getUpgradeTop(), 0, 0, 29, firstHalfHeight, 256, 256);
		blit(guiLeft - UPGRADE_INVENTORY_OFFSET, guiTop + getUpgradeTop() + firstHalfHeight, 0, (float) TOTAL_UPGRADE_GUI_HEIGHT - UPGRADE_BOTTOM_HEIGHT, 29, UPGRADE_BOTTOM_HEIGHT, 256, 256);
	}

	public int getUpgradeTop() {
		return ySize - 94 - getUpgradeHeight();
	}

	public int getUpgradeHeight() {
		return getUpgradeHeightWithoutBottom() + UPGRADE_TOP_HEIGHT;
	}

	private int getUpgradeHeightWithoutBottom() {
		return UPGRADE_BOTTOM_HEIGHT + numberOfUpgradeSlots * UPGRADE_SLOT_HEIGHT + (numberOfUpgradeSlots - 1) * UPGRADE_SPACE_BETWEEN_SLOTS;
	}

	public UpgradeSettingsTabControl getUpgradeSettingsControl() {
		if (settingsTabControl == null) {
			settingsTabControl = new UpgradeSettingsTabControl(new Position(guiLeft + xSize, guiTop + 4), this);
		}
		return settingsTabControl;
	}

	@Nullable
	@Override
	public Slot findSlot(double mouseX, double mouseY) {
		for (int i = 0; i < menu.upgradeSlots.size(); ++i) {
			Slot slot = menu.upgradeSlots.get(i);
			if (isHovering(slot, mouseX, mouseY) && slot.isEnabled()) {
				return slot;
			}
		}

		for (int i = 0; i < menu.realInventorySlots.size(); ++i) {
			Slot slot = menu.realInventorySlots.get(i);
			if (isHovering(slot, mouseX, mouseY) && slot.isEnabled()) {
				return slot;
			}
		}

		return super.findSlot(mouseX, mouseY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		for (UpgradeInventoryPartBase<?> inventoryPart : inventoryParts.values()) {
			if (inventoryPart.handleMouseReleased(mouseX, mouseY, button)) {
				return true;
			}
		}

		handleQuickMoveAll(mouseX, mouseY, button);

		return super.mouseReleased(mouseX, mouseY, button);
	}

	private void handleQuickMoveAll(double mouseX, double mouseY, int button) {
		Slot slot = findSlot(mouseX, mouseY);
		if (doubleclick && !minecraft.player.inventory.getItemStack().isEmpty() && slot != null && button == 0 && container.canTakeItemForPickAll(ItemStack.EMPTY, slot) && hasShiftDown() && !lastQuickMoved.isEmpty()) {
			for (Slot slot2 : container.realInventorySlots) {
				tryQuickMoveSlot(button, slot, slot2);
			}
		}
	}

	private void tryQuickMoveSlot(int button, Slot slot, Slot slot2) {
		if (slot2.canTakeStack(mc.player) && slot2.getHasStack() && slot2.isSameInventory(slot)) {
			ItemStack slotItem = slot2.getStack();
			if (slotItem.sameItem(lastQuickMoved) && ItemStack.areItemStackTagsEqual(lastQuickMoved, slotItem)) {
				if (slotItem.getCount() > slotItem.getMaxStackSize()) {
					PacketHandler.sendToServer(new TransferFullSlotMessage(slot2.slotNumber));
				} else {
					slotClicked(slot2, slot2.slotNumber, button, ClickType.QUICK_MOVE);
				}
			}
		}
	}

	@Override
	protected void slotClicked(Slot slot, int slotNumber, int mouseButton, ClickType type) {
		if (type == ClickType.PICKUP_ALL && !container.getSlotUpgradeContainer(slot).map(c -> c.allowsPickupAll(slot)).orElse(true)) {
			type = ClickType.PICKUP;
		}
		if (slot != null) {
			slotNumber = slot.slotNumber;
		}
		ClientPlayerEntity player = mc.player;

		short nextTransId = player.openContainer.backup(player.inventory);
		ItemStack itemstack = player.openContainer.slotClick(slotNumber, mouseButton, type, player);
		PacketHandler.sendToServer(new WindowClickMessage(container.windowId, slotNumber, mouseButton, type, itemstack, nextTransId));
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		Slot slot = findSlot(mouseX, mouseY);
		if (hasShiftDown() && hasControlDown() && slot instanceof BackpackInventorySlot && button == 0) {
			PacketHandler.sendToServer(new TransferFullSlotMessage(slot.slotNumber));
			return true;
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		for (IGuiEventListener child : children) {
			if (child.isMouseOver(mouseX, mouseY) && child.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
				return true;
			}
		}
		Slot slot = findSlot(mouseX, mouseY);
		ItemStack itemstack = minecraft.player.inventory.getItemStack();
		if (isQuickCrafting && slot != null && !itemstack.isEmpty()
				&& (itemstack.getCount() > dragSlots.size() || quickCraftingType == 2)
				&& BackpackContainer.canMergeItemToSlot(slot, itemstack) && slot.isItemValid(itemstack)
				&& container.canDragTo(slot)) {
			dragSlots.add(slot);
			recalculateQuickCraftRemaining();
		}

		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton) {
		return super.hasClickedOutside(mouseX, mouseY, guiLeftIn, guiTopIn, mouseButton) && hasClickedOutsideOfUpgradeSlots(mouseX, mouseY)
				&& hasClickedOutsideOfUpgradeSettings(mouseX, mouseY);
	}

	private boolean hasClickedOutsideOfUpgradeSettings(double mouseX, double mouseY) {
		return settingsTabControl.getTabRectangles().stream().noneMatch(r -> r.contains((int) mouseX, (int) mouseY));
	}

	private boolean hasClickedOutsideOfUpgradeSlots(double mouseX, double mouseY) {
		return !getUpgradeSlotsRectangle().map(r -> r.contains((int) mouseX, (int) mouseY)).orElse(false);
	}

	public Optional<Rectangle2d> getUpgradeSlotsRectangle() {
		return numberOfUpgradeSlots == 0 ? Optional.empty() : Optional.of(new Rectangle2d(guiLeft - BackpackScreen.UPGRADE_INVENTORY_OFFSET, guiTop + getUpgradeTop(), 32, getUpgradeHeight()));
	}

	private void renderStackCount(String count, int x, int y) {
		GlStateManager.pushMatrix();
		GlStateManager.translated(0.0D, 0.0D, itemRenderer.zLevel + 200.0F);
		float scale = Math.min(1f, (float) 16 / font.getStringWidth(count));
		if (scale < 1f) {
			GlStateManager.scalef(scale, scale, 1.0F);
		}
		font.drawStringWithShadow(count, (x + 19 - 2 - (font.getStringWidth(count) * scale)) / scale,
				(y + 6 + 3 + (1 / (scale * scale) - 1)) / scale, 16777215);
		GlStateManager.popMatrix();
	}

	@Override
	protected void recalculateQuickCraftRemaining() {
		//noinspection ConstantConditions - can't happen here as player is definitely known
		ItemStack cursorStack = minecraft.player.inventory.getItemStack();
		if (!cursorStack.isEmpty() && isQuickCrafting) {
			if (quickCraftingType == 2) {
				quickCraftingRemainder = cursorStack.getMaxStackSize();
			} else {
				quickCraftingRemainder = cursorStack.getCount();

				for (Slot slot : dragSlots) {
					ItemStack itemstack1 = cursorStack.copy();
					ItemStack slotStack = slot.getStack();
					int slotStackCount = slotStack.isEmpty() ? 0 : slotStack.getCount();
					Container.getQuickCraftSlotCount(dragSlots, quickCraftingType, itemstack1, slotStackCount);
					int j = slot.getSlotStackLimit(itemstack1);
					if (itemstack1.getCount() > j) {
						itemstack1.setCount(j);
					}

					quickCraftingRemainder -= itemstack1.getCount() - slotStackCount;
				}
			}
		}
	}

	public static final int ERROR_BACKGROUND_COLOR = 0xF0100010;
	public static final int ERROR_BORDER_COLOR = DyeColor.RED.getColorValue() | 0xFF000000;

	private void renderErrorOverlay() {
		container.getErrorUpgradeSlotChangeResult().ifPresent(upgradeSlotChangeResult -> upgradeSlotChangeResult.getErrorMessage().ifPresent(overlayErrorMessage -> {
			GlStateManager.pushMatrix();
			GlStateManager.translatef(getGuiLeft(), (float) getGuiTop(), 0.0F);
			upgradeSlotChangeResult.getErrorUpgradeSlots().forEach(slotIndex -> renderSlotOverlay(container.getSlot(container.getFirstUpgradeSlot() + slotIndex), DyeColor.RED.getColorValue() | 0xAA000000));
			upgradeSlotChangeResult.getErrorInventorySlots().forEach(slotIndex -> {
				Slot slot = container.getSlot(slotIndex);
				if (slot != null) {
					renderSlotOverlay(slot, DyeColor.RED.getColorValue() | 0xAA000000);
				}
			});
			upgradeSlotChangeResult.getErrorInventoryParts().forEach(partIndex -> {
				if (inventoryParts.size() > partIndex) {
					inventoryParts.get(partIndex).renderErrorOverlay();
				}
			});
			GlStateManager.popMatrix();

			renderErrorMessage(overlayErrorMessage);
		}));
	}

	private void renderErrorMessage(ITextComponent overlayErrorMessage) {
		GlStateManager.pushMatrix();
		GlStateManager.disableDepthTest();
		GlStateManager.translatef((float) width / 2, guiTop + playerInventoryTitleY + 4, 300F);
		FontRenderer fontrenderer = Minecraft.getInstance().fontRenderer;

		int tooltipWidth = font.getStringWidth(overlayErrorMessage);

		List<ITextComponent> wrappedTextLines = new ArrayList<>();
		int maxLineWidth = 260;
		if (tooltipWidth > maxLineWidth) {
			int wrappedTooltipWidth = 0;
			List<ITextComponent> wrappedLine = font.getSplitter().splitLines(overlayErrorMessage, maxLineWidth, Style.EMPTY);

			for (ITextComponent line : wrappedLine) {
				int lineWidth = font.getStringWidth(line);
				if (lineWidth > wrappedTooltipWidth) {wrappedTooltipWidth = lineWidth;}
				wrappedTextLines.add(line);
			}
			tooltipWidth = wrappedTooltipWidth;
		} else {
			wrappedTextLines.add(overlayErrorMessage);
		}

		int tooltipHeight = 8;
		if (wrappedTextLines.size() > 1) {
			tooltipHeight += 2 + (wrappedTextLines.size() - 1) * 10;
		}

		float leftX = (float) -tooltipWidth / 2;

		GuiHelper.renderTooltipBackground(tooltipWidth, (int) leftX, 0, tooltipHeight, ERROR_BACKGROUND_COLOR, ERROR_BORDER_COLOR, ERROR_BORDER_COLOR);
		IRenderTypeBuffer.Impl renderTypeBuffer = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuffer());
		GlStateManager.translated(0.0D, 0.0D, 400.0D);
		GuiHelper.writeTooltipLines(wrappedTextLines, fontrenderer, leftX, 0, matrix4f, renderTypeBuffer, DyeColor.RED.getColorValue());
		renderTypeBuffer.endBatch();
		GlStateManager.popMatrix();
	}

	public interface IButtonReplacer {
		default boolean shouldReplace(BackpackScreen screen, net.minecraft.client.gui.widget.button.Button button) {
			return false;
		}

		default net.minecraft.client.gui.widget.button.Button replace(BackpackScreen screen, net.minecraft.client.gui.widget.button.Button button) {
			return button;
		}
	}
}
