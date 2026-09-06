package net.p3pp3rf1y.sophisticatedbackpacks.backpack;

import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapperLookup;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.p3pp3rf1y.sophisticatedbackpacks.util.LazyOptional;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.api.ITickableUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackRenderInfo;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.NoopBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.TankPosition;
import net.p3pp3rf1y.sophisticatedbackpacks.util.WorldHelper;

import javax.annotation.Nullable;

import static net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlock.*;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.BACKPACK_TILE_TYPE;

import net.p3pp3rf1y.sophisticatedbackpacks.util.inventory.IItemHandler;

import net.p3pp3rf1y.sophisticatedbackpacks.util.inventory.ItemHandlerLookup;

import net.p3pp3rf1y.sophisticatedbackpacks.util.fluid.IFluidHandler;

import net.p3pp3rf1y.sophisticatedbackpacks.util.fluid.FluidHandlerLookup;

import net.p3pp3rf1y.sophisticatedbackpacks.util.energy.IEnergyStorage;

import net.p3pp3rf1y.sophisticatedbackpacks.util.energy.EnergyStorageLookup;

public class BackpackTileEntity extends TileEntity implements ITickableTileEntity, ItemHandlerLookup.IItemHandlerProvider, FluidHandlerLookup.IFluidHandlerProvider, EnergyStorageLookup.IEnergyStorageProvider {
	private IBackpackWrapper backpackWrapper = NoopBackpackWrapper.INSTANCE;
	private boolean updateBlockRender = true;

	public BackpackTileEntity() {
		super(BACKPACK_TILE_TYPE.get());
	}

	public void setBackpack(ItemStack backpack) {
		backpackWrapper = BackpackWrapperLookup.get(backpack).orElse(NoopBackpackWrapper.INSTANCE);
		backpackWrapper.setBackpackSaveHandler(() -> {
			setChanged();
			updateBlockRender = false;
			WorldHelper.notifyBlockUpdate(this);
		});
		backpackWrapper.setInventorySlotChangeHandler(this::setChanged);
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		setBackpackFromNbt(nbt);
		WorldHelper.notifyBlockUpdate(this);
	}

	private void setBackpackFromNbt(CompoundNBT nbt) {
		setBackpack(ItemStack.read(nbt.getCompound("backpackData")));
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		CompoundNBT ret = super.save(compound);
		writeBackpack(ret);
		return ret;
	}

	private void writeBackpack(CompoundNBT ret) {
		ItemStack backpackCopy = backpackWrapper.getBackpack().copy();
		backpackCopy.setTag(backpackCopy.getItem().getShareTag(backpackCopy));
		ret.put("backpackData", backpackCopy.save(new CompoundNBT()));
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT ret = super.getUpdateTag();
		writeBackpack(ret);
		return ret;
	}

	@Nullable
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT updateTag = getUpdateTag();
		updateTag.putBoolean("updateBlockRender", updateBlockRender);
		updateBlockRender = true;
		return new SUpdateTileEntityPacket(worldPosition, 1, updateTag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT tag = pkt.getTag();
		setBackpackFromNbt(tag);
		if (tag.getBoolean("updateBlockRender")) {
			WorldHelper.notifyBlockUpdate(this);
		}
	}

	public IBackpackWrapper getBackpackWrapper() {
		return backpackWrapper;
	}

	@Override
	public void tick() {
		//noinspection ConstantConditions - world is always non null at this point
		if (world.isRemote) {
			return;
		}
		backpackWrapper.getUpgradeHandler().getWrappersThatImplement(ITickableUpgrade.class).forEach(upgrade -> upgrade.tick(null, world, getBlockPos()));
	}

	@Override
	public LazyOptional<IItemHandler> getItemHandler(@Nullable Direction side) {
		return LazyOptional.of(() -> getBackpackWrapper().getInventoryForInputOutput());
	}

	@Override
	public LazyOptional<IFluidHandler> getFluidHandler(@Nullable Direction side) {
		return getBackpackWrapper().getFluidHandler().<LazyOptional<IFluidHandler>>map(handler -> LazyOptional.of(() -> handler)).orElseGet(LazyOptional::empty);
	}

	@Override
	public LazyOptional<IEnergyStorage> getEnergyStorage(@Nullable Direction side) {
		return getBackpackWrapper().getEnergyStorage().<LazyOptional<IEnergyStorage>>map(storage -> LazyOptional.of(() -> storage)).orElseGet(LazyOptional::empty);
	}

	public void refreshRenderState() {
		BlockState state = getBlockState();
		state = state.with(LEFT_TANK, false);
		state = state.with(RIGHT_TANK, false);
		BackpackRenderInfo renderInfo = backpackWrapper.getRenderInfo();
		for (TankPosition pos : renderInfo.getTankRenderInfos().keySet()) {
			if (pos == TankPosition.LEFT) {
				state = state.with(LEFT_TANK, true);
			} else if (pos == TankPosition.RIGHT) {
				state = state.with(RIGHT_TANK, true);
			}
		}
		state = state.with(BATTERY, renderInfo.getBatteryRenderInfo().isPresent());
		world.setBlockAndUpdate(worldPosition, state);
		world.updateNeighborsAt(worldPosition, state.getBlock());
		WorldHelper.notifyBlockUpdate(this);
	}
}
