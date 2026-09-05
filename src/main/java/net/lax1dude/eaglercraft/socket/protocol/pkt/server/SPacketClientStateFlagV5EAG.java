package net.lax1dude.eaglercraft.socket.protocol.pkt.server;

import java.io.IOException;
import net.lax1dude.eaglercraft.socket.protocol.GamePacketInputBuffer;
import net.lax1dude.eaglercraft.socket.protocol.GamePacketOutputBuffer;
import net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessagePacket;

public class SPacketClientStateFlagV5EAG implements GameMessagePacket {
    public long uuidMost;
    public long uuidLeast;
    public int state;

    public SPacketClientStateFlagV5EAG() {
    }

    public void readPacket(GamePacketInputBuffer buffer) throws IOException {
        uuidMost = buffer.readLong();
        uuidLeast = buffer.readLong();
        state = buffer.readVarInt();
    }

    public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
        buffer.writeLong(uuidMost);
        buffer.writeLong(uuidLeast);
        buffer.writeVarInt(state);
    }

    public void handlePacket(GameMessageHandler handler) {
        handler.handleServer(this);
    }

    public int length() {
        return 16 + GamePacketOutputBuffer.getVarIntSize(state);
    }
}
