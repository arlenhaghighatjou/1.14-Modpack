package net.p3pp3rf1y.sophisticatedbackpacks.util.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class InvWrapper implements IItemHandlerModifiable {
	private final IInventory inventory;

	public InvWrapper(IInventory inventory) {
		this.inventory = inventory;
	}

	public IInventory getInventory() {
		return inventory;
	}

	@Override
	public int getSlots() {
		return inventory.getSizeInventory();
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if (stack.isEmpty() || !inventory.isItemValidForSlot(slot, stack)) {
			return stack;
		}

		ItemStack existing = inventory.getStackInSlot(slot);
		int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());
		if (!existing.isEmpty()) {
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
				return stack;
			}
			limit -= existing.getCount();
		}

		if (limit <= 0) {
			return stack;
		}

		int toInsert = Math.min(limit, stack.getCount());
		if (!simulate) {
			if (existing.isEmpty()) {
				ItemStack inserted = stack.copy();
				inserted.setCount(toInsert);
				inventory.setInventorySlotContents(slot, inserted);
			} else {
				existing.grow(toInsert);
				inventory.setInventorySlotContents(slot, existing);
			}
			inventory.markDirty();
		}

		return toInsert >= stack.getCount() ? ItemStack.EMPTY : ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - toInsert);
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount <= 0) {
			return ItemStack.EMPTY;
		}

		ItemStack existing = inventory.getStackInSlot(slot);
		if (existing.isEmpty()) {
			return ItemStack.EMPTY;
		}

		int toExtract = Math.min(amount, existing.getMaxStackSize());
		if (existing.getCount() <= toExtract) {
			if (!simulate) {
				inventory.setInventorySlotContents(slot, ItemStack.EMPTY);
				inventory.markDirty();
			}
			return existing.copy();
		}

		if (!simulate) {
			inventory.setInventorySlotContents(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
			inventory.markDirty();
		}
		return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
	}

	@Override
	public int getSlotLimit(int slot) {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return inventory.isItemValidForSlot(slot, stack);
	}
}
