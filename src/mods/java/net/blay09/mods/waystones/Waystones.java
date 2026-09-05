package net.blay09.mods.waystones;

import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.client.ClientProxy;
import net.blay09.mods.waystones.client.ModRenderers;
import net.blay09.mods.waystones.client.ModScreens;
import net.blay09.mods.waystones.container.ModContainers;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.tileentity.ModTileEntities;
import net.blay09.mods.waystones.worldgen.ModWorldGen;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class Waystones {

    public static final String MOD_ID = "waystones";

    public static CommonProxy proxy = new ClientProxy();

    public static final ItemGroup itemGroup = new ItemGroup(ItemGroup.GROUPS.length, Waystones.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModBlocks.waystone);
        }
    };

    public static void registerContent() {
        ModBlocks.register();
        ModItems.register();
        ModBlocks.registerBlockItems();
        ModTileEntities.register();
        ModContainers.register();
        ModWorldGen.registerFeatures();
        ModWorldGen.registerPlacements();
        ModStats.registerStats();
    }

    public static void setup() {
        ModWorldGen.setupRandomWorldGen();
        ModWorldGen.setupVillageWorldGen();
    }

    public static void setupClient() {
        ModRenderers.registerRenderers();
        ModScreens.registerScreens();
    }

}
