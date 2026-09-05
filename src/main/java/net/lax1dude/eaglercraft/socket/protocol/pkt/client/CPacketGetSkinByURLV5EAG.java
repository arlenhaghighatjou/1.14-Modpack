package net.lax1dude.eaglercraft.socket.protocol.pkt.client;

import java.io.IOException;
import net.lax1dude.eaglercraft.socket.protocol.GamePacketInputBuffer;
import net.lax1dude.eaglercraft.socket.protocol.GamePacketOutputBuffer;
import net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessagePacket;

public class CPacketGetSkinByURLV5EAG implements GameMessagePacket {
    public int requestId;
    public String url;

    public CPacketGetSkinByURLV5EAG() {
    }

    public CPacketGetSkinByURLV5EAG(int requestId, String url) {
        this.requestId = requestId;
        this.url = url;
    }

    public void readPacket(GamePacketInputBuffer buffer) throws IOException {
        requestId = buffer.readVarInt();
        url = buffer.readStringEaglerASCII16();
    }

    public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
        buffer.writeVarInt(requestId);
        buffer.writeStringEaglerASCII16(url);
    }

    public void handlePacket(GameMessageHandler handler) {
        handler.handleClient(this);
    }

    public int length() {
        return GamePacketOutputBuffer.getVarIntSize(requestId) + 2 + url.length();
    }
}
