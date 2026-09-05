package squeek.appleskin.client;

import java.util.List;
import net.lax1dude.eaglercraft.HString;
import net.minecraft.client.Minecraft;
import net.minecraft.util.FoodStats;
import squeek.appleskin.ModConfig;

public class DebugInfoHandler {
	public static void append(List<String> lines) {
		Minecraft mc = Minecraft.getInstance();
		if (ModConfig.SHOW_FOOD_DEBUG_INFO && mc.player != null) {
			FoodStats stats = mc.player.getFoodStats();
			lines.add(HString.format("hunger: %d, sat: %.2f, exh: %.2f/4", stats.getFoodLevel(), stats.getSaturationLevel(), stats.getExhaustionLevel()));
		}
	}
}
