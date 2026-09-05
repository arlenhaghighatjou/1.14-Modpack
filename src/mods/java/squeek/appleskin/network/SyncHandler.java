package squeek.appleskin.network;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;

public class SyncHandler {
	public static final ResourceLocation CHANNEL = new ResourceLocation("appleskin", "sync");
	private float lastSaturation = Float.NaN;
	private float lastExhaustion = Float.NaN;

	public void tick(ServerPlayerEntity player) {
		FoodStats stats = player.getFoodStats();
		float saturation = stats.getSaturationLevel();
		float exhaustion = stats.getExhaustionLevel();
		if (saturation != lastSaturation) {
			send(player, 2, saturation);
			lastSaturation = saturation;
		}
		if (Float.isNaN(lastExhaustion) || Math.abs(lastExhaustion - exhaustion) >= 0.01f) {
			send(player, 1, exhaustion);
			lastExhaustion = exhaustion;
		}
	}

	private static void send(ServerPlayerEntity player, int message, float value) {
		PacketBuffer buffer = new PacketBuffer(Unpooled.buffer(5));
		buffer.writeVarInt(message);
		buffer.writeFloat(value);
		player.connection.sendPacket(new SCustomPayloadPlayPacket(CHANNEL, buffer));
	}
}
