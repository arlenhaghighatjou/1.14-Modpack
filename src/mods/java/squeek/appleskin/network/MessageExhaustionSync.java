package squeek.appleskin.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

public class MessageExhaustionSync {
	private final float exhaustionLevel;

	public MessageExhaustionSync(float value) {
		this.exhaustionLevel = value;
	}

	public static void encode(MessageExhaustionSync message, PacketBuffer buffer) {
		buffer.writeFloat(message.exhaustionLevel);
	}

	public static MessageExhaustionSync decode(PacketBuffer buffer) {
		return new MessageExhaustionSync(buffer.readFloat());
	}

	public static void handle(MessageExhaustionSync message, PlayerEntity player) {
		if (player != null && Float.isFinite(message.exhaustionLevel)) {
			player.getFoodStats().setExhaustionLevel(message.exhaustionLevel);
		}
	}
}
