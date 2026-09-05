package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.container.WaystoneSelectionContainer;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;


public class SelectWaystoneMessage {

    private final IWaystone waystone;

    public SelectWaystoneMessage(IWaystone waystone) {
        this.waystone = waystone;
    }

    public static void encode(SelectWaystoneMessage message, PacketBuffer buf) {
        buf.writeUniqueId(message.waystone.getWaystoneUid());
    }

    public static SelectWaystoneMessage decode(PacketBuffer buf) {
        IWaystone waystone = new WaystoneProxy(buf.readUniqueId());
        return new SelectWaystoneMessage(waystone);
    }

    public static void handle(SelectWaystoneMessage message, ServerPlayerEntity player) {
        WaystoneSelectionContainer container = (WaystoneSelectionContainer) player.openContainer;
        if (PlayerWaystoneManager.isWaystoneActivated(player, message.waystone)) {
            PlayerWaystoneManager.tryTeleportToWaystone(player, message.waystone, container.getWarpMode(), container.getWaystoneFrom());
        }
        player.closeScreen();
    }


}
