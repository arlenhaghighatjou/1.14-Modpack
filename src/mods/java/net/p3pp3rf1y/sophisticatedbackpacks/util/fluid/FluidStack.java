package net.p3pp3rf1y.sophisticatedbackpacks.util.fluid;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

/**
 * An amount of a fluid. Vanilla only tracks fluids as blocks and buckets, so the tank and pump
 * upgrades carry their contents in this instead.
 */
public class FluidStack {
	public static final FluidStack EMPTY = new FluidStack(Fluids.EMPTY, 0);

	private final Fluid fluid;
	private int amount;
	private CompoundNBT tag;

	public FluidStack(Fluid fluid, int amount) {
		this.fluid = fluid == null ? Fluids.EMPTY : fluid;
		this.amount = amount;
	}

	public FluidStack(FluidStack other, int amount) {
		this(other.getFluid(), amount);
		this.tag = other.tag == null ? null : other.tag.copy();
	}

	public Fluid getFluid() {
		return isEmpty() ? Fluids.EMPTY : fluid;
	}

	public Fluid getRawFluid() {
		return fluid;
	}

	public int getAmount() {
		return isEmpty() ? 0 : amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public void grow(int amount) {
		setAmount(this.amount + amount);
	}

	public void shrink(int amount) {
		setAmount(this.amount - amount);
	}

	public boolean isEmpty() {
		return fluid == Fluids.EMPTY || amount <= 0;
	}

	public CompoundNBT getTag() {
		return tag;
	}

	public void setTag(CompoundNBT tag) {
		this.tag = tag;
	}

	public boolean hasTag() {
		return tag != null;
	}

	public FluidStack copy() {
		FluidStack copy = new FluidStack(fluid, amount);
		copy.tag = tag == null ? null : tag.copy();
		return copy;
	}

	public boolean isFluidEqual(FluidStack other) {
		return other != null && getFluid() == other.getFluid();
	}

	public static boolean areFluidStackTagsEqual(FluidStack a, FluidStack b) {
		if (a == null || b == null) {
			return a == b;
		}

		return a.tag == null ? b.tag == null : a.tag.equals(b.tag);
	}

	public CompoundNBT writeToNBT(CompoundNBT nbt) {
		nbt.putString("FluidName", Registry.FLUID.getKey(getFluid()).toString());
		nbt.putInt("Amount", amount);
		if (tag != null) {
			nbt.put("Tag", tag);
		}
		return nbt;
	}

	public static FluidStack loadFluidStackFromNBT(CompoundNBT nbt) {
		if (nbt == null || !nbt.contains("FluidName")) {
			return EMPTY;
		}

		Fluid fluid = Registry.FLUID.getOrDefault(new ResourceLocation(nbt.getString("FluidName")));
		if (fluid == null || fluid == Fluids.EMPTY) {
			return EMPTY;
		}

		FluidStack stack = new FluidStack(fluid, nbt.getInt("Amount"));
		if (nbt.contains("Tag", 10)) {
			stack.tag = nbt.getCompound("Tag");
		}
		return stack;
	}
}
