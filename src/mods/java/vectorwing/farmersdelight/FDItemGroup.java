package vectorwing.farmersdelight;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import vectorwing.farmersdelight.registry.ModBlocks;
import vectorwing.farmersdelight.registry.ModItems;

public class FDItemGroup extends ItemGroup
{
	public FDItemGroup(String label)
	{
		super(label);
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(ModBlocks.STOVE);
	}

//	@Override
//	public void fill(NonNullList<ItemStack> items)
//	{
//		registerWorkstations(items);
//		registerTools(items);
//		registerCrops(items);
//		registerMaterials(items);
//		registerIngredients(items);
//		registerMeals(items);
//		registerPetMeals(items);
//	}

	private void registerWorkstations(NonNullList<ItemStack> items)
	{
		items.add(new ItemStack(ModBlocks.STOVE));
		items.add(new ItemStack(ModBlocks.COOKING_POT));
		items.add(new ItemStack(ModBlocks.BASKET));
		items.add(new ItemStack(ModBlocks.ROPE));
		items.add(new ItemStack(ModBlocks.SAFETY_NET));
	}

	private void registerCrops(NonNullList<ItemStack> items)
	{
		items.add(new ItemStack(ModItems.WILD_CABBAGES));
		items.add(new ItemStack(ModItems.WILD_ONIONS));
		items.add(new ItemStack(ModItems.WILD_TOMATOES));
		items.add(new ItemStack(ModItems.WILD_CARROTS));
		items.add(new ItemStack(ModItems.WILD_POTATOES));
		items.add(new ItemStack(ModItems.WILD_BEETROOTS));
		items.add(new ItemStack(ModItems.CABBAGE));
		items.add(new ItemStack(ModItems.TOMATO));
		items.add(new ItemStack(ModItems.ONION));
		items.add(new ItemStack(ModItems.RICE));
		items.add(new ItemStack(ModItems.CABBAGE_SEEDS));
		items.add(new ItemStack(ModItems.TOMATO_SEEDS));
	}

	private void registerIngredients(NonNullList<ItemStack> items) {
		items.add(new ItemStack(ModItems.FRIED_EGG));
		items.add(new ItemStack(ModItems.MILK_BOTTLE));
		items.add(new ItemStack(ModItems.TOMATO_SAUCE));
		items.add(new ItemStack(ModItems.RAW_PASTA));
		items.add(new ItemStack(ModItems.CAKE_SLICE));
		items.add(new ItemStack(ModItems.SWEET_BERRY_COOKIE));
		items.add(new ItemStack(ModItems.HONEY_COOKIE));
	}

	private void registerTools(NonNullList<ItemStack> items) {
		items.add(new ItemStack(ModItems.FLINT_KNIFE));
		items.add(new ItemStack(ModItems.IRON_KNIFE));
		items.add(new ItemStack(ModItems.DIAMOND_KNIFE));
		items.add(new ItemStack(ModItems.GOLDEN_KNIFE));
	}

	private void registerMaterials(NonNullList<ItemStack> items) {
		items.add(new ItemStack(ModItems.STRAW));
		items.add(new ItemStack(ModItems.CANVAS));
	}

	private void registerMeals(NonNullList<ItemStack> items) {
		items.add(new ItemStack(ModItems.MIXED_SALAD));
		items.add(new ItemStack(ModItems.BARBECUE_STICK));
		items.add(new ItemStack(ModItems.CHICKEN_SANDWICH));
		items.add(new ItemStack(ModItems.HAMBURGER));
		items.add(new ItemStack(ModItems.BEEF_STEW));
		items.add(new ItemStack(ModItems.CHICKEN_SOUP));
		items.add(new ItemStack(ModItems.VEGETABLE_SOUP));
		items.add(new ItemStack(ModItems.FISH_STEW));
		items.add(new ItemStack(ModItems.FRIED_RICE));
		items.add(new ItemStack(ModItems.HONEY_GLAZED_HAM));
		items.add(new ItemStack(ModItems.PASTA_WITH_MEATBALLS));
		items.add(new ItemStack(ModItems.PASTA_WITH_MUTTON_CHOP));
		items.add(new ItemStack(ModItems.VEGETABLE_NOODLES));
		items.add(new ItemStack(ModItems.STEAK_AND_POTATOES));
		items.add(new ItemStack(ModItems.SHEPHERDS_PIE));
	}

	private void registerPetMeals(NonNullList<ItemStack> items) {
		items.add(new ItemStack(ModItems.DOG_FOOD));
	}
}
