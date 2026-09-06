package net.p3pp3rf1y.sophisticatedbackpacks.client.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IRenderedTankUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackTileEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.TankPosition;
import net.p3pp3rf1y.sophisticatedbackpacks.util.fluid.FluidAttributes;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.util.WorldHelper;

import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.*;

public class ModBlockColors {
	private ModBlockColors() {}

	public static void init() {
		BlockColors blockColors = Minecraft.getInstance().getBlockColors();

		blockColors.register((state, blockDisplayReader, pos, tintIndex) -> {
			if (tintIndex < 0 || tintIndex > 3 || pos == null) {
				return -1;
			}
			return WorldHelper.getTile(blockDisplayReader, pos, BackpackTileEntity.class)
					.map(te -> getColor(te, tintIndex))
					.orElse(getDefaultColor(tintIndex));
		}, BACKPACK, IRON_BACKPACK, GOLD_BACKPACK, DIAMOND_BACKPACK, NETHERITE_BACKPACK);
	}

	private static int getColor(BackpackTileEntity te, int tintIndex) {
		if (tintIndex == 0) {
			return te.getBackpackWrapper().getClothColor();
		}
		if (tintIndex == 1) {
			return te.getBackpackWrapper().getBorderColor();
		}
		IRenderedTankUpgrade.TankRenderInfo tankRenderInfo = te.getBackpackWrapper().getRenderInfo().getTankRenderInfos().get(tintIndex == 2 ? TankPosition.LEFT : TankPosition.RIGHT);
		if (tankRenderInfo == null) {
			return -1;
		}
		return tankRenderInfo.getFluid().map(FluidAttributes::getColor).orElse(-1);
	}

	private static int getDefaultColor(int tintIndex) {
		if (tintIndex == 0) {
			return BackpackWrapper.DEFAULT_CLOTH_COLOR;
		}
		if (tintIndex == 1) {
			return BackpackWrapper.DEFAULT_BORDER_COLOR;
		}
		return -1;
	}
}
