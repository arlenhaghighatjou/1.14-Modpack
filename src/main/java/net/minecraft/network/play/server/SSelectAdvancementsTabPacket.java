package net.minecraft.network.play.server;

import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

public class SSelectAdvancementsTabPacket implements IPacket<IClientPlayNetHandler> {

    private ResourceLocation tab;

    public SSelectAdvancementsTabPacket() {
    }

    public SSelectAdvancementsTabPacket(ResourceLocation p_i47596_1_) {
        this.tab = p_i47596_1_;
    }

    public void processPacket(IClientPlayNetHandler handler) {
        handler.handleSelectAdvancementsTab(this);
    }

    public void readPacketData(PacketBuffer buf) throws IOException {
        if (buf.readBoolean()) {
            this.tab = buf.readResourceLocation();
        }

    }

    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeBoolean(this.tab != null);
        if (this.tab != null) {
            buf.writeResourceLocation(this.tab);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getTab() {
        return this.tab;
    }
}
