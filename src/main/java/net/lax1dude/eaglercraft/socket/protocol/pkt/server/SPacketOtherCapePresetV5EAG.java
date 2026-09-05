package net.lax1dude.eaglercraft.socket.protocol.pkt.server;

import java.io.IOException;
import net.lax1dude.eaglercraft.socket.protocol.GamePacketInputBuffer;
import net.lax1dude.eaglercraft.socket.protocol.GamePacketOutputBuffer;
import net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessagePacket;

public class SPacketOtherCapePresetV5EAG implements GameMessagePacket {
    public int requestId;
    public int presetCape;

    public SPacketOtherCapePresetV5EAG() {
    }

    public SPacketOtherCapePresetV5EAG(int requestId, int presetCape) {
        this.requestId = requestId;
        this.presetCape = presetCape;
    }

    public void readPacket(GamePacketInputBuffer buffer) throws IOException {
        requestId = buffer.readVarInt();
        presetCape = buffer.readVarInt();
    }

    public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
        buffer.writeVarInt(requestId);
        buffer.writeVarInt(presetCape);
    }

    public void handlePacket(GameMessageHandler handler) {
        handler.handleServer(this);
    }

    public int length() {
        return GamePacketOutputBuffer.getVarIntSize(requestId) + GamePacketOutputBuffer.getVarIntSize(presetCape);
    }
}
