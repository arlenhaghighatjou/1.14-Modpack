package net.p3pp3rf1y.sophisticatedbackpacks.upgrades.jukebox;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.EntityTickableSound;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.p3pp3rf1y.sophisticatedbackpacks.network.PacketHandler;

import java.util.Map;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import java.util.concurrent.ConcurrentHashMap;

public class BackpackSoundHandler {
	private static final int SOUND_STOP_CHECK_INTERVAL = 10;

	private BackpackSoundHandler() {}

	private static final Map<EaglercraftUUID, ISound> backpackSounds = new ConcurrentHashMap<>();
	private static long lastPlaybackChecked = 0;

	public static void playBackpackSound(EaglercraftUUID backpackUuid, ISound sound) {
		stopBackpackSound(backpackUuid);
		backpackSounds.put(backpackUuid, sound);
		Minecraft.getInstance().getSoundHandler().play(sound);
	}

	public static void stopBackpackSound(EaglercraftUUID backpackUuid) {
		if (backpackSounds.containsKey(backpackUuid)) {
			Minecraft.getInstance().getSoundHandler().stop(backpackSounds.remove(backpackUuid));
			PacketHandler.sendToServer(new SoundStopNotificationMessage(backpackUuid));
		}
	}

	public static void tick(World world) {
		if (!backpackSounds.isEmpty() && lastPlaybackChecked < world.getGameTime() - SOUND_STOP_CHECK_INTERVAL) {
			lastPlaybackChecked = world.getGameTime();
			backpackSounds.entrySet().removeIf(entry -> {
				if (!Minecraft.getInstance().getSoundHandler().isPlaying(entry.getValue())) {
					PacketHandler.sendToServer(new SoundStopNotificationMessage(entry.getKey()));
					return true;
				}
				return false;
			});
		}
	}

	public static void playBackpackSound(SoundEvent soundEvent, EaglercraftUUID backpackUuid, BlockPos pos) {
		playBackpackSound(backpackUuid, new SimpleSound(soundEvent, SoundCategory.RECORDS, 4.0F, 1.0F, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F));
	}

	public static void playBackpackSound(SoundEvent soundEvent, EaglercraftUUID backpackUuid, int entityId) {
		ClientWorld world = Minecraft.getInstance().world;
		if (world == null) {
			return;
		}

		Entity entity = world.getEntityByID(entityId);
		if (!(entity instanceof LivingEntity)) {
			return;
		}
		playBackpackSound(backpackUuid, new EntityTickableSound(soundEvent, SoundCategory.RECORDS, 2, 1, entity));
	}

	public static void onWorldUnload() {
		backpackSounds.clear();
		lastPlaybackChecked = 0;
	}
}
