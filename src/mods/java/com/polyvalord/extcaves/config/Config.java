package com.polyvalord.extcaves.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    // spawn conditions

    // structures
    public static ForgeConfigSpec.BooleanValue gen_structure_cabins;
    public static ForgeConfigSpec.BooleanValue gen_structure_dungeons;

    // blocks
    public static ForgeConfigSpec.BooleanValue gen_block_oldstone;
    public static ForgeConfigSpec.BooleanValue gen_block_brokenstone;
    public static ForgeConfigSpec.BooleanValue gen_block_sedimentstone;
    public static ForgeConfigSpec.BooleanValue gen_block_lavastone;
    public static ForgeConfigSpec.BooleanValue gen_block_dirt;
    public static ForgeConfigSpec.BooleanValue gen_block_dirtstone;
    public static ForgeConfigSpec.BooleanValue gen_block_marlstone;
    public static ForgeConfigSpec.BooleanValue gen_block_packed_ice;

    // mushrooms
    public static ForgeConfigSpec.BooleanValue gen_mushrooms;

    // mosses
    public static ForgeConfigSpec.BooleanValue gen_mosses;

    // vines
    public static ForgeConfigSpec.BooleanValue gen_vines;

    // rocks
    public static ForgeConfigSpec.BooleanValue gen_rock_flints;
    public static ForgeConfigSpec.BooleanValue gen_rock_pebbles;
    public static ForgeConfigSpec.BooleanValue gen_rock_rockpiles;
    public static ForgeConfigSpec.BooleanValue gen_rock_stalagmites;
    public static ForgeConfigSpec.BooleanValue gen_rock_stalactites;


    // spawn rates

    // structures
    public static ForgeConfigSpec.IntValue spawnrate_structure_cabins_oak;
    public static ForgeConfigSpec.IntValue spawnrate_structure_cabins_cobblestone;
    public static ForgeConfigSpec.IntValue spawnrate_structure_cabins_bricks;
    public static ForgeConfigSpec.IntValue spawnrate_structure_cabins_spruce;
    public static ForgeConfigSpec.IntValue spawnrate_structure_cabins_jungle;
    public static ForgeConfigSpec.IntValue spawnrate_structure_cabins_acacia;
    public static ForgeConfigSpec.IntValue spawnrate_structure_dungeons_cobblestone;
    public static ForgeConfigSpec.IntValue spawnrate_structure_dungeons_tall_cobblestone;
    public static ForgeConfigSpec.IntValue spawnrate_structure_dungeons_ice;
    public static ForgeConfigSpec.IntValue spawnrate_structure_dungeons_lavastone;
    public static ForgeConfigSpec.IntValue spawnrate_structure_dungeons_tall_lavastone;

    // blocks
    public static ForgeConfigSpec.IntValue spawnrate_block_oldstone;
    public static ForgeConfigSpec.IntValue spawnrate_block_brokenstone;
    public static ForgeConfigSpec.IntValue spawnrate_block_sedimentstone;
    public static ForgeConfigSpec.IntValue spawnrate_block_lavastone;
    public static ForgeConfigSpec.IntValue spawnrate_block_dirt;
    public static ForgeConfigSpec.IntValue spawnrate_block_dirtstone;
    public static ForgeConfigSpec.IntValue spawnrate_block_marlstone;
    public static ForgeConfigSpec.IntValue spawnrate_block_packed_ice;

    // mushrooms
    public static ForgeConfigSpec.IntValue spawnrate_mushroom_sweetshroom;
    public static ForgeConfigSpec.IntValue spawnrate_mushroom_goldishroom;
    public static ForgeConfigSpec.IntValue spawnrate_mushroom_shinyshroom;
    public static ForgeConfigSpec.IntValue spawnrate_mushroom_lumishroom;
    public static ForgeConfigSpec.IntValue spawnrate_mushroom_fluoshroom;
    public static ForgeConfigSpec.IntValue spawnrate_mushroom_rockshroom;

    // mosses
    public static ForgeConfigSpec.IntValue spawnrate_moss_dry;
    public static ForgeConfigSpec.IntValue spawnrate_moss_fire;
    public static ForgeConfigSpec.IntValue spawnrate_moss_frozen;
    public static ForgeConfigSpec.IntValue spawnrate_moss_hanging_roots;
    public static ForgeConfigSpec.IntValue spawnrate_moss_humid;

    // vines
    public static ForgeConfigSpec.IntValue spawnrate_cave_vine;

    // rocks
    public static ForgeConfigSpec.IntValue spawnrate_rock_flints;
    public static ForgeConfigSpec.IntValue spawnrate_rock_pebbles;
    public static ForgeConfigSpec.IntValue spawnrate_rock_rockpiles;
    public static ForgeConfigSpec.IntValue spawnrate_rock_stalagmites;
    public static ForgeConfigSpec.IntValue spawnrate_rock_stalactites;

    // other

    // patches sizes
    public static ForgeConfigSpec.IntValue patch_size_oldstone;
    public static ForgeConfigSpec.IntValue patch_size_brokenstone;
    public static ForgeConfigSpec.IntValue patch_size_sedimentstone;
    public static ForgeConfigSpec.IntValue patch_size_lavastone;
    public static ForgeConfigSpec.IntValue patch_size_dirt;
    public static ForgeConfigSpec.IntValue patch_size_dirtstone;
    public static ForgeConfigSpec.IntValue patch_size_marlstone;
    public static ForgeConfigSpec.IntValue patch_size_packed_ice;

    // items
    public static ForgeConfigSpec.IntValue item_medecine_cooldown;
    public static ForgeConfigSpec.IntValue item_bandage_health;


    public static void init(ForgeConfigSpec.Builder server) {

        // spawn conditions

        server.push("Spawn_Conditions");

        server.push("Structures");
        gen_structure_cabins = server.comment("\nGenerate Cabins").define("gen_structure_cabins", true);
        gen_structure_dungeons = server.comment("\nGenerate Dungeons").define("gen_structure_dungeons", true);
        server.pop();

        server.push("Blocks");
        gen_block_oldstone = server.comment("\nGenerate Old Stone").define("gen_block_oldstone", true);
        gen_block_brokenstone = server.comment("\nGenerate Broken Stone").define("gen_block_brokenstone", true);
        gen_block_sedimentstone = server.comment("\nGenerate Sediment Stone").define("gen_block_sedimentstone", true);
        gen_block_lavastone = server.comment("\nGenerate Lavastone").define("gen_block_lavastone", true);
        gen_block_dirt = server.comment("\nGenerate Dirt").define("gen_block_dirt", true);
        gen_block_dirtstone = server.comment("\nGenerate Dirt Stone").define("gen_block_dirtstone", true);
        gen_block_marlstone = server.comment("\nGenerate Marlstone").define("gen_block_marlstone", true);
        gen_block_packed_ice = server.comment("\nGenerate Packed Ice").define("gen_block_packed_ice", true);
        server.pop();

        server.push("Mushrooms");
        gen_mushrooms = server.comment("\nGenerate Mushrooms").define("gen_mushrooms", true);
        server.pop();

        server.push("Mosses");
        gen_mosses = server.comment("\nGenerate Mosses").define("gen_mosses", true);
        server.pop();

        server.push("Vines");
        gen_vines = server.comment("\nGenerate Vines").define("gen_vines", true);
        server.pop();

        server.push("Rocks");
        gen_rock_flints = server.comment("\nGenerate Flints").define("gen_rock_flints", true);
        gen_rock_pebbles = server.comment("\nGenerate Pebbles").define("gen_rock_pebbles", true);
        gen_rock_rockpiles = server.comment("\nGenerate Rock Piles").define("gen_rock_rockpiles", true);
        gen_rock_stalagmites = server.comment("\nGenerate Stalagmites").define("gen_rock_stalagmites", true);
        gen_rock_stalactites = server.comment("\nGenerate Stalactites").define("gen_rock_stalactites", true);
        server.pop();

        server.pop();


        // spawn rates

        server.push("Spawn_Rates");

        // structures
        server.push("Structures");
        spawnrate_structure_cabins_oak = server.comment("\nOak Cabins Spawn Rate (default=7)")
                .defineInRange("spawnrate_structure_cabins_oak", 7, 1, 10);
        spawnrate_structure_cabins_cobblestone = server.comment("\nCobblestone Cabins Spawn Rate (default=7)")
                .defineInRange("spawnrate_structure_cabins_cobblestone", 7, 1, 10);
        spawnrate_structure_cabins_bricks = server.comment("\nBricks Cabins Spawn Rate (default=7)")
                .defineInRange("spawnrate_structure_cabins_bricks", 7, 1, 10);
        spawnrate_structure_cabins_spruce = server.comment("\nSpruce Cabins Spawn Rate (default=7)")
                .defineInRange("spawnrate_structure_cabins_spruce", 7, 1, 10);
        spawnrate_structure_cabins_jungle = server.comment("\nJungle Cabins Spawn Rate (default=7)")
                .defineInRange("spawnrate_structure_cabins_jungle", 7, 1, 10);
        spawnrate_structure_cabins_acacia = server.comment("\nAcacia Cabins Spawn Rate (default=7)")
                .defineInRange("spawnrate_structure_cabins_acacia", 7, 1, 10);

        spawnrate_structure_dungeons_cobblestone = server.comment("\nCobblestone Dungeons Spawn Rate (default=5)")
                .defineInRange("spawnrate_structure_dungeons_cobblestone", 5, 1, 10);
        spawnrate_structure_dungeons_tall_cobblestone = server.comment("\nTall Cobblestone Dungeons Spawn Rate (default=5)")
                .defineInRange("spawnrate_structure_dungeons_tall_cobblestone", 5, 1, 10);
        spawnrate_structure_dungeons_ice = server.comment("\nIce Dungeons Spawn Rate (default=5)")
                .defineInRange("spawnrate_structure_dungeons_ice", 5, 1, 10);
        spawnrate_structure_dungeons_lavastone = server.comment("\nLavastone Dungeons Spawn Rate (default=5)")
                .defineInRange("spawnrate_structure_dungeons_lavastone", 5, 1, 10);
        spawnrate_structure_dungeons_tall_lavastone = server.comment("\nTall Lavastone Dungeons Spawn Rate (default=5)")
                .defineInRange("spawnrate_structure_dungeons_tall_lavastone", 5, 1, 10);

        server.pop();

        // blocks
        server.push("Blocks");
        spawnrate_block_oldstone = server.comment("\nOld Stone Spawn Rate (default=10)")
                .defineInRange("spawnrate_block_oldstone", 10, 1, 50);
        spawnrate_block_brokenstone = server.comment("\nBroken Stone Spawn Rate (default=12)")
                .defineInRange("spawnrate_block_brokenstone", 12, 1, 50);
        spawnrate_block_sedimentstone = server.comment("\nSediment Stone Spawn Rate (default=10)")
                .defineInRange("spawnrate_block_sedimentstone", 8, 1, 50);
        spawnrate_block_lavastone = server.comment("\nLavastone Spawn Rate (default=10)")
                .defineInRange("spawnrate_block_lavastone", 8, 1, 50);
        spawnrate_block_dirt = server.comment("\nDirt Spawn Rate (default=5)")
                .defineInRange("spawnrate_block_dirt", 5, 1, 50);
        spawnrate_block_dirtstone = server.comment("\nDirt Stone Spawn Rate (default=5)")
                .defineInRange("spawnrate_block_dirtstone", 5, 1, 50);
        spawnrate_block_marlstone = server.comment("\nMarlstone Spawn Rate (default=5)")
                .defineInRange("spawnrate_block_marlstone", 5, 1, 50);
        spawnrate_block_packed_ice = server.comment("\nPacked Ice Spawn Rate (default=5)")
                .defineInRange("spawnrate_block_packed_ice", 5, 1, 50);
        server.pop();

        // mushrooms
        server.push("Mushrooms");
        spawnrate_mushroom_sweetshroom = server.comment("\nSweetshroom Spawn Rate (default=10)")
                .defineInRange("spawnrate_mushroom_sweetshroom", 10, 1, 50);
        spawnrate_mushroom_goldishroom = server.comment("\nGoldishroom Spawn Rate (default=10)")
                .defineInRange("spawnrate_mushroom_goldishroom", 10, 1, 50);
        spawnrate_mushroom_shinyshroom = server.comment("\nShinyshroom Spawn Rate (default=10)")
                .defineInRange("spawnrate_mushroom_shinyshroom", 10, 1, 50);
        spawnrate_mushroom_lumishroom = server.comment("\nLumishroom Spawn Rate (default=10)")
                .defineInRange("spawnrate_mushroom_lumishroom", 10, 1, 50);
        spawnrate_mushroom_fluoshroom = server.comment("\nFluoshroom Spawn Rate (default=10)")
                .defineInRange("spawnrate_mushroom_fluoshroom", 10, 1, 50);
        spawnrate_mushroom_rockshroom = server.comment("\nRockshroom Spawn Rate (default=10)")
                .defineInRange("spawnrate_mushroom_rockshroom", 10, 1, 50);
        server.pop();

        // mosses
        server.push("Mosses");
        spawnrate_moss_dry = server.comment("\nDry Moss Spawn Rate (default=10)")
                .defineInRange("spawnrate_moss_dry", 10, 1, 50);
        spawnrate_moss_fire = server.comment("\nFire Moss Spawn Rate (default=10)")
                .defineInRange("spawnrate_moss_fire", 10, 1, 50);
        spawnrate_moss_frozen = server.comment("\nFrozen Moss Spawn Rate (default=10)")
                .defineInRange("spawnrate_moss_frozen", 10, 1, 50);
        spawnrate_moss_hanging_roots = server.comment("\nHanging Roots Spawn Rate (default=10)")
                .defineInRange("spawnrate_moss_hanging_roots", 10, 1, 50);
        spawnrate_moss_humid = server.comment("\nHumid Moss Spawn Rate (default=10)")
                .defineInRange("spawnrate_moss_humid", 10, 1, 50);
        server.pop();

        // vines
        server.push("Vines");
        spawnrate_cave_vine = server.comment("\nCave Vine Spawn Rate (default=10)")
                .defineInRange("spawnrate_cave_vine", 10, 1, 50);
        server.pop();

        // rocks
        server.push("Rocks");
        spawnrate_rock_flints = server.comment("\nFlint Spawn Rate (default=10)")
                .defineInRange("spawnrate_rock_flints", 10, 1, 50);
        spawnrate_rock_pebbles = server.comment("\nPebble Spawn Rate (default=10)")
                .defineInRange("spawnrate_rock_pebbles", 10, 1, 50);
        spawnrate_rock_rockpiles = server.comment("\nRock Piles Spawn Rate (default=10)")
                .defineInRange("spawnrate_rock_rockpiles", 10, 1, 50);
        spawnrate_rock_stalagmites = server.comment("\nStalagmite Spawn Rate (default=10)")
                .defineInRange("spawnrate_rock_stalagmites", 10, 1, 50);
        spawnrate_rock_stalactites = server.comment("\nStalactite Spawn Rate (default=10)")
                .defineInRange("spawnrate_rock_stalactites", 10, 1, 50);
        server.pop();

        server.pop();


        // other

        server.push("Patches_Sizes");

        // patches sizes
        patch_size_oldstone = server.comment("\nOld Stone Patches Size (default=40)")
                .defineInRange("patch_size_oldstone", 40, 1, 100);
        patch_size_brokenstone = server.comment("\nBroken Stone Patches Size (default=24)")
                .defineInRange("patch_size_brokenstone", 24, 1, 100);
        patch_size_sedimentstone = server.comment("\nSediment Stone Patches Size (default=40)")
                .defineInRange("patch_size_sedimentstone", 40, 1, 100);
        patch_size_lavastone = server.comment("\nLavastone Patches Size (default=40)")
                .defineInRange("patch_size_lavastone", 40, 1, 100);
        patch_size_dirt = server.comment("\nDirt Patches Size (default=40)")
                .defineInRange("patch_size_dirt", 40, 1, 100);
        patch_size_dirtstone = server.comment("\nDirt Stone Patches Size (default=60)")
                .defineInRange("patch_size_dirtstone", 60, 1, 100);
        patch_size_marlstone = server.comment("\nMarlstone Patches Size (default=40)")
                .defineInRange("patch_size_marlstone", 60, 1, 100);
        patch_size_packed_ice = server.comment("\nPacked Ice Patches Size (default=60)")
                .defineInRange("patch_size_packed_ice", 60, 1, 100);

        server.pop();

        server.push("Items");

        // items properties
        item_medecine_cooldown = server.comment("\nHealing items cooldown in seconds (default=5)")
                .defineInRange("item_medecine_cooldown", 5, 1, 30);

        item_bandage_health = server.comment("\nHealth amount given by the bandage (1 = half heart) (default=4)")
                .defineInRange("item_bandage_health", 4, 1, 40);

        server.pop();

    }
}
