package net.lax1dude.eaglercraft.socket.protocol.client;

import io.netty.buffer.Unpooled;
import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.socket.protocol.GamePacketOutputBuffer;
import net.lax1dude.eaglercraft.socket.protocol.GamePluginMessageConstants;
import net.lax1dude.eaglercraft.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.sp.server.socket.protocol.ServerV3MessageHandler;
import net.lax1dude.eaglercraft.sp.server.socket.protocol.ServerV4MessageHandler;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
 * <p>
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
public class GameProtocolMessageController {

    private static final Logger logger = LogManager.getLogger("GameProtocolMessageController");

    public final GamePluginMessageProtocol protocol;
    public final int sendDirection;
    public final int receiveDirection;
    private final PacketBufferInputWrapper inputStream = new PacketBufferInputWrapper(null);
    private final PacketBufferOutputWrapper outputStream = new PacketBufferOutputWrapper(null);
    private final GameMessageHandler handler;
    private final IPluginMessageSendFunction sendFunction;
    private final IRawBinarySendFunction rawSendFunction;
    private final List<PacketBuffer> sendQueueV4;
    private final boolean noDelay;

    public GameProtocolMessageController(GamePluginMessageProtocol protocol, int sendDirection, GameMessageHandler handler,
                                         IPluginMessageSendFunction sendCallback) {
        this(protocol, sendDirection, handler, sendCallback, null);
    }

    public GameProtocolMessageController(GamePluginMessageProtocol protocol, int sendDirection, GameMessageHandler handler,
                                         IPluginMessageSendFunction sendCallback, IRawBinarySendFunction rawSendCallback) {
        this.protocol = protocol;
        this.sendDirection = sendDirection;
        this.receiveDirection = GamePluginMessageConstants.oppositeDirection(sendDirection);
        this.handler = handler;
        this.sendFunction = sendCallback;
        this.rawSendFunction = rawSendCallback;
        this.noDelay = protocol.ver < 4 || protocol.ver >= 5 || EagRuntime.getConfiguration().isEaglerNoDelay();
        this.sendQueueV4 = !noDelay ? new LinkedList<>() : null;
    }

    public static GameMessageHandler createClientHandler(int protocolVersion, ClientPlayNetHandler netHandler) {
        switch (protocolVersion) {
            case 2:
            case 3:
                return new ClientV3MessageHandler(netHandler);
            case 4:
                return new ClientV4MessageHandler(netHandler);
            case 5:
                return new ClientV5MessageHandler(netHandler);
            default:
                throw new IllegalArgumentException("Unknown protocol verison: " + protocolVersion);
        }
    }

    public static GameMessageHandler createServerHandler(int protocolVersion, ServerPlayNetHandler netHandler) {
        switch (protocolVersion) {
            case 2:
            case 3:
                return new ServerV3MessageHandler(netHandler);
            case 4:
                return new ServerV4MessageHandler(netHandler);
            default:
                throw new IllegalArgumentException("Unknown protocol verison: " + protocolVersion);
        }
    }

    public boolean handlePacket(String channel, PacketBuffer data) throws IOException {
        if (protocol.ver >= 5) {
            return false;
        }
        GameMessagePacket pkt;
        if (protocol.ver >= 4 && data.readableBytes() > 0 && data.getByte(data.readerIndex()) == (byte) 0xFF
                && channel.equals(GamePluginMessageConstants.V4_CHANNEL)) {
            data.readByte();
            inputStream.buffer = data;
            int count = inputStream.readVarInt();
            for (int i = 0, j, k; i < count; ++i) {
                j = data.readVarInt();
                k = data.readerIndex() + j;
                if (j > data.readableBytes()) {
                    throw new IOException("Packet fragment is too long: " + j + " > " + data.readableBytes());
                }
                pkt = protocol.readPacket(channel, receiveDirection, inputStream);
                if (pkt != null) {
                    handlePacket(pkt);
                } else {
                    logger.warn("Could not read packet fragment {} of {}, unknown packet", count, i);
                }
                if (data.readerIndex() != k) {
                    logger.warn("Packet fragment {} was the wrong length: {} != {}",
                            (pkt != null ? pkt.getClass().getSimpleName() : "unknown"), j + data.readerIndex() - k, j);
                    data.readerIndex(k);
                }
            }
            if (data.readableBytes() > 0) {
                logger.warn("Leftover data after reading multi-packet! ({} bytes)", data.readableBytes());
            }
            inputStream.buffer = null;
            return true;
        }
        inputStream.buffer = data;
        pkt = protocol.readPacket(channel, receiveDirection, inputStream);
        if (pkt != null && inputStream.available() > 0) {
            logger.warn("Leftover data after reading packet {}! ({} bytes)", pkt.getClass().getSimpleName(), inputStream.available());
        }
        inputStream.buffer = null;
        if (pkt != null) {
            handlePacket(pkt);
            return true;
        } else {
            return false;
        }
    }

    public boolean handleInjectedPacket(byte[] data) throws IOException {
        if (protocol.ver < 5 || data.length < 2 || data[0] != (byte) 0xEE) {
            return false;
        }
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer(data, data.length).writerIndex(data.length));
        inputStream.setBuffer(buffer);
        try {
            buffer.readByte();
            if (buffer.getByte(buffer.readerIndex()) == (byte) 0xFF) {
                if (buffer.readableBytes() > 32768) {
                    throw new IOException("Impossible large multi-packet received: " + buffer.readableBytes());
                }
                buffer.readByte();
                int count = inputStream.readVarInt();
                for (int i = 0; i < count; ++i) {
                    int length = inputStream.readVarInt();
                    if (length < 1 || length > buffer.readableBytes()) {
                        throw new IOException("Packet fragment is too long: " + length + " > " + buffer.readableBytes());
                    }
                    byte[] fragmentData = new byte[length];
                    buffer.readBytes(fragmentData);
                    PacketBuffer fragment = new PacketBuffer(
                            Unpooled.buffer(fragmentData, fragmentData.length).writerIndex(fragmentData.length));
                    inputStream.setBuffer(fragment);
                    GameMessagePacket packet = protocol.readPacketV5(receiveDirection, inputStream);
                    if (fragment.readableBytes() != 0) {
                        throw new IOException("Packet fragment had " + fragment.readableBytes() + " unread bytes");
                    }
                    handlePacket(packet);
                    inputStream.setBuffer(buffer);
                }
            } else {
                handlePacket(protocol.readPacketV5(receiveDirection, inputStream));
            }
            if (buffer.readableBytes() != 0) {
                throw new IOException("Leftover data after injected packet: " + buffer.readableBytes() + " bytes");
            }
            return true;
        } finally {
            inputStream.setBuffer(null);
        }
    }

    private void handlePacket(GameMessagePacket packet) {
        try {
            packet.handlePacket(handler);
        } catch (Throwable t) {
            logger.error("Failed to handle packet {} in direction {} using handler {}!", packet.getClass().getSimpleName(),
                    GamePluginMessageConstants.getDirectionString(receiveDirection), handler);
            logger.error(t);
        }
    }

    public void sendPacket(GameMessagePacket packet) throws IOException {
        if (protocol.ver >= 5) {
            if (rawSendFunction == null) {
                throw new IOException("Protocol V5 requires an injected message transport");
            }
            int expectedLength = packet.length() + 2;
            PacketBuffer buffer = new PacketBuffer(expectedLength > 1
                    ? Unpooled.buffer(expectedLength) : Unpooled.buffer(64));
            outputStream.setBuffer(buffer);
            outputStream.writeByte(0xEE);
            protocol.writePacketV5(sendDirection, outputStream, packet);
            outputStream.setBuffer(null);
            int length = buffer.writerIndex();
            if (expectedLength > 1 && length != expectedLength) {
                logger.warn("Packet {} was expected to be {} bytes but was serialized to {} bytes!",
                        packet.getClass().getSimpleName(), expectedLength, length);
            }
            byte[] bytes = new byte[length];
            buffer.getBytes(0, bytes);
            rawSendFunction.send(bytes);
            return;
        }
        int len = packet.length() + 1;
        PacketBuffer buf = new PacketBuffer(len != 0 ? Unpooled.buffer(len) : Unpooled.buffer(64));
        outputStream.buffer = buf;
        String chan = protocol.writePacket(sendDirection, outputStream, packet);
        outputStream.buffer = null;
        int j = buf.writerIndex();
        if (len != 0 && j != len && j + 1 != len) {
            logger.warn("Packet {} was expected to be {} bytes but was serialized to {} bytes!",
                    packet.getClass().getSimpleName(), len, j);
        }
        if (sendQueueV4 != null && chan.equals(GamePluginMessageConstants.V4_CHANNEL)) {
            sendQueueV4.add(buf);
        } else {
            sendFunction.sendPluginMessage(chan, buf);
        }
    }

    public void flush() {
        if (sendQueueV4 != null) {
            int queueLen = sendQueueV4.size();
            PacketBuffer pkt;
            if (queueLen == 0) {
                return;
            } else if (queueLen == 1) {
                pkt = sendQueueV4.remove(0);
                sendFunction.sendPluginMessage(GamePluginMessageConstants.V4_CHANNEL, pkt);
            } else {
                int i, j, sendCount, totalLen, lastLen;
                PacketBuffer sendBuffer;
                while (sendQueueV4.size() > 0) {
                    sendCount = 0;
                    totalLen = 0;
                    Iterator<PacketBuffer> itr = sendQueueV4.iterator();
                    do {
                        i = itr.next().readableBytes();
                        lastLen = GamePacketOutputBuffer.getVarIntSize(i) + i;
                        totalLen += lastLen;
                        ++sendCount;
                    } while (totalLen < 32760 && itr.hasNext());
                    if (totalLen >= 32760) {
                        --sendCount;
                        totalLen -= lastLen;
                    }
                    if (sendCount <= 1) {
                        pkt = sendQueueV4.remove(0);
                        sendFunction.sendPluginMessage(GamePluginMessageConstants.V4_CHANNEL, pkt);
                        continue;
                    }
                    sendBuffer = new PacketBuffer(Unpooled.buffer(1 + totalLen + GamePacketOutputBuffer.getVarIntSize(sendCount)));
                    sendBuffer.writeByte(0xFF);
                    sendBuffer.writeVarInt(sendCount);
                    for (j = 0; j < sendCount; ++j) {
                        pkt = sendQueueV4.remove(0);
                        sendBuffer.writeVarInt(pkt.readableBytes());
                        sendBuffer.writeBytes(pkt);
                    }
                    sendFunction.sendPluginMessage(GamePluginMessageConstants.V4_CHANNEL, sendBuffer);
                }
            }
        }
    }

    public interface IRawBinarySendFunction {
        void send(byte[] data);
    }
}
