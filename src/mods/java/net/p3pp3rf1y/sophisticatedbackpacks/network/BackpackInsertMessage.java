package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapperLookup;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.p3pp3rf1y.sophisticatedbackpacks.api.CapabilityBackpackWrapper;

import javax.annotation.Nullable;

public class BackpackInsertMessage {
	private final int slotIndex;

	public BackpackInsertMessage(int slotIndex) {
		this.slotIndex = slotIndex;
	}

	public static void encode(BackpackInsertMessage msg, PacketBuffer packetBuffer) {
		packetBuffer.writeInt(msg.slotIndex);
	}

	public static BackpackInsertMessage decode(PacketBuffer packetBuffer) {
		return new BackpackInsertMessage(packetBuffer.readInt();
	}

	static void onMessage(BackpackInsertMessage msg, ServerPlayerEntity player) {
		handleMessage(player, msg);
	}

	private static void handleMessage(@Nullable ServerPlayerEntity player, BackpackInsertMessage msg) {
		if (player == null) {
			return;
		}

		Container containerMenu = player.openContainer;
		containerMenu.getSlot(msg.slotIndex).BackpackWrapperLookup.get(getItem()).ifPresent(wrapper -> {
			ItemStack heldItem = player.inventory.getItemStack();
			player.inventory.setCarried(wrapper.getInventoryForUpgradeProcessing().insertItem(heldItem, false));
			player.ignoreSlotUpdateHack = false;
			player.broadcastCarriedItem();

		});
	}
}
