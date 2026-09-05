package net.minecraft.network.login.server;

import net.minecraft.client.network.login.IClientLoginNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.CryptManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

public class SEncryptionRequestPacket implements IPacket<IClientLoginNetHandler> {
    private String hashedServerId;
    private Object publicKey;
    private byte[] verifyToken;

    public SEncryptionRequestPacket() {
    }

    public SEncryptionRequestPacket(String serverIdIn, Object publicKeyIn, byte[] verifyTokenIn) {
        this.hashedServerId = serverIdIn;
        this.publicKey = publicKeyIn;
        this.verifyToken = verifyTokenIn;
    }

    public void readPacketData(PacketBuffer buf) throws IOException {
        this.hashedServerId = buf.readString(20);
        this.publicKey = CryptManager.decodePublicKey(buf.readByteArray());
        this.verifyToken = buf.readByteArray();
    }

    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeString(this.hashedServerId);
        buf.writeByteArray(new byte[0]);
        buf.writeByteArray(this.verifyToken);
    }

    public void processPacket(IClientLoginNetHandler handler) {
        handler.handleEncryptionRequest(this);
    }

    @OnlyIn(Dist.CLIENT)
    public String getServerId() {
        return this.hashedServerId;
    }

    @OnlyIn(Dist.CLIENT)
    public Object getPublicKey() {
        return this.publicKey;
    }

    @OnlyIn(Dist.CLIENT)
    public byte[] getVerifyToken() {
        return this.verifyToken;
    }
}
