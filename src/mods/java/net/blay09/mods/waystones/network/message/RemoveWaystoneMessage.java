package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;


public class RemoveWaystoneMessage {

    private final IWaystone waystone;

    public RemoveWaystoneMessage(IWaystone waystone) {
        this.waystone = waystone;
    }

    public static void encode(RemoveWaystoneMessage message, PacketBuffer buf) {
        buf.writeUniqueId(message.waystone.getWaystoneUid());
    }

    public static RemoveWaystoneMessage decode(PacketBuffer buf) {
        IWaystone waystone = new WaystoneProxy(buf.readUniqueId());
        return new RemoveWaystoneMessage(waystone);
    }

    public static void handle(RemoveWaystoneMessage message, ServerPlayerEntity player) {
        PlayerWaystoneManager.deactivateWaystone(player, message.waystone);
    }

}
