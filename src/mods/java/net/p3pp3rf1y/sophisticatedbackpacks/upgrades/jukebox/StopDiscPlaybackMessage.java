package net.p3pp3rf1y.sophisticatedbackpacks.upgrades.jukebox;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;

import net.lax1dude.eaglercraft.EaglercraftUUID;

public class StopDiscPlaybackMessage {
	private final EaglercraftUUID backpackUuid;

	public StopDiscPlaybackMessage(EaglercraftUUID backpackUuid) {
		this.backpackUuid = backpackUuid;
	}

	public static void encode(StopDiscPlaybackMessage msg, PacketBuffer packetBuffer) {
		packetBuffer.writeUniqueId(msg.backpackUuid);
	}

	public static StopDiscPlaybackMessage decode(PacketBuffer packetBuffer) {
		return new StopDiscPlaybackMessage(packetBuffer.readUniqueId());
	}

	public static void onMessage(StopDiscPlaybackMessage msg, ServerPlayerEntity player) {
		handleMessage(msg);
	}

	private static void handleMessage(StopDiscPlaybackMessage msg) {
		BackpackSoundHandler.stopBackpackSound(msg.backpackUuid);
	}
}
