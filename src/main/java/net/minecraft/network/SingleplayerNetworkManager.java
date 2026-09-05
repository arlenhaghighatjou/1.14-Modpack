package net.minecraft.network;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.lax1dude.eaglercraft.internal.EnumEaglerConnectionState;
import net.lax1dude.eaglercraft.internal.IPCPacketData;
import net.lax1dude.eaglercraft.sp.SingleplayerServerController;
import net.lax1dude.eaglercraft.sp.internal.ClientPlatformSingleplayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class SingleplayerNetworkManager extends NetworkManager {

	private int debugPacketCounter = 0;
	private byte[][] recievedPacketBuffer = new byte[16384][];
	private int recievedPacketBufferCounter = 0;
	public boolean isPlayerChannelOpen = false;

	private INetHandler nethandler = null;
	private ProtocolType packetState = ProtocolType.HANDSHAKING;
	private final PacketBuffer temporaryBuffer;
	private boolean clientDisconnected = false;

	public SingleplayerNetworkManager(String channel) {
		super(PacketDirection.CLIENTBOUND);
		this.temporaryBuffer = new PacketBuffer(Unpooled.buffer(0x1FFFF));
	}

	@Override
	public void setConnectionState(ProtocolType newState) {
		this.packetState = newState;
	}

	@Override
	public void setNetHandler(INetHandler handler) {
		this.nethandler = handler;
	}

	public void connect() {
		clearRecieveQueue();
		SingleplayerServerController.openLocalPlayerChannel();
	}

	public EnumEaglerConnectionState getConnectStatus() {
		return isPlayerChannelOpen ? EnumEaglerConnectionState.CONNECTED : EnumEaglerConnectionState.CLOSED;
	}

	@Override
	public void closeChannel(ITextComponent reason) {
		SingleplayerServerController.closeLocalPlayerChannel();
		if (nethandler != null) {
			nethandler.onDisconnect(reason);
		}
		clearRecieveQueue();
		clientDisconnected = true;
	}

	public void addRecievedPacket(byte[] next) {
		if (recievedPacketBufferCounter >= recievedPacketBuffer.length - 1) {
			byte[][] newArray = new byte[recievedPacketBuffer.length * 2][];
			System.arraycopy(recievedPacketBuffer, 0, newArray, 0, recievedPacketBuffer.length);
			recievedPacketBuffer = newArray;
		}
		recievedPacketBuffer[recievedPacketBufferCounter++] = next;
	}

	public void processReceivedPackets() {
		if (nethandler == null)
			return;

		for (int i = 0; i < recievedPacketBufferCounter; ++i) {
			byte[] next = recievedPacketBuffer[i];
			recievedPacketBuffer[i] = null;
			++debugPacketCounter;
			try {
				ByteBuf nettyBuffer = Unpooled.buffer(next, next.length);
				nettyBuffer.writerIndex(next.length);
				PacketBuffer input = new PacketBuffer(nettyBuffer);
				int pktId = input.readVarInt();

				IPacket pkt;
				pkt = packetState.getPacket(PacketDirection.CLIENTBOUND, pktId);

				if (pkt == null) {
					throw new IOException(
							"Recieved packet type " + pktId + " which is undefined in state " + packetState);
				}

				try {
					pkt.readPacketData(input);
				} catch (Throwable t) {
					throw new IOException("Failed to read packet type '" + pkt.getClass().getSimpleName() + "'", t);
				}

				try {
					pkt.processPacket(nethandler);
				} catch (Throwable t) {
					t.printStackTrace();
				}

			} catch (Throwable t) {
			}
		}
		recievedPacketBufferCounter = 0;
	}

	@Override
	public void sendPacket(IPacket<?> pkt) {
		if (!isChannelOpen()) {
			return;
		}

		int i;
		try {
			i = packetState.getPacketId(PacketDirection.SERVERBOUND, pkt);
		} catch (Throwable t) {
			return;
		}

		temporaryBuffer.clear();
		temporaryBuffer.writeVarInt(i);
		try {
			pkt.writePacketData(temporaryBuffer);
		} catch (IOException ex) {
			return;
		}

		int len = temporaryBuffer.writerIndex();
		byte[] bytes = new byte[len];
		temporaryBuffer.getBytes(0, bytes);

		ClientPlatformSingleplayer.sendPacket(new IPCPacketData("~!LOCAL_PLAYER", bytes));
	}

	@Override
	public boolean isChannelOpen() {
		return isPlayerChannelOpen;
	}

	public boolean checkDisconnected() {
		if (!isPlayerChannelOpen) {
            processReceivedPackets();
            clearRecieveQueue();
			doClientDisconnect(new TranslationTextComponent("disconnect.endOfStream"));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isLocalChannel() {
		return true;
	}

	protected void doClientDisconnect(ITextComponent msg) {
		if (!clientDisconnected) {
			clientDisconnected = true;
			if (nethandler != null) {
				this.nethandler.onDisconnect(msg);
			}
		}
	}

	public void clearRecieveQueue() {
		for (int i = 0; i < recievedPacketBufferCounter; ++i) {
			recievedPacketBuffer[i] = null;
		}
		recievedPacketBufferCounter = 0;
	}
}
