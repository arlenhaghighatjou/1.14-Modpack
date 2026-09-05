package net.minecraft.client.network.play;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

import net.lax1dude.eaglercraft.profile.SkinModel;

@OnlyIn(Dist.CLIENT)
public class NetworkPlayerInfo {
    private final GameProfile gameProfile;
    private final Map<Type, ResourceLocation> playerTextures = Maps.newEnumMap(Type.class);
    private GameType gameType;
    private int responseTime;
    private boolean playerTexturesLoaded;
    private String skinType;
    private ITextComponent displayName;
    private ITextComponent displayNameProfanityFilter;
    private int lastHealth;
    private int displayHealth;
    private long lastHealthTime;
    private long healthBlinkTime;
    private long renderVisibilityId;

    public NetworkPlayerInfo(GameProfile profile) {
        this.gameProfile = profile;
    }

    public NetworkPlayerInfo(SPlayerListItemPacket.AddPlayerData entry) {
        this.gameProfile = entry.getProfile();
        this.gameType = entry.getGameMode();
        this.responseTime = entry.getPing();
        this.displayName = entry.getDisplayName();
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    public GameType getGameType() {
        return this.gameType;
    }

    protected void setGameType(GameType gameMode) {
        this.gameType = gameMode;
    }

    public int getResponseTime() {
        return this.responseTime;
    }

    protected void setResponseTime(int latency) {
        this.responseTime = latency;
    }

    public boolean hasLocationSkin() {
        return this.getLocationSkin() != null;
    }

    public String getSkinType() {
        if (Minecraft.getInstance().getConnection() != null) {
            net.lax1dude.eaglercraft.profile.ServerSkinCache cache = Minecraft.getInstance().getConnection().getSkinCache();
            if (cache != null) {
                return cache.getSkin(this.gameProfile).getSkinModel().profileSkinType;
            }
        }
        return DefaultPlayerSkin.getSkinType(this.gameProfile.getId());
    }

    public SkinModel getEaglerSkinModel() {
        if (Minecraft.getInstance().getConnection() != null) {
            net.lax1dude.eaglercraft.profile.ServerSkinCache cache = Minecraft.getInstance().getConnection().getSkinCache();
            if (cache != null) {
                return cache.getSkin(this.gameProfile).getSkinModel();
            }
        }
        return SkinModel.STEVE;
    }

    public ResourceLocation getLocationSkin() {
        if (Minecraft.getInstance().getConnection() != null) {
            net.lax1dude.eaglercraft.profile.ServerSkinCache cache = Minecraft.getInstance().getConnection().getSkinCache();
            if (cache != null) {
                return cache.getSkin(this.gameProfile).getResourceLocation();
            }
        }
        return DefaultPlayerSkin.getDefaultSkin(this.gameProfile.getId());
    }

    public ResourceLocation getLocationCape() {
        if (Minecraft.getInstance().getConnection() != null) {
            net.lax1dude.eaglercraft.profile.ServerCapeCache cache = Minecraft.getInstance().getConnection().getCapeCache();
            if (cache != null) {
                return cache.getCape(this.gameProfile.getId()).getResourceLocation();
            }
        }
        return null;
    }

    public ResourceLocation getLocationElytra() {
        return null;
    }

    public ScorePlayerTeam getPlayerTeam() {
        return Minecraft.getInstance().world.getScoreboard().getPlayersTeam(this.getGameProfile().getName());
    }

    protected void loadPlayerTextures() {

    }

    public void setDisplayName(ITextComponent displayNameIn) {
        this.displayName = displayNameIn;
    }

    public ITextComponent getDisplayName() {
        return this.displayName;
    }

    public ITextComponent getDisplayNameProfanityFilter() {
        if (net.minecraft.client.Minecraft.getInstance().isEnableProfanityFilter()) {
            if (displayNameProfanityFilter == null && displayName != null) {
                displayNameProfanityFilter = net.lax1dude.eaglercraft.profanity_filter.ProfanityFilter.getInstance().profanityFilterChatComponent(displayName);
            }
            return displayNameProfanityFilter != null ? displayNameProfanityFilter : displayName;
        }
        return displayName;
    }

    public int getLastHealth() {
        return this.lastHealth;
    }

    public void setLastHealth(int p_178836_1_) {
        this.lastHealth = p_178836_1_;
    }

    public int getDisplayHealth() {
        return this.displayHealth;
    }

    public void setDisplayHealth(int p_178857_1_) {
        this.displayHealth = p_178857_1_;
    }

    public long getLastHealthTime() {
        return this.lastHealthTime;
    }

    public void setLastHealthTime(long p_178846_1_) {
        this.lastHealthTime = p_178846_1_;
    }

    public long getHealthBlinkTime() {
        return this.healthBlinkTime;
    }

    public void setHealthBlinkTime(long p_178844_1_) {
        this.healthBlinkTime = p_178844_1_;
    }

    public long getRenderVisibilityId() {
        return this.renderVisibilityId;
    }

    public void setRenderVisibilityId(long p_178843_1_) {
        this.renderVisibilityId = p_178843_1_;
    }
}
