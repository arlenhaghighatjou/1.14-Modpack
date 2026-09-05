package vectorwing.farmersdelight.utils;

import net.minecraft.item.ItemStack;
import vectorwing.farmersdelight.tile.inventory.ItemStackInventory;

import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.Random;

/**
 * Util for providing and calculating math-related objects across the mod.
 */
public class MathUtils {
	public static final Random RAND = new Random();

	/**
	 * Calculates a comparator signal from an inventory, respecting per slot limits.
	 * @param handler The inventory to compare.
	 * @return The redstone signal strength.
	 */
	public static int calcRedstoneFromItemHandler(@Nullable ItemStackInventory handler) {
		if (handler == null) {
			return 0;
		} else {
			int i = 0;
			float f = 0.0F;

			for(int j = 0; j < handler.getSlots(); ++j) {
				ItemStack itemstack = handler.getStackInSlot(j);
				if (!itemstack.isEmpty()) {
					f += (float)itemstack.getCount() / (float)Math.min(handler.getSlotLimit(j), itemstack.getMaxStackSize());
					++i;
				}
			}

			f = f / (float)handler.getSlots();
			return net.minecraft.util.math.MathHelper.floor(f * 14.0F) + (i > 0 ? 1 : 0);
		}
	}
}
