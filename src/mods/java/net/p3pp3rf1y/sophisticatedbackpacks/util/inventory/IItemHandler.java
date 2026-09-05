package net.p3pp3rf1y.sophisticatedbackpacks.util.inventory;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * A slotted item container. Ported from the item handler capability the mod is built on, so the
 * upgrades and backpack wrappers keep their insert/extract semantics on top of vanilla inventories.
 */
public interface IItemHandler {
	int getSlots();

	@Nonnull
	ItemStack getStackInSlot(int slot);

	@Nonnull
	ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate);

	@Nonnull
	ItemStack extractItem(int slot, int amount, boolean simulate);

	int getSlotLimit(int slot);

	boolean isItemValid(int slot, @Nonnull ItemStack stack);
}
