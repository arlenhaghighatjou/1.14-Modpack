package net.minecraft.network;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.SocketAddress;
import java.util.Queue;

public class NetworkManager {
    private static final Logger LOGGER = LogManager.getLogger();

    protected final Logger logger = LogManager.getLogger();
    protected ProtocolType packetState;
    protected int debugPacketCounter;
    protected final PacketBuffer temporaryBuffer = new PacketBuffer(io.netty.buffer.Unpooled.buffer());

    private final PacketDirection direction;
    private final Queue<NetworkManager.QueuedPacket> outboundPacketsQueue = new java.util.LinkedList<>();
    private SocketAddress socketAddress;
    private INetHandler packetListener;
    private ITextComponent terminationReason;
    private boolean isEncrypted;
    private boolean disconnected;
    private int field_211394_q;
    private int field_211395_r;
    private float field_211396_s;
    private float field_211397_t;
    private int ticks;
    private boolean field_211399_v;

    public NetworkManager(PacketDirection packetDirection) {
        this.direction = packetDirection;
    }

    public void setConnectionState(ProtocolType newState) {
        this.packetState = newState;
    }

    private static <T extends INetHandler> void processPacket(IPacket<T> p_197664_0_, INetHandler p_197664_1_) {
        p_197664_0_.processPacket((T) p_197664_1_);
    }

    public void setNetHandler(INetHandler handler) {
        Validate.notNull(handler, "packetListener");
        LOGGER.debug("Set listener of {} to {}", this, handler);
        this.packetListener = handler;
    }

    public void sendPacket(IPacket<?> packetIn) {

    }

    private void dispatchPacket(IPacket<?> inPacket) {

    }

    private void flushOutboundQueue() {

    }

    public void tick() {

    }

    public SocketAddress getRemoteAddress() {
        return this.socketAddress;
    }

    public SocketAddress getAddress() {
        return this.socketAddress;
    }

    public void sendEaglerMessage(net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessagePacket packet) {
        INetHandler handler = getNetHandler();
        if (handler instanceof net.minecraft.network.play.ServerPlayNetHandler) {
            ((net.minecraft.network.play.ServerPlayNetHandler) handler).sendEaglerMessage(packet);
        } else if (handler instanceof net.minecraft.client.network.play.ClientPlayNetHandler) {
            ((net.minecraft.client.network.play.ClientPlayNetHandler) handler).sendEaglerMessage(packet);
        }
    }

    public void closeChannel(ITextComponent message) {

    }

    public boolean isLocalChannel() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public static NetworkManager createNetworkManagerAndConnect(String address, int serverPort, boolean useNativeTransport) {
        final NetworkManager networkmanager = new NetworkManager(PacketDirection.CLIENTBOUND);
        return networkmanager;
    }

    @OnlyIn(Dist.CLIENT)
    public static NetworkManager provideLocalClient(SocketAddress address) {
        final NetworkManager networkmanager = new NetworkManager(PacketDirection.CLIENTBOUND);
        return networkmanager;
    }

    public void enableEncryption(Object key) {
        this.isEncrypted = true;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isEncrypted() {
        return this.isEncrypted;
    }

    public boolean isChannelOpen() {
        return false;
    }

    public boolean hasNoChannel() {
        return false;
    }

    public INetHandler getNetHandler() {
        return this.packetListener;
    }

    public ITextComponent getExitMessage() {
        return this.terminationReason;
    }

    public void disableAutoRead() {
    }

    public void setCompressionThreshold(int threshold) {

    }

    public void handleDisconnection() {

    }

    public void connect() {
    }

    public net.lax1dude.eaglercraft.internal.EnumEaglerConnectionState getConnectStatus() {
        return net.lax1dude.eaglercraft.internal.EnumEaglerConnectionState.CLOSED;
    }

    public void processReceivedPackets() {
    }

    public boolean checkDisconnected() {
        return this.disconnected;
    }

    @OnlyIn(Dist.CLIENT)
    public float getPacketsReceived() {
        return this.field_211396_s;
    }

    @OnlyIn(Dist.CLIENT)
    public float getPacketsSent() {
        return this.field_211397_t;
    }

    static class QueuedPacket {
        private final IPacket<?> packet;

        public QueuedPacket(IPacket<?> p_i48604_1_) {
            this.packet = p_i48604_1_;
        }
    }
}
