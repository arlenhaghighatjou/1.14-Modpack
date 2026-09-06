package net.p3pp3rf1y.sophisticatedbackpacks.init;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.TableLootEntry;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.data.CopyBackpackDataFunction;

import java.util.List;

public class ModLoot {
	private ModLoot() {}

	private static final List<String> CHEST_TABLES = ImmutableList.of("abandoned_mineshaft", "bastion_treasure", "desert_pyramid", "end_city_treasure", "nether_bridge", "shipwreck_treasure", "simple_dungeon", "woodland_mansion");

	public static void init() {
		LootFunctionManager.registerFunction(new CopyBackpackDataFunction.Serializer());
	}

	public static void lootLoad(ResourceLocation name, LootTable table) {
		if (Boolean.FALSE.equals(Config.COMMON.chestLootEnabled)) {
			return;
		}

		String chestsPrefix = "minecraft:chests/";
		String tableName = name.toString();

		if (tableName.startsWith(chestsPrefix) && CHEST_TABLES.contains(tableName.substring(chestsPrefix.length()))) {
			table.addPool(getInjectPool(tableName.substring("minecraft:".length())));
		}
	}

	private static LootPool getInjectPool(String entryName) {
		return LootPool.builder().addEntry(getInjectEntry(entryName)).rolls(new RandomValueRange(0, 1)).build();
	}

	private static LootEntry.Builder<?> getInjectEntry(String name) {
		return TableLootEntry.builder(new ResourceLocation(SophisticatedBackpacks.MOD_ID, "inject/" + name)).weight(1);
	}
}
