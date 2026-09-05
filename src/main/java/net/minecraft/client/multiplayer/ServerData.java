package net.minecraft.client.multiplayer;

import net.lax1dude.eaglercraft.internal.IServerQuery;
import net.lax1dude.eaglercraft.internal.QueryResponse;
import net.lax1dude.eaglercraft.profile.EaglerSkinTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
public class ServerData {
    private static final Logger logger = LogManager.getLogger("MOTDQuery");
    public String serverName;
    public String serverIP;
    public String populationInfo;
    public String serverMOTD;
    public long pingToServer;
    public int version = SharedConstants.getVersion().getProtocolVersion();
    public String gameVersion = SharedConstants.getVersion().getName();
    public boolean pinged;
    public String playerList;
    private ServerData.ServerResourceMode resourceMode = ServerData.ServerResourceMode.PROMPT;
    private String serverIcon;
    public boolean lanServer;
    public boolean enableCookies = false;
    public IServerQuery currentQuery = null;
    public long pingSentTime = -1l;
    public boolean hasPing = false;
    public boolean serverIconDirty = false;
    public boolean serverIconEnabled = false;
    public final ResourceLocation iconResourceLocation;
    public EaglerSkinTexture iconTextureObject = null;
    private static int serverTextureId = 0;

    public ServerData(String name, String ip, boolean isLan) {
        this.serverName = name;
        this.serverIP = ip;
        this.lanServer = isLan;
        this.iconResourceLocation = new ResourceLocation("eagler:servers/icons/tex_" + serverTextureId++);

    }

    public CompoundNBT getNBTCompound() {
        CompoundNBT compoundnbt = new CompoundNBT();
        compoundnbt.putString("name", this.serverName);
        compoundnbt.putString("ip", this.serverIP);
        if (this.serverIcon != null) {
            compoundnbt.putString("icon", this.serverIcon);
        }

        if (this.resourceMode == ServerData.ServerResourceMode.ENABLED) {
            compoundnbt.putBoolean("acceptTextures", true);
        } else if (this.resourceMode == ServerData.ServerResourceMode.DISABLED) {
            compoundnbt.putBoolean("acceptTextures", false);
        }

        return compoundnbt;
    }

    public ServerData.ServerResourceMode getResourceMode() {
        return this.resourceMode;
    }

    public void setResourceMode(ServerData.ServerResourceMode mode) {
        this.resourceMode = mode;
    }

    public static ServerData getServerDataFromNBTCompound(CompoundNBT nbtCompound) {
        ServerData serverdata = new ServerData(nbtCompound.getString("name"), nbtCompound.getString("ip"), false);
        if (nbtCompound.contains("icon", 8)) {
            serverdata.setBase64EncodedIconData(nbtCompound.getString("icon"));
        }

        if (nbtCompound.contains("acceptTextures", 1)) {
            if (nbtCompound.getBoolean("acceptTextures")) {
                serverdata.setResourceMode(ServerData.ServerResourceMode.ENABLED);
            } else {
                serverdata.setResourceMode(ServerData.ServerResourceMode.DISABLED);
            }
        } else {
            serverdata.setResourceMode(ServerData.ServerResourceMode.PROMPT);
        }

        return serverdata;
    }

    public String getBase64EncodedIconData() {
        return this.serverIcon;
    }

    public void setBase64EncodedIconData(String icon) {
        this.serverIcon = icon;
    }

    public boolean isOnLAN() {
        return this.lanServer;
    }

    public void copyFrom(ServerData serverDataIn) {
        this.serverIP = serverDataIn.serverIP;
        this.serverName = serverDataIn.serverName;
        this.setResourceMode(serverDataIn.getResourceMode());
        this.serverIcon = serverDataIn.serverIcon;
        this.lanServer = serverDataIn.lanServer;
        this.enableCookies = serverDataIn.enableCookies;
    }

    public void setMOTDFromQuery(QueryResponse pkt) {
        try {
            if (pkt.isResponseJSON()) {
                JSONObject motdData = pkt.getResponseJSON();
                JSONArray motd = motdData.getJSONArray("motd");
                this.serverMOTD = motd.length() > 0
                        ? (motd.length() > 1 ? motd.getString(0) + "\n" + motd.getString(1) : motd.getString(0))
                        : "";
                int max = motdData.optInt("max", 0);
                if (max > 0) {
                    this.populationInfo = "" + motdData.optInt("online", 0) + "/" + max;
                } else {
                    this.populationInfo = "" + motdData.optInt("online", 0);
                }
                this.playerList = null;
                JSONArray players = motdData.optJSONArray("players");
                if (players != null && players.length() > 0) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0, l = players.length(); i < l; ++i) {
                        if (i > 0) {
                            builder.append('\n');
                        }
                        builder.append(players.getString(i));
                    }
                    this.playerList = builder.toString();
                }
                serverIconEnabled = motdData.optBoolean("icon", false);
                if (!serverIconEnabled) {
                    this.serverIcon = null;
                }

                this.pinged = true;
                if (this.pingToServer <= 0) {
                    this.pingToServer = 10L;
                }
            } else {
                throw new IOException("Response was not JSON!");
            }
        } catch (Throwable t) {
            pingToServer = -1l;
            logger.error("Could not decode QueryResponse from: {}", serverIP);
            logger.error(t);
        }
    }

    public void setIconPacket(byte[] pkt) {
        try {
            if (!serverIconEnabled) {
                throw new IOException("Unexpected icon packet on text-only MOTD");
            }
            if (pkt.length != 16384) {
                throw new IOException("MOTD icon packet is the wrong size!");
            }
            int[] pixels = new int[4096];
            for (int i = 0, j; i < 4096; ++i) {
                j = i << 2;
                pixels[i] = ((int) pkt[j] & 0xFF) | (((int) pkt[j + 1] & 0xFF) << 8) | (((int) pkt[j + 2] & 0xFF) << 16)
                        | (((int) pkt[j + 3] & 0xFF) << 24);
            }
            if (iconTextureObject != null) {
                iconTextureObject.copyPixelsIn(pixels);
            } else {
                iconTextureObject = new EaglerSkinTexture(pixels, 64, 64);
                Minecraft.getInstance().getTextureManager().loadTexture(iconResourceLocation, iconTextureObject);
            }
        } catch (Throwable t) {
            pingToServer = -1l;
            logger.error("Could not decode MOTD icon from: {}", serverIP);
            logger.error(t);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static enum ServerResourceMode {
        ENABLED("enabled"),
        DISABLED("disabled"),
        PROMPT("prompt");

        private final ITextComponent motd;

        private ServerResourceMode(String name) {
            this.motd = new TranslationTextComponent("addServer.resourcePack." + name);
        }

        public ITextComponent getMotd() {
            return this.motd;
        }
    }
}
