package net.lax1dude.eaglercraft.socket.protocol.pkt.server;

import java.io.IOException;
import net.lax1dude.eaglercraft.socket.protocol.GamePacketInputBuffer;
import net.lax1dude.eaglercraft.socket.protocol.GamePacketOutputBuffer;
import net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessagePacket;

public class SPacketOtherCapeCustomV5EAG implements GameMessagePacket {
    public int requestId;
    public byte[] customCape;

    public SPacketOtherCapeCustomV5EAG() {
    }

    public SPacketOtherCapeCustomV5EAG(int requestId, byte[] customCape) {
        this.requestId = requestId;
        this.customCape = customCape;
    }

    public void readPacket(GamePacketInputBuffer buffer) throws IOException {
        requestId = buffer.readVarInt();
        customCape = new byte[1173];
        buffer.readFully(customCape);
    }

    public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
        if (customCape.length != 1173) {
            throw new IOException("Custom cape data length is not 1173 bytes: " + customCape.length);
        }
        buffer.writeVarInt(requestId);
        buffer.write(customCape);
    }

    public void handlePacket(GameMessageHandler handler) {
        handler.handleServer(this);
    }

    public int length() {
        return GamePacketOutputBuffer.getVarIntSize(requestId) + 1173;
    }
}
