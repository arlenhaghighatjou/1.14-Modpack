package net.p3pp3rf1y.sophisticatedbackpacks.util.inventory;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;

public class SidedInvWrapper implements IItemHandlerModifiable {
	private final ISidedInventory inventory;
	private final Direction side;

	public SidedInvWrapper(ISidedInventory inventory, Direction side) {
		this.inventory = inventory;
		this.side = side;
	}

	private int slotAt(int slot) {
		int[] slots = inventory.getSlotsForFace(side);
		return slot < slots.length ? slots[slot] : -1;
	}

	@Override
	public int getSlots() {
		return inventory.getSlotsForFace(side).length;
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot) {
		int target = slotAt(slot);
		return target < 0 ? ItemStack.EMPTY : inventory.getStackInSlot(target);
	}

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		int target = slotAt(slot);
		if (target >= 0) {
			inventory.setInventorySlotContents(target, stack);
		}
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		int target = slotAt(slot);
		if (target < 0 || stack.isEmpty() || !inventory.canInsertItem(target, stack, side)) {
			return stack;
		}

		ItemStack existing = inventory.getStackInSlot(target);
		int limit = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());
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
				inventory.setInventorySlotContents(target, ItemHandlerHelper.copyStackWithSize(stack, toInsert));
			} else {
				existing.grow(toInsert);
				inventory.setInventorySlotContents(target, existing);
			}
			inventory.markDirty();
		}

		return toInsert >= stack.getCount() ? ItemStack.EMPTY : ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - toInsert);
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		int target = slotAt(slot);
		if (target < 0 || amount <= 0) {
			return ItemStack.EMPTY;
		}

		ItemStack existing = inventory.getStackInSlot(target);
		if (existing.isEmpty() || !inventory.canExtractItem(target, existing, side)) {
			return ItemStack.EMPTY;
		}

		int toExtract = Math.min(amount, existing.getMaxStackSize());
		if (existing.getCount() <= toExtract) {
			if (!simulate) {
				inventory.setInventorySlotContents(target, ItemStack.EMPTY);
				inventory.markDirty();
			}
			return existing.copy();
		}

		if (!simulate) {
			inventory.setInventorySlotContents(target, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
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
		int target = slotAt(slot);
		return target >= 0 && inventory.isItemValidForSlot(target, stack);
	}
}
