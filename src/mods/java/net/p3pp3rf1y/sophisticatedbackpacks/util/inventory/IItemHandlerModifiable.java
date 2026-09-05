package net.p3pp3rf1y.sophisticatedbackpacks.util.inventory;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IItemHandlerModifiable extends IItemHandler {
	void setStackInSlot(int slot, @Nonnull ItemStack stack);
}
