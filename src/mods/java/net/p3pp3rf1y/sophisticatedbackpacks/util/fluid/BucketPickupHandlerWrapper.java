package net.p3pp3rf1y.sophisticatedbackpacks.util.fluid;

import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BucketPickupHandlerWrapper implements IFluidHandler {
	private final IBucketPickupHandler block;
	private final World world;
	private final BlockPos pos;

	public BucketPickupHandlerWrapper(IBucketPickupHandler block, World world, BlockPos pos) {
		this.block = block;
		this.world = world;
		this.pos = pos;
	}

	private Fluid getFluid() {
		return world.getFluidState(pos).getFluid();
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		Fluid fluid = getFluid();
		return fluid == Fluids.EMPTY ? FluidStack.EMPTY : new FluidStack(fluid, FluidAttributes.BUCKET);
	}

	@Override
	public int getTankCapacity(int tank) {
		return FluidAttributes.BUCKET;
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
		if (resource.isEmpty() || resource.getFluid() != getFluid()) {
			return FluidStack.EMPTY;
		}
		return drain(resource.getAmount(), action);
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		if (maxDrain < FluidAttributes.BUCKET) {
			return FluidStack.EMPTY;
		}

		if (action.simulate()) {
			Fluid fluid = getFluid();
			return fluid == Fluids.EMPTY ? FluidStack.EMPTY : new FluidStack(fluid, FluidAttributes.BUCKET);
		}

		Fluid picked = block.pickupFluid(world, pos, world.getBlockState(pos));
		return picked == Fluids.EMPTY ? FluidStack.EMPTY : new FluidStack(picked, FluidAttributes.BUCKET);
	}
}
