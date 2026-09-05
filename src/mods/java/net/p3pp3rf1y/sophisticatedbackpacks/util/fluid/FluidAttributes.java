package net.p3pp3rf1y.sophisticatedbackpacks.util.fluid;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;

/**
 * The bits of fluid presentation the tank and pump upgrades need. Vanilla keeps this on the block
 * models instead of the fluid, so the two fluids that exist are mapped here.
 */
public class FluidAttributes {
	/** One bucket, in millibuckets. */
	public static final int BUCKET = 1000;

	private static final ResourceLocation WATER_STILL = new ResourceLocation("block/water_still");
	private static final ResourceLocation LAVA_STILL = new ResourceLocation("block/lava_still");

	private static final int WATER_COLOR = 0xFF3F76E4;
	private static final int NO_TINT = 0xFFFFFFFF;

	private FluidAttributes() {}

	public static ResourceLocation getStillTexture(Fluid fluid) {
		return isLava(fluid) ? LAVA_STILL : WATER_STILL;
	}

	public static int getColor(Fluid fluid) {
		return isLava(fluid) ? NO_TINT : WATER_COLOR;
	}

	private static boolean isLava(Fluid fluid) {
		return fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA;
	}
}
