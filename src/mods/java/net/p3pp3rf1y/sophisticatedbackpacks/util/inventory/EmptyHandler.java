package net.p3pp3rf1y.sophisticatedbackpacks.util.inventory;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class EmptyHandler implements IItemHandlerModifiable {
	public static final EmptyHandler INSTANCE = new EmptyHandler();

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
	}

	@Override
	public int getSlots() {
		return 0;
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	@Nonnull
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		return stack;
	}

	@Override
	@Nonnull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 0;
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return false;
	}
}
