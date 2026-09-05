package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;


public class SortWaystoneMessage {

    private final int index;
    private final int otherIndex;

    public SortWaystoneMessage(int index, int otherIndex) {
        this.index = index;
        this.otherIndex = otherIndex;
    }

    public static void encode(SortWaystoneMessage message, PacketBuffer buf) {
        buf.writeByte(message.index);
        buf.writeByte(message.otherIndex);
    }

    public static SortWaystoneMessage decode(PacketBuffer buf) {
        int index = buf.readByte();
        int otherIndex = buf.readByte();
        return new SortWaystoneMessage(index, otherIndex);
    }

    public static void handle(SortWaystoneMessage message, ServerPlayerEntity player) {
        PlayerWaystoneManager.swapWaystoneSorting(player, message.index, message.otherIndex);
    }

}
