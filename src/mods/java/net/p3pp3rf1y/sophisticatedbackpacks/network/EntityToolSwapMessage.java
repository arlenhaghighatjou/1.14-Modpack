package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapperLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.api.CapabilityBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IEntityToolSwapUpgrade;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;

public class EntityToolSwapMessage {
	private final int entityId;

	public EntityToolSwapMessage(int entityId) {
		this.entityId = entityId;
	}

	public static void encode(EntityToolSwapMessage msg, PacketBuffer packetBuffer) {
		packetBuffer.writeInt(msg.entityId);
	}

	public static EntityToolSwapMessage decode(PacketBuffer packetBuffer) {
		return new EntityToolSwapMessage(packetBuffer.readInt());
	}

	static void onMessage(EntityToolSwapMessage msg, ServerPlayerEntity player) {
		handleMessage(msg, player);
	}

	private static void handleMessage(EntityToolSwapMessage msg, @Nullable ServerPlayerEntity sender) {
		if (sender == null) {
			return;
		}

		World world = sender.world;
		Entity entity = world.getEntity(msg.entityId);

		if (entity == null) {
			return;
		}

		AtomicBoolean result = new AtomicBoolean(false);
		AtomicBoolean anyUpgradeCanInteract = new AtomicBoolean(false);
		SophisticatedBackpacks.PROXY.getPlayerInventoryProvider().runOnBackpacks(sender, (backpack, inventoryName, identifier, slot) -> BackpackWrapperLookup.get(backpack)
				.map(backpackWrapper -> {
							backpackWrapper.getUpgradeHandler().getWrappersThatImplement(IEntityToolSwapUpgrade.class)
									.forEach(upgrade -> {
										if (!upgrade.canProcessEntityInteract() || result.get()) {
											return;
										}
										anyUpgradeCanInteract.set(true);

										result.set(upgrade.onEntityInteract(world, entity, sender));
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
			sender.sendStatusMessage(new TranslationTextComponent("gui.sophisticatedbackpacks.status.no_tool_found_for_entity"), true);
		}
	}
}
