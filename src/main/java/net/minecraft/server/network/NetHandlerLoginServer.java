package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import net.lax1dude.eaglercraft.*;
import net.lax1dude.eaglercraft.sp.server.EaglerMinecraftServer;
import net.lax1dude.eaglercraft.sp.server.socket.IntegratedServerPlayerNetworkManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.login.IServerLoginNetHandler;
import net.minecraft.network.login.client.CCustomPayloadLoginPacket;
import net.minecraft.network.login.client.CEncryptionResponsePacket;
import net.minecraft.network.login.client.CLoginStartPacket;
import net.minecraft.network.login.server.SDisconnectLoginPacket;
import net.minecraft.network.login.server.SLoginSuccessPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class NetHandlerLoginServer implements IServerLoginNetHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final EaglercraftRandom RANDOM = new EaglercraftRandom();
    private final byte[] verifyToken = new byte[4];
    private final MinecraftServer server;
    public final IntegratedServerPlayerNetworkManager networkManager;
    private NetHandlerLoginServer.LoginState currentLoginState = NetHandlerLoginServer.LoginState.HELLO;

    private int connectionTimer;
    private GameProfile loginGameProfile;
    private byte[] loginSkinPacket;
    private byte[] loginCapePacket;
    private int selectedProtocol = 3;
    private EaglercraftUUID clientBrandUUID;
    private String serverId = "";
    private ServerPlayerEntity player;

    public NetHandlerLoginServer(MinecraftServer serverIn, IntegratedServerPlayerNetworkManager networkManagerIn) {
        this.server = serverIn;
        this.networkManager = networkManagerIn;
        RANDOM.nextBytes(this.verifyToken);
    }

    public void update() {
        if (this.currentLoginState == NetHandlerLoginServer.LoginState.READY_TO_ACCEPT) {
            this.tryAcceptPlayer();
        } else if (this.currentLoginState == NetHandlerLoginServer.LoginState.DELAY_ACCEPT) {
            ServerPlayerEntity entityplayermp = this.server.getPlayerList().getPlayerByUUID(this.loginGameProfile.getId());

            if (entityplayermp == null) {
                this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
                this.server.getPlayerList().initializeConnectionToPlayer(this.networkManager,
                        this.player, this.selectedProtocol, this.clientBrandUUID);
                ((EaglerMinecraftServer) player.server).getSkinService()
                        .processLoginPacket(this.loginSkinPacket, player, this.selectedProtocol);
                if (this.loginCapePacket != null) {
                    ((EaglerMinecraftServer) player.server).getCapeService()
                            .processLoginPacket(this.loginCapePacket, player);
                }
                this.player = null;
            }
        }

        if (this.connectionTimer++ == 600) {
            this.func_194026_b(new TranslationTextComponent("multiplayer.disconnect.slow_login"));
        }
    }

    public void func_194026_b(ITextComponent p_194026_1_) {
        try {
            LOGGER.info("Disconnecting {}: {}", this.getConnectionInfo(), p_194026_1_.getString());
            this.networkManager.sendPacket(new SDisconnectLoginPacket(p_194026_1_));
            this.networkManager.closeChannel(p_194026_1_);
        } catch (Exception exception) {
            LOGGER.error("Error whilst disconnecting player", (Throwable) exception);
        }
    }

    public void tryAcceptPlayer() {
        String s = this.server.getPlayerList().allowUserToConnect(this.loginGameProfile);
        if (s != null) {
            this.func_194026_b(new StringTextComponent(s));
        } else {
            this.currentLoginState = NetHandlerLoginServer.LoginState.ACCEPTED;
            this.networkManager.sendPacket(new SLoginSuccessPacket(this.loginGameProfile, this.selectedProtocol));
            this.networkManager.setConnectionState(ProtocolType.PLAY);
            ServerPlayerEntity entityplayermp = this.server.getPlayerList().getPlayerByUUID(this.loginGameProfile.getId());
            if (entityplayermp != null) {
                this.currentLoginState = NetHandlerLoginServer.LoginState.DELAY_ACCEPT;
                this.player = this.server.getPlayerList().createPlayerForUser(this.loginGameProfile);
            } else {
                entityplayermp = this.server.getPlayerList().createPlayerForUser(this.loginGameProfile);
                this.server.getPlayerList().initializeConnectionToPlayer(this.networkManager, entityplayermp,
                        this.selectedProtocol, this.clientBrandUUID);
                ((EaglerMinecraftServer) entityplayermp.server).getSkinService()
                        .processLoginPacket(this.loginSkinPacket, entityplayermp, this.selectedProtocol);
                if (this.loginCapePacket != null) {
                    ((EaglerMinecraftServer) entityplayermp.server).getCapeService()
                            .processLoginPacket(this.loginCapePacket, entityplayermp);
                }
            }
        }

    }

    public void onDisconnect(ITextComponent reason) {
        LOGGER.info("{} lost connection: {}", this.getConnectionInfo(), reason.getString());
    }

    @Override
    public NetworkManager getNetworkManager() {
        return null;
    }

    public String getConnectionInfo() {
        return this.loginGameProfile != null
                ? this.loginGameProfile.toString() + " (channel:" + this.networkManager.playerChannel + ")"
                : ("channel:" + this.networkManager.playerChannel);
    }

    public void processLoginStart(CLoginStartPacket packetIn) {
        Validate.validState(this.currentLoginState == NetHandlerLoginServer.LoginState.HELLO, "Unexpected hello packet");
        if (packetIn.getProtocols() != null) {
            try {
                DataInputStream dis = new DataInputStream(new EaglerInputStream(packetIn.getProtocols()));
                int maxSupported = -1;
                int protocolCount = dis.readUnsignedShort();
                for (int i = 0; i < protocolCount; ++i) {
                    int p = dis.readUnsignedShort();
                    if ((p == 3 || p == 4) && p > maxSupported) {
                        maxSupported = p;
                    }
                }
                if (maxSupported != -1) {
                    selectedProtocol = maxSupported;
                } else {
                    this.func_194026_b(new StringTextComponent("Unknown protocol!"));
                    return;
                }
            } catch (IOException ex) {
                selectedProtocol = 3;
            }
        } else {
            selectedProtocol = 3;
        }
        this.loginGameProfile = this.getOfflineProfile(packetIn.getProfile());
        this.loginSkinPacket = packetIn.getSkin();
        this.loginCapePacket = packetIn.getCape();
        this.clientBrandUUID = selectedProtocol <= 3 ? EaglercraftVersion.legacyClientUUIDInSharedWorld : packetIn.getBrandUUID();
        if (ClientUUIDLoadingCache.PENDING_UUID.equals(clientBrandUUID)
                || ClientUUIDLoadingCache.VANILLA_UUID.equals(clientBrandUUID)) {
            this.clientBrandUUID = null;
        }
        this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
    }

    public void processEncryptionResponse(CEncryptionResponsePacket packetIn) {
    }

    public void processCustomPayloadLogin(CCustomPayloadLoginPacket packetIn) {
    }

    protected GameProfile getOfflineProfile(GameProfile original) {
        EaglercraftUUID uuid = EaglercraftUUID
                .nameUUIDFromBytes(("OfflinePlayer:" + original.getName()).getBytes(StandardCharsets.UTF_8));
        return new GameProfile(new EaglercraftUUID(uuid.msb, uuid.lsb), original.getName());
    }

    static enum LoginState {
        HELLO, KEY, AUTHENTICATING, READY_TO_ACCEPT, DELAY_ACCEPT, ACCEPTED;
    }
}
