package vectorwing.farmersdelight.items;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class FuelBlockItem extends BlockItem {
	public final int burnTime;

	public FuelBlockItem(Block blockIn, Properties properties) {
		super(blockIn, properties);
		this.burnTime = 100;
	}

	public FuelBlockItem(Block blockIn, Properties properties, int burnTime) {
		super(blockIn, properties);
		this.burnTime = burnTime;
	}

}
