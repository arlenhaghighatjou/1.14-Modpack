package modpack;

import biomesoplenty.core.BiomesOPlenty;
import net.blay09.mods.waystones.Waystones;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vectorwing.farmersdelight.FarmersDelight;

/**
 * Brings up every mod in the pack in the order the game expects: content first, then the setup
 * that reads it back.
 */
public class ModLoader {
	private static boolean registered;
	private static boolean clientRegistered;

	private ModLoader() {}

	public static void registerContent() {
		if (registered) {
			return;
		}

		registered = true;

		new BiomesOPlenty().commonSetup();

		FarmersDelight.registerContent();
		FarmersDelight.commonSetup();

		Waystones.registerContent();
		Waystones.setup();
	}

	@OnlyIn(Dist.CLIENT)
	public static void registerClient() {
		if (clientRegistered) {
			return;
		}

		clientRegistered = true;

		BiomesOPlenty.instance.clientSetup();
		BiomesOPlenty.instance.loadComplete();

		FarmersDelight.clientSetup();

		Waystones.setupClient();
	}
}
