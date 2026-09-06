package net.p3pp3rf1y.sophisticatedbackpacks.util.energy;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.util.LazyOptional;

import javax.annotation.Nullable;

public class EnergyStorageLookup {
	private EnergyStorageLookup() {}

	public static LazyOptional<IEnergyStorage> get(ItemStack stack) {
		return BackpackWrapperLookup.get(stack)
				.resolveOptional().flatMap(wrapper -> wrapper.getEnergyStorage().map(storage -> LazyOptional.of(() -> storage)))
				.orElseGet(LazyOptional::empty);
	}

	public static LazyOptional<IEnergyStorage> get(TileEntity tile, @Nullable Direction side) {
		if (tile instanceof IEnergyStorageProvider) {
			return ((IEnergyStorageProvider) tile).getEnergyStorage(side);
		}
		return LazyOptional.empty();
	}

	public interface IEnergyStorageProvider {
		LazyOptional<IEnergyStorage> getEnergyStorage(@Nullable Direction side);
	}
}
