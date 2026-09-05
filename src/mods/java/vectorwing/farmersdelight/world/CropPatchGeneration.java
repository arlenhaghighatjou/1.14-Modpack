package vectorwing.farmersdelight.world;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.BushConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.Placement;
import vectorwing.farmersdelight.registry.ModBiomeFeatures;
import vectorwing.farmersdelight.registry.ModBlocks;
import vectorwing.farmersdelight.setup.Configuration;

public class CropPatchGeneration
{
	public static BushConfig CABBAGE_PATCH_CONFIG;
	public static BushConfig ONION_PATCH_CONFIG;
	public static BushConfig TOMATO_PATCH_CONFIG;
	public static BushConfig CARROT_PATCH_CONFIG;
	public static BushConfig POTATO_PATCH_CONFIG;
	public static BushConfig BEETROOT_PATCH_CONFIG;
	public static BushConfig RICE_PATCH_CONFIG;

	public static void generateCrop()
	{
		CABBAGE_PATCH_CONFIG = new BushConfig(ModBlocks.WILD_CABBAGES.getDefaultState());
		ONION_PATCH_CONFIG = new BushConfig(ModBlocks.WILD_ONIONS.getDefaultState());
		TOMATO_PATCH_CONFIG = new BushConfig(ModBlocks.WILD_TOMATOES.getDefaultState());
		CARROT_PATCH_CONFIG = new BushConfig(ModBlocks.WILD_CARROTS.getDefaultState());
		POTATO_PATCH_CONFIG = new BushConfig(ModBlocks.WILD_POTATOES.getDefaultState());
		BEETROOT_PATCH_CONFIG = new BushConfig(ModBlocks.WILD_BEETROOTS.getDefaultState());
		RICE_PATCH_CONFIG = new BushConfig(ModBlocks.WILD_RICE.getDefaultState());

		for (Biome biome : Registry.BIOME)
		{
			if (biome.getDefaultTemperature() >= 1.0F)
			{
				if (Configuration.GENERATE_WILD_TOMATOES)
				{
					biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.BUSH, TOMATO_PATCH_CONFIG, Placement.CHANCE_HEIGHTMAP_DOUBLE, new ChanceConfig(Configuration.CHANCE_WILD_TOMATOES)));
				}
			}
			if (biome == Biomes.BEACH)
			{
				if (Configuration.GENERATE_WILD_CABBAGES)
				{
					biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.BUSH, CABBAGE_PATCH_CONFIG, Placement.COUNT_HEIGHTMAP_DOUBLE, new FrequencyConfig(Configuration.FREQUENCY_WILD_CABBAGES)));
				}
				if (Configuration.GENERATE_WILD_BEETROOTS)
				{
					biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.BUSH, BEETROOT_PATCH_CONFIG, Placement.COUNT_HEIGHTMAP_DOUBLE, new FrequencyConfig(Configuration.FREQUENCY_WILD_BEETROOTS)));
				}
			}
			if (biome == Biomes.SWAMP || biome == Biomes.SWAMP_HILLS || biome == Biomes.JUNGLE || biome == Biomes.BAMBOO_JUNGLE)
			{
				if (Configuration.GENERATE_WILD_RICE)
				{
					biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(ModBiomeFeatures.RICE, RICE_PATCH_CONFIG, Placement.CHANCE_HEIGHTMAP_DOUBLE, new ChanceConfig(Configuration.CHANCE_WILD_RICE)));
				}
			}
			if (biome.getDefaultTemperature() > 0.3 && biome.getDefaultTemperature() < 1.0)
			{
				if (Configuration.GENERATE_WILD_CARROTS)
				{
					biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.BUSH, CARROT_PATCH_CONFIG, Placement.CHANCE_HEIGHTMAP_DOUBLE, new ChanceConfig(Configuration.CHANCE_WILD_CARROTS)));
				}
				if (Configuration.GENERATE_WILD_ONIONS)
				{
					biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.BUSH, ONION_PATCH_CONFIG, Placement.CHANCE_HEIGHTMAP_DOUBLE, new ChanceConfig(Configuration.CHANCE_WILD_ONIONS)));
				}
			}
			if (biome.getDefaultTemperature() > 0.0 && biome.getDefaultTemperature() <= 0.3)
			{
				if (Configuration.GENERATE_WILD_POTATOES)
				{
					biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.BUSH, POTATO_PATCH_CONFIG, Placement.CHANCE_HEIGHTMAP_DOUBLE, new ChanceConfig(Configuration.CHANCE_WILD_POTATOES)));
				}
			}
		}
	}
}
