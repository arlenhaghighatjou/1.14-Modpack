package net.blay09.mods.waystones.tileentity;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public class ModTileEntities {
    public static TileEntityType<WaystoneTileEntity> waystone;

    public static void register() {
        waystone = build(WaystoneTileEntity::new, new ResourceLocation(Waystones.MOD_ID, "waystone"), ModBlocks.waystone, ModBlocks.mossyWaystone, ModBlocks.sandyWaystone);
    }

    private static <T extends TileEntity> TileEntityType<T> build(Supplier<T> factory, ResourceLocation registryName, Block... blocks) {
        //noinspection ConstantConditions dataFixerType can be null apparently
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, registryName, TileEntityType.Builder.create(factory, blocks).build(null));
    }
}
