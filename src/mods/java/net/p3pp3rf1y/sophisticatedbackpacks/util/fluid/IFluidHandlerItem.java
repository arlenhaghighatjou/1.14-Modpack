package net.p3pp3rf1y.sophisticatedbackpacks.util.fluid;

import net.minecraft.item.ItemStack;

/**
 * A fluid handler that lives on an item stack, so filling or draining it writes back to the stack.
 */
public interface IFluidHandlerItem extends IFluidHandler {
	ItemStack getContainer();
}
