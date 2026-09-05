package vectorwing.farmersdelight.registry;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.blocks.*;

public class ModBlocks
{
	public static Block STOVE;
	public static Block COOKING_POT;
	public static Block BASKET;
	public static Block CUTTING_BOARD;
	public static Block CABBAGE_CRATE;
	public static Block TOMATO_CRATE;
	public static Block ONION_CRATE;
	public static Block RICE_BALE;
	public static Block SAFETY_NET;
	public static Block OAK_PANTRY;
	public static Block BIRCH_PANTRY;
	public static Block SPRUCE_PANTRY;
	public static Block JUNGLE_PANTRY;
	public static Block ACACIA_PANTRY;
	public static Block DARK_OAK_PANTRY;
	public static Block TATAMI;
	public static Block FULL_TATAMI_MAT;
	public static Block HALF_TATAMI_MAT;
	public static Block ORGANIC_COMPOST;
	public static Block RICH_SOIL;
	public static Block RICH_SOIL_FARMLAND;
	public static Block BROWN_MUSHROOM_COLONY;
	public static Block RED_MUSHROOM_COLONY;
	public static Block ROPE;
	public static Block APPLE_PIE;
	public static Block SWEET_BERRY_CHEESECAKE;
	public static Block CHOCOLATE_PIE;
	public static Block WILD_CABBAGES;
	public static Block WILD_ONIONS;
	public static Block WILD_TOMATOES;
	public static Block WILD_CARROTS;
	public static Block WILD_POTATOES;
	public static Block WILD_BEETROOTS;
	public static Block WILD_RICE;
	public static Block CABBAGE_CROP;
	public static Block ONION_CROP;
	public static Block TOMATO_CROP;
	public static Block RICE_CROP;
	public static Block RICE_UPPER_CROP;
	@Deprecated
	public static Block TALL_RICE_CROP;

	public static void registerBlocks()
	{
		STOVE = registerBlock("stove", new StoveBlock());
		COOKING_POT = registerBlock("cooking_pot", new CookingPotBlock());
		BASKET = registerBlock("basket", new BasketBlock());
		CUTTING_BOARD = registerBlock("cutting_board", new CuttingBoardBlock());
		CABBAGE_CRATE = registerBlock("cabbage_crate", new Block(Block.Properties.create(Material.WOOD).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
		TOMATO_CRATE = registerBlock("tomato_crate", new Block(Block.Properties.create(Material.WOOD).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
		ONION_CRATE = registerBlock("onion_crate", new Block(Block.Properties.create(Material.WOOD).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
		RICE_BALE = registerBlock("rice_bale", new RiceBaleBlock(Block.Properties.from(Blocks.HAY_BLOCK)));
		SAFETY_NET = registerBlock("safety_net", new SafetyNetBlock());
		OAK_PANTRY = registerBlock("oak_pantry", new PantryBlock(Block.Properties.from(Blocks.BARREL)));
		BIRCH_PANTRY = registerBlock("birch_pantry", new PantryBlock(Block.Properties.from(Blocks.BARREL)));
		SPRUCE_PANTRY = registerBlock("spruce_pantry", new PantryBlock(Block.Properties.from(Blocks.BARREL)));
		JUNGLE_PANTRY = registerBlock("jungle_pantry", new PantryBlock(Block.Properties.from(Blocks.BARREL)));
		ACACIA_PANTRY = registerBlock("acacia_pantry", new PantryBlock(Block.Properties.from(Blocks.BARREL)));
		DARK_OAK_PANTRY = registerBlock("dark_oak_pantry", new PantryBlock(Block.Properties.from(Blocks.BARREL)));
		TATAMI = registerBlock("tatami", new TatamiBlock());
		FULL_TATAMI_MAT = registerBlock("full_tatami_mat", new TatamiMatBlock());
		HALF_TATAMI_MAT = registerBlock("half_tatami_mat", new TatamiHalfMatBlock());
		ORGANIC_COMPOST = registerBlock("organic_compost", new OrganicCompostBlock(Block.Properties.from(Blocks.DIRT)));
		RICH_SOIL = registerBlock("rich_soil", new RichSoilBlock(Block.Properties.from(Blocks.DIRT).tickRandomly()));
		RICH_SOIL_FARMLAND = registerBlock("rich_soil_farmland", new RichSoilFarmlandBlock(Block.Properties.from(Blocks.FARMLAND)));
		BROWN_MUSHROOM_COLONY = registerBlock("brown_mushroom_colony", new MushroomColonyBlock(Block.Properties.from(Blocks.BROWN_MUSHROOM), () -> Items.BROWN_MUSHROOM));
		RED_MUSHROOM_COLONY = registerBlock("red_mushroom_colony", new MushroomColonyBlock(Block.Properties.from(Blocks.RED_MUSHROOM), () -> Items.RED_MUSHROOM));
		ROPE = registerBlock("rope", new RopeBlock());
		APPLE_PIE = registerBlock("apple_pie", new PieBlock(Block.Properties.from(Blocks.CAKE), () -> ModItems.APPLE_PIE_SLICE));
		SWEET_BERRY_CHEESECAKE = registerBlock("sweet_berry_cheesecake", new PieBlock(Block.Properties.from(Blocks.CAKE), () -> ModItems.SWEET_BERRY_CHEESECAKE_SLICE));
		CHOCOLATE_PIE = registerBlock("chocolate_pie", new PieBlock(Block.Properties.from(Blocks.CAKE), () -> ModItems.CHOCOLATE_PIE_SLICE));
		WILD_CABBAGES = registerBlock("wild_cabbages", new WildPatchBlock(Block.Properties.from(Blocks.TALL_GRASS)));
		WILD_ONIONS = registerBlock("wild_onions", new WildCropsBlock(Block.Properties.from(Blocks.TALL_GRASS)));
		WILD_TOMATOES = registerBlock("wild_tomatoes", new WildPatchBlock(Block.Properties.from(Blocks.TALL_GRASS)));
		WILD_CARROTS = registerBlock("wild_carrots", new WildCropsBlock(Block.Properties.from(Blocks.TALL_GRASS)));
		WILD_POTATOES = registerBlock("wild_potatoes", new WildPatchBlock(Block.Properties.from(Blocks.TALL_GRASS)));
		WILD_BEETROOTS = registerBlock("wild_beetroots", new WildPatchBlock(Block.Properties.from(Blocks.TALL_GRASS)));
		WILD_RICE = registerBlock("wild_rice", new WildRiceBlock(Block.Properties.from(Blocks.TALL_GRASS)));
		CABBAGE_CROP = registerBlock("cabbages", new CabbagesBlock(Block.Properties.from(Blocks.WHEAT)));
		ONION_CROP = registerBlock("onions", new OnionsBlock(Block.Properties.from(Blocks.WHEAT)));
		TOMATO_CROP = registerBlock("tomatoes", new TomatoesBlock(Block.Properties.from(Blocks.WHEAT)));
		RICE_CROP = registerBlock("rice_crop", new RiceCropBlock(Block.Properties.from(Blocks.WHEAT)));
		RICE_UPPER_CROP = registerBlock("rice_upper_crop", new RiceUpperCropBlock(Block.Properties.from(Blocks.WHEAT)));
		TALL_RICE_CROP = registerBlock("tall_rice_crop", new LegacyTallRiceCropBlock(Block.Properties.from(Blocks.WHEAT)));

		Block.addDirtBlock(RICH_SOIL);
	}

	public static Block registerBlock(String name, Block block)
	{
		Registry.register(Registry.BLOCK, new ResourceLocation(FarmersDelight.MODID, name), block);
		for (BlockState state : block.getStateContainer().getValidStates())
		{
			state.func_215692_c();
			Block.BLOCK_STATE_IDS.add(state);
		}
		return block;
	}
}
