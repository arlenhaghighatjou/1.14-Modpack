package net.lax1dude.eaglercraft.socket.protocol.pkt.server;

import java.io.IOException;
import net.lax1dude.eaglercraft.socket.protocol.GamePacketInputBuffer;
import net.lax1dude.eaglercraft.socket.protocol.GamePacketOutputBuffer;
import net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessagePacket;

public class SPacketOtherTexturesV5EAG implements GameMessagePacket {
    public int requestId;
    public int skinID;
    public byte[] customSkin;
    public int capeID;
    public byte[] customCape;

    public SPacketOtherTexturesV5EAG() {
    }

    public void readPacket(GamePacketInputBuffer buffer) throws IOException {
        requestId = buffer.readVarInt();
        skinID = buffer.readVarInt();
        capeID = buffer.readVarInt();
        if (skinID < 0) {
            customSkin = new byte[12288];
            buffer.readFully(customSkin);
        }
        if (capeID < 0) {
            customCape = new byte[1173];
            buffer.readFully(customCape);
        }
    }

    public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
        buffer.writeVarInt(requestId);
        buffer.writeVarInt(skinID);
        buffer.writeVarInt(capeID);
        if (skinID < 0) {
            if (customSkin.length != 12288) throw new IOException("Custom skin data length is not 12288 bytes");
            buffer.write(customSkin);
        }
        if (capeID < 0) {
            if (customCape.length != 1173) throw new IOException("Custom cape data length is not 1173 bytes");
            buffer.write(customCape);
        }
    }

    public void handlePacket(GameMessageHandler handler) {
        handler.handleServer(this);
    }

    public int length() {
        int length = GamePacketOutputBuffer.getVarIntSize(requestId)
                + GamePacketOutputBuffer.getVarIntSize(skinID) + GamePacketOutputBuffer.getVarIntSize(capeID);
        if (skinID < 0) length += 12288;
        if (capeID < 0) length += 1173;
        return length;
    }
}
