package net.p3pp3rf1y.sophisticatedbackpacks.util.inventory;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.util.LazyOptional;

import javax.annotation.Nullable;

public class ItemHandlerLookup {
	private ItemHandlerLookup() {}

	public static LazyOptional<IItemHandler> get(TileEntity tile, @Nullable Direction side) {
		if (tile instanceof IItemHandlerProvider) {
			return ((IItemHandlerProvider) tile).getItemHandler(side);
		}
		if (!(tile instanceof IInventory)) {
			return LazyOptional.empty();
		}
		if (side != null && tile instanceof ISidedInventory) {
			ISidedInventory sided = (ISidedInventory) tile;
			return LazyOptional.of(() -> new SidedInvWrapper(sided, side));
		}
		IInventory inventory = (IInventory) tile;
		return LazyOptional.of(() -> new InvWrapper(inventory));
	}

	public static LazyOptional<IItemHandler> get(Entity entity, @Nullable Direction side) {
		if (!(entity instanceof PlayerEntity)) {
			return LazyOptional.empty();
		}
		PlayerEntity player = (PlayerEntity) entity;
		return LazyOptional.of(() -> new InvWrapper(player.inventory));
	}

	public static LazyOptional<IItemHandler> get(ItemStack stack) {
		return BackpackWrapperLookup.get(stack).map(wrapper -> LazyOptional.of(() -> (IItemHandler) wrapper.getInventoryForInputOutput())).orElseGet(LazyOptional::empty);
	}

	public interface IItemHandlerProvider {
		LazyOptional<IItemHandler> getItemHandler(@Nullable Direction side);
	}
}
