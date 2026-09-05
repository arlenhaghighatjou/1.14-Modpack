package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkHooks;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContext;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.IContextAwareContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;

import javax.annotation.Nullable;

public class BackpackOpenMessage {
	private static final int CHEST_SLOT = 38;
	private static final int OFFHAND_SLOT = 40;
	private final int slotIndex;
	private final String identifier;
	public BackpackOpenMessage() {
		this(-1);
	}

	public BackpackOpenMessage(int backpackSlot) {
		this(backpackSlot, "");
	}
	public BackpackOpenMessage(int backpackSlot, String identifier) {
		slotIndex = backpackSlot;
		this.identifier = identifier;
	}

	public static void encode(BackpackOpenMessage msg, PacketBuffer packetBuffer) {
		packetBuffer.writeInt(msg.slotIndex);
		packetBuffer.writeString(msg.identifier);
	}

	public static BackpackOpenMessage decode(PacketBuffer packetBuffer) {
		return new BackpackOpenMessage(packetBuffer.readInt(), packetBuffer.readString());
	}

	static void onMessage(BackpackOpenMessage msg, ServerPlayerEntity player) {
		handleMessage(player, msg);
	}

	private static void handleMessage(@Nullable ServerPlayerEntity player, BackpackOpenMessage msg) {
		if (player == null) {
			return;
		}

		if (player.openContainer instanceof BackpackContainer) {
			BackpackContext backpackContext = ((BackpackContainer) player.openContainer).getBackpackContext();
			if (msg.slotIndex == -1) {
				openBackpack(player, backpackContext.getParentBackpackContext());
			} else if (((BackpackContainer) player.openContainer).isBackpackInventorySlot(msg.slotIndex)) {
				openBackpack(player, backpackContext.getSubBackpackContext(msg.slotIndex));
			}
		} else if (player.openContainer instanceof IContextAwareContainer) {
			BackpackContext backpackContext = ((IContextAwareContainer) player.openContainer).getBackpackContext();
			openBackpack(player, backpackContext);
		} else if (msg.slotIndex > -1 && player.openContainer instanceof PlayerContainer) {
			int slotIndex = msg.slotIndex;
			String inventoryProvider = PlayerInventoryProvider.MAIN_INVENTORY;
			if (msg.slotIndex == CHEST_SLOT) {
				inventoryProvider = PlayerInventoryProvider.ARMOR_INVENTORY;
			} else if (msg.slotIndex == OFFHAND_SLOT) {
				inventoryProvider = PlayerInventoryProvider.OFFHAND_INVENTORY;
				slotIndex = 0;
			}

			BackpackContext.Item backpackContext = new BackpackContext.Item(inventoryProvider, msg.identifier, slotIndex, true);
			openBackpack(player, backpackContext);
		} else {
			findAndOpenFirstBackpack(player);
		}
	}

	private static void findAndOpenFirstBackpack(ServerPlayerEntity player) {
		SophisticatedBackpacks.PROXY.getPlayerInventoryProvider().runOnBackpacks(player, (backpack, inventoryName, identifier, slot) -> {
			BackpackContext.Item backpackContext = new BackpackContext.Item(inventoryName, identifier, slot);
			NetworkHooks.openGui(player, new SimpleNamedContainerProvider((w, p, pl) -> new BackpackContainer(w, pl, backpackContext), backpack.getDisplayName()),
					backpackContext::toBuffer);
			return true;
		});
	}

	private static void openBackpack(ServerPlayerEntity player, BackpackContext backpackContext) {
		NetworkHooks.openGui(player, new SimpleNamedContainerProvider((w, p, pl) -> new BackpackContainer(w, pl, backpackContext), backpackContext.getDisplayName(player)),
				backpackContext::toBuffer);
	}
}
