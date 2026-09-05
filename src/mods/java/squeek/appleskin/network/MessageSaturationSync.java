package squeek.appleskin.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

public class MessageSaturationSync {
	private final float saturationLevel;

	public MessageSaturationSync(float value) {
		this.saturationLevel = value;
	}

	public static void encode(MessageSaturationSync message, PacketBuffer buffer) {
		buffer.writeFloat(message.saturationLevel);
	}

	public static MessageSaturationSync decode(PacketBuffer buffer) {
		return new MessageSaturationSync(buffer.readFloat());
	}

	public static void handle(MessageSaturationSync message, PlayerEntity player) {
		if (player != null && Float.isFinite(message.saturationLevel)) {
			player.getFoodStats().setFoodSaturationLevel(message.saturationLevel);
		}
	}
}
