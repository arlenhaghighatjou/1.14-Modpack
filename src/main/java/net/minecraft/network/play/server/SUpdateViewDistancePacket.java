package net.minecraft.network.play.server;

import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

public class SUpdateViewDistancePacket implements IPacket<IClientPlayNetHandler> {
    private int field_218759_a;

    public SUpdateViewDistancePacket() {
    }

    public SUpdateViewDistancePacket(int p_i50765_1_) {
        this.field_218759_a = p_i50765_1_;
    }

    public void readPacketData(PacketBuffer buf) throws IOException {
        this.field_218759_a = buf.readVarInt();
    }

    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.field_218759_a);
    }

    public void processPacket(IClientPlayNetHandler handler) {
        handler.func_217270_a(this);
    }

    @OnlyIn(Dist.CLIENT)
    public int func_218758_b() {
        return this.field_218759_a;
    }
}
