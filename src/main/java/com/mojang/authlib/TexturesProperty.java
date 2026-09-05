package com.mojang.authlib;

import com.mojang.authlib.properties.Property;
import net.lax1dude.eaglercraft.ArrayUtils;
import net.lax1dude.eaglercraft.Base64;
import org.json.JSONObject;

import java.util.Collection;

public class TexturesProperty {

    public static final TexturesProperty defaultNull = new TexturesProperty(null, "default", null, false);
    public final String skin;
    public final String model;
    public final String cape;
    public final boolean eaglerPlayer;

    private TexturesProperty(String skin, String model, String cape, boolean eaglerPlayer) {
        this.skin = skin;
        this.model = model;
        this.cape = cape;
        this.eaglerPlayer = eaglerPlayer;
    }

    public static TexturesProperty parseProfile(GameProfile profile) {
        Collection<Property> etr = profile.getProperties().get("textures");
        if (!etr.isEmpty()) {
            Property prop = etr.iterator().next();
            String str;
            try {
                str = ArrayUtils.asciiString(Base64.decodeBase64(prop.getValue()));
            } catch (Throwable t) {
                return defaultNull;
            }
            boolean isEagler = false;
            etr = profile.getProperties().get("isEaglerPlayer");
            if (!etr.isEmpty()) {
                prop = etr.iterator().next();
                isEagler = prop.getValue().equalsIgnoreCase("true");
            }
            return parseTextures(str, isEagler);
        } else {
            return defaultNull;
        }
    }

    public static TexturesProperty parseTextures(String string, boolean isEagler) {
        String skin = null;
        String model = "default";
        String cape = null;
        try {
            JSONObject json = new JSONObject(string);
            json = json.optJSONObject("textures");
            if (json != null) {
                JSONObject skinObj = json.optJSONObject("SKIN");
                if (skinObj != null) {
                    skin = skinObj.optString("url");
                    JSONObject meta = skinObj.optJSONObject("metadata");
                    if (meta != null) {
                        model = meta.optString("model", model);
                    }
                }
                JSONObject capeObj = json.optJSONObject("CAPE");
                if (capeObj != null) {
                    cape = capeObj.optString("url");
                }
            }
        } catch (Throwable t) {
        }
        return new TexturesProperty(skin, model, cape, isEagler);
    }

}