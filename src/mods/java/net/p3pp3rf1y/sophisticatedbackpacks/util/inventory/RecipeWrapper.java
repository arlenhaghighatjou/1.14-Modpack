package net.p3pp3rf1y.sophisticatedbackpacks.util.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Shows an item handler to the recipe manager, which only speaks vanilla inventories.
 */
public class RecipeWrapper implements IInventory {
	protected final IItemHandlerModifiable inv;

	public RecipeWrapper(IItemHandlerModifiable inv) {
		this.inv = inv;
	}

	@Override
	public int getSizeInventory() {
		return inv.getSlots();
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < inv.getSlots(); i++) {
			if (!inv.getStackInSlot(i).isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return inv.getStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack stack = inv.getStackInSlot(index);
		return stack.isEmpty() ? ItemStack.EMPTY : inv.extractItem(index, count, false);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack stack = inv.getStackInSlot(index);
		return stack.isEmpty() ? ItemStack.EMPTY : inv.extractItem(index, stack.getCount(), false);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inv.setStackInSlot(index, stack);
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return true;
	}

	@Override
	public void clear() {
		for (int i = 0; i < inv.getSlots(); i++) {
			inv.setStackInSlot(i, ItemStack.EMPTY);
		}
	}
}
