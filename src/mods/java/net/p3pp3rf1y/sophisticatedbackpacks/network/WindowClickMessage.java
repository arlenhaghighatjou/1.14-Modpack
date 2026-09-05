package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SConfirmTransactionPacket;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;

import javax.annotation.Nullable;

public class WindowClickMessage {
	private final int windowId;
	private final int slotNumber;
	private final int mouseButton;
	private final ClickType clickType;
	private final ItemStack clickedItem;
	private final short actionNumber;

	public WindowClickMessage(int windowId, int slotNumber, int mouseButton, ClickType clickType, ItemStack clickedItem, short actionNumber) {
		this.windowId = windowId;
		this.slotNumber = slotNumber;
		this.mouseButton = mouseButton;
		this.clickType = clickType;
		this.clickedItem = clickedItem;
		this.actionNumber = actionNumber;
	}

	public static void encode(WindowClickMessage msg, PacketBuffer packetBuffer) {
		packetBuffer.writeByte(msg.windowId);
		packetBuffer.writeShort(msg.slotNumber);
		packetBuffer.writeByte(msg.mouseButton);
		packetBuffer.writeEnum(msg.clickType);
		PacketHelper.writeItemStack(msg.clickedItem, packetBuffer);
		packetBuffer.writeShort(msg.actionNumber);
	}

	public static WindowClickMessage decode(PacketBuffer packetBuffer) {
		return new WindowClickMessage(packetBuffer.readByte(), packetBuffer.readShort(), packetBuffer.readByte(), packetBuffer.readEnum(ClickType.class),
				PacketHelper.readItemStack(packetBuffer), packetBuffer.readShort());
	}

	static void onMessage(WindowClickMessage msg, ServerPlayerEntity player) {
		handleMessage(player, msg);
	}

	private static void handleMessage(@Nullable ServerPlayerEntity player, WindowClickMessage msg) {
		if (player == null || player.openContainer.containerId != msg.windowId || !(player.openContainer instanceof BackpackContainer)) {
			return;
		}

		player.resetLastActionTime();
		if (player.isSpectator()) {
			syncSlotsForSpectator(player);
		} else {
			ItemStack stackClickResult = player.openContainer.clicked(msg.slotNumber, msg.mouseButton, msg.clickType, player);
			if (ItemStack.matches(msg.clickedItem, stackClickResult)) {
				player.connection.sendPacket(new SConfirmTransactionPacket(msg.windowId, msg.actionNumber, true));
				player.ignoreSlotUpdateHack = true;
				player.openContainer.detectAndSendChanges();
				player.broadcastCarriedItem();
				player.ignoreSlotUpdateHack = false;
			} else {
				player.connection.sendPacket(new SConfirmTransactionPacket(msg.windowId, msg.actionNumber, false));
				player.openContainer.setSynched(player, false);
				PacketHandler.sendToClient(player, new SyncContainerStacksMessage(player.openContainer.containerId, player.openContainer.getItems()));
				player.connection.sendPacket(new SSetSlotPacket(-1, -1, player.inventory.getItemStack()));
			}
		}
	}

	private static void syncSlotsForSpectator(ServerPlayerEntity player) {
		PacketHandler.sendToClient(player, new SyncContainerStacksMessage(player.openContainer.containerId, player.openContainer.getItems()));
	}
}
