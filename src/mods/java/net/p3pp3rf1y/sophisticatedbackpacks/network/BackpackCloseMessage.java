package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;

import javax.annotation.Nullable;

@SuppressWarnings("java:S1118")
public class BackpackCloseMessage {
	static void onMessage(ServerPlayerEntity player) {
		handleMessage(player);
	}

	private static void handleMessage(@Nullable ServerPlayerEntity player) {
		if (player == null) {
			return;
		}

		if (player.openContainer instanceof BackpackContainer) {
			player.closeContainer();
		}
	}
}
