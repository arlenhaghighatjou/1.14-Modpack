package vectorwing.farmersdelight.items;

import net.minecraft.item.Item;

public class FuelItem extends Item {
	public final int burnTime;

	public FuelItem(Properties properties) {
		super(properties);
		this.burnTime = 100;
	}

	public FuelItem(Properties properties, int burnTime) {
		super(properties);
		this.burnTime = burnTime;
	}

}
