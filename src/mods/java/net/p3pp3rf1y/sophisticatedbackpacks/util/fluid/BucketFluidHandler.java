package net.p3pp3rf1y.sophisticatedbackpacks.util.fluid;

import net.p3pp3rf1y.sophisticatedbackpacks.util.fluid.FluidAttributes;

import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

public class BucketFluidHandler implements IFluidHandlerItem {
	private ItemStack container;

	public BucketFluidHandler(ItemStack container) {
		this.container = container;
	}

	@Override
	public ItemStack getContainer() {
		return container;
	}

	private Fluid getFluid() {
		if (!(container.getItem() instanceof BucketItem)) {
			return Fluids.EMPTY;
		}
		return ((BucketItem) container.getItem()).getFluid();
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
		return container.getCount() == 1 && stack.getAmount() >= FluidAttributes.BUCKET && getFluid() == Fluids.EMPTY && stack.getFluid().getFilledBucket() != Items.AIR;
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (!isFluidValid(0, resource)) {
			return 0;
		}

		if (action.execute()) {
			container = new ItemStack(resource.getFluid().getFilledBucket());
		}
		return FluidAttributes.BUCKET;
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
		Fluid fluid = getFluid();
		if (fluid == Fluids.EMPTY || container.getCount() != 1 || maxDrain < FluidAttributes.BUCKET) {
			return FluidStack.EMPTY;
		}

		if (action.execute()) {
			container = new ItemStack(Items.BUCKET);
		}
		return new FluidStack(fluid, FluidAttributes.BUCKET);
	}
}
