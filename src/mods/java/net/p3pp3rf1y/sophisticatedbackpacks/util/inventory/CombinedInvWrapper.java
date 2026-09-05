package net.p3pp3rf1y.sophisticatedbackpacks.util.inventory;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Treats several item handlers as one continuous run of inventorySlots.
 */
public class CombinedInvWrapper implements IItemHandlerModifiable {
	protected final IItemHandlerModifiable[] handlers;
	protected final int[] baseIndex;
	protected final int slotCount;

	public CombinedInvWrapper(IItemHandlerModifiable... handlers) {
		this.handlers = handlers;
		this.baseIndex = new int[handlers.length];
		int index = 0;
		for (int i = 0; i < handlers.length; i++) {
			index += handlers[i].getSlots();
			baseIndex[i] = index;
		}
		this.slotCount = index;
	}

	protected int getIndexForSlot(int slot) {
		if (slot < 0) {
			return -1;
		}

		for (int i = 0; i < baseIndex.length; i++) {
			if (slot < baseIndex[i]) {
				return i;
			}
		}

		return -1;
	}

	protected IItemHandlerModifiable getHandlerFromIndex(int index) {
		if (index < 0 || index >= handlers.length) {
			return EmptyHandler.INSTANCE;
		}

		return handlers[index];
	}

	protected int getSlotFromIndex(int slot, int index) {
		if (index <= 0 || index >= baseIndex.length) {
			return slot;
		}

		return slot - baseIndex[index - 1];
	}

	@Override
	public int getSlots() {
		return slotCount;
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		int index = getIndexForSlot(slot);
		return getHandlerFromIndex(index).getStackInSlot(getSlotFromIndex(slot, index));
	}

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		int index = getIndexForSlot(slot);
		getHandlerFromIndex(index).setStackInSlot(getSlotFromIndex(slot, index), stack);
	}

	@Override
	@Nonnull
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		int index = getIndexForSlot(slot);
		return getHandlerFromIndex(index).insertItem(getSlotFromIndex(slot, index), stack, simulate);
	}

	@Override
	@Nonnull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		int index = getIndexForSlot(slot);
		return getHandlerFromIndex(index).extractItem(getSlotFromIndex(slot, index), amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		int index = getIndexForSlot(slot);
		return getHandlerFromIndex(index).getSlotLimit(getSlotFromIndex(slot, index));
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		int index = getIndexForSlot(slot);
		return getHandlerFromIndex(index).isItemValid(getSlotFromIndex(slot, index), stack);
	}
}
