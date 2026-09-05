package net.lax1dude.eaglercraft.socket.protocol.pkt.client;

import java.io.IOException;
import net.lax1dude.eaglercraft.socket.protocol.GamePacketInputBuffer;
import net.lax1dude.eaglercraft.socket.protocol.GamePacketOutputBuffer;
import net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessagePacket;

public class CPacketGetOtherCapeV5EAG implements GameMessagePacket {
    public int requestId;
    public long uuidMost;
    public long uuidLeast;

    public CPacketGetOtherCapeV5EAG() {
    }

    public CPacketGetOtherCapeV5EAG(int requestId, long uuidMost, long uuidLeast) {
        this.requestId = requestId;
        this.uuidMost = uuidMost;
        this.uuidLeast = uuidLeast;
    }

    public void readPacket(GamePacketInputBuffer buffer) throws IOException {
        requestId = buffer.readVarInt();
        uuidMost = buffer.readLong();
        uuidLeast = buffer.readLong();
    }

    public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
        buffer.writeVarInt(requestId);
        buffer.writeLong(uuidMost);
        buffer.writeLong(uuidLeast);
    }

    public void handlePacket(GameMessageHandler handler) {
        handler.handleClient(this);
    }

    public int length() {
        return GamePacketOutputBuffer.getVarIntSize(requestId) + 16;
    }
}
