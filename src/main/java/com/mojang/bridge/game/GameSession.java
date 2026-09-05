package com.mojang.bridge.game;

import net.lax1dude.eaglercraft.EaglercraftUUID;

public interface GameSession {
    int getPlayerCount();

    boolean isRemoteServer();

    String getDifficulty();

    String getGameMode();

    EaglercraftUUID getSessionId();
}
