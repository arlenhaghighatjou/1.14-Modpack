package com.polyvalord.extcaves.items;

import com.polyvalord.extcaves.ExtCaves;
import com.polyvalord.extcaves.config.Config;

import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.SwordItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class RegItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ExtCaves.MODID);

    // items
    public static final RegistryObject<Item> MUSHROOM_SWEETSHROOM_COOKED = register("mushroom_sweetshroom_cooked", () -> new Item(new Item.Properties().group(ExtCaves.extcaves_tab).food(FoodList.FOOD_SWEETSHROOM_COOKED)));
    public static final RegistryObject<Item> STEW_STICKY = register("stew_sticky", () -> new ItemStew(new Item.Properties().group(ExtCaves.extcaves_tab).food(FoodList.FOOD_STEW_STICKY)));
    public static final RegistryObject<Item> STEW_FLUORESCENT = register("stew_fluorescent", () -> new ItemStew(new Item.Properties().group(ExtCaves.extcaves_tab).food(FoodList.FOOD_STEW_FLUORESCENT)));
    public static final RegistryObject<Item> STEW_HARD = register("stew_hard", () -> new ItemStew(new Item.Properties().group(ExtCaves.extcaves_tab).food(FoodList.FOOD_STEW_HARD)));

    public static final RegistryObject<Item> MEDICINE_BANDAGE = register("medicine_bandage", () -> new ItemMedicine(Config.item_bandage_health.get(), new Item.Properties().group(ExtCaves.extcaves_tab)));
    // *removed old medicine bandage
    
    public static final RegistryObject<Item> PEBBLE_STONE = register("pebble_stone", () -> new Item(new Item.Properties().group(ExtCaves.extcaves_tab)));
    public static final RegistryObject<Item> PEBBLE_SEDIMENT_STONE = register("pebble_sedimentstone", () -> new Item(new Item.Properties().group(ExtCaves.extcaves_tab)));
    public static final RegistryObject<Item> PEBBLE_LAVASTONE = register("pebble_lavastone", () -> new Item(new Item.Properties().group(ExtCaves.extcaves_tab)));
    public static final RegistryObject<Item> PEBBLE_ANDESITE = register("pebble_andesite", () -> new Item(new Item.Properties().group(ExtCaves.extcaves_tab)));
    public static final RegistryObject<Item> PEBBLE_DIORITE = register("pebble_diorite", () -> new Item(new Item.Properties().group(ExtCaves.extcaves_tab)));
    public static final RegistryObject<Item> PEBBLE_GRANITE = register("pebble_granite", () -> new Item(new Item.Properties().group(ExtCaves.extcaves_tab)));
    public static final RegistryObject<Item> CLAY_LUMP = register("clay_lump", () -> new Item(new Item.Properties().group(ExtCaves.extcaves_tab)));
    public static final RegistryObject<Item> BRICK_HALF = register("brick_half", () -> new Item(new Item.Properties().group(ExtCaves.extcaves_tab)));
    public static final RegistryObject<Item> PLANT_FIBER = register("plant_fiber", () -> new Item(new Item.Properties().group(ExtCaves.extcaves_tab)));

    // not available until fully done
    //public static final RegistryObject<Item> SURFACE_CRYSTAL = register("surface_crystal", () -> new ItemSurfaceCrystal(new Item.Properties().group(ExtCaves.extcaves_tab)));

    public static final RegistryObject<Item> PICKER_STONE = register("picker_stone", () -> new PickaxeItem(ItemTiers.STONE_WEAK, 1, -2.2F, new Item.Properties().group(ExtCaves.extcaves_tab)));
    public static final RegistryObject<Item> PICKER_FLINT = register("picker_flint", () -> new PickaxeItem(ItemTiers.FLINT, 1, -2.2F, new Item.Properties().group(ExtCaves.extcaves_tab)));

    public static final RegistryObject<Item> BUTCHER_KNIFE = register("butcher_knife", () -> new SwordItem(ItemTier.IRON, 4, -2.2F, new Item.Properties().group(ExtCaves.extcaves_tab)));
    public static final RegistryObject<Item> CHEF_KNIFE = register("chef_knife", () -> new SwordItem(ItemTier.IRON, 3, -2.2F, new Item.Properties().group(ExtCaves.extcaves_tab)));
    public static final RegistryObject<Item> GOURMET_FORK = register("gourmet_fork", () -> new SwordItem(ItemTier.IRON, 2, -2.2F, new Item.Properties().group(ExtCaves.extcaves_tab)));
    public static final RegistryObject<Item> GOURMET_SPOON = register("gourmet_spoon", () -> new SwordItem(ItemTier.IRON, 1, -2.2F, new Item.Properties().group(ExtCaves.extcaves_tab)));
    public static final RegistryObject<Item> IRON_DAGGER = register("iron_dagger", () -> new SwordItem(ItemTier.IRON, 2, -2.2F, new Item.Properties().group(ExtCaves.extcaves_tab)));
    public static final RegistryObject<Item> RUSTY_SWORD = register("rusty_sword", () -> new SwordItem(ItemTiers.RUSTY, 3, -2.4F, new Item.Properties().group(ExtCaves.extcaves_tab)));
    public static final RegistryObject<Item> RUSTY_PICKAXE = register("rusty_pickaxe", () -> new PickaxeItem(ItemTiers.RUSTY, 1, -2.8F, new Item.Properties().group(ExtCaves.extcaves_tab)));
    public static final RegistryObject<Item> WOODEN_CANE = register("wooden_cane", () -> new SwordItem(ItemTier.WOOD, 3, -2.0F, new Item.Properties().group(ExtCaves.extcaves_tab)));

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> supplier) {
        return ITEMS.register(name, supplier);
    }
}
