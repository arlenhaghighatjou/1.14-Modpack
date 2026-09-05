package net.minecraft.network.play.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;
import java.util.Locale;

public class CCustomPayloadPacket implements IPacket<IServerPlayNetHandler> {
    public static final ResourceLocation BRAND = new ResourceLocation("brand");
    private ResourceLocation channel;
    private PacketBuffer data;

    public CCustomPayloadPacket() {
    }

    @OnlyIn(Dist.CLIENT)
    public CCustomPayloadPacket(ResourceLocation channelIn, PacketBuffer dataIn) {
        this.channel = channelIn;
        this.data = dataIn;
    }

    public void readPacketData(PacketBuffer buf) throws IOException {
        String s = buf.readString(32767);
        try {
            this.channel = new ResourceLocation(s);
        } catch (ResourceLocationException e) {
            String mapped = net.lax1dude.eaglercraft.socket.protocol.GamePluginMessageConstants.toResourceLocation(s);
            if (!mapped.equals(s)) {
                this.channel = new ResourceLocation(mapped);
            } else {
                this.channel = new ResourceLocation("legacy",
                        s.toLowerCase(Locale.ROOT).replace('|', '_').replace('-', '_'));
            }
        }
        int i = buf.readableBytes();
        if (i >= 0 && i <= 32767) {
            this.data = new PacketBuffer(buf.readBytes(i));
        } else {
            throw new IOException("Payload may not be larger than 32767 bytes");
        }
    }

    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeResourceLocation(this.channel);
        buf.writeBytes((ByteBuf) this.data);
    }

    public ResourceLocation getChannelName() {
        return this.channel;
    }

    public PacketBuffer getBufferData() {
        return this.data;
    }

    public void processPacket(IServerPlayNetHandler handler) {
        handler.processCustomPayload(this);

    }
}
