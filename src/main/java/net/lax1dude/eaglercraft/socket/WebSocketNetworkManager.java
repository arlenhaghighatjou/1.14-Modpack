package net.lax1dude.eaglercraft.socket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.lax1dude.eaglercraft.internal.EnumEaglerConnectionState;
import net.lax1dude.eaglercraft.internal.IWebSocketClient;
import net.lax1dude.eaglercraft.internal.IWebSocketFrame;
import net.minecraft.network.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.io.IOException;
import java.util.List;

public class WebSocketNetworkManager extends NetworkManager {

    protected final IWebSocketClient webSocketClient;
    private int compressionThreshold = -1;

    public WebSocketNetworkManager(IWebSocketClient webSocketClient) {
        super(PacketDirection.CLIENTBOUND);
        this.webSocketClient = webSocketClient;
    }

    public void connect() {
    }

    public EnumEaglerConnectionState getConnectStatus() {
        return webSocketClient.getState();
    }

    @Override
    public void closeChannel(ITextComponent reason) {
        webSocketClient.close();
        if (getNetHandler() != null) {
            getNetHandler().onDisconnect(reason);
        }
    }

    @Override
    public boolean isChannelOpen() {
        return webSocketClient.isOpen();
    }

    @Override
    public void processReceivedPackets() {
        if (getNetHandler() == null) return;
        if (webSocketClient.availableStringFrames() > 0) {
            logger.warn("discarding {} string frames recieved on a binary connection", webSocketClient.availableStringFrames());
            webSocketClient.clearStringFrames();
        }
        List<IWebSocketFrame> pkts = webSocketClient.getNextBinaryFrames();

        if (pkts == null) {
            return;
        }

        for (int i = 0, l = pkts.size(); i < l; ++i) {
            IWebSocketFrame next = pkts.get(i);
            ++debugPacketCounter;
            try {
                byte[] asByteArray = next.getByteArray();
                INetHandler netHandler = getNetHandler();
                if (netHandler instanceof net.minecraft.client.network.play.ClientPlayNetHandler) {
                    net.lax1dude.eaglercraft.socket.protocol.client.GameProtocolMessageController controller =
                            ((net.minecraft.client.network.play.ClientPlayNetHandler) netHandler).getEaglerMessageController();
                    if (controller != null && controller.handleInjectedPacket(asByteArray)) {
                        continue;
                    }
                }
                ByteBuf nettyBuffer = Unpooled.buffer(asByteArray, asByteArray.length);
                nettyBuffer.writerIndex(asByteArray.length);
                PacketBuffer input = new PacketBuffer(nettyBuffer);

                if (compressionThreshold == -1 && packetState == net.minecraft.network.ProtocolType.PLAY) {
                    input.markReaderIndex();
                    try {
                        int firstByte = input.readUnsignedByte();
                        if (firstByte == 0) {
                            logger.info("Auto-detected compressed connection (first byte == 0)");
                            compressionThreshold = 0;
                        } else {
                            logger.debug("Non-compressed connection detected. First byte={}", firstByte);
                            compressionThreshold = -2; // disabled
                        }
                    } catch (Exception e) {
                        compressionThreshold = -2;
                    }
                    input.resetReaderIndex();
                }

                if (compressionThreshold >= 0) {
                    int uncompressedSize = input.readVarInt();
                    if (uncompressedSize != 0) {
                        if (uncompressedSize < compressionThreshold) {
                            logger.debug("Compressed packet size {} below threshold {}, decompressing anyway", uncompressedSize, compressionThreshold);
                        }
                        byte[] decompressed = new byte[uncompressedSize];
                        int compressedLen = input.readableBytes();
                        byte[] compressedBytes = new byte[compressedLen];
                        input.readBytes(compressedBytes);
                        net.lax1dude.eaglercraft.EaglerZLIB.inflateFull(compressedBytes, decompressed);
                        input = new PacketBuffer(Unpooled.wrappedBuffer(decompressed));
                    }
                }

                int pktId = input.readVarInt();

                if (pktId >= 100) {
                    continue; // Ignore raw Eaglercraft RPC frames (like Voice Chat) safely without spamming logs
                }

                IPacket<?> pkt;
                pkt = packetState.getPacket(PacketDirection.CLIENTBOUND, pktId);

                if (pkt == null) {
                    throw new IOException("Recieved packet type " + pktId + " which is undefined in state " + packetState);
                }

                try {
                    pkt.readPacketData(input);
                } catch (Throwable t) {
                    throw new IOException("Failed to read packet type '" + pkt.getClass().getSimpleName() + "'", t);
                }

                try {
                    @SuppressWarnings("unchecked")
                    IPacket<INetHandler> typedPkt = (IPacket<INetHandler>) pkt;
                    typedPkt.processPacket(getNetHandler());
                } catch (Throwable t) {
                    logger.error("Failed to process {}! It'll be skipped for debug purposes.", pkt.getClass().getSimpleName());
                    logger.error(t);
                }

            } catch (Throwable t) {
                logger.error("Failed to process websocket frame {}! It'll be skipped for debug purposes.", debugPacketCounter);
                logger.error(t);
            }
        }
    }

    @Override
    public void sendPacket(IPacket<?> pkt) {
        if (!isChannelOpen()) {
            logger.error("Packet was sent on a closed connection: {}", pkt.getClass().getSimpleName());
            return;
        }

        int i;
        try {
            i = packetState.getPacketId(PacketDirection.SERVERBOUND, pkt);
        } catch (Throwable t) {
            logger.error("Incorrect packet for state: {}", pkt.getClass().getSimpleName());
            return;
        }

        temporaryBuffer.clear();
        if (compressionThreshold >= 0) {
            temporaryBuffer.writeVarInt(0);
        }
        temporaryBuffer.writeVarInt(i);
        try {
            pkt.writePacketData(temporaryBuffer);
        } catch (IOException ex) {
            logger.error("Failed to write packet {}!", pkt.getClass().getSimpleName());
            return;
        }

        int len = temporaryBuffer.writerIndex();
        byte[] bytes = new byte[len];
        temporaryBuffer.getBytes(0, bytes);

        webSocketClient.send(bytes);
    }

    @Override
    public boolean checkDisconnected() {
        if (webSocketClient.isClosed()) {
            closeChannel(new TranslationTextComponent("disconnect.endOfStream"));
            return true;
        } else {
            return false;
        }
    }

}
