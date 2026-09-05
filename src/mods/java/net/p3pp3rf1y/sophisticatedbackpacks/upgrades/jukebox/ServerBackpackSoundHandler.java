package net.p3pp3rf1y.sophisticatedbackpacks.upgrades.jukebox;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.p3pp3rf1y.sophisticatedbackpacks.network.PacketHandler;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import net.lax1dude.eaglercraft.EaglercraftUUID;

public class ServerBackpackSoundHandler {
	private ServerBackpackSoundHandler() {}

	private static final int KEEP_ALIVE_CHECK_INTERVAL = 10;
	private static final Map<RegistryKey<World>, Long> lastWorldCheck = new HashMap<>();
	private static final Map<RegistryKey<World>, Map<EaglercraftUUID, KeepAliveInfo>> worldBackpackKeepAlive = new HashMap<>();

	public static void init() {
		MinecraftForge.EVENT_BUS.addListener(ServerBackpackSoundHandler::tick);
	}

	public static void tick(TickEvent.WorldTickEvent event) {
		if (event.phase != TickEvent.Phase.END || event.world.isRemote()) {
			return;
		}
		ServerWorld world = (ServerWorld) event.world;
		RegistryKey<World> dim = world.getDimension();
		if (lastWorldCheck.computeIfAbsent(dim, key -> world.getGameTime()) > world.getGameTime() - KEEP_ALIVE_CHECK_INTERVAL || !worldBackpackKeepAlive.containsKey(dim)) {
			return;
		}
		lastWorldCheck.put(dim, world.getGameTime());

		worldBackpackKeepAlive.get(dim).entrySet().removeIf(entry -> {
			if (entry.getValue().getLastKeepAliveTime() < world.getGameTime() - KEEP_ALIVE_CHECK_INTERVAL) {
				sendStopMessage(world, entry.getValue().getLastPosition(), entry.getKey());
				return true;
			}
			return false;
		});
	}

	public static void updateKeepAlive(EaglercraftUUID backpackUuid, World world, Vec3d position, Runnable onNoLongerRunning) {
		RegistryKey<World> dim = world.dimension();
		if (!worldBackpackKeepAlive.containsKey(dim) || !worldBackpackKeepAlive.get(dim).containsKey(backpackUuid)) {
			onNoLongerRunning.run();
			return;
		}
		if (worldBackpackKeepAlive.get(dim).containsKey(backpackUuid)) {
			worldBackpackKeepAlive.get(dim).get(backpackUuid).update(world.getGameTime(), position);
		}
	}

	public static void onSoundStopped(ServerWorld world, EaglercraftUUID backpackUuid) {
		removeKeepAliveInfo(world, backpackUuid);
	}

	private static class KeepAliveInfo {
		private final WeakReference<Runnable> onStopHandler;
		private long lastKeepAliveTime;
		private Vec3d lastPosition;

		private KeepAliveInfo(Runnable onStopHandler, long lastKeepAliveTime, Vec3d lastPosition) {
			this.onStopHandler = new WeakReference<>(onStopHandler);
			this.lastKeepAliveTime = lastKeepAliveTime;
			this.lastPosition = lastPosition;
		}

		public long getLastKeepAliveTime() {
			return lastKeepAliveTime;
		}

		public Vec3d getLastPosition() {
			return lastPosition;
		}

		public void update(long gameTime, Vec3d position) {
			lastKeepAliveTime = gameTime;
			lastPosition = position;
		}

		public void runOnStop() {
			Runnable handler = onStopHandler.get();
			if (handler != null) {
				handler.run();
			}
		}
	}

	public static void startPlayingDisc(ServerWorld serverWorld, BlockPos position, EaglercraftUUID backpackUuid, int discItemId, Runnable onStopHandler) {
		Vec3d pos = Vec3d.atCenterOf(position);
		PacketHandler.sendToAllNear(serverWorld, serverWorld.getDimension(), pos, 128, new PlayDiscMessage(backpackUuid, discItemId, position));
		putKeepAliveInfo(serverWorld, backpackUuid, onStopHandler, pos);
	}

	public static void startPlayingDisc(ServerWorld serverWorld, Vec3d position, EaglercraftUUID backpackUuid, int entityId, int discItemId, Runnable onStopHandler) {
		PacketHandler.sendToAllNear(serverWorld, serverWorld.getDimension(), position, 128, new PlayDiscMessage(backpackUuid, discItemId, entityId));
		putKeepAliveInfo(serverWorld, backpackUuid, onStopHandler, position);
	}

	private static void putKeepAliveInfo(ServerWorld serverWorld, EaglercraftUUID backpackUuid, Runnable onStopHandler, Vec3d pos) {
		worldBackpackKeepAlive.computeIfAbsent(serverWorld.getDimension(), dim -> new HashMap<>()).put(backpackUuid, new KeepAliveInfo(onStopHandler, serverWorld.getGameTime(), pos));
	}

	public static void stopPlayingDisc(ServerWorld serverWorld, Vec3d position, EaglercraftUUID backpackUuid) {
		removeKeepAliveInfo(serverWorld, backpackUuid);
		sendStopMessage(serverWorld, position, backpackUuid);
	}

	private static void removeKeepAliveInfo(ServerWorld serverWorld, EaglercraftUUID backpackUuid) {
		RegistryKey<World> dim = serverWorld.getDimension();
		if (worldBackpackKeepAlive.containsKey(dim) && worldBackpackKeepAlive.get(dim).containsKey(backpackUuid)) {
			worldBackpackKeepAlive.get(dim).remove(backpackUuid).runOnStop();
		}
	}

	private static void sendStopMessage(ServerWorld serverWorld, Vec3d position, EaglercraftUUID backpackUuid) {
		PacketHandler.sendToAllNear(serverWorld, serverWorld.getDimension(), position, 128, new StopDiscPlaybackMessage(backpackUuid));
	}
}
