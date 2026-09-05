package net.lax1dude.eaglercraft.socket.protocol.client;

import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketClientStateFlagV5EAG;
import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketDisplayWebViewBlobV5EAG;
import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketDisplayWebViewURLV5EAG;
import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketOtherCapeCustomV5EAG;
import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketOtherCapePresetV5EAG;
import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketOtherSkinCustomV5EAG;
import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketOtherSkinPresetV5EAG;
import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketOtherTexturesV5EAG;
import net.minecraft.client.network.play.ClientPlayNetHandler;

public class ClientV5MessageHandler extends ClientV4MessageHandler {

    public ClientV5MessageHandler(ClientPlayNetHandler netHandler) {
        super(netHandler);
    }

    public void handleServer(SPacketOtherSkinPresetV5EAG packet) {
        netHandler.getSkinCache().cacheSkinPresetV5(packet.requestId, packet.presetSkin);
    }

    public void handleServer(SPacketOtherSkinCustomV5EAG packet) {
        netHandler.getSkinCache().cacheSkinCustomV5(packet.requestId, packet.customSkin, packet.modelID);
    }

    public void handleServer(SPacketOtherCapePresetV5EAG packet) {
        netHandler.getCapeCache().cacheCapePresetV5(packet.requestId, packet.presetCape);
    }

    public void handleServer(SPacketOtherCapeCustomV5EAG packet) {
        netHandler.getCapeCache().cacheCapeCustomV5(packet.requestId, packet.customCape);
    }

    public void handleServer(SPacketOtherTexturesV5EAG packet) {
    }

    public void handleServer(SPacketClientStateFlagV5EAG packet) {
    }

    public void handleServer(SPacketDisplayWebViewURLV5EAG packet) {
    }

    public void handleServer(SPacketDisplayWebViewBlobV5EAG packet) {
    }
}
