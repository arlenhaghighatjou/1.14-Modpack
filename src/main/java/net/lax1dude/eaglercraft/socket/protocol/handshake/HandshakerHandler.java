/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.lax1dude.eaglercraft.socket.protocol.handshake;

import io.netty.buffer.Unpooled;
import net.lax1dude.eaglercraft.ArrayUtils;
import net.lax1dude.eaglercraft.EaglerOutputStream;
import net.lax1dude.eaglercraft.EaglercraftVersion;
import net.lax1dude.eaglercraft.internal.IWebSocketClient;
import net.lax1dude.eaglercraft.internal.IWebSocketFrame;
import net.lax1dude.eaglercraft.profile.GuiAuthenticationScreen;
import net.lax1dude.eaglercraft.socket.HandshakePacketTypes;
import net.lax1dude.eaglercraft.socket.RateLimitTracker;
import net.lax1dude.eaglercraft.socket.WebSocketNetworkManager;
import net.lax1dude.eaglercraft.socket.protocol.GamePluginMessageConstants;
import net.lax1dude.eaglercraft.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.socket.protocol.client.GameProtocolMessageController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HandshakerHandler {

    protected static final int NEW = 0, SENT_HANDSHAKE = 1, PROCESSING = 2, FINISHED = 3;
    static final Logger logger = LogManager.getLogger("HandshakerHandler");
    private static final int protocolV3 = 3;
    private static final int protocolV4 = 4;
    private static final int protocolV5 = 5;
    protected final Minecraft mc;
    protected final IWebSocketClient websocket;
    protected final ConnectingScreen parent;
    protected final Screen ret;
    protected final String username;
    protected final String password;
    protected final boolean allowPlaintext;
    protected final boolean enableCookies;
    protected final byte[] cookieData;
    protected HandshakerInstance handshaker;
    protected boolean nicknameSelection = true;
    protected int baseState = NEW;
    protected WebSocketNetworkManager networkManager;
    protected int handshakeProtocolVersion = -1;
    public HandshakerHandler(ConnectingScreen parent, IWebSocketClient websocket, String username, String password,
                             boolean allowPlaintext, boolean enableCookies, byte[] cookieData) {
        this.mc = parent.mc;
        this.websocket = websocket;
        this.parent = parent;
        this.ret = parent.getPreviousGuiScreen();
        this.username = username;
        this.password = password;
        this.allowPlaintext = allowPlaintext;
        this.enableCookies = enableCookies;
        this.cookieData = cookieData;
    }

    public static byte[] getSPHandshakeProtocolData() {
        try {
            EaglerOutputStream bao = new EaglerOutputStream();
            DataOutputStream d = new DataOutputStream(bao);
            d.writeShort(3); // supported eagler protocols count
            d.writeShort(protocolV3); // client supports v3
            d.writeShort(protocolV4); // client supports v4
            d.writeShort(protocolV5); // client supports v5
            return bao.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected static void writeASCII(PacketBuffer buffer, String str) {
        for (int i = 0, l = str.length(); i < l; ++i) {
            buffer.writeByte(str.charAt(i));
        }
    }

    protected static byte[] toByteArray(PacketBuffer buffer) {
        int readerIndex = buffer.readerIndex();
        int writerIndex = buffer.writerIndex();
        byte[] bytes = new byte[writerIndex - readerIndex];
        buffer.getBytes(readerIndex, bytes);
        return bytes;
    }

    public void tick() {
        if (baseState == NEW) {
            if (websocket.isClosed()) {
                handleError("Connection Closed", null);
                return;
            }
            baseState = SENT_HANDSHAKE;
            beginHandshake();
        } else if (baseState == SENT_HANDSHAKE) {
            IWebSocketFrame frame = websocket.getNextBinaryFrame();
            if (frame != null) {
                byte[] data = frame.getByteArray();
                handleServerHandshake(new PacketBuffer(Unpooled.buffer(data, data.length).writerIndex(data.length)));
            }
        } else if (baseState == PROCESSING) {
            handshaker.tick();
        } else if (baseState == FINISHED) {
            if (networkManager != null) {
                try {
                    networkManager.processReceivedPackets();
                } catch (Exception e) {
                    logger.error("Failed to process received packets", e);
                }
            }
        }
    }

    protected void beginHandshake() {
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());

        buffer.writeByte(HandshakePacketTypes.PROTOCOL_CLIENT_VERSION);

        buffer.writeByte(2); // legacy protocol version

        buffer.writeBytes(getSPHandshakeProtocolData()); // write supported eagler protocol versions

        buffer.writeShort(1); // supported game protocols count
        buffer.writeShort(net.minecraft.util.SharedConstants.getVersion().getProtocolVersion()); // client supports 1.14 protocol

        String clientBrand = EaglercraftVersion.projectForkName;
        buffer.writeByte(clientBrand.length());
        writeASCII(buffer, clientBrand);

        String clientVers = EaglercraftVersion.projectOriginVersion;
        buffer.writeByte(clientVers.length());
        writeASCII(buffer, clientVers);

        buffer.writeBoolean(password != null);

        buffer.writeByte(username.length());
        writeASCII(buffer, username);

        websocket.send(toByteArray(buffer));
    }

    protected void handleServerHandshake(PacketBuffer packet) {
        try {
            int pktId = packet.readUnsignedByte();
            switch (pktId) {
                case HandshakePacketTypes.PROTOCOL_SERVER_VERSION:
                    handleServerVersion(packet);
                    break;
                case HandshakePacketTypes.PROTOCOL_VERSION_MISMATCH:
                    handleVersionMismatch(packet);
                    break;
                case HandshakePacketTypes.PROTOCOL_SERVER_ERROR:
                    handleServerError(packet, false);
                    break;
                default:
                    handleError("connect.failed", new StringTextComponent("Unknown packet type " + pktId + " received"));
                    break;
            }
        } catch (Exception ex) {
            handleError("connect.failed", new StringTextComponent("Invalid packet received"));
            logger.error("Invalid packet received");
            logger.error(ex);
        }
    }

    protected void handleServerVersion(PacketBuffer packet) {
        int protocolVersion = packet.readUnsignedShort();
        handshakeProtocolVersion = protocolVersion;

        if (protocolVersion != protocolV3 && protocolVersion != protocolV4 && protocolVersion != protocolV5) {
            logger.info("Incompatible server version: {}", protocolVersion);
            handleError("connect.failed", new StringTextComponent(protocolVersion < protocolV3 ? "Outdated Server" : "Outdated Client"));
            return;
        }

        int gameVers = packet.readUnsignedShort();
        if (gameVers != net.minecraft.util.SharedConstants.getVersion().getProtocolVersion()) {
            logger.info("Incompatible minecraft protocol version: {}", gameVers);
            handleError("connect.failed", new StringTextComponent("This server does not support 1.14.4!"));
            return;
        }

        logger.info("Server protocol: {}", protocolVersion);

        int msgLen = packet.readUnsignedByte();
        byte[] dat = new byte[msgLen];
        packet.readBytes(dat);
        String pluginBrand = ArrayUtils.asciiString(dat);

        msgLen = packet.readUnsignedByte();
        dat = new byte[msgLen];
        packet.readBytes(dat);
        String pluginVersion = ArrayUtils.asciiString(dat);

        logger.info("Server version: {}", pluginVersion);
        logger.info("Server brand: {}", pluginBrand);

        int authType = packet.readUnsignedByte();
        int saltLength = (int) packet.readUnsignedShort() & 0xFFFF;

        byte[] salt = new byte[saltLength];
        packet.readBytes(salt);

        if (protocolVersion >= protocolV5) {
            nicknameSelection = packet.readBoolean();
        }

        baseState = PROCESSING;
        switch (protocolVersion) {
            case protocolV3:
                handshaker = new HandshakerV3(this);
                break;
            case protocolV4:
                handshaker = new HandshakerV4(this);
                break;
            case protocolV5:
                handshaker = new HandshakerV5(this);
                break;
        }

        handshaker.begin(pluginBrand, pluginVersion, authType, salt);
    }

    protected void handleVersionMismatch(PacketBuffer packet) {
        StringBuilder protocols = new StringBuilder();
        int c = packet.readUnsignedShort();
        for (int i = 0; i < c; ++i) {
            if (i > 0) {
                protocols.append(", ");
            }
            protocols.append("v").append(packet.readUnsignedShort());
        }

        StringBuilder games = new StringBuilder();
        c = packet.readUnsignedShort();
        for (int i = 0; i < c; ++i) {
            if (i > 0) {
                games.append(", ");
            }
            games.append("mc").append(packet.readUnsignedShort());
        }

        logger.info("Incompatible client: v3/v4/v5 & mc47");
        logger.info("Server supports: {}", protocols);
        logger.info("Server supports: {}", games);

        int msgLen = packet.readUnsignedByte();
        byte[] dat = new byte[msgLen];
        packet.readBytes(dat);
        String msg = new String(dat, StandardCharsets.UTF_8);

        handleError("connect.failed", new StringTextComponent(msg));
    }

    protected void handleServerError(PacketBuffer packet, boolean v3) {
        int errCode = packet.readUnsignedByte();
        int msgLen;
        if (v3) {
            msgLen = packet.readUnsignedShort();
            if (msgLen == 0 && packet.readableBytes() == 65536) {
                // workaround for bug in EaglerXBungee 1.2.7 and below
                msgLen = 65536;
            }
        } else {
            msgLen = packet.readUnsignedByte();
            if (msgLen == 0 && packet.readableBytes() == 256) {
                // workaround for bug in EaglerXBungee 1.2.7 and below
                msgLen = 256;
            }
        }
        byte[] dat = new byte[msgLen];
        packet.readBytes(dat);
        String msg = new String(dat, StandardCharsets.UTF_8);
        if (errCode == HandshakePacketTypes.SERVER_ERROR_RATELIMIT_BLOCKED) {
            handleRatelimit(false, new StringTextComponent(msg));
        } else if (errCode == HandshakePacketTypes.SERVER_ERROR_RATELIMIT_LOCKED) {
            handleRatelimit(true, new StringTextComponent(msg));
        } else if (errCode == HandshakePacketTypes.SERVER_ERROR_AUTHENTICATION_REQUIRED) {
            handleAuthRequired(msg);
        } else if (errCode == HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE) {
            ITextComponent errorComp;
            if (v3) {
                try {
                    errorComp = ITextComponent.Serializer.fromJson(msg);
                    if (errorComp == null) errorComp = new StringTextComponent(msg);
                } catch (Exception ex) {
                    errorComp = new StringTextComponent(msg);
                }
            } else {
                errorComp = new StringTextComponent(msg);
            }
            handleError("connect.failed", errorComp);
        } else {
            handleError("connect.failed", new StringTextComponent("Server Error Code " + errCode + "\n" + msg));
        }
    }

    protected void handleSuccess() {
        if (baseState != FINISHED) {
            baseState = FINISHED;
            websocket.setEnableStringFrames(false);
            websocket.clearStringFrames();
            networkManager = new WebSocketNetworkManager(websocket);

            mc.getSession().update(handshaker.username, handshaker.uuid);
            networkManager.setConnectionState(net.minecraft.network.ProtocolType.PLAY);
            net.minecraft.client.network.play.ClientPlayNetHandler nhpc = new net.minecraft.client.network.play.ClientPlayNetHandler(
                    this.mc, this.ret, networkManager, this.mc.getSession().getProfile());

            int gameProtocol = handshakeProtocolVersion;
            if (gameProtocol < 0) gameProtocol = 3;
            GamePluginMessageProtocol mp = GamePluginMessageProtocol.getByVersion(gameProtocol);
            if (mp != null) {
                nhpc.setEaglerMessageController(
                        new GameProtocolMessageController(mp, GamePluginMessageConstants.CLIENT_TO_SERVER,
                                GameProtocolMessageController.createClientHandler(gameProtocol, nhpc),
                                (ch, msg) -> nhpc.sendPacket(new CCustomPayloadPacket(new ResourceLocation(net.lax1dude.eaglercraft.socket.protocol.GamePluginMessageConstants.toResourceLocation(ch)), msg)),
                                websocket::send));
            }

            networkManager.setNetHandler(nhpc);
        }
    }

    protected void handleServerRedirectTo(String address) {
        mc.handleReconnectPacket(address);
        websocket.close();
        if (baseState != FINISHED) {
            baseState = FINISHED;
            mc.displayGuiScreen(ret);
        }
    }

    protected void handleRatelimit(boolean locked, ITextComponent detail) {
        if (locked) {
            RateLimitTracker.registerLockOut(websocket.getCurrentURI());
        } else {
            RateLimitTracker.registerBlock(websocket.getCurrentURI());
        }
        websocket.close();
        if (baseState != FINISHED) {
            baseState = FINISHED;
            mc.displayGuiScreen(new DisconnectedScreen(ret, "connect.failed", new StringTextComponent("Too Many Requests!\nTry again later")));
        }
    }

    protected void handleError(String message, ITextComponent detail) {
        websocket.close();
        if (baseState != FINISHED) {
            baseState = FINISHED;
            mc.displayGuiScreen(new DisconnectedScreen(ret, message, detail != null ? detail : new StringTextComponent("")));
        }
    }

    protected void handleAuthRequired(String message) {
        websocket.close();
        if (baseState != FINISHED) {
            baseState = FINISHED;
            mc.displayGuiScreen(new GuiAuthenticationScreen(parent, ret, message));
        }
    }

}
