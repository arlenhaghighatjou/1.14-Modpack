package net.p3pp3rf1y.sophisticatedbackpacks.network;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.jukebox.PlayDiscMessage;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.jukebox.SoundStopNotificationMessage;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.jukebox.StopDiscPlaybackMessage;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.tank.TankClickMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * The mod's packets, carried on a vanilla custom payload channel. Each message keeps its own
 * encoder, decoder and handler; the leading id says which one a payload belongs to.
 */
public class PacketHandler {
	private PacketHandler() {}

	public static final ResourceLocation CHANNEL = new ResourceLocation(SophisticatedBackpacks.MOD_ID, "channel");

	private static final List<MessageType<?>> TYPES = new ArrayList<>();
	private static final Map<Class<?>, Integer> IDS = new HashMap<>();

	private static class MessageType<M> {
		private final BiConsumer<M, PacketBuffer> encoder;
		private final Function<PacketBuffer, M> decoder;
		private final BiConsumer<M, ServerPlayerEntity> handler;

		private MessageType(BiConsumer<M, PacketBuffer> encoder, Function<PacketBuffer, M> decoder, BiConsumer<M, ServerPlayerEntity> handler) {
			this.encoder = encoder;
			this.decoder = decoder;
			this.handler = handler;
		}

		@SuppressWarnings("unchecked")
		private void encode(Object message, PacketBuffer buffer) {
			encoder.accept((M) message, buffer);
		}

		private void handle(PacketBuffer buffer, ServerPlayerEntity player) {
			handler.accept(decoder.apply(buffer), player);
		}
	}

	public static <M> void registerMessage(Class<M> messageType, BiConsumer<M, PacketBuffer> encoder, Function<PacketBuffer, M> decoder, BiConsumer<M, ServerPlayerEntity> messageConsumer) {
		IDS.put(messageType, TYPES.size());
		TYPES.add(new MessageType<>(encoder, decoder, messageConsumer));
	}

	private static PacketBuffer write(Object message) {
		Integer id = IDS.get(message.getClass());
		if (id == null) {
			return null;
		}

		PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
		buffer.writeVarInt(id);
		TYPES.get(id).encode(message, buffer);
		return buffer;
	}

	public static <M> void sendToServer(M message) {
		PacketBuffer buffer = write(message);
		if (buffer != null) {
			Minecraft.getInstance().getConnection().sendPacket(new CCustomPayloadPacket(CHANNEL, buffer));
		}
	}

	public static <M> void sendToClient(ServerPlayerEntity player, M message) {
		PacketBuffer buffer = write(message);
		if (buffer != null) {
			player.connection.sendPacket(new SCustomPayloadPlayPacket(CHANNEL, buffer));
		}
	}

	public static <M> void sendToAllNear(ServerWorld world, DimensionType dimension, Vec3d position, int range, M message) {
		for (ServerPlayerEntity player : world.getPlayers()) {
			if (player.world.getDimension().getType() == dimension && player.getDistanceSq(position) <= (double) range * range) {
				sendToClient(player, message);
			}
		}
	}

	public static void handleServer(PacketBuffer buffer, ServerPlayerEntity player) {
		int id = buffer.readVarInt();
		if (id >= 0 && id < TYPES.size()) {
			TYPES.get(id).handle(buffer, player);
		}
	}

	public static void handleClient(PacketBuffer buffer) {
		int id = buffer.readVarInt();
		if (id >= 0 && id < TYPES.size()) {
			TYPES.get(id).handle(buffer, null);
		}
	}

	public static void openContainer(ServerPlayerEntity player, net.minecraft.inventory.container.INamedContainerProvider provider, net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContext context) {
		PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
		context.toBuffer(buffer);
		byte[] data = new byte[buffer.readableBytes()];
		buffer.readBytes(data);
		sendToClient(player, new ContainerOpenDataMessage(data));
		player.openContainer(provider);
	}

	public static void init() {

		registerMessage(BackpackOpenMessage.class, BackpackOpenMessage::encode, BackpackOpenMessage::decode, BackpackOpenMessage::onMessage);
		registerMessage(SyncContainerClientDataMessage.class, SyncContainerClientDataMessage::encode, SyncContainerClientDataMessage::decode, SyncContainerClientDataMessage::onMessage);
		registerMessage(UpgradeToggleMessage.class, UpgradeToggleMessage::encode, UpgradeToggleMessage::decode, UpgradeToggleMessage::onMessage);
		registerMessage(RequestBackpackInventoryContentsMessage.class, RequestBackpackInventoryContentsMessage::encode, RequestBackpackInventoryContentsMessage::decode, RequestBackpackInventoryContentsMessage::onMessage);
		registerMessage(BackpackContentsMessage.class, BackpackContentsMessage::encode, BackpackContentsMessage::decode, BackpackContentsMessage::onMessage);
		registerMessage(InventoryInteractionMessage.class, InventoryInteractionMessage::encode, InventoryInteractionMessage::decode, InventoryInteractionMessage::onMessage);
		registerMessage(TransferFullSlotMessage.class, TransferFullSlotMessage::encode, TransferFullSlotMessage::decode, TransferFullSlotMessage::onMessage);
		registerMessage(SyncContainerStacksMessage.class, SyncContainerStacksMessage::encode, SyncContainerStacksMessage::decode, SyncContainerStacksMessage::onMessage);
		registerMessage(SyncSlotStackMessage.class, SyncSlotStackMessage::encode, SyncSlotStackMessage::decode, SyncSlotStackMessage::onMessage);
		registerMessage(WindowClickMessage.class, WindowClickMessage::encode, WindowClickMessage::decode, WindowClickMessage::onMessage);
		registerMessage(PlayDiscMessage.class, PlayDiscMessage::encode, PlayDiscMessage::decode, PlayDiscMessage::onMessage);
		registerMessage(StopDiscPlaybackMessage.class, StopDiscPlaybackMessage::encode, StopDiscPlaybackMessage::decode, StopDiscPlaybackMessage::onMessage);
		registerMessage(SoundStopNotificationMessage.class, SoundStopNotificationMessage::encode, SoundStopNotificationMessage::decode, SoundStopNotificationMessage::onMessage);
		registerMessage(BlockToolSwapMessage.class, BlockToolSwapMessage::encode, BlockToolSwapMessage::decode, BlockToolSwapMessage::onMessage);
		registerMessage(EntityToolSwapMessage.class, EntityToolSwapMessage::encode, EntityToolSwapMessage::decode, EntityToolSwapMessage::onMessage);
		registerMessage(SyncClientInfoMessage.class, SyncClientInfoMessage::encode, SyncClientInfoMessage::decode, SyncClientInfoMessage::onMessage);
		registerMessage(TankClickMessage.class, TankClickMessage::encode, TankClickMessage::decode, TankClickMessage::onMessage);
		registerMessage(SyncPlayerSettingsMessage.class, SyncPlayerSettingsMessage::encode, SyncPlayerSettingsMessage::decode, SyncPlayerSettingsMessage::onMessage);
		registerMessage(BackpackCloseMessage.class, (backpackCloseMessage, packetBuffer) -> {}, packetBuffer -> new BackpackCloseMessage(), (backpackCloseMessage, contextSupplier) -> BackpackCloseMessage.onMessage(contextSupplier));
		registerMessage(BackpackInsertMessage.class, BackpackInsertMessage::encode, BackpackInsertMessage::decode, BackpackInsertMessage::onMessage);
		registerMessage(ContainerOpenDataMessage.class, ContainerOpenDataMessage::encode, ContainerOpenDataMessage::decode, ContainerOpenDataMessage::onMessage);
	}
}
