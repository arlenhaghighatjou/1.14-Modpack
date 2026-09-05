package squeek.appleskin.helpers;

import net.lax1dude.eaglercraft.Keyboard;
import net.minecraft.client.Minecraft;

public class KeyHelper {

	public static boolean isCtrlKeyDown() {
		return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)
				|| Minecraft.IS_RUNNING_ON_MAC && (Keyboard.isKeyDown(Keyboard.KEY_LMETA) || Keyboard.isKeyDown(Keyboard.KEY_RMETA));
	}

	public static boolean isShiftKeyDown() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}
}
