package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapperLookup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.p3pp3rf1y.sophisticatedbackpacks.util.fluid.FluidStack;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.Dimension;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.GuiHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.TextureBlitData;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.TranslationHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.UV;
import net.p3pp3rf1y.sophisticatedbackpacks.network.PacketHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.network.RequestBackpackInventoryContentsMessage;
import net.p3pp3rf1y.sophisticatedbackpacks.util.CountAbbreviator;
import net.p3pp3rf1y.sophisticatedbackpacks.util.InventoryHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.lax1dude.eaglercraft.EaglercraftUUID;

public class BackpackTooltipRenderer {

	private static final String BACKPACK_ITEM_NAME = "backpack";

	private BackpackTooltipRenderer() {}

	private static final int REFRESH_INTERVAL = 20;
	private static boolean shouldRefreshContents = true;
	private static long lastRequestTime = 0;
	private static ContentsTooltipPart contentsTooltipPart;
	@Nullable
	private static EaglercraftUUID backpackUuid = null;

	public static void onWorldLoad() {
		shouldRefreshContents = true;
		lastRequestTime = 0;
	}

	public static boolean handleBackpackTooltipRender(ItemStack backpack, int x, int y, FontRenderer font) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientPlayerEntity player = minecraft.player;
		if (!(backpack.getItem() instanceof BackpackItem) || !Screen.hasShiftDown() || player == null) {
			return false;
		}
		return renderBackpackTooltip(backpack, minecraft, player, x, y, font);
	}

	public static boolean renderBackpackTooltip(ItemStack backpack, Minecraft minecraft, ClientPlayerEntity player, int x, int y, FontRenderer font) {
		return BackpackWrapperLookup.get(backpack).map(wrapper -> {
			initContents(minecraft, player, wrapper);

			List<ITextComponent> lines = backpack.getTooltip(player, minecraft.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
			if (backpackUuid != null) {
				int multiplier = wrapper.getInventoryHandler().getStackSizeMultiplier();
				if (multiplier > 1) {
					lines.add(new TranslationTextComponent("item.sophisticatedbackpacks.backpack.tooltip.stack_multiplier",
							new StringTextComponent(Integer.toString(multiplier)).applyTextStyle(TextFormatting.WHITE)
					).applyTextStyle(TextFormatting.GREEN));
				}
				addEnergytooltip(wrapper, lines);
				addFluidTooltip(wrapper, lines);
			}
			renderContentsTooltip(backpack, minecraft, x, y, font, lines);

			return true;
		}).orElse(false);
	}

	public static void renderTooltipWithContents(ItemStack backpack, Minecraft minecraft, int x, int y, FontRenderer font, List<ITextComponent> lines) {
		BackpackWrapperLookup.get(backpack).ifPresent(wrapper -> {
			if (minecraft.player != null) {
				initContents(minecraft, minecraft.player, wrapper);
				renderContentsTooltip(backpack, minecraft, x, y, font, lines);
			}
		});
	}

	private static void renderContentsTooltip(ItemStack backpack, Minecraft minecraft, int x, int y, FontRenderer font, List<ITextComponent> lines) {
		GuiHelper.renderTooltip(minecraft, lines, x, y, contentsTooltipPart, font, backpack);
	}

	private static void initContents(Minecraft minecraft, ClientPlayerEntity player, IBackpackWrapper wrapper) {
		EaglercraftUUID newUuid = wrapper.getContentsUuid().orElse(null);
		if (backpackUuid == null && newUuid != null || backpackUuid != null && !backpackUuid.equals(newUuid)) {
			lastRequestTime = 0;
			backpackUuid = newUuid;
			shouldRefreshContents = true;
		}
		requestContents(player, wrapper);
		refreshContents(wrapper, minecraft);
	}

	private static void addEnergytooltip(IBackpackWrapper wrapper, List<ITextComponent> lines) {
		wrapper.getEnergyStorage().ifPresent(energyStorage -> lines.add(new TranslationTextComponent(TranslationHelper.translItemTooltip(BACKPACK_ITEM_NAME) + ".energy",
				new StringTextComponent(CountAbbreviator.abbreviate(energyStorage.getEnergyStored())).applyTextStyle(TextFormatting.WHITE)).applyTextStyle(TextFormatting.RED)
		));
	}

	private static void addFluidTooltip(IBackpackWrapper wrapper, List<ITextComponent> lines) {
		wrapper.getFluidHandler().ifPresent(fluidHandler -> {
			for (int tank = 0; tank < fluidHandler.getTanks(); tank++) {
				FluidStack fluid = fluidHandler.getFluidInTank(tank);
				if (fluid.isEmpty()) {
					lines.add(new TranslationTextComponent(TranslationHelper.translItemTooltip(BACKPACK_ITEM_NAME) + ".fluid_empty").applyTextStyle(TextFormatting.BLUE));
				} else {
					lines.add(new TranslationTextComponent(TranslationHelper.translItemTooltip(BACKPACK_ITEM_NAME) + ".fluid",
							new StringTextComponent(CountAbbreviator.abbreviate(fluid.getAmount())).applyTextStyle(TextFormatting.WHITE),
							new TranslationTextComponent(fluid.getTranslationKey()).applyTextStyle(TextFormatting.BLUE)

					));
				}
			}
		});
	}

	private static void requestContents(ClientPlayerEntity player, IBackpackWrapper wrapper) {
		if (lastRequestTime + REFRESH_INTERVAL < player.world.getGameTime()) {
			lastRequestTime = player.world.getGameTime();
			wrapper.getContentsUuid().ifPresent(uuid -> PacketHandler.sendToServer(new RequestBackpackInventoryContentsMessage(uuid)));
		}
	}

	private static void refreshContents(IBackpackWrapper wrapper, Minecraft minecraft) {
		if (shouldRefreshContents) {
			shouldRefreshContents = false;
			if (backpackUuid != null) {
				wrapper.onContentsNbtUpdated();
				List<ItemStack> sortedContents = InventoryHelper.getCompactedStacksSortedByCount(wrapper.getInventoryHandler());
				contentsTooltipPart = new ContentsTooltipPart(minecraft, new TreeMap<>(wrapper.getUpgradeHandler().getSlotWrappers()), sortedContents);
			} else {
				contentsTooltipPart = getEmptyInventoryTooltip(minecraft);
			}
		}
		if (contentsTooltipPart == null) {
			contentsTooltipPart = getEmptyInventoryTooltip(minecraft);
		}
	}

	private static ContentsTooltipPart getEmptyInventoryTooltip(Minecraft minecraft) {
		return new ContentsTooltipPart(minecraft, new HashMap<>(), new ArrayList<>());
	}

	//TODO this probably needs to move somewhere else, but there's no easy way to understand what STACK requested refresh of contents and tooltip is the only one at the moment
	public static void refreshContents() {
		shouldRefreshContents = true;
	}

	private static class ContentsTooltipPart implements GuiHelper.ITooltipRenderPart {
		private static final TextureBlitData UPGRADE_ON = new TextureBlitData(GuiHelper.ICONS, Dimension.SQUARE_256, new UV(4, 128), Dimension.RECTANGLE_4_10);
		private static final TextureBlitData UPGRADE_OFF = new TextureBlitData(GuiHelper.ICONS, Dimension.SQUARE_256, new UV(0, 128), Dimension.RECTANGLE_4_10);
		private static final int MAX_STACKS_ON_LINE = 9;
		private static final int DEFAULT_STACK_WIDTH = 18;
		private static final int COUNT_PADDING = 2;
		private final Minecraft minecraft;
		private final Map<Integer, IUpgradeWrapper> upgrades;
		private final List<ItemStack> backpackContents;
		private int height;
		private int width;

		public ContentsTooltipPart(Minecraft minecraft, Map<Integer, IUpgradeWrapper> upgrades, List<ItemStack> backpackContents) {
			this.minecraft = minecraft;
			this.upgrades = upgrades;
			this.backpackContents = backpackContents;
			calculateHeight();
			calculateWidth();
		}

		private void calculateWidth() {
			int upgradesWidth = calculateUpgradesWidth();
			int contentsWidth = calculateContentsWidth();
			int stacksWidth = Math.max(upgradesWidth, contentsWidth);
			width = stacksWidth > 0 ? stacksWidth : getEmptyTooltipWidth();
		}

		private int calculateUpgradesWidth() {
			int upgradesWidth = 0;
			for (IUpgradeWrapper upgradeWrapper : upgrades.values()) {
				upgradesWidth += (upgradeWrapper.canBeDisabled() ? 4 : 0) + DEFAULT_STACK_WIDTH;
			}
			return upgradesWidth;
		}

		private int calculateContentsWidth() {
			FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
			int contentsWidth = 0;
			for (int i = 0; i < backpackContents.size() && i < MAX_STACKS_ON_LINE; i++) {
				int countWidth = getStackCountWidth(fontRenderer, backpackContents.get(i));
				contentsWidth += Math.max(countWidth, DEFAULT_STACK_WIDTH);
			}

			return contentsWidth;
		}

		private int getStackCountWidth(FontRenderer fontRenderer, ItemStack stack) {
			return fontRenderer.getStringWidth(CountAbbreviator.abbreviate(stack.getCount())) + COUNT_PADDING;
		}

		private void calculateHeight() {
			int upgradesHeight = upgrades.isEmpty() ? 0 : 32;
			int inventoryHeight = backpackContents.isEmpty() ? 0 : 12 + (1 + (backpackContents.size() - 1)/ MAX_STACKS_ON_LINE) * 20;
			int totalHeight = upgradesHeight + inventoryHeight;
			height = totalHeight > 0 ? totalHeight : 12;
		}

		@Override
		public int getWidth() {
			return width;
		}

		private int getEmptyTooltipWidth() {
			return Minecraft.getInstance().fontRenderer.getStringWidth(new TranslationTextComponent(BackpackItem.BACKPACK_TOOLTIP + "empty").getFormattedText());
		}

		@Override
		public int getHeight() {
			return height;
		}

		@Override
		public void render(int leftX, int topY, FontRenderer font) {
			if (!upgrades.isEmpty()) {
				topY = renderTooltipLine(leftX, topY, font, "upgrades");
				topY = renderUpgrades(leftX, topY);
			}
			if (!backpackContents.isEmpty()) {
				topY = renderTooltipLine(leftX, topY, font, "inventory");
				renderContents(leftX, topY);
			}
			if (upgrades.isEmpty() && backpackContents.isEmpty()) {
				renderTooltipLine(leftX, topY, font, "empty");
			}
		}

		private int renderTooltipLine(int leftX, int topY, FontRenderer font, String tooltip) {
			return GuiHelper.writeTooltipLines(Collections.singletonList(new TranslationTextComponent(BackpackItem.BACKPACK_TOOLTIP + tooltip).applyTextStyle(TextFormatting.YELLOW)),
					font, leftX, topY, -1);
		}

		private int renderUpgrades(int leftX, int topY) {
			int x = leftX;
			for (IUpgradeWrapper upgradeWrapper : upgrades.values()) {
				if (upgradeWrapper.canBeDisabled()) {
					GuiHelper.blit(minecraft, x, topY + 3, upgradeWrapper.isEnabled() ? UPGRADE_ON : UPGRADE_OFF);
					x += 4;
				}
				GuiHelper.renderItemInGUI(minecraft, upgradeWrapper.getUpgradeStack(), x, topY, true);
				x += DEFAULT_STACK_WIDTH;
			}
			topY += 20;
			return topY;
		}

		private void renderContents(int leftX, int topY) {
			int x = leftX;
			for (int i = 0; i < backpackContents.size(); i++) {
				int y = topY + i / MAX_STACKS_ON_LINE * 20;
				if (i % MAX_STACKS_ON_LINE == 0) {
					x = leftX;
				}
				ItemStack stack = backpackContents.get(i);
				int stackWidth = Math.max(getStackCountWidth(minecraft.fontRenderer, stack), DEFAULT_STACK_WIDTH);
				int xOffset = stackWidth - DEFAULT_STACK_WIDTH;
				GuiHelper.renderItemInGUI(minecraft, stack, x + xOffset, y, true, CountAbbreviator.abbreviate(stack.getCount()));
				x += stackWidth;
			}
		}
	}
}
