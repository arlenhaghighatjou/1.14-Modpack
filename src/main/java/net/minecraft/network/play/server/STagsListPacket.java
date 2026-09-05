package net.minecraft.network.play.server;

import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.NetworkTagManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

public class STagsListPacket implements IPacket<IClientPlayNetHandler> {
    private NetworkTagManager tags;

    public STagsListPacket() {
    }

    public STagsListPacket(NetworkTagManager p_i48211_1_) {
        this.tags = p_i48211_1_;
    }

    public void readPacketData(PacketBuffer buf) throws IOException {
        this.tags = NetworkTagManager.read(buf);
    }

    public void writePacketData(PacketBuffer buf) throws IOException {
        this.tags.write(buf);
    }

    public void processPacket(IClientPlayNetHandler handler) {
        handler.handleTags(this);
    }

    @OnlyIn(Dist.CLIENT)
    public NetworkTagManager getTags() {
        return this.tags;
    }
}
