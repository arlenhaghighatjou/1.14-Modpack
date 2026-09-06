package net.p3pp3rf1y.sophisticatedbackpacks.util.fluid;

import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.util.LazyOptional;

import javax.annotation.Nullable;

public class FluidHandlerLookup {
	private FluidHandlerLookup() {}

	public static LazyOptional<IFluidHandler> get(TileEntity tile, @Nullable Direction side) {
		if (tile instanceof IFluidHandlerProvider) {
			return ((IFluidHandlerProvider) tile).getFluidHandler(side);
		}
		return LazyOptional.empty();
	}

	public static LazyOptional<IFluidHandlerItem> getItem(ItemStack stack) {
		if (stack.getItem() instanceof BucketItem) {
			return LazyOptional.of(() -> new BucketFluidHandler(stack));
		}
		if (!Boolean.TRUE.equals(Config.COMMON.itemFluidHandlerEnabled)) {
			return LazyOptional.empty();
		}
		return BackpackWrapperLookup.get(stack)
				.resolveOptional().flatMap(wrapper -> wrapper.getFluidHandler().map(handler -> LazyOptional.of(() -> (IFluidHandlerItem) handler)))
				.orElseGet(LazyOptional::empty);
	}

	public interface IFluidHandlerProvider {
		LazyOptional<IFluidHandler> getFluidHandler(@Nullable Direction side);
	}
}
