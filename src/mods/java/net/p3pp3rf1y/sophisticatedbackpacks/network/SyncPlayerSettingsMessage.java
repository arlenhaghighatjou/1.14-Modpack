package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackSettingsManager;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public class SyncPlayerSettingsMessage {
	@Nullable
	private final CompoundNBT settingsNbt;

	public SyncPlayerSettingsMessage(@Nullable CompoundNBT settingsNbt) {
		this.settingsNbt = settingsNbt;
	}

	public static void encode(SyncPlayerSettingsMessage msg, PacketBuffer packetBuffer) {
		packetBuffer.writeCompoundTag(msg.settingsNbt);
	}

	public static SyncPlayerSettingsMessage decode(PacketBuffer packetBuffer) {
		return new SyncPlayerSettingsMessage(packetBuffer.readCompoundTag());
	}

	static void onMessage(SyncPlayerSettingsMessage msg, ServerPlayerEntity player) {
		handleMessage(msg);
	}

	private static void handleMessage(SyncPlayerSettingsMessage msg) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player == null || msg.settingsNbt == null) {
			return;
		}
		//need to call the static call indirectly otherwise this message class is class loaded during packethandler init and crashes on server due to missing ClientPlayerEntity
		BiConsumer<PlayerEntity, CompoundNBT> setSettings = BackpackSettingsManager::setPlayerBackpackSettingsTag;
		setSettings.accept(player, msg.settingsNbt);
	}
}
