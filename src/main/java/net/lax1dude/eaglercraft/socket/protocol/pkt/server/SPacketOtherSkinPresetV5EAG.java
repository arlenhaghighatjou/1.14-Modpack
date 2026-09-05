package net.lax1dude.eaglercraft.socket.protocol.pkt.server;

import java.io.IOException;
import net.lax1dude.eaglercraft.socket.protocol.GamePacketInputBuffer;
import net.lax1dude.eaglercraft.socket.protocol.GamePacketOutputBuffer;
import net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessagePacket;

public class SPacketOtherSkinPresetV5EAG implements GameMessagePacket {
    public int requestId;
    public int presetSkin;

    public SPacketOtherSkinPresetV5EAG() {
    }

    public SPacketOtherSkinPresetV5EAG(int requestId, int presetSkin) {
        this.requestId = requestId;
        this.presetSkin = presetSkin;
    }

    public void readPacket(GamePacketInputBuffer buffer) throws IOException {
        requestId = buffer.readVarInt();
        presetSkin = buffer.readVarInt();
    }

    public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
        buffer.writeVarInt(requestId);
        buffer.writeVarInt(presetSkin);
    }

    public void handlePacket(GameMessageHandler handler) {
        handler.handleServer(this);
    }

    public int length() {
        return GamePacketOutputBuffer.getVarIntSize(requestId) + GamePacketOutputBuffer.getVarIntSize(presetSkin);
    }
}
