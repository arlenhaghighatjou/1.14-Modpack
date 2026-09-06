package net.p3pp3rf1y.sophisticatedbackpacks.util.fluid;

public class EmptyFluidHandler implements IFluidHandler {
	public static final EmptyFluidHandler INSTANCE = new EmptyFluidHandler();

	protected EmptyFluidHandler() {}

	@Override
	public int getTanks() {
		return 0;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return FluidStack.EMPTY;
	}

	@Override
	public int getTankCapacity(int tank) {
		return 0;
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return false;
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		return FluidStack.EMPTY;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		return FluidStack.EMPTY;
	}
}
