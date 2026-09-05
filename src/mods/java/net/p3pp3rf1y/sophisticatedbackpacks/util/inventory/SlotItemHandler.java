package net.p3pp3rf1y.sophisticatedbackpacks.util.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Presents one slot of an item handler through the vanilla container slot the screen draws.
 */
public class SlotItemHandler extends Slot {
	private static final net.minecraft.inventory.IInventory EMPTY_INVENTORY = new net.minecraft.inventory.Inventory(0);

	private final IItemHandler itemHandler;
	private final int index;

	public SlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(EMPTY_INVENTORY, index, xPosition, yPosition);
		this.itemHandler = itemHandler;
		this.index = index;
	}

	public IItemHandler getItemHandler() {
		return itemHandler;
	}

	public int getSlotIndex() {
		return index;
	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}

		return itemHandler.isItemValid(index, stack);
	}

	@Override
	@Nonnull
	public ItemStack getStack() {
		return itemHandler.getStackInSlot(index);
	}

	@Override
	public void putStack(@Nonnull ItemStack stack) {
		((IItemHandlerModifiable) itemHandler).setStackInSlot(index, stack);
		onSlotChanged();
	}

	@Override
	public int getSlotStackLimit() {
		return itemHandler.getSlotLimit(index);
	}

	@Override
	public int getItemStackLimit(@Nonnull ItemStack stack) {
		ItemStack maxAdd = stack.copy();
		int maxInput = stack.getMaxStackSize();
		maxAdd.setCount(maxInput);

		ItemStack currentStack = itemHandler.getStackInSlot(index);
		((IItemHandlerModifiable) itemHandler).setStackInSlot(index, ItemStack.EMPTY);
		ItemStack remainder = itemHandler.insertItem(index, maxAdd, true);
		((IItemHandlerModifiable) itemHandler).setStackInSlot(index, currentStack);

		return maxInput - remainder.getCount();
	}

	@Override
	public boolean canTakeStack(PlayerEntity playerIn) {
		return !itemHandler.extractItem(index, 1, true).isEmpty();
	}

	@Override
	@Nonnull
	public ItemStack decrStackSize(int amount) {
		return itemHandler.extractItem(index, amount, false);
	}
}
