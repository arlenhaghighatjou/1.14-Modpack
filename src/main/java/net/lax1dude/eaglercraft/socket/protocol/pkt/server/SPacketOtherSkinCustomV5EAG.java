package net.lax1dude.eaglercraft.socket.protocol.pkt.server;

import java.io.IOException;
import net.lax1dude.eaglercraft.socket.protocol.GamePacketInputBuffer;
import net.lax1dude.eaglercraft.socket.protocol.GamePacketOutputBuffer;
import net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessagePacket;

public class SPacketOtherSkinCustomV5EAG implements GameMessagePacket {
    public int requestId;
    public int modelID;
    public byte[] customSkin;

    public SPacketOtherSkinCustomV5EAG() {
    }

    public SPacketOtherSkinCustomV5EAG(int requestId, int modelID, byte[] customSkin) {
        this.requestId = requestId;
        this.modelID = modelID;
        this.customSkin = customSkin;
    }

    public void readPacket(GamePacketInputBuffer buffer) throws IOException {
        requestId = buffer.readVarInt();
        modelID = buffer.readUnsignedByte();
        customSkin = new byte[12288];
        buffer.readFully(customSkin);
    }

    public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
        if (customSkin.length != 12288) {
            throw new IOException("Custom skin data length is not 12288 bytes: " + customSkin.length);
        }
        buffer.writeVarInt(requestId);
        buffer.writeByte(modelID);
        buffer.write(customSkin);
    }

    public void handlePacket(GameMessageHandler handler) {
        handler.handleServer(this);
    }

    public int length() {
        return GamePacketOutputBuffer.getVarIntSize(requestId) + 12289;
    }
}
