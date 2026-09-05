package net.minecraft.network.play.server;

import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

public class SSpawnPaintingPacket implements IPacket<IClientPlayNetHandler> {
    private int entityID;
    private EaglercraftUUID uniqueId;
    private BlockPos position;
    private Direction facing;
    private int title;

    public SSpawnPaintingPacket() {
    }

    public SSpawnPaintingPacket(PaintingEntity painting) {
        this.entityID = painting.getEntityId();
        this.uniqueId = painting.getUniqueID();
        this.position = painting.getHangingPosition();
        this.facing = painting.getHorizontalFacing();
        this.title = Registry.MOTIVE.getId(painting.art);
    }

    public void readPacketData(PacketBuffer buf) throws IOException {
        this.entityID = buf.readVarInt();
        this.uniqueId = buf.readUniqueId();
        this.title = buf.readVarInt();
        this.position = buf.readBlockPos();
        this.facing = Direction.byHorizontalIndex(buf.readUnsignedByte());
    }

    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.entityID);
        buf.writeUniqueId(this.uniqueId);
        buf.writeVarInt(this.title);
        buf.writeBlockPos(this.position);
        buf.writeByte(this.facing.getHorizontalIndex());
    }

    public void processPacket(IClientPlayNetHandler handler) {
        handler.handleSpawnPainting(this);
    }

    @OnlyIn(Dist.CLIENT)
    public int getEntityID() {
        return this.entityID;
    }

    @OnlyIn(Dist.CLIENT)
    public EaglercraftUUID getUniqueId() {
        return this.uniqueId;
    }

    @OnlyIn(Dist.CLIENT)
    public BlockPos getPosition() {
        return this.position;
    }

    @OnlyIn(Dist.CLIENT)
    public Direction getFacing() {
        return this.facing;
    }

    @OnlyIn(Dist.CLIENT)
    public PaintingType getType() {
        return Registry.MOTIVE.getByValue(this.title);
    }
}
