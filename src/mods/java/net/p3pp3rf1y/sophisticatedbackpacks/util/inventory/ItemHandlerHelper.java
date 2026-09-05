package net.p3pp3rf1y.sophisticatedbackpacks.util.inventory;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemHandlerHelper {
	private ItemHandlerHelper() {}

	public static boolean canItemStacksStack(@Nonnull ItemStack a, @Nonnull ItemStack b) {
		if (a.isEmpty() || !a.isItemEqual(b) || a.hasTag() != b.hasTag()) {
			return false;
		}

		return (!a.hasTag() || a.getTag().equals(b.getTag()));
	}

	@Nonnull
	public static ItemStack copyStackWithSize(@Nonnull ItemStack stack, int size) {
		if (size == 0) {
			return ItemStack.EMPTY;
		}

		ItemStack copy = stack.copy();
		copy.setCount(size);
		return copy;
	}

	@Nonnull
	public static ItemStack insertItem(IItemHandler dest, @Nonnull ItemStack stack, boolean simulate) {
		if (dest == null || stack.isEmpty()) {
			return stack;
		}

		for (int i = 0; i < dest.getSlots(); i++) {
			stack = dest.insertItem(i, stack, simulate);
			if (stack.isEmpty()) {
				return ItemStack.EMPTY;
			}
		}

		return stack;
	}
}
