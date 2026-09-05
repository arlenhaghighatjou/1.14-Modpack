package vectorwing.farmersdelight.registry;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.tile.container.CookingPotContainer;

public class ModContainerTypes
{
	public static ContainerType<CookingPotContainer> COOKING_POT;

	public static void registerContainerTypes()
	{
		COOKING_POT = Registry.register(Registry.MENU, new ResourceLocation(FarmersDelight.MODID, "cooking_pot"), new ContainerType<>(CookingPotContainer::new));
	}
}
