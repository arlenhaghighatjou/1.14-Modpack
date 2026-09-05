package squeek.appleskin.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

public class NetworkHelper {
	public static void handle(PacketBuffer buffer, PlayerEntity player) {
		if (!buffer.isReadable()) {
			return;
		}
		int message = buffer.readVarInt();
		if (buffer.readableBytes() != 4) {
			return;
		}
		if (message == 1) {
			MessageExhaustionSync.handle(MessageExhaustionSync.decode(buffer), player);
		} else if (message == 2) {
			MessageSaturationSync.handle(MessageSaturationSync.decode(buffer), player);
		}
	}
}
