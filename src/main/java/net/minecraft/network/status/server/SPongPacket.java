package net.minecraft.network.status.server;

import net.minecraft.client.network.status.IClientStatusNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class SPongPacket implements IPacket<IClientStatusNetHandler> {
    private long clientTime;

    public SPongPacket() {
    }

    public SPongPacket(long clientTimeIn) {
        this.clientTime = clientTimeIn;
    }

    public void readPacketData(PacketBuffer buf) throws IOException {
        this.clientTime = buf.readLong();
    }

    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeLong(this.clientTime);
    }

    public void processPacket(IClientStatusNetHandler handler) {
        handler.handlePong(this);
    }
}
