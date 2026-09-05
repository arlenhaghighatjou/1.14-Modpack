package net.p3pp3rf1y.sophisticatedbackpacks.network;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;

/**
 * Carries the context the client needs to rebuild the container it is about to be shown. Vanilla
 * only sends a window id and title, so the extra data travels on the mod's own channel first.
 */
public class ContainerOpenDataMessage {
	private static PacketBuffer pending;

	private final byte[] data;

	public ContainerOpenDataMessage(byte[] data) {
		this.data = data;
	}

	public static void encode(ContainerOpenDataMessage message, PacketBuffer buffer) {
		buffer.writeByteArray(message.data);
	}

	public static ContainerOpenDataMessage decode(PacketBuffer buffer) {
		return new ContainerOpenDataMessage(buffer.readByteArray());
	}

	public static void onMessage(ContainerOpenDataMessage message, ServerPlayerEntity player) {
		pending = new PacketBuffer(Unpooled.wrappedBuffer(message.data));
	}

	/** The context sent alongside the last container the server opened. */
	public static PacketBuffer getPending() {
		return pending == null ? new PacketBuffer(Unpooled.buffer()) : pending.duplicate();
	}
}
