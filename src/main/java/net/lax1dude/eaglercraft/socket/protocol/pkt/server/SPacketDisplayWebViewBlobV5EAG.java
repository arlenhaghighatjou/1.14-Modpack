package net.lax1dude.eaglercraft.socket.protocol.pkt.server;

import java.io.IOException;
import net.lax1dude.eaglercraft.socket.protocol.GamePacketInputBuffer;
import net.lax1dude.eaglercraft.socket.protocol.GamePacketOutputBuffer;
import net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessagePacket;

public class SPacketDisplayWebViewBlobV5EAG implements GameMessagePacket {
    public int flags;
    public String embedTitle;
    public byte[] embedHash;

    public SPacketDisplayWebViewBlobV5EAG() {
    }

    public void readPacket(GamePacketInputBuffer buffer) throws IOException {
        flags = buffer.readUnsignedByte();
        embedTitle = buffer.readStringMC(255);
        embedHash = new byte[20];
        buffer.readFully(embedHash);
    }

    public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
        if (embedHash.length != 20) throw new IOException("Hash is not 20 bytes");
        buffer.writeByte(flags);
        buffer.writeStringMC(embedTitle);
        buffer.write(embedHash);
    }

    public void handlePacket(GameMessageHandler handler) {
        handler.handleServer(this);
    }

    public int length() {
        return -1;
    }
}
