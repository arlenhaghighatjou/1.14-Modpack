package vectorwing.farmersdelight.data;

import net.minecraft.tags.ItemTags;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.data.recipes.CuttingRecipes;
import vectorwing.farmersdelight.data.recipes.SmeltingRecipes;
import vectorwing.farmersdelight.registry.ModBlocks;
import vectorwing.farmersdelight.registry.ModItems;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.block.Blocks;
import net.minecraft.data.*;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import vectorwing.farmersdelight.utils.tags.ForgeTags;

import java.util.function.Consumer;

public class Recipes extends RecipeProvider
{
	public Recipes(DataGenerator generator)
	{
		super(generator);
	}

	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
		recipesVanillaAlternatives(consumer);
		recipesBlocks(consumer);
		recipesTools(consumer);
		recipesMaterials(consumer);
		recipesFoodstuffs(consumer);
		recipesFoodBlocks(consumer);
		recipesCraftedMeals(consumer);

		SmeltingRecipes.register(consumer);
		CuttingRecipes.register(consumer);
	}

	/**
	 * The following recipes should ALWAYS define a custom save location.
	 * If not, they fall on the minecraft namespace, overriding vanilla recipes instead of being alternatives.
	 */
	private void recipesVanillaAlternatives(Consumer<IFinishedRecipe> consumer) {
		ShapelessRecipeBuilder.shapelessRecipe(Items.PUMPKIN_SEEDS)
				.addIngredient(ModItems.PUMPKIN_SLICE)
				.addCriterion("has_pumpkin_slice", InventoryChangeTrigger.Instance.forItems(ModItems.PUMPKIN_SLICE))
				.build(consumer, new ResourceLocation(FarmersDelight.MODID, "pumpkin_seeds_from_slice"));
		ShapedRecipeBuilder.shapedRecipe(Items.SCAFFOLDING, 6)
				.patternLine("b#b")
				.patternLine("b b")
				.patternLine("b b")
				.key('b', Items.BAMBOO)
				.key('#', ModItems.CANVAS)
				.addCriterion("canvas", InventoryChangeTrigger.Instance.forItems(ModItems.CANVAS))
				.build(consumer, new ResourceLocation(FarmersDelight.MODID, "scaffolding_from_canvas"));
		ShapedRecipeBuilder.shapedRecipe(Items.LEAD)
				.patternLine("rr ")
				.patternLine("rr ")
				.patternLine("  r")
				.key('r', ModItems.ROPE)
				.addCriterion("rope", InventoryChangeTrigger.Instance.forItems(ModItems.ROPE))
				.build(consumer, new ResourceLocation(FarmersDelight.MODID, "lead_from_rope"));
		ShapedRecipeBuilder.shapedRecipe(Items.PAINTING)
				.patternLine("sss")
				.patternLine("scs")
				.patternLine("sss")
				.key('s', Items.STICK)
				.key('c', ModItems.CANVAS)
				.addCriterion("canvas", InventoryChangeTrigger.Instance.forItems(ModItems.CANVAS))
				.build(consumer, new ResourceLocation(FarmersDelight.MODID, "painting_from_canvas"));
		ShapedRecipeBuilder.shapedRecipe(Items.PUMPKIN)
				.patternLine("##")
				.patternLine("##")
				.key('#', ModItems.PUMPKIN_SLICE)
				.addCriterion("pumpkin_slice", InventoryChangeTrigger.Instance.forItems(ModItems.PUMPKIN_SLICE))
				.build(consumer, new ResourceLocation(FarmersDelight.MODID, "pumpkin_from_slices"));
		ShapedRecipeBuilder.shapedRecipe(Items.CAKE)
				.patternLine("mmm")
				.patternLine("ses")
				.patternLine("www")
				.key('m', ModItems.MILK_BOTTLE)
				.key('s', Items.SUGAR)
				.key('e', Items.EGG)
				.key('w', Items.WHEAT)
				.addCriterion("milk_bottle", InventoryChangeTrigger.Instance.forItems(ModItems.MILK_BOTTLE))
				.build(consumer, new ResourceLocation(FarmersDelight.MODID, "cake_from_milk_bottle"));
		ShapelessRecipeBuilder.shapelessRecipe(Items.CAKE)
				.addIngredient(ModItems.CAKE_SLICE)
				.addIngredient(ModItems.CAKE_SLICE)
				.addIngredient(ModItems.CAKE_SLICE)
				.addIngredient(ModItems.CAKE_SLICE)
				.addIngredient(ModItems.CAKE_SLICE)
				.addIngredient(ModItems.CAKE_SLICE)
				.addIngredient(ModItems.CAKE_SLICE)
				.addCriterion("cake_slice", InventoryChangeTrigger.Instance.forItems(ModItems.CAKE_SLICE))
				.build(consumer, new ResourceLocation(FarmersDelight.MODID, "cake_from_slices"));
		ShapelessRecipeBuilder.shapelessRecipe(Items.BOOK)
				.addIngredient(Items.PAPER)
				.addIngredient(Items.PAPER)
				.addIngredient(Items.PAPER)
				.addIngredient(ModItems.CANVAS)
				.addCriterion("canvas", InventoryChangeTrigger.Instance.forItems(ModItems.CANVAS))
				.build(consumer, new ResourceLocation(FarmersDelight.MODID, "book_from_canvas"));
		ShapelessRecipeBuilder.shapelessRecipe(Items.MILK_BUCKET)
				.addIngredient(Items.BUCKET)
				.addIngredient(ModItems.MILK_BOTTLE)
				.addIngredient(ModItems.MILK_BOTTLE)
				.addIngredient(ModItems.MILK_BOTTLE)
				.addCriterion("has_milk_bottle", InventoryChangeTrigger.Instance.forItems(ModItems.MILK_BOTTLE))
				.build(consumer, new ResourceLocation(FarmersDelight.MODID, "milk_bucket_from_bottles"));
	}

	private void recipesBlocks(Consumer<IFinishedRecipe> consumer) {
		ShapedRecipeBuilder.shapedRecipe(ModBlocks.STOVE)
				.patternLine("iii")
				.patternLine("B B")
				.patternLine("BCB")
				.key('i', Items.IRON_INGOT)
				.key('B', Blocks.BRICKS)
				.key('C', Blocks.CAMPFIRE)
				.addCriterion("campfire", InventoryChangeTrigger.Instance.forItems(Blocks.CAMPFIRE))
				.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(ModBlocks.COOKING_POT)
				.patternLine("bSb")
				.patternLine("iWi")
				.patternLine("iii")
				.key('b', Items.BRICK)
				.key('i', Items.IRON_INGOT)
				.key('S', Items.WOODEN_SHOVEL)
				.key('W', Items.WATER_BUCKET)
				.addCriterion("iron_ingot", InventoryChangeTrigger.Instance.forItems(Items.IRON_INGOT))
				.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(ModBlocks.BASKET)
				.patternLine("b b")
				.patternLine("# #")
				.patternLine("b#b")
				.key('b', Items.BAMBOO)
				.key('#', ModItems.CANVAS)
				.addCriterion("canvas", InventoryChangeTrigger.Instance.forItems(ModItems.CANVAS))
				.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(ModBlocks.CUTTING_BOARD)
				.patternLine("/##")
				.patternLine("/##")
				.key('/', Items.STICK)
				.key('#', ItemTags.PLANKS)
				.addCriterion("stick", InventoryChangeTrigger.Instance.forItems(Items.STICK))
				.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(ModBlocks.OAK_PANTRY)
				.patternLine("___")
				.patternLine("D D")
				.patternLine("___")
				.key('_', Items.OAK_SLAB)
				.key('D', Items.OAK_TRAPDOOR)
				.addCriterion("oak_trapdoor", InventoryChangeTrigger.Instance.forItems(Items.OAK_TRAPDOOR))
				.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(ModBlocks.BIRCH_PANTRY)
				.patternLine("___")
				.patternLine("D D")
				.patternLine("___")
				.key('_', Items.BIRCH_SLAB)
				.key('D', Items.BIRCH_TRAPDOOR)
				.addCriterion("birch_trapdoor", InventoryChangeTrigger.Instance.forItems(Items.BIRCH_TRAPDOOR))
				.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(ModBlocks.SPRUCE_PANTRY)
				.patternLine("___")
				.patternLine("D D")
				.patternLine("___")
				.key('_', Items.SPRUCE_SLAB)
				.key('D', Items.SPRUCE_TRAPDOOR)
				.addCriterion("spruce_trapdoor", InventoryChangeTrigger.Instance.forItems(Items.SPRUCE_TRAPDOOR))
				.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(ModBlocks.JUNGLE_PANTRY)
				.patternLine("___")
				.patternLine("D D")
				.patternLine("___")
				.key('_', Items.JUNGLE_SLAB)
				.key('D', Items.JUNGLE_TRAPDOOR)
				.addCriterion("jungle_trapdoor", InventoryChangeTrigger.Instance.forItems(Items.JUNGLE_TRAPDOOR))
				.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(ModBlocks.ACACIA_PANTRY)
				.patternLine("___")
				.patternLine("D D")
				.patternLine("___")
				.key('_', Items.ACACIA_SLAB)
				.key('D', Items.ACACIA_TRAPDOOR)
				.addCriterion("acacia_trapdoor", InventoryChangeTrigger.Instance.forItems(Items.ACACIA_TRAPDOOR))
				.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(ModBlocks.DARK_OAK_PANTRY)
				.patternLine("___")
				.patternLine("D D")
				.patternLine("___")
				.key('_', Items.DARK_OAK_SLAB)
				.key('D', Items.DARK_OAK_TRAPDOOR)
				.addCriterion("dark_oak_trapdoor", InventoryChangeTrigger.Instance.forItems(Items.DARK_OAK_TRAPDOOR))
				.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(ModItems.ROPE, 3)
				.patternLine("s")
				.patternLine("s")
				.patternLine("s")
				.key('s', ModItems.STRAW)
				.addCriterion("straw", InventoryChangeTrigger.Instance.forItems(ModItems.STRAW))
				.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(ModItems.SAFETY_NET, 1)
				.patternLine("rr")
				.patternLine("rr")
				.key('r', ModItems.ROPE)
				.addCriterion("rope", InventoryChangeTrigger.Instance.forItems(ModItems.ROPE))
				.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(ModItems.CABBAGE_CRATE, 1)
				.patternLine("###")
				.patternLine("###")
				.patternLine("###")
				.key('#', ModItems.CABBAGE)
				.addCriterion("cabbage", InventoryChangeTrigger.Instance.forItems(ModItems.CABBAGE))
				.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(ModItems.TOMATO_CRATE, 1)
				.patternLine("###")
				.patternLine("###")
				.patternLine("###")
				.key('#', ModItems.TOMATO)
				.addCriterion("tomato", InventoryChangeTrigger.Instance.forItems(ModItems.TOMATO))
				.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(ModItems.ONION_CRATE, 1)
				.patternLine("###")
				.patternLine("###")
				.patternLine("###")
				.key('#', ModItems.ONION)
				.addCriterion("onion", InventoryChangeTrigger.Instance.forItems(ModItems.ONION))
				.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(ModItems.RICE_BALE, 1)
				.patternLine("###")
				.patternLine("###")
				.patternLine("###")
				.key('#', ModItems.RICE_PANICLE)
				.addCriterion("rice_panicle", InventoryChangeTrigger.Instance.forItems(ModItems.RICE_PANICLE))
				.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(ModItems.ORGANIC_COMPOST, 1)
				.addIngredient(Items.DIRT)
				.addIngredient(Items.ROTTEN_FLESH)
				.addIngredient(Items.ROTTEN_FLESH)
				.addIngredient(ModItems.STRAW)
				.addIngredient(ModItems.STRAW)
				.addIngredient(Items.BONE_MEAL)
				.addIngredient(Items.BONE_MEAL)
				.addIngredient(Items.BONE_MEAL)
				.addIngredient(Items.BONE_MEAL)
				.addCriterion("rotten_flesh", InventoryChangeTrigger.Instance.forItems(Items.ROTTEN_FLESH))
				.addCriterion("straw", InventoryChangeTrigger.Instance.forItems(ModItems.STRAW))
				.build(consumer, new ResourceLocation(FarmersDelight.MODID, "organic_compost_from_rotten_flesh"));
		ShapelessRecipeBuilder.shapelessRecipe(ModItems.ORGANIC_COMPOST, 1)
				.addIngredient(Items.DIRT)
				.addIngredient(ModItems.STRAW)
				.addIngredient(ModItems.STRAW)
				.addIngredient(Items.BONE_MEAL)
				.addIngredient(Items.BONE_MEAL)
				.addIngredient(ModItems.TREE_BARK)
				.addIngredient(ModItems.TREE_BARK)
				.addIngredient(ModItems.TREE_BARK)
				.addIngredient(ModItems.TREE_BARK)
				.addCriterion("tree_bark", InventoryChangeTrigger.Instance.forItems(ModItems.TREE_BARK))
				.addCriterion("straw", InventoryChangeTrigger.Instance.forItems(ModItems.STRAW))
				.build(consumer, new ResourceLocation(FarmersDelight.MODID, "organic_compost_from_tree_bark"));
		ShapedRecipeBuilder.shapedRecipe(ModItems.TATAMI, 2)
				.patternLine("cs")
				.patternLine("sc")
				.key('c', ModItems.CANVAS)
				.key('s', ModItems.STRAW)
				.addCriterion("has_canvas", InventoryChangeTrigger.Instance.forItems(ModItems.CANVAS))
				.build(consumer);

		// BREAKING DOWN
		ShapelessRecipeBuilder.shapelessRecipe(ModItems.FULL_TATAMI_MAT, 2)
				.addIngredient(ModItems.TATAMI)
				.addCriterion("has_canvas", InventoryChangeTrigger.Instance.forItems(ModItems.CANVAS))
				.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(ModItems.HALF_TATAMI_MAT, 2)
				.addIngredient(ModItems.FULL_TATAMI_MAT)
				.addCriterion("has_canvas", InventoryChangeTrigger.Instance.forItems(ModItems.CANVAS))
				.build(consumer);

		// COMBINING BACK
		ShapelessRecipeBuilder.shapelessRecipe(ModItems.FULL_TATAMI_MAT, 1)
				.addIngredient(ModItems.HALF_TATAMI_MAT)
				.addIngredient(ModItems.HALF_TATAMI_MAT)
				.addCriterion("has_canvas", InventoryChangeTrigger.Instance.forItems(ModItems.CANVAS))
				.build(consumer, new ResourceLocation(FarmersDelight.MODID, "full_tatami_mat_from_halves"));
		ShapelessRecipeBuilder.shapelessRecipe(ModItems.TATAMI, 1)
				.addIngredient(ModItems.FULL_TATAMI_MAT)
				.addIngredient(ModItems.FULL_TATAMI_MAT)
				.addCriterion("has_canvas", InventoryChangeTrigger.Instance.forItems(ModItems.CANVAS))
				.build(consumer, new ResourceLocation(FarmersDelight.MODID, "tatami_block_from_full"));
	}

	private void recipesTools(Consumer<IFinishedRecipe> consumer) {
		ShapedRecipeBuilder.shapedRecipe(ModItems.FLINT_KNIFE)
				.patternLine(" m")
				.patternLine("s ")
				.key('m', Items.FLINT)
				.key('s', Items.STICK)
				.addCriterion("stick", InventoryChangeTrigger.Instance.forItems(Items.STICK))
				.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(ModItems.IRON_KNIFE)
				.patternLine(" m")
				.patternLine("s ")
				.key('m', Items.IRON_INGOT)
				.key('s', Items.STICK)
				.addCriterion("iron_ingot", InventoryChangeTrigger.Instance.forItems(Items.IRON_INGOT))
				.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(ModItems.DIAMOND_KNIFE)
				.patternLine(" m")
				.patternLine("s ")
				.key('m', Items.DIAMOND)
				.key('s', Items.STICK)
				.addCriterion("diamond", InventoryChangeTrigger.Instance.forItems(Items.DIAMOND))
				.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(ModItems.GOLDEN_KNIFE)
				.patternLine(" m")
				.patternLine("s ")
				.key('m', Items.GOLD_INGOT)
				.key('s', Items.STICK)
				.addCriterion("gold_ingot", InventoryChangeTrigger.Instance.forItems(Items.GOLD_INGOT))
				.build(consumer);
	}

	private void recipesMaterials(Consumer<IFinishedRecipe> consumer) {
		ShapedRecipeBuilder.shapedRecipe(ModItems.CANVAS)
				.patternLine("##")
				.patternLine("##")
				.key('#', ModItems.STRAW)
				.addCriterion("straw", InventoryChangeTrigger.Instance.forItems(ModItems.STRAW))
				.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(ModItems.CABBAGE, 9)
				.addIngredient(ModItems.CABBAGE_CRATE)
				.addCriterion("cabbage_crate", InventoryChangeTrigger.Instance.forItems(ModItems.CABBAGE_CRATE))
				.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(ModItems.TOMATO, 9)
				.addIngredient(ModItems.TOMATO_CRATE)
				.addCriterion("tomato_crate", InventoryChangeTrigger.Instance.forItems(ModItems.TOMATO_CRATE))
				.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(ModItems.ONION, 9)
				.addIngredient(ModItems.ONION_CRATE)
				.addCriterion("onion_crate", InventoryChangeTrigger.Instance.forItems(ModItems.ONION_CRATE))
				.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(ModItems.RICE_PANICLE, 9)
				.addIngredient(ModItems.RICE_BALE)
				.addCriterion("rice_bale", InventoryChangeTrigger.Instance.forItems(ModItems.RICE_BALE))
				.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(ModItems.RICE)
				.addIngredient(ModItems.RICE_PANICLE)
				.addCriterion("rice_panicle", InventoryChangeTrigger.Instance.forItems(ModItems.RICE_PANICLE))
				.build(consumer);
	}

	private void recipesFoodstuffs(Consumer<IFinishedRecipe> consumer) {
		ShapelessRecipeBuilder.shapelessRecipe(ModItems.TOMATO_SEEDS)
				.addIngredient(ModItems.TOMATO)
				.addCriterion("has_tomato", InventoryChangeTrigger.Instance.forItems(ModItems.TOMATO))
				.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(ModItems.MILK_BOTTLE, 3)
				.addIngredient(Items.MILK_BUCKET)
				.addIngredient(Items.GLASS_BOTTLE)
				.addIngredient(Items.GLASS_BOTTLE)
				.addIngredient(Items.GLASS_BOTTLE)
				.addCriterion("has_milk_bucket", InventoryChangeTrigger.Instance.forItems(Items.MILK_BUCKET))
				.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(ModItems.RAW_PASTA)
				.addIngredient(Items.EGG)
				.addIngredient(Items.WHEAT)
				.addIngredient(Items.WHEAT)
				.addCriterion("has_egg", InventoryChangeTrigger.Instance.forItems(Items.EGG))
				.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(ModItems.PIE_CRUST, 1)
				.patternLine("wMw")
				.patternLine(" w ")
				.key('w', Items.WHEAT)
				.key('M', ForgeTags.MILK)
				.addCriterion("has_wheat", InventoryChangeTrigger.Instance.forItems(Items.WHEAT))
				.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(ModItems.SWEET_BERRY_COOKIE, 8)
				.addIngredient(Items.SWEET_BERRIES)
				.addIngredient(Items.WHEAT)
				.addIngredient(Items.WHEAT)
				.addCriterion("has_sweet_berries", InventoryChangeTrigger.Instance.forItems(Items.SWEET_BERRIES))
				.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(ModItems.HONEY_COOKIE, 8)
				.addIngredient(Items.HONEY_BOTTLE)
				.addIngredient(Items.WHEAT)
				.addIngredient(Items.WHEAT)
				.addCriterion("has_honey_bottle", InventoryChangeTrigger.Instance.forItems(Items.HONEY_BOTTLE))
				.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(ModItems.CABBAGE)
				.addIngredient(ModItems.CABBAGE_LEAF)
				.addIngredient(ModItems.CABBAGE_LEAF)
				.addCriterion("has_cabbage_leaf", InventoryChangeTrigger.Instance.forItems(ModItems.CABBAGE_LEAF))
				.build(consumer, new ResourceLocation(FarmersDelight.MODID, "cabbage_from_leaves"));
		ShapelessRecipeBuilder.shapelessRecipe(ModItems.HORSE_FEED, 1)
				.addIngredient(Ingredient.fromItems(Items.HAY_BLOCK, ModItems.RICE_BALE))
				.addIngredient(Items.APPLE)
				.addIngredient(Items.APPLE)
				.addIngredient(Items.GOLDEN_CARROT)
				.addCriterion("has_golden_carrot", InventoryChangeTrigger.Instance.forItems(Items.GOLDEN_CARROT))
				.build(consumer);
	}

	private void recipesFoodBlocks(Consumer<IFinishedRecipe> consumer) {
		ShapedRecipeBuilder.shapedRecipe(ModItems.APPLE_PIE, 1)
				.patternLine("###")
				.patternLine("aaa")
				.patternLine("xOx")
				.key('#', Items.WHEAT)
				.key('a', Items.APPLE)
				.key('x', Items.SUGAR)
				.key('O', ModItems.PIE_CRUST)
				.addCriterion("pie_crust", InventoryChangeTrigger.Instance.forItems(ModItems.PIE_CRUST))
				.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(ModItems.APPLE_PIE, 1)
				.patternLine("##")
				.patternLine("##")
				.key('#', ModItems.APPLE_PIE_SLICE)
				.addCriterion("apple_pie_slice", InventoryChangeTrigger.Instance.forItems(ModItems.APPLE_PIE_SLICE))
				.build(consumer, new ResourceLocation(FarmersDelight.MODID, "apple_pie_from_slices"));
		ShapedRecipeBuilder.shapedRecipe(ModItems.SWEET_BERRY_CHEESECAKE, 1)
				.patternLine("sss")
				.patternLine("sss")
				.patternLine("mOm")
				.key('s', Items.SWEET_BERRIES)
				.key('m', ForgeTags.MILK)
				.key('O', ModItems.PIE_CRUST)
				.addCriterion("pie_crust", InventoryChangeTrigger.Instance.forItems(ModItems.PIE_CRUST))
				.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(ModItems.SWEET_BERRY_CHEESECAKE, 1)
				.patternLine("##")
				.patternLine("##")
				.key('#', ModItems.SWEET_BERRY_CHEESECAKE_SLICE)
				.addCriterion("sweet_berry_cheesecake_slice", InventoryChangeTrigger.Instance.forItems(ModItems.SWEET_BERRY_CHEESECAKE_SLICE))
				.build(consumer, new ResourceLocation(FarmersDelight.MODID, "sweet_berry_cheesecake_from_slices"));
		ShapedRecipeBuilder.shapedRecipe(ModItems.CHOCOLATE_PIE, 1)
				.patternLine("ccc")
				.patternLine("mmm")
				.patternLine("xOx")
				.key('c', Items.COCOA_BEANS)
				.key('m', ForgeTags.MILK)
				.key('x', Items.SUGAR)
				.key('O', ModItems.PIE_CRUST)
				.addCriterion("pie_crust", InventoryChangeTrigger.Instance.forItems(ModItems.PIE_CRUST))
				.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(ModItems.CHOCOLATE_PIE, 1)
				.patternLine("##")
				.patternLine("##")
				.key('#', ModItems.CHOCOLATE_PIE_SLICE)
				.addCriterion("chocolate_pie_slice", InventoryChangeTrigger.Instance.forItems(ModItems.CHOCOLATE_PIE_SLICE))
				.build(consumer, new ResourceLocation(FarmersDelight.MODID, "chocolate_pie_from_slices"));
	}

	private void recipesCraftedMeals(Consumer<IFinishedRecipe> consumer) {
		ShapelessRecipeBuilder.shapelessRecipe(ModItems.MIXED_SALAD)
				.addIngredient(ForgeTags.SALAD_INGREDIENTS)
				.addIngredient(ForgeTags.CROPS_TOMATO)
				.addIngredient(ForgeTags.CROPS_ONION)
				.addIngredient(Items.BEETROOT)
				.addIngredient(Items.BOWL)
				.addCriterion("has_bowl", InventoryChangeTrigger.Instance.forItems(Items.BOWL))
				.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(ModItems.BARBECUE_STICK, 2)
				.addIngredient(ForgeTags.CROPS_TOMATO)
				.addIngredient(ForgeTags.CROPS_ONION)
				.addIngredient(Items.COOKED_BEEF)
				.addIngredient(Items.COOKED_CHICKEN)
				.addIngredient(Items.STICK)
				.addIngredient(Items.STICK)
				.addCriterion("barbecue", InventoryChangeTrigger.Instance.forItems(Items.COOKED_BEEF))
				.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(ModItems.EGG_SANDWICH)
				.addIngredient(ForgeTags.BREAD)
				.addIngredient(ForgeTags.COOKED_EGGS)
				.addIngredient(ForgeTags.COOKED_EGGS)
				.addCriterion("fried_egg", InventoryChangeTrigger.Instance.forItems(ModItems.FRIED_EGG))
				.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(ModItems.CHICKEN_SANDWICH)
				.addIngredient(ForgeTags.BREAD)
				.addIngredient(ForgeTags.COOKED_CHICKEN)
				.addIngredient(ForgeTags.SALAD_INGREDIENTS)
				.addIngredient(ForgeTags.CROPS_TOMATO)
				.addCriterion("cooked_chicken", InventoryChangeTrigger.Instance.forItems(Items.COOKED_CHICKEN))
				.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(ModItems.HAMBURGER)
				.addIngredient(ForgeTags.BREAD)
				.addIngredient(ModItems.BEEF_PATTY)
				.addIngredient(ForgeTags.SALAD_INGREDIENTS)
				.addIngredient(ForgeTags.CROPS_TOMATO)
				.addIngredient(ForgeTags.CROPS_ONION)
				.addCriterion("hamburger", InventoryChangeTrigger.Instance.forItems(Items.COOKED_BEEF))
				.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(ModItems.STUFFED_POTATO)
				.addIngredient(Items.BAKED_POTATO)
				.addIngredient(ForgeTags.COOKED_BEEF)
				.addIngredient(Items.CARROT)
				.addIngredient(ForgeTags.MILK)
				.addCriterion("baked_potato", InventoryChangeTrigger.Instance.forItems(Items.BAKED_POTATO))
				.build(consumer);
	}
}
