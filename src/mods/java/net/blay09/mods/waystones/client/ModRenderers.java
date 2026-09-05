package net.blay09.mods.waystones.client;

import net.blay09.mods.waystones.client.render.WaystoneRenderer;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class ModRenderers {
    public static void registerRenderers() {
        TileEntityRendererDispatcher.instance.register(WaystoneTileEntity.class, new WaystoneRenderer());
    }
}
