package net.lax1dude.eaglercraft.socket.protocol.pkt.server;

import java.io.IOException;
import net.lax1dude.eaglercraft.socket.protocol.GamePacketInputBuffer;
import net.lax1dude.eaglercraft.socket.protocol.GamePacketOutputBuffer;
import net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessagePacket;

public class SPacketDisplayWebViewURLV5EAG implements GameMessagePacket {
    public int flags;
    public String embedTitle;
    public String embedURL;

    public SPacketDisplayWebViewURLV5EAG() {
    }

    public void readPacket(GamePacketInputBuffer buffer) throws IOException {
        flags = buffer.readUnsignedByte();
        embedTitle = buffer.readStringMC(255);
        embedURL = buffer.readStringEaglerASCII16();
    }

    public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
        buffer.writeByte(flags);
        buffer.writeStringMC(embedTitle);
        buffer.writeStringEaglerASCII16(embedURL);
    }

    public void handlePacket(GameMessageHandler handler) {
        handler.handleServer(this);
    }

    public int length() {
        return -1;
    }
}
