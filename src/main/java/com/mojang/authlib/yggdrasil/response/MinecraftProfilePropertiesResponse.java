package com.mojang.authlib.yggdrasil.response;

import com.mojang.authlib.properties.PropertyMap;
import net.lax1dude.eaglercraft.EaglercraftUUID;

public class MinecraftProfilePropertiesResponse extends Response {
    private EaglercraftUUID id;
    private String name;
    private PropertyMap properties;

    public EaglercraftUUID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public PropertyMap getProperties() {
        return this.properties;
    }
}
