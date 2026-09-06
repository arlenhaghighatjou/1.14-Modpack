package net.p3pp3rf1y.sophisticatedbackpacks.upgrades.tank;

import net.p3pp3rf1y.sophisticatedbackpacks.util.fluid.FluidHandlerLookup;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.p3pp3rf1y.sophisticatedbackpacks.util.fluid.FluidStack;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.UpgradeContainerBase;

import javax.annotation.Nullable;

public class TankClickMessage {
	private final int upgradeSlot;

	public TankClickMessage(int upgradeSlot) {
		this.upgradeSlot = upgradeSlot;
	}

	public static void encode(TankClickMessage msg, PacketBuffer packetBuffer) {
		packetBuffer.writeInt(msg.upgradeSlot);
	}

	public static TankClickMessage decode(PacketBuffer packetBuffer) {
		return new TankClickMessage(packetBuffer.readInt());
	}

	public static void onMessage(TankClickMessage msg, ServerPlayerEntity player) {
		handleMessage(player, msg);
	}

	private static void handleMessage(@Nullable ServerPlayerEntity sender, TankClickMessage msg) {
		if (sender == null || !(sender.openContainer instanceof BackpackContainer)) {
			return;
		}
		UpgradeContainerBase<?, ?> upgradeContainer = ((BackpackContainer) sender.openContainer).getUpgradeContainers().get(msg.upgradeSlot);
		if (!(upgradeContainer instanceof TankUpgradeContainer)) {
			return;
		}
		TankUpgradeContainer tankContainer = (TankUpgradeContainer) upgradeContainer;
		ItemStack cursorStack = sender.inventory.getItemStack();
		FluidHandlerLookup.getItem(cursorStack).ifPresent(fluidHandler -> {
			TankUpgradeWrapper tankWrapper = tankContainer.getUpgradeWrapper();
			FluidStack tankContents = tankWrapper.getContents();
			if (tankContents.isEmpty()) {
				drainHandler(sender, fluidHandler, tankWrapper);
			} else {
				if (!tankWrapper.fillHandler(fluidHandler, itemStackIn -> {
					sender.inventory.setCarried(itemStackIn);
					sender.connection.sendPacket(new SSetSlotPacket(-1, -1, sender.inventory.getItemStack()));
				})) {
					drainHandler(sender, fluidHandler, tankWrapper);
				}
			}
		});
	}

	private static void drainHandler(ServerPlayerEntity sender, net.p3pp3rf1y.sophisticatedbackpacks.util.fluid.IFluidHandlerItem fluidHandler, TankUpgradeWrapper tankWrapper) {
		tankWrapper.drainHandler(fluidHandler, itemStackIn -> {
			sender.inventory.setItemStack(itemStackIn);
			sender.connection.sendPacket(new SSetSlotPacket(-1, -1, sender.inventory.getItemStack()));
		});
	}
}
