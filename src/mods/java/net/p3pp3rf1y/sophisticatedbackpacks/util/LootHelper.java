package net.p3pp3rf1y.sophisticatedbackpacks.util;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import net.p3pp3rf1y.sophisticatedbackpacks.util.inventory.IItemHandlerModifiable;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;

import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.Random;

public class LootHelper {
	private LootHelper() {}

	public static List<ItemStack> getLoot(ResourceLocation lootTableName, MinecraftServer server, ServerWorld world, Entity entity) {
		LootTable lootTable = server.getLootTables().get(lootTableName);
		LootContext.Builder lootBuilder = (new LootContext.Builder(world)).withParameter(LootParameters.ORIGIN, Vec3d.atCenterOf(entity.getPosition())).withOptionalRandomSeed(world.rand.nextLong());
		List<ItemStack> lootStacks = new ArrayList<>();
		lootTable.getRandomItemsRaw(lootBuilder.create(LootParameterSets.CHEST), lootStacks::add);
		return lootStacks;
	}

	public static void fillWithLoot(Random rand, List<ItemStack> loot, IItemHandlerModifiable inventory) {
		List<Integer> slots = InventoryHelper.getEmptySlotsRandomized(inventory, rand);
		InventoryHelper.shuffleItems(loot, inventorySlots.size(), rand);

		for (ItemStack lootStack : loot) {
			if (inventorySlots.isEmpty()) {
				SophisticatedBackpacks.LOGGER.warn("Tried to over-fill backpack");
				return;
			}

			if (!lootStack.isEmpty()) {
				inventory.setStackInSlot(inventorySlots.remove(inventorySlots.size() - 1), lootStack);
			}
		}
	}
}
