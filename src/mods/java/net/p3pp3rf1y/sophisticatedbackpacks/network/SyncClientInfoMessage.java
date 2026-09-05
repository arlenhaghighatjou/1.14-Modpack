package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapperLookup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.p3pp3rf1y.sophisticatedbackpacks.api.CapabilityBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;

import javax.annotation.Nullable;

public class SyncClientInfoMessage {
	private final int slotIndex;
	@Nullable
	private final CompoundNBT renderInfoNbt;
	private final int columnsTaken;

	public SyncClientInfoMessage(int slotNumber, @Nullable CompoundNBT renderInfoNbt, int columnsTaken) {
		slotIndex = slotNumber;
		this.renderInfoNbt = renderInfoNbt;
		this.columnsTaken = columnsTaken;
	}

	public static void encode(SyncClientInfoMessage msg, PacketBuffer packetBuffer) {
		packetBuffer.writeInt(msg.slotIndex);
		packetBuffer.writeCompoundTag(msg.renderInfoNbt);
		packetBuffer.writeInt(msg.columnsTaken);
	}

	public static SyncClientInfoMessage decode(PacketBuffer packetBuffer) {
		return new SyncClientInfoMessage(packetBuffer.readInt(), packetBuffer.readCompoundTag(), packetBuffer.readInt());
	}

	static void onMessage(SyncClientInfoMessage msg, ServerPlayerEntity player) {
		handleMessage(msg);
	}

	private static void handleMessage(SyncClientInfoMessage msg) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player == null || msg.renderInfoNbt == null || !(player.openContainer instanceof BackpackContainer)) {
			return;
		}
		ItemStack backpack = player.inventory.items.get(msg.slotIndex);
		BackpackWrapperLookup.get(backpack).ifPresent(backpackWrapper -> {
			backpackWrapper.getRenderInfo().deserializeFrom(msg.renderInfoNbt);
			backpackWrapper.setColumnsTaken(msg.columnsTaken);
		});
	}
}
