package net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper;

import net.minecraft.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.util.LazyOptional;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Hands out the wrapper that backs a backpack stack. Replaces the capability the stack used to
 * carry: one wrapper per stack instance, built the first time it is asked for.
 */
public class BackpackWrapperLookup {
	private static final Map<ItemStack, IBackpackWrapper> WRAPPERS = new WeakHashMap<>();

	private BackpackWrapperLookup() {}

	public static LazyOptional<IBackpackWrapper> get(ItemStack stack) {
		if (stack.isEmpty() || !(stack.getItem() instanceof BackpackItem)) {
			return LazyOptional.empty();
		}

		return LazyOptional.of(() -> WRAPPERS.computeIfAbsent(stack, BackpackWrapper::new));
	}
}
