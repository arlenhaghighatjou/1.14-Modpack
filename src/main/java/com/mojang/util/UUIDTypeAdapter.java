package com.mojang.util;

import net.lax1dude.eaglercraft.EaglercraftUUID;
import org.json.JSONObject;

public class UUIDTypeAdapter {
    public static JSONObject toJSONObject(EaglercraftUUID value) {
        JSONObject obj = new JSONObject();
        obj.put("uuid", fromUUID(value));
        return obj;
    }

    public static EaglercraftUUID fromJSONObject(JSONObject obj) {
        String uuidStr = obj.getString("uuid");
        return fromString(uuidStr);
    }

    public static String fromUUID(EaglercraftUUID value) {
        return value.toString().replace("-", "");
    }

    public static EaglercraftUUID fromString(String input) {
        return EaglercraftUUID.fromString(input.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }
}
