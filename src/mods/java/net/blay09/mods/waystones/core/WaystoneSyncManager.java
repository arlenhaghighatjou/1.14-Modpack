package net.blay09.mods.waystones.core;

import net.minecraft.server.MinecraftServer;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.PlayerKnownWaystonesMessage;
import net.blay09.mods.waystones.network.message.PlayerWaystoneCooldownsMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.List;

public class WaystoneSyncManager {

    public static void sendKnownWaystonesToAll() {
        List<ServerPlayerEntity> players = MinecraftServer.getCurrentServer().getPlayerList().getPlayers();
        for (ServerPlayerEntity player : players) {
            sendKnownWaystones(player);
        }
    }

    public static void sendKnownWaystones(PlayerEntity player) {
        List<IWaystone> waystones = PlayerWaystoneManager.getWaystones(player);
        NetworkHandler.sendTo(new PlayerKnownWaystonesMessage(waystones), player);
    }

    public static void sendWaystoneCooldowns(PlayerEntity player) {
        long inventoryButtonCooldownUntil = PlayerWaystoneManager.getInventoryButtonCooldownUntil(player);
        long warpStoneCooldownUntil = PlayerWaystoneManager.getWarpStoneCooldownUntil(player);
        NetworkHandler.sendTo(new PlayerWaystoneCooldownsMessage(inventoryButtonCooldownUntil, warpStoneCooldownUntil), player);
    }
}
