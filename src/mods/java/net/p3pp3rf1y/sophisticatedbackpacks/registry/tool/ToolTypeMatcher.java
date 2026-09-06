package net.p3pp3rf1y.sophisticatedbackpacks.registry.tool;

import net.minecraft.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.util.tool.ToolType;

class ToolTypeMatcher implements CacheableStackPredicate {
	private final ToolType toolType;

	public ToolTypeMatcher(ToolType toolType) {
		this.toolType = toolType;
	}

	@Override
	public boolean test(ItemStack stack) {
		return net.p3pp3rf1y.sophisticatedbackpacks.util.tool.ToolType.of(stack).contains(toolType);
	}

	@Override
	public boolean preventsCaching(ItemStack stack) {
		return true;
	}
}
