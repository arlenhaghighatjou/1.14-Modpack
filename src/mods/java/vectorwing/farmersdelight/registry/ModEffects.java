package vectorwing.farmersdelight.registry;

import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.effects.ComfortEffect;
import vectorwing.farmersdelight.effects.NourishedEffect;

public class ModEffects
{
	public static Effect NOURISHED;
	public static Effect COMFORT;

	public static void registerEffects()
	{
		NOURISHED = Registry.register(Registry.EFFECTS, new ResourceLocation(FarmersDelight.MODID, "nourished"), new NourishedEffect());
		COMFORT = Registry.register(Registry.EFFECTS, new ResourceLocation(FarmersDelight.MODID, "comfort"), new ComfortEffect());
	}
}
