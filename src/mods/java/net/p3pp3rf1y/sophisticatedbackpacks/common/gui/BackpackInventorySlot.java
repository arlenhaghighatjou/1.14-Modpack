package net.p3pp3rf1y.sophisticatedbackpacks.common.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.util.inventory.IItemHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.api.ISlotChangeResponseUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackInventoryHandler;

import javax.annotation.Nonnull;

public class BackpackInventorySlot extends Slot {
	private static final IInventory EMPTY_INVENTORY = new Inventory(0);

	private final boolean isClientSide;
	private final IBackpackWrapper backpackWrapper;
	private final BackpackInventoryHandler inventoryHandler;
	private final int slotIndex;

	public BackpackInventorySlot(boolean isClientSide, IBackpackWrapper backpackWrapper, BackpackInventoryHandler inventoryHandler, int slotIndex, int lineIndex, int yPosition) {
		super(EMPTY_INVENTORY, slotIndex, 8 + lineIndex * 18, yPosition);
		this.isRemote = isClientSide;
		this.backpackWrapper = backpackWrapper;
		this.inventoryHandler = inventoryHandler;
		this.slotIndex = slotIndex;
	}

	@Override
	public void onSlotChanged() {
		super.onSlotChanged();
		// saving here as well because there are many cases where vanilla modifies stack directly without and inventory handler isn't aware of it
		// however it does notify the slot of change
		backpackWrapper.getInventoryHandler().onContentsChanged(slotIndex);
		processSlotChangeResponse(slotIndex, backpackWrapper.getInventoryHandler(), backpackWrapper);
	}

	private void processSlotChangeResponse(int slot, IItemHandler handler, IBackpackWrapper backpackWrapper) {
		if (!isClientSide) {
			backpackWrapper.getUpgradeHandler().getWrappersThatImplementFromMainBackpack(ISlotChangeResponseUpgrade.class).forEach(u -> u.onSlotChange(handler, slot));
		}
	}

	@Nonnull
	@Override
	public ItemStack getStack() {
		return inventoryHandler.getStackInSlot(slotIndex);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		return inventoryHandler.isItemValid(slotIndex, stack);
	}

	@Override
	public void putStack(ItemStack stack) {
		inventoryHandler.setStackInSlot(slotIndex, stack);
		onSlotChanged();
	}

	@Override
	public int getSlotStackLimit() {
		return inventoryHandler.getSlotLimit(slotIndex);
	}

	@Override
	public void onSlotChange(@Nonnull ItemStack oldStackIn, @Nonnull ItemStack newStackIn) {
		//noop
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return inventoryHandler.getStackLimit(slotIndex, stack);
	}

	@Override
	public boolean canTakeStack(PlayerEntity playerIn) {
		return !inventoryHandler.extractItem(slotIndex, 1, true).isEmpty();
	}

	@Override
	@Nonnull
	public ItemStack decrStackSize(int amount) {
		return inventoryHandler.extractItem(slotIndex, amount, false);
	}
}
