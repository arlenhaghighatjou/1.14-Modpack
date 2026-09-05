package net.blay09.mods.waystones.worldgen;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.config.WaystoneConfig;
import net.blay09.mods.waystones.config.WorldGenStyle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.util.registry.Registry;

import com.mojang.datafixers.util.Pair;
import net.lax1dude.eaglercraft.Random;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ModWorldGen {
    private static final ResourceLocation villageWaystoneStructure = new ResourceLocation("waystones", "village/common/waystone");
    private static final ResourceLocation desertVillageWaystoneStructure = new ResourceLocation("waystones", "village/desert/waystone");
    private static final ResourceLocation emptyStructure = new ResourceLocation("empty");

    private static WaystoneFeature waystoneFeature;
    private static WaystoneFeature mossyWaystoneFeature;
    private static WaystoneFeature sandyWaystoneFeature;
    private static WaystonePlacement waystonePlacement;

    public static void registerFeatures() {
        waystoneFeature = Registry.register(Registry.FEATURE, new ResourceLocation(Waystones.MOD_ID, "waystone"), new WaystoneFeature(NoFeatureConfig::deserialize, ModBlocks.waystone.getDefaultState()));
        mossyWaystoneFeature = Registry.register(Registry.FEATURE, new ResourceLocation(Waystones.MOD_ID, "mossy_waystone"), new WaystoneFeature(NoFeatureConfig::deserialize, ModBlocks.mossyWaystone.getDefaultState()));
        sandyWaystoneFeature = Registry.register(Registry.FEATURE, new ResourceLocation(Waystones.MOD_ID, "sandy_waystone"), new WaystoneFeature(NoFeatureConfig::deserialize, ModBlocks.sandyWaystone.getDefaultState()));
    }

    public static void registerPlacements() {
        waystonePlacement = Registry.register(Registry.DECORATOR, new ResourceLocation(Waystones.MOD_ID, "waystone"), new WaystonePlacement(NoPlacementConfig::deserialize));
    }

    public static void setupRandomWorldGen() {
        if (WaystoneConfig.COMMON.worldGenFrequency > 0) {
            Biome.BIOMES.forEach(biome -> {
                WaystoneFeature feature = getWaystoneFeature(biome);
                ConfiguredFeature<?> configuredFeature = Biome.createDecoratedFeature(feature, NoFeatureConfig.NO_FEATURE_CONFIG, waystonePlacement, NoPlacementConfig.NO_PLACEMENT_CONFIG);
                biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, configuredFeature);
            });
        }
    }

    private static WaystoneFeature getWaystoneFeature(Biome it) {
        WorldGenStyle worldGenStyle = WaystoneConfig.COMMON.worldGenStyle;
        switch (worldGenStyle) {
            case MOSSY:
                return mossyWaystoneFeature;
            case SANDY:
                return sandyWaystoneFeature;
            case BIOME:
                ResourceLocation biomeRegistryName = Objects.requireNonNull(Registry.BIOME.getKey(it));
                if (biomeRegistryName.getPath().contains("desert")) {
                    return sandyWaystoneFeature;
                } else if (biomeRegistryName.getPath().contains("jungle")) {
                    return mossyWaystoneFeature;
                } else {
                    return waystoneFeature;
                }
            default:
                return waystoneFeature;
        }
    }

    public static void setupVillageWorldGen() {
        JigsawManager.field_214891_a.register(new JigsawPattern(villageWaystoneStructure, emptyStructure, Collections.emptyList(), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawManager.field_214891_a.register(new JigsawPattern(desertVillageWaystoneStructure, emptyStructure, Collections.emptyList(), JigsawPattern.PlacementBehaviour.RIGID));

        if (WaystoneConfig.COMMON.addVillageStructure) {
            PlainsVillagePools.init();
            SnowyVillagePools.init();
            SavannaVillagePools.init();
            DesertVillagePools.init();
            TaigaVillagePools.init();

            addWaystoneStructureToVillageConfig("village/plains/houses", villageWaystoneStructure);
            addWaystoneStructureToVillageConfig("village/snowy/houses", villageWaystoneStructure);
            addWaystoneStructureToVillageConfig("village/savanna/houses", villageWaystoneStructure);
            addWaystoneStructureToVillageConfig("village/desert/houses", desertVillageWaystoneStructure);
            addWaystoneStructureToVillageConfig("village/taiga/houses", villageWaystoneStructure);
        }
    }

    private static void addWaystoneStructureToVillageConfig(String villagePiece, ResourceLocation waystoneStructure) {
        ResourceLocation poolName = new ResourceLocation(villagePiece);
        JigsawPattern houses = JigsawManager.field_214891_a.get(poolName);

        List<Pair<JigsawPiece, Integer>> pieces = new ArrayList<>();
        for (JigsawPiece piece : houses.func_214943_b(new Random())) {
            pieces.add(Pair.of(piece, 1));
        }

        pieces.add(Pair.of(new SingleJigsawPiece(waystoneStructure.toString(), Collections.emptyList(), JigsawPattern.PlacementBehaviour.RIGID), 1));
        JigsawManager.field_214891_a.register(new JigsawPattern(poolName, houses.func_214947_b(), pieces, JigsawPattern.PlacementBehaviour.RIGID));
    }
}
