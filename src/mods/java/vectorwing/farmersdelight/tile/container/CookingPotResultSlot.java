package vectorwing.farmersdelight.tile.container;

import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;

public class CookingPotResultSlot extends Slot
{
	public CookingPotResultSlot(IInventory inventoryIn, int index, int xPosition, int yPosition)
	{
		super(inventoryIn, index, xPosition, yPosition);
	}

	public boolean isItemValid(ItemStack stack) {
		return false;
	}
}
