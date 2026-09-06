package net.p3pp3rf1y.sophisticatedbackpacks.registry.tool;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;

class ItemTagMatcher implements CacheableStackPredicate {
	private final Tag<Item> itemTag;

	public ItemTagMatcher(Tag<Item> itemTag) {
		this.itemTag = itemTag;
	}

	@Override
	public boolean test(ItemStack stack) {
		return itemTag.contains(stack.getItem());
	}
}
