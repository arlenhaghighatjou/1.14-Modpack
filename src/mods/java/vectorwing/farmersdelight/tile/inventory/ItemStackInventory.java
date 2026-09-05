package vectorwing.farmersdelight.tile.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

public class ItemStackInventory implements IInventory
{
	protected final NonNullList<ItemStack> stacks;

	public ItemStackInventory(int size)
	{
		this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
	}

	public int getSlots()
	{
		return this.stacks.size();
	}

	@Override
	public int getSizeInventory()
	{
		return this.stacks.size();
	}

	@Override
	public boolean isEmpty()
	{
		for (ItemStack stack : this.stacks) {
			if (!stack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int index)
	{
		return this.stacks.get(index);
	}

	public void setStackInSlot(int index, ItemStack stack)
	{
		this.stacks.set(index, stack);
		this.onContentsChanged(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		ItemStack stack = ItemStackHelper.getAndSplit(this.stacks, index, count);
		if (!stack.isEmpty()) {
			this.onContentsChanged(index);
		}
		return stack;
	}

	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		ItemStack stack = ItemStackHelper.getAndRemove(this.stacks, index);
		if (!stack.isEmpty()) {
			this.onContentsChanged(index);
		}
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		this.stacks.set(index, stack);
		if (stack.getCount() > this.getSlotLimit(index)) {
			stack.setCount(this.getSlotLimit(index));
		}
		this.onContentsChanged(index);
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		return true;
	}

	public int getSlotLimit(int index)
	{
		return this.getInventoryStackLimit();
	}

	public ItemStack insertItem(int index, ItemStack stack, boolean simulate)
	{
		if (stack.isEmpty() || !this.isItemValidForSlot(index, stack)) {
			return stack;
		}

		ItemStack existing = this.stacks.get(index);
		int limit = Math.min(this.getSlotLimit(index), stack.getMaxStackSize());

		if (!existing.isEmpty()) {
			if (!ItemStack.areItemsEqual(stack, existing) || !ItemStack.areItemStackTagsEqual(stack, existing)) {
				return stack;
			}
			limit -= existing.getCount();
		}

		if (limit <= 0) {
			return stack;
		}

		int inserted = Math.min(limit, stack.getCount());

		if (!simulate) {
			if (existing.isEmpty()) {
				ItemStack copy = stack.copy();
				copy.setCount(inserted);
				this.stacks.set(index, copy);
			} else {
				existing.grow(inserted);
			}
			this.onContentsChanged(index);
		}

		if (inserted == stack.getCount()) {
			return ItemStack.EMPTY;
		}

		ItemStack remainder = stack.copy();
		remainder.shrink(inserted);
		return remainder;
	}

	public ItemStack extractItem(int index, int amount, boolean simulate)
	{
		ItemStack existing = this.stacks.get(index);
		if (existing.isEmpty() || amount <= 0) {
			return ItemStack.EMPTY;
		}

		int extracted = Math.min(amount, existing.getCount());

		if (simulate) {
			ItemStack copy = existing.copy();
			copy.setCount(extracted);
			return copy;
		}

		return this.decrStackSize(index, extracted);
	}

	@Override
	public void markDirty()
	{
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player)
	{
		return true;
	}

	@Override
	public void clear()
	{
		this.stacks.clear();
	}

	public CompoundNBT serializeNBT()
	{
		CompoundNBT compound = new CompoundNBT();
		ItemStackHelper.saveAllItems(compound, this.stacks, true);
		compound.putInt("Size", this.stacks.size());
		return compound;
	}

	public void deserializeNBT(CompoundNBT compound)
	{
		this.stacks.clear();
		ItemStackHelper.loadAllItems(compound, this.stacks);
	}

	protected void onContentsChanged(int index)
	{
	}
}
