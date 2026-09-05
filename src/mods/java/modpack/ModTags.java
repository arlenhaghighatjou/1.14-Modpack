package modpack;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import vectorwing.farmersdelight.registry.ModItems;

import java.util.Map;

/**
 * The item tags the pack's recipes reference but no data pack defines. Forge normally ships the
 * "forge" ones and the mod's data generator the rest, so the pack has to supply them itself.
 * A data pack that does define one of these wins, since its builder is already in the map.
 */
public class ModTags {
	private ModTags() {}

	public static void addItemTags(Map<ResourceLocation, Tag.Builder<Item>> tags) {
		forge(tags, "gems/emerald", Items.EMERALD);
		forge(tags, "nuggets/gold", Items.GOLD_NUGGET);
		forge(tags, "dyes/purple", Items.PURPLE_DYE);
		forge(tags, "shears", Items.SHEARS);
		forge(tags, "milk", Items.MILK_BUCKET, ModItems.MILK_BOTTLE);
		forge(tags, "raw_beef", Items.BEEF);
		forge(tags, "raw_chicken", Items.CHICKEN);
		forge(tags, "raw_fishes/cod", Items.COD);
		forge(tags, "raw_fishes", Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH);
		forge(tags, "cooked_fishes/salmon", Items.COOKED_SALMON);
		forge(tags, "crops/onion", ModItems.ONION);
		forge(tags, "crops/tomato", ModItems.TOMATO);
		forge(tags, "crops/rice", ModItems.RICE);
		forge(tags, "crops/cabbage", ModItems.CABBAGE);
		forge(tags, "vegetables", Items.CARROT, Items.POTATO, Items.BEETROOT, ModItems.CABBAGE, ModItems.ONION, ModItems.TOMATO);
		forge(tags, "pasta", ModItems.RAW_PASTA);
		forge(tags, "salad_ingredients", ModItems.CABBAGE_LEAF, ModItems.TOMATO, ModItems.ONION, Items.CARROT, Items.BEETROOT);

		mod(tags, "farmersdelight", "tools/knives", ModItems.FLINT_KNIFE, ModItems.IRON_KNIFE, ModItems.DIAMOND_KNIFE, ModItems.GOLDEN_KNIFE);
		mod(tags, "farmersdelight", "straw_harvesters", ModItems.FLINT_KNIFE, ModItems.IRON_KNIFE, ModItems.DIAMOND_KNIFE, ModItems.GOLDEN_KNIFE);
		mod(tags, "farmersdelight", "wolf_prey", Items.CHICKEN, Items.RABBIT, Items.MUTTON);
		mod(tags, "farmersdelight", "comfort_foods", Items.MUSHROOM_STEW, Items.RABBIT_STEW, Items.BEETROOT_SOUP, Items.SUSPICIOUS_STEW);
	}

	private static void forge(Map<ResourceLocation, Tag.Builder<Item>> tags, String path, Item... items) {
		mod(tags, "forge", path, items);
	}

	private static void mod(Map<ResourceLocation, Tag.Builder<Item>> tags, String namespace, String path, Item... items) {
		ResourceLocation id = new ResourceLocation(namespace, path);
		Tag.Builder<Item> builder = tags.get(id);
		if (builder == null) {
			builder = Tag.Builder.create();
			tags.put(id, builder);
		}

		for (Item item : items) {
			if (item != null) {
				builder.add(item);
			}
		}
	}
}
