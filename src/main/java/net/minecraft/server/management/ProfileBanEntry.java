package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.Date;
import java.util.Objects;

public class ProfileBanEntry extends BanEntry<GameProfile> {
    public ProfileBanEntry(GameProfile profile) {
        this(profile, (Date) null, (String) null, (Date) null, (String) null);
    }

    public ProfileBanEntry(GameProfile profile, Date startDate, String banner, Date endDate, String banReason) {
        super(profile, startDate, banner, endDate, banReason);
    }

    public ProfileBanEntry(JsonObject json) {
        super(toGameProfile(json), json);
    }

    protected void onSerialization(JsonObject data) {
        if (this.getValue() != null) {
            data.addProperty("uuid", this.getValue().getId() == null ? "" : this.getValue().getId().toString());
            data.addProperty("name", this.getValue().getName());
            super.onSerialization(data);
        }
    }

    public ITextComponent getDisplayName() {
        GameProfile gameprofile = this.getValue();
        return new StringTextComponent(gameprofile.getName() != null ? gameprofile.getName() : Objects.toString(gameprofile.getId(), "(Unknown)"));
    }

    private static GameProfile toGameProfile(JsonObject json) {
        if (json.has("uuid") && json.has("name")) {
            String s = json.get("uuid").getAsString();

            EaglercraftUUID uuid;
            try {
                uuid = EaglercraftUUID.fromString(s);
            } catch (Throwable var4) {
                return null;
            }

            return new GameProfile(uuid, json.get("name").getAsString());
        } else {
            return null;
        }
    }
}
