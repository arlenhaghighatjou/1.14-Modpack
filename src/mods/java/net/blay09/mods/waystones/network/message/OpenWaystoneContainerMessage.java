package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.core.WarpMode;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

/**
 * Carries the data the client needs to build the container it is about to be shown.
 */
public class OpenWaystoneContainerMessage {

    public static WarpMode pendingWarpMode = WarpMode.WARP_SCROLL;
    @Nullable
    public static BlockPos pendingPos;

    private final WarpMode warpMode;
    @Nullable
    private final BlockPos pos;

    public OpenWaystoneContainerMessage(WarpMode warpMode, @Nullable BlockPos pos) {
        this.warpMode = warpMode;
        this.pos = pos;
    }

    public static void encode(OpenWaystoneContainerMessage message, PacketBuffer buf) {
        buf.writeByte(message.warpMode.ordinal());
        buf.writeBoolean(message.pos != null);
        if (message.pos != null) {
            buf.writeBlockPos(message.pos);
        }
    }

    public static OpenWaystoneContainerMessage decode(PacketBuffer buf) {
        WarpMode warpMode = WarpMode.values[buf.readByte()];
        BlockPos pos = buf.readBoolean() ? buf.readBlockPos() : null;
        return new OpenWaystoneContainerMessage(warpMode, pos);
    }

    public static void handle(OpenWaystoneContainerMessage message) {
        pendingWarpMode = message.warpMode;
        pendingPos = message.pos;
    }
}
