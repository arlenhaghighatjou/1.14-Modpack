package com.mojang.authlib.yggdrasil.response;

import com.mojang.authlib.properties.PropertyMap;
import net.lax1dude.eaglercraft.EaglercraftUUID;

public class HasJoinedMinecraftServerResponse extends Response {
    private EaglercraftUUID id;
    private PropertyMap properties;

    public EaglercraftUUID getId() {
        return this.id;
    }

    public PropertyMap getProperties() {
        return this.properties;
    }
}
