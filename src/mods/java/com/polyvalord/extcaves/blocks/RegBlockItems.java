package com.polyvalord.extcaves.blocks;

import com.polyvalord.extcaves.ExtCaves;
import com.polyvalord.extcaves.items.FoodList;
import com.polyvalord.extcaves.items.ItemDoubleBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegBlockItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ExtCaves.MODID);

    public static final RegistryObject<Item> MUSHROOM_SWEETSHROOM = registerBlockItem(RegBlocks.MUSHROOM_SWEETSHROOM, new Item.Properties().food(FoodList.FOOD_SWEETSHROOM));
    public static final RegistryObject<Item> MUSHROOM_GOLDISHROOM = registerBlockItem(RegBlocks.MUSHROOM_GOLDISHROOM, new Item.Properties().food(FoodList.FOOD_GOLDISHROOM));
    public static final RegistryObject<Item> MUSHROOM_SHINYSHROOM = registerBlockItem(RegBlocks.MUSHROOM_SHINYSHROOM, new Item.Properties().food(FoodList.FOOD_SHINYSHROOM));
    public static final RegistryObject<Item> MUSHROOM_LUMISHROOM = registerBlockItem(RegBlocks.MUSHROOM_LUMISHROOM, new Item.Properties().food(FoodList.FOOD_LUMISHROOM));
    public static final RegistryObject<Item> MUSHROOM_FLUOSHROOM = registerBlockItem(RegBlocks.MUSHROOM_FLUOSHROOM, new Item.Properties().food(FoodList.FOOD_FLUOSHROOM));
    public static final RegistryObject<Item> MUSHROOM_ROCKSHROOM = registerBlockItem(RegBlocks.MUSHROOM_ROCKSHROOM, new Item.Properties().food(FoodList.FOOD_ROCKSHROOM));

    public static final RegistryObject<Item> MOSS_DRY = registerBlockItem(RegBlocks.MOSS_DRY, new Item.Properties());
    public static final RegistryObject<Item> MOSS_FIRE = registerBlockItem(RegBlocks.MOSS_FIRE, new Item.Properties());
    public static final RegistryObject<Item> MOSS_FROZEN = registerBlockItem(RegBlocks.MOSS_FROZEN, new Item.Properties());
    public static final RegistryObject<Item> MOSS_HANGING_ROOTS = registerBlockItem(RegBlocks.MOSS_HANGING_ROOTS, new Item.Properties());
    public static final RegistryObject<Item> MOSS_HUMID_GROUND = registerBlockItem(RegBlocks.MOSS_HUMID_GROUND, new Item.Properties());
    public static final RegistryObject<Item> MOSS_HUMID_CEILING = registerBlockItem(RegBlocks.MOSS_HUMID_CEILING, new Item.Properties());

    public static final RegistryObject<Item> CAVE_VINE_END = registerBlockItem(RegBlocks.CAVE_VINE_END, new Item.Properties());

    public static final RegistryObject<Item> OLDSTONE = registerBlockItem(RegBlocks.OLDSTONE, new Item.Properties());
    public static final RegistryObject<Item> BROKENSTONE = registerBlockItem(RegBlocks.BROKENSTONE, new Item.Properties());
    public static final RegistryObject<Item> SEDIMENT_STONE = registerBlockItem(RegBlocks.SEDIMENT_STONE, new Item.Properties());
    public static final RegistryObject<Item> LAVASTONE = registerBlockItem(RegBlocks.LAVASTONE, new Item.Properties());
    public static final RegistryObject<Item> DIRTSTONE = registerBlockItem(RegBlocks.DIRTSTONE, new Item.Properties());
    public static final RegistryObject<Item> DIRTSTONE_COBBLE = registerBlockItem(RegBlocks.DIRTSTONE_COBBLE, new Item.Properties());
    public static final RegistryObject<Item> MARLSTONE = registerBlockItem(RegBlocks.MARLSTONE, new Item.Properties());
    public static final RegistryObject<Item> MARLSTONE_POWDER = registerBlockItem(RegBlocks.MARLSTONE_POWDER, new Item.Properties());
    public static final RegistryObject<Item> BRICKS_ICE = registerBlockItem(RegBlocks.BRICKS_ICE, new Item.Properties());
    public static final RegistryObject<Item> BRICKS_SNOW = registerBlockItem(RegBlocks.BRICKS_SNOW, new Item.Properties());
    public static final RegistryObject<Item> POLISHED_LAVASTONE = registerBlockItem(RegBlocks.POLISHED_LAVASTONE, new Item.Properties());

    public static final RegistryObject<Item> LAVASTONE_SLAB = registerBlockItem(RegBlocks.LAVASTONE_SLAB, new Item.Properties());
    public static final RegistryObject<Item> DIRTSTONE_SLAB = registerBlockItem(RegBlocks.DIRTSTONE_SLAB, new Item.Properties());
    public static final RegistryObject<Item> DIRTSTONE_COBBLE_SLAB = registerBlockItem(RegBlocks.DIRTSTONE_COBBLE_SLAB, new Item.Properties());
    public static final RegistryObject<Item> BRICKS_ICE_SLAB = registerBlockItem(RegBlocks.BRICKS_ICE_SLAB, new Item.Properties());
    public static final RegistryObject<Item> BRICKS_SNOW_SLAB = registerBlockItem(RegBlocks.BRICKS_SNOW_SLAB, new Item.Properties());
    public static final RegistryObject<Item> POLISHED_LAVASTONE_SLAB = registerBlockItem(RegBlocks.POLISHED_LAVASTONE_SLAB, new Item.Properties());

    public static final RegistryObject<Item> LAVASTONE_STAIRS = registerBlockItem(RegBlocks.LAVASTONE_STAIRS, new Item.Properties());
    public static final RegistryObject<Item> DIRTSTONE_STAIRS = registerBlockItem(RegBlocks.DIRTSTONE_STAIRS, new Item.Properties());
    public static final RegistryObject<Item> DIRTSONE_COBBLE_STAIRS = registerBlockItem(RegBlocks.DIRTSTONE_COBBLE_STAIRS, new Item.Properties());
    public static final RegistryObject<Item> BRICKS_ICE_STAIRS = registerBlockItem(RegBlocks.BRICKS_ICE_STAIRS, new Item.Properties());
    public static final RegistryObject<Item> BRICKS_SNOW_STAIRS = registerBlockItem(RegBlocks.BRICKS_SNOW_STAIRS, new Item.Properties());
    public static final RegistryObject<Item> POLISHED_LAVASTONE_STAIRS = registerBlockItem(RegBlocks.POLISHED_LAVASTONE_STAIRS, new Item.Properties());

    public static final RegistryObject<Item> ROCK_FLINT = registerBlockItem(RegBlocks.ROCK_FLINT, new Item.Properties());
    public static final RegistryObject<Item> ROCK_PEBBLE_STONE = registerBlockItem(RegBlocks.ROCK_PEBBLE_STONE, new Item.Properties());
    public static final RegistryObject<Item> ROCK_PEBBLE_SEDIMENTSTONE = registerBlockItem(RegBlocks.ROCK_PEBBLE_SEDIMENTSTONE, new Item.Properties());
    public static final RegistryObject<Item> ROCK_PEBBLE_LAVASTONE = registerBlockItem(RegBlocks.ROCK_PEBBLE_LAVASTONE, new Item.Properties());
    public static final RegistryObject<Item> ROCK_PEBBLE_ANDESITE = registerBlockItem(RegBlocks.ROCK_PEBBLE_ANDESITE, new Item.Properties());
    public static final RegistryObject<Item> ROCK_PEBBLE_DIORITE = registerBlockItem(RegBlocks.ROCK_PEBBLE_DIORITE, new Item.Properties());
    public static final RegistryObject<Item> ROCK_PEBBLE_GRANITE = registerBlockItem(RegBlocks.ROCK_PEBBLE_GRANITE, new Item.Properties());
    public static final RegistryObject<Item> ROCKPILE_TWO_STONE = registerBlockItem(RegBlocks.ROCKPILE_TWO_STONE, new Item.Properties());
    public static final RegistryObject<Item> ROCKPILE_TWO_SEDIMENTSTONE = registerBlockItem(RegBlocks.ROCKPILE_TWO_SEDIMENTSTONE, new Item.Properties());
    public static final RegistryObject<Item> ROCKPILE_TWO_LAVASTONE = registerBlockItem(RegBlocks.ROCKPILE_TWO_LAVASTONE, new Item.Properties());
    public static final RegistryObject<Item> ROCKPILE_TWO_ANDESITE = registerBlockItem(RegBlocks.ROCKPILE_TWO_ANDESITE, new Item.Properties());
    public static final RegistryObject<Item> ROCKPILE_TWO_DIORITE = registerBlockItem(RegBlocks.ROCKPILE_TWO_DIORITE, new Item.Properties());
    public static final RegistryObject<Item> ROCKPILE_TWO_GRANITE = registerBlockItem(RegBlocks.ROCKPILE_TWO_GRANITE, new Item.Properties());
    public static final RegistryObject<Item> ROCKPILE_THREE_STONE = registerBlockItem(RegBlocks.ROCKPILE_THREE_STONE, new Item.Properties());
    public static final RegistryObject<Item> ROCKPILE_THREE_SEDIMENTSTONE = registerBlockItem(RegBlocks.ROCKPILE_THREE_SEDIMENTSTONE, new Item.Properties());
    public static final RegistryObject<Item> ROCKPILE_THREE_LAVASTONE = registerBlockItem(RegBlocks.ROCKPILE_THREE_LAVASTONE, new Item.Properties());
    public static final RegistryObject<Item> ROCKPILE_THREE_ANDESITE = registerBlockItem(RegBlocks.ROCKPILE_THREE_ANDESITE, new Item.Properties());
    public static final RegistryObject<Item> ROCKPILE_THREE_DIORITE = registerBlockItem(RegBlocks.ROCKPILE_THREE_DIORITE, new Item.Properties());
    public static final RegistryObject<Item> ROCKPILE_THREE_GRANITE = registerBlockItem(RegBlocks.ROCKPILE_THREE_GRANITE, new Item.Properties());

    public static final RegistryObject<Item> ROCK_STALAGMITE_STONE = registerBlockItem(RegBlocks.ROCK_STALAGMITE_STONE, new Item.Properties());
    public static final RegistryObject<Item> ROCK_STALAGMITE_SEDIMENTSTONE = registerBlockItem(RegBlocks.ROCK_STALAGMITE_SEDIMENTSTONE, new Item.Properties());
    public static final RegistryObject<Item> ROCK_STALAGMITE_LAVASTONE = registerBlockItem(RegBlocks.ROCK_STALAGMITE_LAVASTONE, new Item.Properties());
    public static final RegistryObject<Item> ROCK_STALAGMITE_PACKED_ICE = registerBlockItem(RegBlocks.ROCK_STALAGMITE_PACKED_ICE, new Item.Properties());
    public static final RegistryObject<Item> ROCK_STALAGMITE_TALL_STONE = registerDoubleBlockItem(true, RegBlocks.ROCK_STALAGMITE_TALL_STONE, new Item.Properties());
    public static final RegistryObject<Item> ROCK_STALAGMITE_TALL_SEDIMENTSTONE = registerDoubleBlockItem(true, RegBlocks.ROCK_STALAGMITE_TALL_SEDIMENTSTONE, new Item.Properties());
    public static final RegistryObject<Item> ROCK_STALAGMITE_TALL_LAVASTONE = registerDoubleBlockItem(true, RegBlocks.ROCK_STALAGMITE_TALL_LAVASTONE, new Item.Properties());
    public static final RegistryObject<Item> ROCK_STALAGMITE_TALL_PACKED_ICE = registerDoubleBlockItem(true, RegBlocks.ROCK_STALAGMITE_TALL_PACKED_ICE, new Item.Properties());
    public static final RegistryObject<Item> ROCK_STALACTITE_STONE = registerBlockItem(RegBlocks.ROCK_STALACTITE_STONE, new Item.Properties());
    public static final RegistryObject<Item> ROCK_STALACTITE_SEDIMENTSTONE = registerBlockItem(RegBlocks.ROCK_STALACTITE_SEDIMENTSTONE, new Item.Properties());
    public static final RegistryObject<Item> ROCK_STALACTITE_LAVASTONE = registerBlockItem(RegBlocks.ROCK_STALACTITE_LAVASTONE, new Item.Properties());
    public static final RegistryObject<Item> ROCK_STALACTITE_PACKED_ICE = registerBlockItem(RegBlocks.ROCK_STALACTITE_PACKED_ICE, new Item.Properties());
    public static final RegistryObject<Item> ROCK_STALACTITE_TALL_STONE = registerDoubleBlockItem(false, RegBlocks.ROCK_STALACTITE_TALL_STONE, new Item.Properties());
    public static final RegistryObject<Item> ROCK_STALACTITE_TALL_SEDIMENTSTONE = registerDoubleBlockItem(false, RegBlocks.ROCK_STALACTITE_TALL_SEDIMENTSTONE, new Item.Properties());
    public static final RegistryObject<Item> ROCK_STALACTITE_TALL_LAVASTONE = registerDoubleBlockItem(false, RegBlocks.ROCK_STALACTITE_TALL_LAVASTONE, new Item.Properties());
    public static final RegistryObject<Item> ROCK_STALACTITE_TALL_PACKED_ICE = registerDoubleBlockItem(false, RegBlocks.ROCK_STALACTITE_TALL_PACKED_ICE, new Item.Properties());

    public static final RegistryObject<Item> TREASURE_POT = registerBlockItem(RegBlocks.TREASURE_POT, new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final RegistryObject<Item> TREASURE_POT_SHORT = registerBlockItem(RegBlocks.TREASURE_POT_SHORT, new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final RegistryObject<Item> TREASURE_POT_LONG = registerBlockItem(RegBlocks.TREASURE_POT_LONG, new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final RegistryObject<Item> TREASURE_POT_QUARTZ_SHORT = registerBlockItem(RegBlocks.TREASURE_POT_QUARTZ_SHORT, new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final RegistryObject<Item> TREASURE_POT_QUARTZ_LONG = registerBlockItem(RegBlocks.TREASURE_POT_QUARTZ_LONG, new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final RegistryObject<Item> DECORATION_POT = registerBlockItem(RegBlocks.DECORATION_POT, new Item.Properties());
    public static final RegistryObject<Item> DECORATION_POT_SHORT = registerBlockItem(RegBlocks.DECORATION_POT_SHORT, new Item.Properties());
    public static final RegistryObject<Item> DECORATION_POT_LONG = registerBlockItem(RegBlocks.DECORATION_POT_LONG, new Item.Properties());
    public static final RegistryObject<Item> DECORATION_POT_QUARTZ_SHORT = registerBlockItem(RegBlocks.DECORATION_POT_QUARTZ_SHORT, new Item.Properties());
    public static final RegistryObject<Item> DECORATION_POT_QUARTZ_LONG = registerBlockItem(RegBlocks.DECORATION_POT_QUARTZ_LONG, new Item.Properties());

    private static RegistryObject<Item> registerBlockItem(RegistryObject<Block> block, Item.Properties props) {
        return ITEMS.register(block.getId().getPath(), () -> createBlockItem(block, props));
    }

    private static RegistryObject<Item> registerDoubleBlockItem(boolean isUp, RegistryObject<Block> block, Item.Properties props) {
        return ITEMS.register(block.getId().getPath(), () -> createDoubleBlockItem(isUp, block, props));
    }

    private static Item createBlockItem(RegistryObject<Block> block, Item.Properties properties) {
        properties.group(ExtCaves.extcaves_tab);
        properties.maxStackSize(64);
        return new BlockItem(block.get(), properties);
    }

    private static Item createDoubleBlockItem(boolean isup, RegistryObject<Block> block, Item.Properties properties) {
        properties.group(ExtCaves.extcaves_tab);
        properties.maxStackSize(64);
        return new ItemDoubleBlock(isup, block.get(), properties);
    }
}
