package squeek.appleskin.helpers;

import net.minecraft.entity.player.PlayerEntity;

public class HungerHelper {

	public static float getMaxExhaustion(PlayerEntity player) {
		return 4.0f;
	}

	public static float getExhaustion(PlayerEntity player) {
		return player.getFoodStats().getExhaustionLevel();
	}

	public static void setExhaustion(PlayerEntity player, float exhaustion) {
		player.getFoodStats().setExhaustionLevel(exhaustion);
	}
}
