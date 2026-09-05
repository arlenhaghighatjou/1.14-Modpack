package net.p3pp3rf1y.sophisticatedbackpacks.util.energy;

/**
 * Something that stores energy. The battery upgrade keeps its charge here.
 */
public interface IEnergyStorage {
	int receiveEnergy(int maxReceive, boolean simulate);

	int extractEnergy(int maxExtract, boolean simulate);

	int getEnergyStored();

	int getMaxEnergyStored();

	boolean canExtract();

	boolean canReceive();
}
