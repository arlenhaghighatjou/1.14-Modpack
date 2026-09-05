package net.minecraft.network.play.server;

import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

public class SSpawnPositionPacket implements IPacket<IClientPlayNetHandler> {
    private BlockPos spawnBlockPos;

    public SSpawnPositionPacket() {
    }

    public SSpawnPositionPacket(BlockPos posIn) {
        this.spawnBlockPos = posIn;
    }

    public void readPacketData(PacketBuffer buf) throws IOException {
        this.spawnBlockPos = buf.readBlockPos();
    }

    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeBlockPos(this.spawnBlockPos);
    }

    public void processPacket(IClientPlayNetHandler handler) {
        handler.handleSpawnPosition(this);
    }

    @OnlyIn(Dist.CLIENT)
    public BlockPos getSpawnPos() {
        return this.spawnBlockPos;
    }
}
