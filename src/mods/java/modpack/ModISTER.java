package modpack;

import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackISTER;

/**
 * Built in item renderers the pack mods bring along.
 */
@OnlyIn(Dist.CLIENT)
public class ModISTER {
	private ModISTER() {}

	public static boolean render(ItemStack stack) {
		if (stack.getItem() instanceof BackpackItem) {
			BackpackISTER.INSTANCE.renderByItem(stack);
			return true;
		}
		return false;
	}
}
