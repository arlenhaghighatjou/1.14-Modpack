package vectorwing.farmersdelight.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.crafting.CuttingBoardRecipe;

public class ModRecipeSerializers
{
	public static void registerRecipeSerializers()
	{
		Registry.register(Registry.RECIPE_SERIALIZER, new ResourceLocation(FarmersDelight.MODID, "cooking"), CookingPotRecipe.SERIALIZER);
		Registry.register(Registry.RECIPE_SERIALIZER, new ResourceLocation(FarmersDelight.MODID, "cutting"), CuttingBoardRecipe.SERIALIZER);
	}
}
