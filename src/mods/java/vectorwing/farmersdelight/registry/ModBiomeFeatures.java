package vectorwing.farmersdelight.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.BushConfig;
import net.minecraft.world.gen.feature.Feature;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.world.features.RiceCropFeature;

public class ModBiomeFeatures
{
	public static Feature<BushConfig> RICE;

	public static void registerFeatures()
	{
		RICE = Registry.register(Registry.FEATURE, new ResourceLocation(FarmersDelight.MODID, "rice"), new RiceCropFeature(BushConfig::deserialize));
	}
}
