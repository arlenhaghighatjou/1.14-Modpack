package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;

public class OpList extends UserList<GameProfile, OpEntry> {
    public OpList(VFile2 saveFile) {
        super(saveFile);
    }

    protected UserListEntry<GameProfile> createEntry(JsonObject entryData) {
        return new OpEntry(entryData);
    }

    public String[] getKeys() {
        String[] astring = new String[this.getEntries().size()];
        int i = 0;

        for (UserListEntry<GameProfile> userlistentry : this.getEntries()) {
            astring[i++] = userlistentry.getValue().getName();
        }

        return astring;
    }

    public boolean bypassesPlayerLimit(GameProfile profile) {
        OpEntry opentry = this.getEntry(profile);
        return opentry != null ? opentry.bypassesPlayerLimit() : false;
    }

    protected String getObjectKey(GameProfile obj) {
        return obj.getId().toString();
    }
}
