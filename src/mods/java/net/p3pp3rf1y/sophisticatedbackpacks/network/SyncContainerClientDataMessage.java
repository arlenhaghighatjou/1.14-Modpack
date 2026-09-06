package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.ISyncedContainer;

import javax.annotation.Nullable;

public class SyncContainerClientDataMessage {
	@Nullable
	private final CompoundNBT data;

	public SyncContainerClientDataMessage(@Nullable CompoundNBT data) {
		this.data = data;
	}

	public static void encode(SyncContainerClientDataMessage msg, PacketBuffer packetBuffer) {
		packetBuffer.writeCompoundTag(msg.data);
	}

	public static SyncContainerClientDataMessage decode(PacketBuffer packetBuffer) {
		return new SyncContainerClientDataMessage(packetBuffer.readCompoundTag());
	}

	public static void onMessage(SyncContainerClientDataMessage msg, ServerPlayerEntity player) {
		handleMessage(sender, msg);
	}

	private static void handleMessage(@Nullable ServerPlayerEntity sender, SyncContainerClientDataMessage message) {
		if (sender == null || message.data == null) {
			return;
		}

		if (sender.openContainer instanceof ISyncedContainer) {
			ISyncedContainer container = (ISyncedContainer) sender.openContainer;
			container.handleMessage(message.data);
		}
	}
}
