package net.minecraft.server.management;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.*;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.lax1dude.eaglercraft.IOUtils;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraft.entity.player.PlayerEntity;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PlayerProfileCache {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    private static boolean onlineMode;
    private final Map<String, PlayerProfileCache.ProfileEntry> usernameToProfileEntryMap = Maps.newHashMap();
    private final Map<EaglercraftUUID, PlayerProfileCache.ProfileEntry> uuidToProfileEntryMap = Maps.newHashMap();
    private final Deque<GameProfile> gameProfiles = Lists.newLinkedList();
    private final GameProfileRepository profileRepo;
    protected final Gson gson;
    private final VFile2 usercacheFile;

    public PlayerProfileCache(GameProfileRepository profileRepoIn, VFile2 usercacheFileIn) {
        this.profileRepo = profileRepoIn;
        this.usercacheFile = usercacheFileIn;
        GsonBuilder gsonbuilder = new GsonBuilder();
        gsonbuilder.registerTypeHierarchyAdapter(PlayerProfileCache.ProfileEntry.class, new PlayerProfileCache.Serializer());
        this.gson = gsonbuilder.create();
        this.load();
    }

    private static GameProfile lookupProfile(GameProfileRepository profileRepoIn, String name) {
        final GameProfile[] agameprofile = new GameProfile[1];
        ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
            public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_) {
                agameprofile[0] = p_onProfileLookupSucceeded_1_;
            }

            public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_) {
                agameprofile[0] = null;
            }
        };
        profileRepoIn.findProfilesByNames(new String[]{name}, Agent.MINECRAFT, profilelookupcallback);
        if (!isOnlineMode() && agameprofile[0] == null) {
            EaglercraftUUID uuid = PlayerEntity.getUUID(new GameProfile((EaglercraftUUID) null, name));
            GameProfile gameprofile = new GameProfile(uuid, name);
            profilelookupcallback.onProfileLookupSucceeded(gameprofile);
        }

        return agameprofile[0];
    }

    public static void setOnlineMode(boolean onlineModeIn) {
        onlineMode = onlineModeIn;
    }

    private static boolean isOnlineMode() {
        return onlineMode;
    }

    public void addEntry(GameProfile gameProfile) {
        this.addEntry(gameProfile, (Date) null);
    }

    private void addEntry(GameProfile gameProfile, Date expirationDate) {
        EaglercraftUUID uuid = gameProfile.getId();
        if (expirationDate == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(2, 1);
            expirationDate = calendar.getTime();
        }

        PlayerProfileCache.ProfileEntry playerprofilecache$profileentry1 = new PlayerProfileCache.ProfileEntry(gameProfile, expirationDate);
        if (this.uuidToProfileEntryMap.containsKey(uuid)) {
            PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = this.uuidToProfileEntryMap.get(uuid);
            this.usernameToProfileEntryMap.remove(playerprofilecache$profileentry.getGameProfile().getName().toLowerCase(Locale.ROOT));
            this.gameProfiles.remove(gameProfile);
        }

        this.usernameToProfileEntryMap.put(gameProfile.getName().toLowerCase(Locale.ROOT), playerprofilecache$profileentry1);
        this.uuidToProfileEntryMap.put(uuid, playerprofilecache$profileentry1);
        this.gameProfiles.addFirst(gameProfile);
        this.save();
    }

    public GameProfile getGameProfileForUsername(String username) {
        String s = username.toLowerCase(Locale.ROOT);
        PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = this.usernameToProfileEntryMap.get(s);
        if (playerprofilecache$profileentry != null && (new Date()).getTime() >= playerprofilecache$profileentry.expirationDate.getTime()) {
            this.uuidToProfileEntryMap.remove(playerprofilecache$profileentry.getGameProfile().getId());
            this.usernameToProfileEntryMap.remove(playerprofilecache$profileentry.getGameProfile().getName().toLowerCase(Locale.ROOT));
            this.gameProfiles.remove(playerprofilecache$profileentry.getGameProfile());
            playerprofilecache$profileentry = null;
        }

        if (playerprofilecache$profileentry != null) {
            GameProfile gameprofile = playerprofilecache$profileentry.getGameProfile();
            this.gameProfiles.remove(gameprofile);
            this.gameProfiles.addFirst(gameprofile);
        } else {
            GameProfile gameprofile1 = lookupProfile(this.profileRepo, s);
            if (gameprofile1 != null) {
                this.addEntry(gameprofile1);
                playerprofilecache$profileentry = this.usernameToProfileEntryMap.get(s);
            }
        }

        this.save();
        return playerprofilecache$profileentry == null ? null : playerprofilecache$profileentry.getGameProfile();
    }

    public GameProfile getProfileByUUID(EaglercraftUUID uuid) {
        PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = this.uuidToProfileEntryMap.get(uuid);
        return playerprofilecache$profileentry == null ? null : playerprofilecache$profileentry.getGameProfile();
    }

    private PlayerProfileCache.ProfileEntry getByUUID(EaglercraftUUID uuid) {
        PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = this.uuidToProfileEntryMap.get(uuid);
        if (playerprofilecache$profileentry != null) {
            GameProfile gameprofile = playerprofilecache$profileentry.getGameProfile();
            this.gameProfiles.remove(gameprofile);
            this.gameProfiles.addFirst(gameprofile);
        }

        return playerprofilecache$profileentry;
    }

    public void load() {

    }

    public void save() {
        String s = this.gson.toJson(this.getEntriesWithLimit(1000));
        BufferedWriter bufferedwriter = null;

        try {
            if (this.usercacheFile != null) {
                bufferedwriter = new BufferedWriter(new java.io.OutputStreamWriter(this.usercacheFile.getOutputStream()));
                bufferedwriter.write(s);
            }
            return;
        } catch (FileNotFoundException var8) {
            ;
        } catch (IOException var9) {
            return;
        } finally {
            IOUtils.closeQuietly((Writer) bufferedwriter);
        }

    }

    private List<PlayerProfileCache.ProfileEntry> getEntriesWithLimit(int limitSize) {
        List<PlayerProfileCache.ProfileEntry> list = Lists.newArrayList();

        for (GameProfile gameprofile : Lists.newArrayList(Iterators.limit(this.gameProfiles.iterator(), limitSize))) {
            PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = this.getByUUID(gameprofile.getId());
            if (playerprofilecache$profileentry != null) {
                list.add(playerprofilecache$profileentry);
            }
        }

        return list;
    }

    class ProfileEntry {
        private final GameProfile gameProfile;
        private final Date expirationDate;

        private ProfileEntry(GameProfile gameProfileIn, Date expirationDateIn) {
            this.gameProfile = gameProfileIn;
            this.expirationDate = expirationDateIn;
        }

        public GameProfile getGameProfile() {
            return this.gameProfile;
        }

        public Date getExpirationDate() {
            return this.expirationDate;
        }
    }

    class Serializer implements JsonDeserializer<PlayerProfileCache.ProfileEntry>, JsonSerializer<PlayerProfileCache.ProfileEntry> {
        private Serializer() {
        }

        public JsonElement serialize(PlayerProfileCache.ProfileEntry p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("name", p_serialize_1_.getGameProfile().getName());
            EaglercraftUUID uuid = p_serialize_1_.getGameProfile().getId();
            jsonobject.addProperty("uuid", uuid == null ? "" : uuid.toString());
            jsonobject.addProperty("expiresOn", PlayerProfileCache.DATE_FORMAT.format(p_serialize_1_.getExpirationDate()));
            return jsonobject;
        }

        public PlayerProfileCache.ProfileEntry deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            if (p_deserialize_1_.isJsonObject()) {
                JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
                JsonElement jsonelement = jsonobject.get("name");
                JsonElement jsonelement1 = jsonobject.get("uuid");
                JsonElement jsonelement2 = jsonobject.get("expiresOn");
                if (jsonelement != null && jsonelement1 != null) {
                    String s = jsonelement1.getAsString();
                    String s1 = jsonelement.getAsString();
                    Date date = null;
                    if (jsonelement2 != null) {
                        try {
                            date = PlayerProfileCache.DATE_FORMAT.parse(jsonelement2.getAsString());
                        } catch (ParseException var14) {
                            date = null;
                        }
                    }

                    if (s1 != null && s != null) {
                        EaglercraftUUID uuid;
                        try {
                            uuid = EaglercraftUUID.fromString(s);
                        } catch (Throwable var13) {
                            return null;
                        }

                        return PlayerProfileCache.this.new ProfileEntry(new GameProfile(uuid, s1), date);
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }
}
