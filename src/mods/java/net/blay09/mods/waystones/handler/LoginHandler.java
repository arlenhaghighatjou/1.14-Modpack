package net.blay09.mods.waystones.handler;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneManager;
import net.blay09.mods.waystones.core.WaystoneSyncManager;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.List;

public class LoginHandler {

    public static void onPlayerLogin(ServerPlayerEntity player) {
        // Introduce all global waystones to this player
        List<IWaystone> globalWaystones = WaystoneManager.get().getGlobalWaystones();
        for (IWaystone waystone : globalWaystones) {
            if (!PlayerWaystoneManager.isWaystoneActivated(player, waystone)) {
                PlayerWaystoneManager.activateWaystone(player, waystone);
            }
        }

        WaystoneSyncManager.sendKnownWaystones(player);
        WaystoneSyncManager.sendWaystoneCooldowns(player);
    }

}
