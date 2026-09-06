package net.p3pp3rf1y.sophisticatedbackpacks.util.inventory;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemHandlerHelper {
	private ItemHandlerHelper() {}

	public static boolean canItemStacksStack(@Nonnull ItemStack a, @Nonnull ItemStack b) {
		if (a.isEmpty() || !a.isItemEqual(b) || a.hasTag() != b.hasTag()) {
			return false;
		}

		return (!a.hasTag() || a.getTag().equals(b.getTag()));
	}

	@Nonnull
	public static ItemStack copyStackWithSize(@Nonnull ItemStack stack, int size) {
		if (size == 0) {
			return ItemStack.EMPTY;
		}

		ItemStack copy = stack.copy();
		copy.setCount(size);
		return copy;
	}

	@Nonnull
	public static ItemStack insertItem(IItemHandler dest, @Nonnull ItemStack stack, boolean simulate) {
		if (dest == null || stack.isEmpty()) {
			return stack;
		}

		for (int i = 0; i < dest.getSlots(); i++) {
			stack = dest.insertItem(i, stack, simulate);
			if (stack.isEmpty()) {
				return ItemStack.EMPTY;
			}
		}

		return stack;
	}

	@Nonnull
	public static ItemStack insertItemStacked(IItemHandler dest, @Nonnull ItemStack stack, boolean simulate) {
		if (dest == null || stack.isEmpty()) {
			return stack;
		}

		if (!stack.isStackable()) {
			return insertItem(dest, stack, simulate);
		}

		for (int i = 0; i < dest.getSlots(); i++) {
			ItemStack existing = dest.getStackInSlot(i);
			if (!existing.isEmpty() && canItemStacksStack(stack, existing)) {
				stack = dest.insertItem(i, stack, simulate);
				if (stack.isEmpty()) {
					return ItemStack.EMPTY;
				}
			}
		}

		return insertItem(dest, stack, simulate);
	}

	public static void giveItemToPlayer(PlayerEntity player, @Nonnull ItemStack stack) {
		if (stack.isEmpty()) {
			return;
		}

		IItemHandler inventory = new InvWrapper(player.inventory);
		ItemStack remainder = insertItemStacked(inventory, stack, false);
		if (remainder.isEmpty()) {
			player.world.playSound(null, player.posX, player.posY, player.posZ, net.minecraft.util.SoundEvents.ENTITY_ITEM_PICKUP, net.minecraft.util.SoundCategory.PLAYERS, 0.2F, 1.0F);
			return;
		}

		ItemEntity entity = new ItemEntity(player.world, player.posX, player.posY + 0.5D, player.posZ, remainder);
		entity.setPickupDelay(40);
		player.world.addEntity(entity);
	}
}
