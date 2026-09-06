package com.polyvalord.extcaves.blocks;

import com.polyvalord.extcaves.ExtCaves;
import com.polyvalord.extcaves.blocks.basic.BlockSlab;
import com.polyvalord.extcaves.blocks.basic.BlockStairs;
import com.polyvalord.extcaves.blocks.decoration.*;
import com.polyvalord.extcaves.blocks.moss.*;
import com.polyvalord.extcaves.blocks.mushrooms.*;
import com.polyvalord.extcaves.blocks.rocks.*;
import com.polyvalord.extcaves.blocks.treasures.*;
import com.polyvalord.extcaves.blocks.vines.BlockCaveVine;
import com.polyvalord.extcaves.blocks.vines.BlockCaveVineEnd;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class RegBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ExtCaves.MODID);

    // Shrooms
    public static final RegistryObject<Block> MUSHROOM_SWEETSHROOM = register("mushroom_sweetshroom", () -> new BlockSweetshroom(8, 5, Block.Properties.create(Material.PLANTS).hardnessAndResistance(0.0F).tickRandomly().doesNotBlockMovement().sound(SoundType.PLANT)));
    public static final RegistryObject<Block> MUSHROOM_GOLDISHROOM = register("mushroom_goldishroom", () -> new BlockGoldishroom(12, 3, Block.Properties.create(Material.PLANTS).hardnessAndResistance(0.0F).tickRandomly().doesNotBlockMovement().sound(SoundType.PLANT)));
    public static final RegistryObject<Block> MUSHROOM_SHINYSHROOM = register("mushroom_shinyshroom", () -> new BlockShinyshroom(15, 3, Block.Properties.create(Material.PLANTS).hardnessAndResistance(0.0F).tickRandomly().doesNotBlockMovement().sound(SoundType.PLANT)));
    public static final RegistryObject<Block> MUSHROOM_LUMISHROOM = register("mushroom_lumishroom", () -> new BlockLumishroom(10, 4, Block.Properties.create(Material.PLANTS).hardnessAndResistance(0.0F).tickRandomly().doesNotBlockMovement().sound(SoundType.PLANT).setLightLevel((p_235470_0_) -> 9)));
    public static final RegistryObject<Block> MUSHROOM_FLUOSHROOM = register("mushroom_fluoshroom", () -> new BlockFluoshroom(10, 5, Block.Properties.create(Material.PLANTS).hardnessAndResistance(0.0F).tickRandomly().doesNotBlockMovement().sound(SoundType.PLANT).setLightLevel((p_235470_0_) -> 8)));
    public static final RegistryObject<Block> MUSHROOM_ROCKSHROOM = register("mushroom_rockshroom", () -> new BlockRockshroom(18, 2, Block.Properties.create(Material.PLANTS).hardnessAndResistance(0.0F).tickRandomly().doesNotBlockMovement().sound(SoundType.PLANT)));

    // Mosses
    public static final RegistryObject<Block> MOSS_DRY = register("moss_dry", () -> new BlockMossDry(Block.Properties.create(Material.PLANTS).hardnessAndResistance(0.0F).doesNotBlockMovement().sound(SoundType.PLANT)));
    public static final RegistryObject<Block> MOSS_FIRE = register("moss_fire", () -> new BlockMossFire(Block.Properties.create(Material.PLANTS).hardnessAndResistance(0.0F).doesNotBlockMovement().sound(SoundType.PLANT).setLightLevel((p_235470_0_) -> 12)));
    public static final RegistryObject<Block> MOSS_FROZEN = register("moss_frozen", () -> new BlockMossFrozen(Block.Properties.create(Material.PLANTS).hardnessAndResistance(0.0F).doesNotBlockMovement().sound(SoundType.PLANT)));
    public static final RegistryObject<Block> MOSS_HANGING_ROOTS = register("moss_hanging_roots", () -> new BlockMossHangingRoots(Block.Properties.create(Material.PLANTS).hardnessAndResistance(0.0F).doesNotBlockMovement().sound(SoundType.PLANT)));
    public static final RegistryObject<Block> MOSS_HUMID_GROUND = register("moss_humid_ground", () -> new BlockMossHumidGround(Block.Properties.create(Material.PLANTS).hardnessAndResistance(0.0F).doesNotBlockMovement().sound(SoundType.PLANT)));
    public static final RegistryObject<Block> MOSS_HUMID_CEILING = register("moss_humid_ceiling", () -> new BlockMossHumidCeiling(Block.Properties.create(Material.PLANTS).hardnessAndResistance(0.0F).doesNotBlockMovement().sound(SoundType.PLANT)));

    // Vines
    public static final RegistryObject<Block> CAVE_VINE = register("cave_vine", () -> new BlockCaveVine(AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.PLANT)));
    public static final RegistryObject<Block> CAVE_VINE_END = register("cave_vine_end", () -> new BlockCaveVineEnd(AbstractBlock.Properties.create(Material.PLANTS).tickRandomly().doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.PLANT)));

    // Blocks
    public static final RegistryObject<Block> OLDSTONE = register("oldstone", () -> new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.5F, 4.5F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> BROKENSTONE = register("brokenstone", () -> new BlockBrokenstone(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.75F, 1.5F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> SEDIMENT_STONE = register("sedimentstone", () -> new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.0F, 3.0F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> LAVASTONE = register("lavastone", () -> new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F, 8.0F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> DIRTSTONE = register("dirtstone", () -> new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.2F, 3.0F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> DIRTSTONE_COBBLE = register("dirtstone_cobble", () -> new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.5F, 6.0F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> MARLSTONE = register("marlstone", () -> new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.2F, 3.0F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> MARLSTONE_POWDER = register("marlstone_powder", () -> new SandBlock(6901053, Block.Properties.create(Material.SAND).hardnessAndResistance(0.5F).harvestTool(ToolType.SHOVEL).sound(SoundType.SAND)));
    public static final RegistryObject<Block> BRICKS_ICE = register("bricks_ice", () -> new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F, 6.0F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> BRICKS_SNOW = register("bricks_snow", () -> new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F, 6.0F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> POLISHED_LAVASTONE = register("polished_lavastone", () -> new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F, 8.0F).harvestTool(ToolType.PICKAXE)));

    public static final RegistryObject<Block> LAVASTONE_SLAB = register("lavastone_slab", () -> new BlockSlab(Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F, 8.0F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> DIRTSTONE_SLAB = register("dirtstone_slab", () -> new BlockSlab(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.2F, 3.0F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> DIRTSTONE_COBBLE_SLAB = register("dirtstone_cobble_slab", () -> new BlockSlab(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.5F, 6.0F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> BRICKS_ICE_SLAB = register("bricks_ice_slab", () -> new BlockSlab(Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F, 6.0F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> BRICKS_SNOW_SLAB = register("bricks_snow_slab", () -> new BlockSlab(Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F, 6.0F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> POLISHED_LAVASTONE_SLAB = register("polished_lavastone_slab", () -> new BlockSlab(Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F, 8.0F).harvestTool(ToolType.PICKAXE)));

    public static final RegistryObject<Block> LAVASTONE_STAIRS = register("lavastone_stairs", () -> new BlockStairs(Blocks.COBBLESTONE.getDefaultState(), Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F, 8.0F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> DIRTSTONE_STAIRS = register("dirtstone_stairs", () -> new BlockStairs(Blocks.COBBLESTONE.getDefaultState(), Block.Properties.create(Material.ROCK).hardnessAndResistance(1.2F, 3.0F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> DIRTSTONE_COBBLE_STAIRS = register("dirtstone_cobble_stairs", () -> new BlockStairs(Blocks.COBBLESTONE.getDefaultState(), Block.Properties.create(Material.ROCK).hardnessAndResistance(1.5F, 6.0F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> BRICKS_ICE_STAIRS = register("bricks_ice_stairs", () -> new BlockStairs(Blocks.COBBLESTONE.getDefaultState(), Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F, 6.0F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> BRICKS_SNOW_STAIRS = register("bricks_snow_stairs", () -> new BlockStairs(Blocks.COBBLESTONE.getDefaultState(), Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F, 6.0F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> POLISHED_LAVASTONE_STAIRS = register("polished_lavastone_stairs", () -> new BlockStairs(Blocks.COBBLESTONE.getDefaultState(), Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F, 8.0F).harvestTool(ToolType.PICKAXE)));

    // rocks
    public static final RegistryObject<Block> ROCK_FLINT = register("rock_flint", () -> new BlockFlint(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.0F)));
    public static final RegistryObject<Block> ROCK_PEBBLE_STONE = register("rock_pebble_stone", () -> new BlockPebble(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.0F)));
    public static final RegistryObject<Block> ROCK_PEBBLE_SEDIMENTSTONE = register("rock_pebble_sedimentstone", () -> new BlockPebble(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.0F)));
    public static final RegistryObject<Block> ROCK_PEBBLE_LAVASTONE = register("rock_pebble_lavastone", () -> new BlockPebble(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.0F)));
    public static final RegistryObject<Block> ROCK_PEBBLE_ANDESITE = register("rock_pebble_andesite", () -> new BlockPebble(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.0F)));
    public static final RegistryObject<Block> ROCK_PEBBLE_DIORITE = register("rock_pebble_diorite", () -> new BlockPebble(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.0F)));
    public static final RegistryObject<Block> ROCK_PEBBLE_GRANITE = register("rock_pebble_granite", () -> new BlockPebble(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.0F)));
    public static final RegistryObject<Block> ROCKPILE_TWO_STONE = register("rockpile_two_stone", () -> new BlockRockpileTwo(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.3F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> ROCKPILE_TWO_SEDIMENTSTONE = register("rockpile_two_sedimentstone", () -> new BlockRockpileTwo(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.2F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> ROCKPILE_TWO_LAVASTONE = register("rockpile_two_lavastone", () -> new BlockRockpileTwo(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.4F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> ROCKPILE_TWO_ANDESITE = register("rockpile_two_andesite", () -> new BlockRockpileTwo(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.3F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> ROCKPILE_TWO_DIORITE = register("rockpile_two_diorite", () -> new BlockRockpileTwo(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.3F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> ROCKPILE_TWO_GRANITE = register("rockpile_two_granite", () -> new BlockRockpileTwo(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.3F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> ROCKPILE_THREE_STONE = register("rockpile_three_stone", () -> new BlockRockpileThree(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.45F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> ROCKPILE_THREE_SEDIMENTSTONE = register("rockpile_three_sedimentstone", () -> new BlockRockpileThree(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.3F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> ROCKPILE_THREE_LAVASTONE = register("rockpile_three_lavastone", () -> new BlockRockpileThree(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.6F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> ROCKPILE_THREE_ANDESITE = register("rockpile_three_andesite", () -> new BlockRockpileThree(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.45F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> ROCKPILE_THREE_DIORITE = register("rockpile_three_diorite", () -> new BlockRockpileThree(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.45F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> ROCKPILE_THREE_GRANITE = register("rockpile_three_granite", () -> new BlockRockpileThree(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.45F).harvestTool(ToolType.PICKAXE)));

    // speleothems
    public static final RegistryObject<Block> ROCK_STALAGMITE_STONE = register("rock_stalagmite_stone", () -> new BlockStalagmite(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.45F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> ROCK_STALAGMITE_SEDIMENTSTONE = register("rock_stalagmite_sedimentstone", () -> new BlockStalagmite(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.3F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> ROCK_STALAGMITE_LAVASTONE = register("rock_stalagmite_lavastone", () -> new BlockStalagmite(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.6F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> ROCK_STALAGMITE_PACKED_ICE = register("rock_stalagmite_packed_ice", () -> new BlockStalagmite(Block.Properties.create(Material.PACKED_ICE).slipperiness(0.98F).hardnessAndResistance(0.15F).harvestTool(ToolType.PICKAXE).sound(SoundType.GLASS)));
    public static final RegistryObject<Block> ROCK_STALAGMITE_TALL_STONE = register("rock_stalagmite_tall_stone", () -> new BlockStalagmiteTall(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.75F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> ROCK_STALAGMITE_TALL_SEDIMENTSTONE = register("rock_stalagmite_tall_sedimentstone", () -> new BlockStalagmiteTall(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.5F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> ROCK_STALAGMITE_TALL_LAVASTONE = register("rock_stalagmite_tall_lavastone", () -> new BlockStalagmiteTall(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.0F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> ROCK_STALAGMITE_TALL_PACKED_ICE = register("rock_stalagmite_tall_packed_ice", () -> new BlockStalagmiteTall(Block.Properties.create(Material.PACKED_ICE).slipperiness(0.98F).hardnessAndResistance(0.25F).harvestTool(ToolType.PICKAXE).sound(SoundType.GLASS)));
    public static final RegistryObject<Block> ROCK_STALACTITE_STONE = register("rock_stalactite_stone", () -> new BlockStalactite(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.45F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> ROCK_STALACTITE_SEDIMENTSTONE = register("rock_stalactite_sedimentstone", () -> new BlockStalactite(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.3F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> ROCK_STALACTITE_LAVASTONE = register("rock_stalactite_lavastone", () -> new BlockStalactite(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.6F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> ROCK_STALACTITE_PACKED_ICE = register("rock_stalactite_packed_ice", () -> new BlockStalactite(Block.Properties.create(Material.PACKED_ICE).slipperiness(0.98F).hardnessAndResistance(0.15F).harvestTool(ToolType.PICKAXE).sound(SoundType.GLASS)));
    public static final RegistryObject<Block> ROCK_STALACTITE_TALL_STONE = register("rock_stalactite_tall_stone", () -> new BlockStalactiteTall(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.75F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> ROCK_STALACTITE_TALL_SEDIMENTSTONE = register("rock_stalactite_tall_sedimentstone", () -> new BlockStalactiteTall(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.5F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> ROCK_STALACTITE_TALL_LAVASTONE = register("rock_stalactite_tall_lavastone", () -> new BlockStalactiteTall(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.0F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> ROCK_STALACTITE_TALL_PACKED_ICE = register("rock_stalactite_tall_packed_ice", () -> new BlockStalactiteTall(Block.Properties.create(Material.PACKED_ICE).slipperiness(0.98F).hardnessAndResistance(0.25F).harvestTool(ToolType.PICKAXE).sound(SoundType.GLASS)));

    // treasures
    public static final RegistryObject<Block> TREASURE_POT = register("treasure_pot", () -> new BlockTreasurePot(Block.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(0.2F).sound(SoundType.STONE)));
    public static final RegistryObject<Block> TREASURE_POT_SHORT = register("treasure_pot_short", () -> new BlockTreasurePotShort(Block.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(0.2F).sound(SoundType.STONE)));
    public static final RegistryObject<Block> TREASURE_POT_LONG = register("treasure_pot_long", () -> new BlockTreasurePotLong(Block.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(0.2F).sound(SoundType.STONE)));
    public static final RegistryObject<Block> TREASURE_POT_QUARTZ_SHORT = register("treasure_pot_quartz_short", () -> new BlockTreasurePotQuartzShort(Block.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(0.2F).sound(SoundType.STONE)));
    public static final RegistryObject<Block> TREASURE_POT_QUARTZ_LONG = register("treasure_pot_quartz_long", () -> new BlockTreasurePotQuartzLong(Block.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(0.2F).sound(SoundType.STONE)));

    // decorations
    public static final RegistryObject<Block> DECORATION_POT = register("decoration_pot", () -> new BlockDecorationPot(Block.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(0.2F).sound(SoundType.STONE)));
    public static final RegistryObject<Block> DECORATION_POT_SHORT = register("decoration_pot_short", () -> new BlockDecorationPotShort(Block.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(0.2F).sound(SoundType.STONE)));
    public static final RegistryObject<Block> DECORATION_POT_LONG = register("decoration_pot_long", () -> new BlockDecorationPotLong(Block.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(0.2F).sound(SoundType.STONE)));
    public static final RegistryObject<Block> DECORATION_POT_QUARTZ_SHORT = register("decoration_pot_quartz_short", () -> new BlockDecorationPotQuartzShort(Block.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(0.2F).sound(SoundType.STONE)));
    public static final RegistryObject<Block> DECORATION_POT_QUARTZ_LONG = register("decoration_pot_quartz_long", () -> new BlockDecorationPotQuartzLong(Block.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(0.2F).sound(SoundType.STONE)));

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> supplier) {
        return BLOCKS.register(name, supplier);
    }

}