package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.util.InventoryInteractionHelper;

import javax.annotation.Nullable;

public class InventoryInteractionMessage {
	private final BlockPos pos;
	private final Direction face;

	public InventoryInteractionMessage(BlockPos pos, Direction face) {
		this.pos = pos;
		this.face = face;
	}

	public static void encode(InventoryInteractionMessage msg, PacketBuffer packetBuffer) {
		packetBuffer.writeLong(msg.pos.toLong());
		packetBuffer.writeEnum(msg.face);
	}

	public static InventoryInteractionMessage decode(PacketBuffer packetBuffer) {
		return new InventoryInteractionMessage(BlockPos.fromLong(packetBuffer.readLong()), packetBuffer.readEnum(Direction.class));
	}

	static void onMessage(InventoryInteractionMessage msg, ServerPlayerEntity player) {
		handleMessage(msg, player);
	}

	private static void handleMessage(InventoryInteractionMessage msg, @Nullable ServerPlayerEntity sender) {
		if (sender == null) {
			return;
		}
		SophisticatedBackpacks.PROXY.getPlayerInventoryProvider().runOnBackpacks(sender, (backpack, inventoryName, identifier, slot) -> {
			InventoryInteractionHelper.tryInventoryInteraction(msg.pos, sender.world, backpack, msg.face, sender);
			sender.swing(Hand.MAIN_HAND, true);
			return true;
		});
	}
}
