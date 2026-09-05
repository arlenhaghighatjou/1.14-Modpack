package com.mojang.authlib.yggdrasil.request;

import net.lax1dude.eaglercraft.EaglercraftUUID;

public class JoinMinecraftServerRequest {
    public String accessToken;
    public EaglercraftUUID selectedProfile;
    public String serverId;
}
