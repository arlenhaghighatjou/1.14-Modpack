package net.p3pp3rf1y.sophisticatedbackpacks.api;

import net.p3pp3rf1y.sophisticatedbackpacks.util.inventory.IItemHandler;

public interface ISlotChangeResponseUpgrade {
	void onSlotChange(IItemHandler inventoryHandler, int slot);
}
