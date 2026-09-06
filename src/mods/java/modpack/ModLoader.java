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
	/**
	 * The namespaces the pack ships assets and data under. The game only reads resources from
	 * namespaces it has been told about, so every mod has to appear here to be seen.
	 */
	private static final String[] NAMESPACES = {"appleskin", "biomesoplenty", "farmersdelight", "sophisticatedbackpacks", "waystones", "forge"};

	private static boolean registered;
	private static boolean clientRegistered;

	private ModLoader() {}

	public static boolean isLoaded(String modId) {
		if ("minecraft".equals(modId)) {
			return true;
		}
		for (String namespace : NAMESPACES) {
			if (namespace.equals(modId)) {
				return true;
			}
		}
		return false;
	}

	public static String[] resourceNamespaces(String... vanilla) {
		String[] all = new String[vanilla.length + NAMESPACES.length];
		System.arraycopy(vanilla, 0, all, 0, vanilla.length);
		System.arraycopy(NAMESPACES, 0, all, vanilla.length, NAMESPACES.length);
		return all;
	}

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
