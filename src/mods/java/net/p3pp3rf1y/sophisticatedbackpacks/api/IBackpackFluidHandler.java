package net.p3pp3rf1y.sophisticatedbackpacks.api;

import net.minecraft.fluid.Fluid;
import net.minecraft.tags.Tag;
import net.p3pp3rf1y.sophisticatedbackpacks.util.fluid.FluidStack;
import net.p3pp3rf1y.sophisticatedbackpacks.util.fluid.IFluidHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.util.fluid.IFluidHandlerItem;

public interface IBackpackFluidHandler extends IFluidHandlerItem {
	default int fill(Tag<Fluid> fluidTag, int maxFill, Fluid fallbackFluid, FluidAction action) {
		return fill(fluidTag, maxFill, fallbackFluid, action, false);
	}

	default int fill(Tag<Fluid> fluidTag, int maxFill, Fluid fallbackFluid, FluidAction action, boolean ignoreInOutLimit) {
		for (int tank = 0; tank < getTanks(); tank++) {
			Fluid tankFluid = getFluidInTank(tank).getFluid();
			if (fluidTag.contains(tankFluid)) {
				return fill(new FluidStack(tankFluid, maxFill), action, ignoreInOutLimit);
			}
		}
		return fill(new FluidStack(fallbackFluid, maxFill), action, ignoreInOutLimit);
	}

	int fill(FluidStack resource, FluidAction action, boolean ignoreInOutLimit);

	FluidStack drain(Tag<Fluid> resourceTag, int maxDrain, FluidAction action, boolean ignoreInOutLimit);

	FluidStack drain(FluidStack resource, FluidAction action, boolean ignoreInOutLimit);

	FluidStack drain(int maxDrain, FluidAction action, boolean ignoreInOutLimit);
}
