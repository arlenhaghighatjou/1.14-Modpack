package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackStorage;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackTooltipRenderer;

import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.EaglercraftUUID;

public class BackpackContentsMessage {
	private final EaglercraftUUID backpackUuid;
	@Nullable
	private final CompoundNBT backpackContents;

	public BackpackContentsMessage(EaglercraftUUID backpackUuid, @Nullable CompoundNBT backpackContents) {
		this.backpackUuid = backpackUuid;
		this.backpackContents = backpackContents;
	}

	public static void encode(BackpackContentsMessage msg, PacketBuffer packetBuffer) {
		packetBuffer.writeUniqueId(msg.backpackUuid);
		packetBuffer.writeCompoundTag(msg.backpackContents);
	}

	public static BackpackContentsMessage decode(PacketBuffer packetBuffer) {
		return new BackpackContentsMessage(packetBuffer.readUniqueId(), packetBuffer.readCompoundTag());
	}

	static void onMessage(BackpackContentsMessage msg, ServerPlayerEntity player) {
		handleMessage(msg);
	}

	private static void handleMessage(BackpackContentsMessage msg) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player == null || msg.backpackContents == null) {
			return;
		}

		BackpackStorage.get().setBackpackContents(msg.backpackUuid, msg.backpackContents);
		BackpackTooltipRenderer.refreshContents();
	}
}
