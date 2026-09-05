package net.minecraft.network.play.client;

import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.world.server.ServerWorld;

import java.io.IOException;

public class CSpectatePacket implements IPacket<IServerPlayNetHandler> {
    private EaglercraftUUID id;

    public CSpectatePacket() {
    }

    public CSpectatePacket(EaglercraftUUID uniqueIdIn) {
        this.id = uniqueIdIn;
    }

    public void readPacketData(PacketBuffer buf) throws IOException {
        this.id = buf.readUniqueId();
    }

    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeUniqueId(this.id);
    }

    public void processPacket(IServerPlayNetHandler handler) {
        handler.handleSpectate(this);
    }

    public Entity getEntity(ServerWorld worldIn) {
        return worldIn.getEntityByUuid(this.id);
    }
}
