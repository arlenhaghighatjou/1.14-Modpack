package net.lax1dude.eaglercraft.sp.server.socket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.lax1dude.eaglercraft.EaglerOutputStream;
import net.lax1dude.eaglercraft.EaglerZLIB;
import net.lax1dude.eaglercraft.internal.EnumEaglerConnectionState;
import net.lax1dude.eaglercraft.internal.IPCPacketData;
import net.lax1dude.eaglercraft.sp.SingleplayerServerController;
import net.lax1dude.eaglercraft.sp.server.EaglerIntegratedServerWorker;
import net.lax1dude.eaglercraft.sp.server.internal.ServerPlatformSingleplayer;
import net.minecraft.network.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class IntegratedServerPlayerNetworkManager extends NetworkManager {

    public static final int fragmentSize = 0xFF00;
    public static final int compressionThreshold = 1024;
    private static byte[] compressedPacketTmp;
    public final String playerChannel;
    private final List<byte[]> recievedPacketBuffer = new LinkedList<>();
    private final boolean enableSendCompression;
    private boolean firstPacket = true;
    private List<byte[]> fragmentedPacket = new ArrayList<>();

    public IntegratedServerPlayerNetworkManager(String playerChannel) {
        super(PacketDirection.SERVERBOUND);
        this.playerChannel = playerChannel;
        this.enableSendCompression = !SingleplayerServerController.PLAYER_CHANNEL.equals(playerChannel);
    }

    @Override
    public void setConnectionState(ProtocolType newState) {
        this.packetState = newState;
    }

    @Override
    public void setNetHandler(INetHandler handler) {
        super.setNetHandler(handler);
    }

    @Override
    public boolean isChannelOpen() {
        return EaglerIntegratedServerWorker.getChannelExists(playerChannel);
    }

    @Override
    public void connect() {
        fragmentedPacket.clear();
        firstPacket = true;
    }

    @Override
    public EnumEaglerConnectionState getConnectStatus() {
        return EaglerIntegratedServerWorker.getChannelExists(playerChannel) ? EnumEaglerConnectionState.CONNECTED : EnumEaglerConnectionState.CLOSED;
    }

    @Override
    public void closeChannel(ITextComponent reason) {
        EaglerIntegratedServerWorker.closeChannel(playerChannel);
        if (getNetHandler() != null) {
            getNetHandler().onDisconnect(reason);
        }
    }

    public void addRecievedPacket(byte[] next) {
        recievedPacketBuffer.add(next);
    }

    @Override
    public void processReceivedPackets() {
        if (getNetHandler() == null) return;

        while (!recievedPacketBuffer.isEmpty()) {
            byte[] data = recievedPacketBuffer.remove(0);
            byte[] fullData;

            if (enableSendCompression) {
                if (firstPacket) {
                    if (data.length > 2 && data[0] == (byte) 0x02 && data[1] == (byte) 0x3D) {
                        EaglerOutputStream kickPacketBAO = new EaglerOutputStream();
                        try {
                            DataOutputStream kickDAO = new DataOutputStream(kickPacketBAO);
                            kickDAO.write(0);
                            kickDAO.write(0xFF);
                            String msg = "This is an EaglercraftX 1.8 LAN world!";
                            kickDAO.write(0x00);
                            kickDAO.write(msg.length());
                            for (int j = 0, l = msg.length(); j < l; ++j) {
                                kickDAO.writeChar(msg.charAt(j));
                            }
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        ServerPlatformSingleplayer.sendPacket(new IPCPacketData(playerChannel, kickPacketBAO.toByteArray()));
                        closeChannel(new StringTextComponent("Recieved unsuppoorted connection from an Eaglercraft 1.5.2 client!"));
                        firstPacket = false;
                        return;
                    }
                    firstPacket = false;
                }
                if (data[0] == 0) {
                    if (fragmentedPacket.isEmpty()) {
                        fullData = new byte[data.length - 1];
                        System.arraycopy(data, 1, fullData, 0, fullData.length);
                    } else {
                        fragmentedPacket.add(data);
                        int len = 0;
                        int fragCount = fragmentedPacket.size();
                        for (int j = 0; j < fragCount; ++j) {
                            len += fragmentedPacket.get(j).length - 1;
                        }
                        fullData = new byte[len];
                        len = 0;
                        for (int j = 0; j < fragCount; ++j) {
                            byte[] f = fragmentedPacket.get(j);
                            System.arraycopy(f, 1, fullData, len, f.length - 1);
                            len += f.length - 1;
                        }
                        fragmentedPacket.clear();
                    }
                } else if (data[0] == 1) {
                    fragmentedPacket.add(data);
                    continue;
                } else {
                    logger.error("Recieved {} byte fragment of unknown type: {}", data.length, ((int) data[0] & 0xFF));
                    continue;
                }

            } else {
                fullData = data;
            }

            ++debugPacketCounter;
            try {
                ByteBuf nettyBuffer = Unpooled.buffer(fullData, fullData.length);
                nettyBuffer.writerIndex(fullData.length);
                PacketBuffer input = new PacketBuffer(nettyBuffer);
                int pktId = input.readVarInt();

                IPacket<?> pkt;
                pkt = packetState.getPacket(PacketDirection.SERVERBOUND, pktId);

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
                logger.error("Failed to process socket frame {}! It'll be skipped for debug purposes.", debugPacketCounter);
                logger.error(t);
            }
        }
    }

    @Override
    public void sendPacket(IPacket pkt) {
        if (!isChannelOpen()) {
            return;
        }

        int i;
        try {
            i = packetState.getPacketId(PacketDirection.CLIENTBOUND, pkt);
        } catch (Throwable t) {
            logger.error("Incorrect packet for state: {}", pkt.getClass().getSimpleName());
            return;
        }

        temporaryBuffer.clear();
        temporaryBuffer.writeVarInt(i);
        try {
            pkt.writePacketData(temporaryBuffer);
        } catch (IOException ex) {
            logger.error("Failed to write packet {}!", pkt.getClass().getSimpleName());
            return;
        }

        int len = temporaryBuffer.readableBytes();
        if (enableSendCompression) {
            if (len > compressionThreshold) {
                if (compressedPacketTmp == null || compressedPacketTmp.length < len) {
                    compressedPacketTmp = new byte[len];
                }
                int cmpLen;
                try {
                    cmpLen = EaglerZLIB.deflateFull(temporaryBuffer.array(), 0, len, compressedPacketTmp, 0, compressedPacketTmp.length);
                } catch (IOException ex) {
                    logger.error("Failed to compress packet {}!", pkt.getClass().getSimpleName());
                    logger.error(ex);
                    return;
                }
                byte[] compressedData = new byte[5 + cmpLen];
                compressedData[0] = (byte) 2;
                compressedData[1] = (byte) ((len >>> 24) & 0xFF);
                compressedData[2] = (byte) ((len >>> 16) & 0xFF);
                compressedData[3] = (byte) ((len >>> 8) & 0xFF);
                compressedData[4] = (byte) (len & 0xFF);
                System.arraycopy(compressedPacketTmp, 0, compressedData, 5, cmpLen);
                if (compressedData.length > fragmentSize) {
                    int fragmentSizeN1 = fragmentSize - 1;
                    for (int j = 1; j < compressedData.length; j += fragmentSizeN1) {
                        byte[] fragData = new byte[((j + fragmentSizeN1 > (compressedData.length - 1)) ? ((compressedData.length - 1) % fragmentSizeN1) : fragmentSizeN1) + 1];
                        System.arraycopy(compressedData, j, fragData, 1, fragData.length - 1);
                        fragData[0] = (j + fragmentSizeN1 < compressedData.length) ? (byte) 1 : (byte) 2;
                        ServerPlatformSingleplayer.sendPacket(new IPCPacketData(playerChannel, fragData));
                    }
                } else {
                    ServerPlatformSingleplayer.sendPacket(new IPCPacketData(playerChannel, compressedData));
                }
            } else {
                int fragmentSizeN1 = fragmentSize - 1;
                if (len > fragmentSizeN1) {
                    do {
                        int readLen = len > fragmentSizeN1 ? fragmentSizeN1 : len;
                        byte[] frag = new byte[readLen + 1];
                        temporaryBuffer.readBytes(frag, 1, readLen);
                        frag[0] = temporaryBuffer.readableBytes() == 0 ? (byte) 0 : (byte) 1;
                        ServerPlatformSingleplayer.sendPacket(new IPCPacketData(playerChannel, frag));
                    } while ((len = temporaryBuffer.readableBytes()) > 0);
                } else {
                    byte[] bytes = new byte[len + 1];
                    bytes[0] = 0;
                    temporaryBuffer.readBytes(bytes, 1, len);
                    ServerPlatformSingleplayer.sendPacket(new IPCPacketData(playerChannel, bytes));
                }
            }
        } else {
            byte[] bytes = new byte[len];
            temporaryBuffer.readBytes(bytes, 0, len);
            ServerPlatformSingleplayer.sendPacket(new IPCPacketData(playerChannel, bytes));
        }
    }

    @Override
    public boolean checkDisconnected() {
        return false;
    }

    @Override
    public boolean isLocalChannel() {
        return false;
    }

    public void tick() {
        processReceivedPackets();
        if (getNetHandler() instanceof net.minecraft.server.network.NetHandlerLoginServer) {
            ((net.minecraft.server.network.NetHandlerLoginServer) getNetHandler()).update();
        } else if (getNetHandler() instanceof net.minecraft.network.play.ServerPlayNetHandler) {
            ((net.minecraft.network.play.ServerPlayNetHandler) getNetHandler()).tick();
        }
    }
}
