package net.p3pp3rf1y.sophisticatedbackpacks.util;

import net.minecraft.util.registry.Registry;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import org.apache.commons.lang3.Validate;

import java.util.Optional;

public class RegistryHelper {
	private RegistryHelper() {}

	public static ResourceLocation getRL(String regName) {
		return new ResourceLocation(getModRegistryName(regName));
	}

	public static String getModRegistryName(String regName) {
		return SophisticatedBackpacks.MOD_ID + ":" + regName;
	}

	public static ResourceLocation getItemKey(Item item) {
		ResourceLocation itemKey = Registry.ITEM.getKey(item);
		Validate.notNull(itemKey, "itemKey");
		return itemKey;
	}

	public static Optional<ResourceLocation> getRegistryName(Object registryEntry) {
		if (registryEntry instanceof Item) {
			return Optional.ofNullable(Registry.ITEM.getKey((Item) registryEntry));
		}
		if (registryEntry instanceof Block) {
			return Optional.ofNullable(Registry.BLOCK.getKey((Block) registryEntry));
		}
		if (registryEntry instanceof EntityType) {
			return Optional.ofNullable(Registry.ENTITY_TYPE.getKey((EntityType<?>) registryEntry));
		}
		return Optional.empty();
	}
}