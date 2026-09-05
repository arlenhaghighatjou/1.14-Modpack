package squeek.appleskin;

import net.lax1dude.eaglercraft.EagRuntime;

public class ModConfig {
	public static boolean SHOW_FOOD_VALUES_IN_TOOLTIP = true;
	public static boolean ALWAYS_SHOW_FOOD_VALUES_TOOLTIP = true;
	public static boolean SHOW_SATURATION_OVERLAY = true;
	public static boolean SHOW_FOOD_VALUES_OVERLAY = true;
	public static boolean SHOW_FOOD_EXHAUSTION_UNDERLAY = true;
	public static boolean SHOW_FOOD_DEBUG_INFO = true;

	static {
		byte[] data = EagRuntime.getStorage("appleskin-config");
		if (data != null && data.length == 7 && data[0] == 1) {
			SHOW_FOOD_VALUES_IN_TOOLTIP = data[1] != 0;
			ALWAYS_SHOW_FOOD_VALUES_TOOLTIP = data[2] != 0;
			SHOW_SATURATION_OVERLAY = data[3] != 0;
			SHOW_FOOD_VALUES_OVERLAY = data[4] != 0;
			SHOW_FOOD_EXHAUSTION_UNDERLAY = data[5] != 0;
			SHOW_FOOD_DEBUG_INFO = data[6] != 0;
		}
	}

	public static void save() {
		EagRuntime.setStorage("appleskin-config", new byte[] {1, (byte) (SHOW_FOOD_VALUES_IN_TOOLTIP ? 1 : 0), (byte) (ALWAYS_SHOW_FOOD_VALUES_TOOLTIP ? 1 : 0), (byte) (SHOW_SATURATION_OVERLAY ? 1 : 0), (byte) (SHOW_FOOD_VALUES_OVERLAY ? 1 : 0), (byte) (SHOW_FOOD_EXHAUSTION_UNDERLAY ? 1 : 0), (byte) (SHOW_FOOD_DEBUG_INFO ? 1 : 0)});
	}
}
