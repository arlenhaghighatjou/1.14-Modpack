package vectorwing.farmersdelight.setup;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComposterBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraft.world.storage.loot.TableLootEntry;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraft.world.storage.loot.LootTable;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.loot.functions.CopyMealFunction;
import vectorwing.farmersdelight.registry.ModAdvancements;
import vectorwing.farmersdelight.registry.ModBlocks;
import vectorwing.farmersdelight.registry.ModEffects;
import vectorwing.farmersdelight.registry.ModItems;
import vectorwing.farmersdelight.tile.dispenser.CuttingBoardDispenseBehavior;
import vectorwing.farmersdelight.utils.tags.ModTags;
import vectorwing.farmersdelight.world.CropPatchGeneration;
import vectorwing.farmersdelight.world.VillageStructures;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import net.lax1dude.eaglercraft.Random;
import java.util.Set;

@ParametersAreNonnullByDefault
public class CommonEventHandler
{
	private static final ResourceLocation SHIPWRECK_SUPPLY_CHEST = LootTables.CHESTS_SHIPWRECK_SUPPLY;
	private static final Set<ResourceLocation> VILLAGE_HOUSE_CHESTS = Sets.newHashSet(
			LootTables.CHESTS_VILLAGE_VILLAGE_PLAINS_HOUSE,
			LootTables.CHESTS_VILLAGE_VILLAGE_SAVANNA_HOUSE,
			LootTables.CHESTS_VILLAGE_VILLAGE_SNOWY_HOUSE,
			LootTables.CHESTS_VILLAGE_VILLAGE_TAIGA_HOUSE,
			LootTables.CHESTS_VILLAGE_VILLAGE_DESERT_HOUSE);
	private static final String[] SCAVENGING_ENTITIES = new String[] { "cow", "chicken", "rabbit", "horse", "donkey", "mule", "llama", "shulker" };

	public static void init()
	{
		registerCompostables();

		ModAdvancements.register();

		if (Configuration.GENERATE_VILLAGE_COMPOST_HEAPS) {
			VillageStructures.init();
		}

		LootFunctionManager.registerFunction(new CopyMealFunction.Serializer());

		if (Configuration.DISPENSER_TOOLS_CUTTING_BOARD) {
			CuttingBoardDispenseBehavior.registerBehaviour(Items.WOODEN_PICKAXE, new CuttingBoardDispenseBehavior());
			CuttingBoardDispenseBehavior.registerBehaviour(Items.WOODEN_AXE, new CuttingBoardDispenseBehavior());
			CuttingBoardDispenseBehavior.registerBehaviour(Items.WOODEN_SHOVEL, new CuttingBoardDispenseBehavior());
			CuttingBoardDispenseBehavior.registerBehaviour(Items.STONE_PICKAXE, new CuttingBoardDispenseBehavior());
			CuttingBoardDispenseBehavior.registerBehaviour(Items.STONE_AXE, new CuttingBoardDispenseBehavior());
			CuttingBoardDispenseBehavior.registerBehaviour(Items.STONE_SHOVEL, new CuttingBoardDispenseBehavior());
			CuttingBoardDispenseBehavior.registerBehaviour(Items.IRON_PICKAXE, new CuttingBoardDispenseBehavior());
			CuttingBoardDispenseBehavior.registerBehaviour(Items.IRON_AXE, new CuttingBoardDispenseBehavior());
			CuttingBoardDispenseBehavior.registerBehaviour(Items.IRON_SHOVEL, new CuttingBoardDispenseBehavior());
			CuttingBoardDispenseBehavior.registerBehaviour(Items.DIAMOND_PICKAXE, new CuttingBoardDispenseBehavior());
			CuttingBoardDispenseBehavior.registerBehaviour(Items.DIAMOND_AXE, new CuttingBoardDispenseBehavior());
			CuttingBoardDispenseBehavior.registerBehaviour(Items.DIAMOND_SHOVEL, new CuttingBoardDispenseBehavior());
			CuttingBoardDispenseBehavior.registerBehaviour(Items.GOLDEN_PICKAXE, new CuttingBoardDispenseBehavior());
			CuttingBoardDispenseBehavior.registerBehaviour(Items.GOLDEN_AXE, new CuttingBoardDispenseBehavior());
			CuttingBoardDispenseBehavior.registerBehaviour(Items.GOLDEN_SHOVEL, new CuttingBoardDispenseBehavior());
			CuttingBoardDispenseBehavior.registerBehaviour(Items.SHEARS, new CuttingBoardDispenseBehavior());
			CuttingBoardDispenseBehavior.registerBehaviour(ModItems.FLINT_KNIFE, new CuttingBoardDispenseBehavior());
			CuttingBoardDispenseBehavior.registerBehaviour(ModItems.IRON_KNIFE, new CuttingBoardDispenseBehavior());
			CuttingBoardDispenseBehavior.registerBehaviour(ModItems.DIAMOND_KNIFE, new CuttingBoardDispenseBehavior());
			CuttingBoardDispenseBehavior.registerBehaviour(ModItems.GOLDEN_KNIFE, new CuttingBoardDispenseBehavior());
		}

		registerVillagerTrades();

		HoeItem.HOE_LOOKUP.put(ModBlocks.RICH_SOIL, ModBlocks.RICH_SOIL_FARMLAND.getDefaultState());

		CropPatchGeneration.generateCrop();
	}

	public static void registerCompostables() {
		// 30% chance
		ComposterBlock.CHANCES.put(ModItems.TREE_BARK, 0.3F);
		ComposterBlock.CHANCES.put(ModItems.STRAW, 0.3F);
		ComposterBlock.CHANCES.put(ModItems.CABBAGE_SEEDS, 0.3F);
		ComposterBlock.CHANCES.put(ModItems.TOMATO_SEEDS, 0.3F);
		ComposterBlock.CHANCES.put(ModItems.RICE, 0.65F);
		ComposterBlock.CHANCES.put(ModItems.RICE_PANICLE, 0.65F);

		// 50% chance
		ComposterBlock.CHANCES.put(ModItems.PUMPKIN_SLICE, 0.65F);
		ComposterBlock.CHANCES.put(ModItems.CABBAGE_LEAF, 0.65F);

		// 65% chance
		ComposterBlock.CHANCES.put(ModItems.CABBAGE, 0.65F);
		ComposterBlock.CHANCES.put(ModItems.ONION, 0.65F);
		ComposterBlock.CHANCES.put(ModItems.TOMATO, 0.65F);
		ComposterBlock.CHANCES.put(ModItems.WILD_CABBAGES, 0.65F);
		ComposterBlock.CHANCES.put(ModItems.WILD_ONIONS, 0.65F);
		ComposterBlock.CHANCES.put(ModItems.WILD_TOMATOES, 0.65F);
		ComposterBlock.CHANCES.put(ModItems.WILD_CARROTS, 0.65F);
		ComposterBlock.CHANCES.put(ModItems.WILD_POTATOES, 0.65F);
		ComposterBlock.CHANCES.put(ModItems.WILD_BEETROOTS, 0.65F);
		ComposterBlock.CHANCES.put(ModItems.WILD_RICE, 0.65F);
		ComposterBlock.CHANCES.put(ModItems.PIE_CRUST, 0.65F);

		// 85% chance
		ComposterBlock.CHANCES.put(ModItems.RICE_BALE, 0.85F);
		ComposterBlock.CHANCES.put(ModItems.SWEET_BERRY_COOKIE, 0.85F);
		ComposterBlock.CHANCES.put(ModItems.HONEY_COOKIE, 0.85F);
		ComposterBlock.CHANCES.put(ModItems.CAKE_SLICE, 0.85F);
		ComposterBlock.CHANCES.put(ModItems.APPLE_PIE_SLICE, 0.85F);
		ComposterBlock.CHANCES.put(ModItems.SWEET_BERRY_CHEESECAKE_SLICE, 0.85F);
		ComposterBlock.CHANCES.put(ModItems.CHOCOLATE_PIE_SLICE, 0.85F);
		ComposterBlock.CHANCES.put(ModItems.RAW_PASTA, 0.85F);

		// 100% chance
		ComposterBlock.CHANCES.put(ModItems.APPLE_PIE, 1.0F);
		ComposterBlock.CHANCES.put(ModItems.SWEET_BERRY_CHEESECAKE, 1.0F);
		ComposterBlock.CHANCES.put(ModItems.CHOCOLATE_PIE, 1.0F);
		ComposterBlock.CHANCES.put(ModItems.DUMPLINGS, 1.0F);
		ComposterBlock.CHANCES.put(ModItems.STUFFED_PUMPKIN, 1.0F);
	}

	public static void registerVillagerTrades() {
		if (!Configuration.FARMERS_BUY_FD_CROPS) return;

		Int2ObjectMap<VillagerTrades.ITrade[]> trades = VillagerTrades.field_221239_a.get(VillagerProfession.FARMER);
		if (trades == null) return;

		addTrades(trades, 1, new EmeraldForItemsTrade(ModItems.ONION, 26, 16, 2), new EmeraldForItemsTrade(ModItems.TOMATO, 26, 16, 2));
		addTrades(trades, 2, new EmeraldForItemsTrade(ModItems.CABBAGE, 16, 16, 5), new EmeraldForItemsTrade(ModItems.RICE, 20, 16, 5));
	}

	private static void addTrades(Int2ObjectMap<VillagerTrades.ITrade[]> trades, int level, VillagerTrades.ITrade... added) {
		VillagerTrades.ITrade[] existing = trades.get(level);
		if (existing == null) {
			trades.put(level, added);
			return;
		}

		VillagerTrades.ITrade[] merged = new VillagerTrades.ITrade[existing.length + added.length];
		System.arraycopy(existing, 0, merged, 0, existing.length);
		System.arraycopy(added, 0, merged, existing.length, added.length);
		trades.put(level, merged);
	}

	public static void onItemUseFinish(LivingEntity entity, ItemStack stack) {
		Item food = stack.getItem();

		// Adds 3:00 of Jump Boost II when eating Rabbit Stew
		if (Configuration.RABBIT_STEW_JUMP_BOOST && food.equals(Items.RABBIT_STEW)) {
			entity.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 3600, 1));
		}

		// Adds 5:00 of Comfort when eating foods inside the tag farmersdelight:comfort_foods
		if (Configuration.COMFORT_FOOD_TAG_EFFECT && food.isIn(ModTags.COMFORT_FOODS)) {
			entity.addPotionEffect(new EffectInstance(ModEffects.COMFORT, 6000, 0));
		}
	}

	static class EmeraldForItemsTrade implements VillagerTrades.ITrade {
		private final Item tradeItem;
		private final int count;
		private final int maxUses;
		private final int xpValue;
		private final float priceMultiplier;

		public EmeraldForItemsTrade(IItemProvider tradeItemIn, int countIn, int maxUsesIn, int xpValueIn) {
			this.tradeItem = tradeItemIn.asItem();
			this.count = countIn;
			this.maxUses = maxUsesIn;
			this.xpValue = xpValueIn;
			this.priceMultiplier = 0.05F;
		}

		public MerchantOffer getOffer(Entity trader, Random rand) {
			ItemStack itemstack = new ItemStack(this.tradeItem, this.count);
			return new MerchantOffer(itemstack, new ItemStack(Items.EMERALD), this.maxUses, this.xpValue, this.priceMultiplier);
		}
	}

	public static void onLootLoad(ResourceLocation name, LootTable table)
	{
		for (String entity : SCAVENGING_ENTITIES) {
			if (name.equals(new ResourceLocation("minecraft", "entities/" + entity))) {
				table.addPool(LootPool.builder().addEntry(TableLootEntry.builder(new ResourceLocation(FarmersDelight.MODID, "inject/" + entity))).build());
			}
		}

		if (Configuration.CROPS_ON_SHIPWRECKS && name.equals(SHIPWRECK_SUPPLY_CHEST)) {
			table.addPool(LootPool.builder().addEntry(TableLootEntry.builder(new ResourceLocation(FarmersDelight.MODID, "inject/shipwreck_supply")).weight(1).quality(0)).build());
		}

		if (Configuration.CROPS_ON_VILLAGE_HOUSES && VILLAGE_HOUSE_CHESTS.contains(name)) {
			table.addPool(LootPool.builder().addEntry(
							TableLootEntry.builder(new ResourceLocation(FarmersDelight.MODID, "inject/crops_villager_houses")).weight(1).quality(0)).build());
		}
	}
}
