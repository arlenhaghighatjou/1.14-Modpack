package com.polyvalord.extcaves.world;

import com.polyvalord.extcaves.ExtCaves;
import com.polyvalord.extcaves.world.features.FeatureCabin;
import com.polyvalord.extcaves.world.features.FeatureDungeon;
import com.polyvalord.extcaves.world.features.FeatureRandomPatch;

import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

import java.util.function.Supplier;

public class RegFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, ExtCaves.MODID);

    // features
    public static final RegistryObject<Feature<BlockClusterFeatureConfig>> PATCH_GROUND = register("patch_ground", () -> new FeatureRandomPatch(false, BlockClusterFeatureConfig.field_236587_a_));
    public static final RegistryObject<Feature<BlockClusterFeatureConfig>> PATCH_CEILING = register("patch_ceiling", () -> new FeatureRandomPatch(true, BlockClusterFeatureConfig.field_236587_a_));

    // structures
    public static final RegistryObject<Feature<NoFeatureConfig>> STRUCTURE_CABIN_OAK = register("structure_cabin_oak", () -> new FeatureCabin(0, NoFeatureConfig.field_236558_a_));
    public static final RegistryObject<Feature<NoFeatureConfig>> STRUCTURE_CABIN_COBBLESTONE = register("structure_cabin_cobblestone", () -> new FeatureCabin(1, NoFeatureConfig.field_236558_a_));
    public static final RegistryObject<Feature<NoFeatureConfig>> STRUCTURE_CABIN_BRICKS = register("structure_cabin_bricks", () -> new FeatureCabin(2, NoFeatureConfig.field_236558_a_));
    public static final RegistryObject<Feature<NoFeatureConfig>> STRUCTURE_CABIN_SPRUCE = register("structure_cabin_spruce", () -> new FeatureCabin(3, NoFeatureConfig.field_236558_a_));
    public static final RegistryObject<Feature<NoFeatureConfig>> STRUCTURE_CABIN_JUNGLE = register("structure_cabin_jungle", () -> new FeatureCabin(4, NoFeatureConfig.field_236558_a_));
    public static final RegistryObject<Feature<NoFeatureConfig>> STRUCTURE_CABIN_ACACIA = register("structure_cabin_acacia", () -> new FeatureCabin(5, NoFeatureConfig.field_236558_a_));

    public static final RegistryObject<Feature<NoFeatureConfig>> STRUCTURE_DUNGEON_COBBLESTONE = register("structure_dungeon_cobblestone", () -> new FeatureDungeon(0, 0, false, NoFeatureConfig.field_236558_a_));
    public static final RegistryObject<Feature<NoFeatureConfig>> STRUCTURE_DUNGEON_TALL_COBBLESTONE = register("structure_dungeon_tall_cobblestone", () -> new FeatureDungeon(1, 0, true, NoFeatureConfig.field_236558_a_));
    public static final RegistryObject<Feature<NoFeatureConfig>> STRUCTURE_DUNGEON_ICE = register("structure_dungeon_ice", () -> new FeatureDungeon(2, 1, false, NoFeatureConfig.field_236558_a_));
    public static final RegistryObject<Feature<NoFeatureConfig>> STRUCTURE_DUNGEON_LAVASTONE = register("structure_dungeon_lavastone", () -> new FeatureDungeon(3, 2, false, NoFeatureConfig.field_236558_a_));
    public static final RegistryObject<Feature<NoFeatureConfig>> STRUCTURE_DUNGEON_TALL_LAVASTONE = register("structure_dungeon_tall_lavastone", () -> new FeatureDungeon(4, 2, true, NoFeatureConfig.field_236558_a_));

    private static <T extends IFeatureConfig> RegistryObject<Feature<T>> register(String name, Supplier<? extends Feature<T>> feature) {
        return FEATURES.register(name, feature);
    }
}
