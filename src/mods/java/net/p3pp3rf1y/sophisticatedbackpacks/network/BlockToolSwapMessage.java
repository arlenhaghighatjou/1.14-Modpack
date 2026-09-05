package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapperLookup;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.api.CapabilityBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IBlockToolSwapUpgrade;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;

public class BlockToolSwapMessage {
	private final BlockPos pos;

	public BlockToolSwapMessage(BlockPos pos) {
		this.pos = pos;
	}

	public static void encode(BlockToolSwapMessage msg, PacketBuffer packetBuffer) {
		packetBuffer.writeLong(msg.pos.asLong());
	}

	public static BlockToolSwapMessage decode(PacketBuffer packetBuffer) {
		return new BlockToolSwapMessage(BlockPos.of(packetBuffer.readLong()));
	}

	static void onMessage(BlockToolSwapMessage msg, ServerPlayerEntity player) {
		handleMessage(msg, player);
	}

	private static void handleMessage(BlockToolSwapMessage msg, @Nullable ServerPlayerEntity sender) {
		if (sender == null) {
			return;
		}
		AtomicBoolean result = new AtomicBoolean(false);
		AtomicBoolean anyUpgradeCanInteract = new AtomicBoolean(false);
		SophisticatedBackpacks.PROXY.getPlayerInventoryProvider().runOnBackpacks(sender, (backpack, inventoryName, identifier, slot) -> BackpackWrapperLookup.get(backpack)
				.map(backpackWrapper -> {
							backpackWrapper.getUpgradeHandler().getWrappersThatImplement(IBlockToolSwapUpgrade.class)
									.forEach(upgrade -> {
										if (!upgrade.canProcessBlockInteract() || result.get()) {
											return;
										}
										anyUpgradeCanInteract.set(true);

										result.set(upgrade.onBlockInteract(sender.world, msg.pos, sender.world.getBlockState(msg.pos), sender));
									});
							return result.get();
						}
				).orElse(false)
		);

		if (!anyUpgradeCanInteract.get()) {
			sender.sendStatusMessage(new TranslationTextComponent("gui.sophisticatedbackpacks.status.no_tool_swap_upgrade_present"), true);
			return;
		}
		if (!result.get()) {
			sender.sendStatusMessage(new TranslationTextComponent("gui.sophisticatedbackpacks.status.no_tool_found_for_block"), true);
		}
	}
}
