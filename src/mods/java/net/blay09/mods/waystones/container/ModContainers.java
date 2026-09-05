package net.blay09.mods.waystones.container;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.network.message.OpenWaystoneContainerMessage;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class ModContainers {
    public static ContainerType<WaystoneSelectionContainer> waystoneSelection;
    public static ContainerType<WaystoneSettingsContainer> waystoneSettings;

    public static void register() {
        waystoneSelection = Registry.register(Registry.MENU, new ResourceLocation(Waystones.MOD_ID, "waystone_selection"), new ContainerType<>((windowId, inv) -> {
            WarpMode warpMode = OpenWaystoneContainerMessage.pendingWarpMode;
            IWaystone fromWaystone = null;
            if (warpMode == WarpMode.WAYSTONE_TO_WAYSTONE) {
                fromWaystone = findWaystone(inv.player.world, OpenWaystoneContainerMessage.pendingPos);
            }

            return new WaystoneSelectionContainer(windowId, warpMode, fromWaystone);
        }));

        waystoneSettings = Registry.register(Registry.MENU, new ResourceLocation(Waystones.MOD_ID, "waystone_settings"), new ContainerType<>((windowId, inv) ->
                new WaystoneSettingsContainer(windowId, findWaystone(inv.player.world, OpenWaystoneContainerMessage.pendingPos))));
    }

    private static IWaystone findWaystone(net.minecraft.world.World world, BlockPos pos) {
        if (pos == null) {
            return null;
        }

        TileEntity tileEntity = world.getTileEntity(pos);
        return tileEntity instanceof WaystoneTileEntity ? ((WaystoneTileEntity) tileEntity).getWaystone() : null;
    }

}
