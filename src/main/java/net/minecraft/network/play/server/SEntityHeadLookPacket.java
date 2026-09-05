package net.minecraft.network.play.server;

import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

public class SEntityHeadLookPacket implements IPacket<IClientPlayNetHandler> {
    private int entityId;
    private byte yaw;

    public SEntityHeadLookPacket() {
    }

    public SEntityHeadLookPacket(Entity entityIn, byte yawIn) {
        this.entityId = entityIn.getEntityId();
        this.yaw = yawIn;
    }

    public void readPacketData(PacketBuffer buf) throws IOException {
        this.entityId = buf.readVarInt();
        this.yaw = buf.readByte();
    }

    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.entityId);
        buf.writeByte(this.yaw);
    }

    public void processPacket(IClientPlayNetHandler handler) {
        handler.handleEntityHeadLook(this);
    }

    @OnlyIn(Dist.CLIENT)
    public Entity getEntity(World worldIn) {
        return worldIn.getEntityByID(this.entityId);
    }

    @OnlyIn(Dist.CLIENT)
    public byte getYaw() {
        return this.yaw;
    }
}
