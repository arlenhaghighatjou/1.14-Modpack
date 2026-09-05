package net.p3pp3rf1y.sophisticatedbackpacks.client.init;

import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapperLookup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.p3pp3rf1y.sophisticatedbackpacks.api.CapabilityBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IRenderedTankUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackRenderInfo;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.TankPosition;
import net.p3pp3rf1y.sophisticatedbackpacks.util.fluid.FluidAttributes;

import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.*;

public class ModItemColors {
	private ModItemColors() {}

	public static void init() {
		ItemColors itemColors = Minecraft.getInstance().getItemColors();

		itemColors.register((backpack, layer) -> {
			if (layer > 3 || !(backpack.getItem() instanceof BackpackItem)) {
				return -1;
			}
			return BackpackWrapperLookup.get(backpack).map(backpackWrapper -> {
				if (layer == 0) {
					return backpackWrapper.getClothColor();
				} else if (layer == 1) {
					return backpackWrapper.getBorderColor();
				}
				return getFluidColor(backpackWrapper.getRenderInfo(), layer == 2 ? TankPosition.LEFT : TankPosition.RIGHT);
			}).orElse(-1);
		}, BACKPACK.get(), IRON_BACKPACK.get(), GOLD_BACKPACK.get(), DIAMOND_BACKPACK.get(), NETHERITE_BACKPACK.get());
	}

	private static int getFluidColor(BackpackRenderInfo renderInfo, TankPosition position) {
		IRenderedTankUpgrade.TankRenderInfo tankRenderInfo = renderInfo.getTankRenderInfos().get(position);
		if (tankRenderInfo == null) {
			return -1;
		}
		return tankRenderInfo.getFluid().map(FluidAttributes::getColor).orElse(-1);
	}
}
