package net.p3pp3rf1y.sophisticatedbackpacks.util.fluid;

import net.p3pp3rf1y.sophisticatedbackpacks.util.fluid.FluidAttributes;

import net.p3pp3rf1y.sophisticatedbackpacks.util.fluid.FluidHandlerLookup;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.p3pp3rf1y.sophisticatedbackpacks.util.inventory.IItemHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.util.inventory.ItemHandlerHelper;

import javax.annotation.Nullable;

public class FluidUtil {
	private FluidUtil() {}

	public static FluidStack tryFluidTransfer(IFluidHandler destination, IFluidHandler source, FluidStack resource, boolean doTransfer) {
		FluidStack drainable = source.drain(resource, IFluidHandler.FluidAction.SIMULATE);
		if (drainable.isEmpty()) {
			return FluidStack.EMPTY;
		}

		int filled = destination.fill(drainable, IFluidHandler.FluidAction.SIMULATE);
		if (filled <= 0) {
			return FluidStack.EMPTY;
		}

		if (!doTransfer) {
			return new FluidStack(drainable, filled);
		}

		FluidStack drained = source.drain(new FluidStack(drainable, filled), IFluidHandler.FluidAction.EXECUTE);
		if (drained.isEmpty()) {
			return FluidStack.EMPTY;
		}

		destination.fill(drained, IFluidHandler.FluidAction.EXECUTE);
		return drained;
	}

	public static FluidActionResult tryEmptyContainerAndStow(ItemStack container, IFluidHandler destination, IItemHandler inventory, int maxAmount, @Nullable PlayerEntity player, boolean doDrain) {
		if (container.isEmpty()) {
			return FluidActionResult.FAILURE;
		}

		IFluidHandlerItem containerHandler = FluidHandlerLookup.getItem(container).orElse(null);
		if (containerHandler == null) {
			return FluidActionResult.FAILURE;
		}

		FluidStack drained = containerHandler.drain(maxAmount, IFluidHandler.FluidAction.SIMULATE);
		if (drained.isEmpty() || destination.fill(drained, IFluidHandler.FluidAction.SIMULATE) <= 0) {
			return FluidActionResult.FAILURE;
		}

		if (!doDrain) {
			return new FluidActionResult(container);
		}

		FluidStack actuallyDrained = containerHandler.drain(maxAmount, IFluidHandler.FluidAction.EXECUTE);
		destination.fill(actuallyDrained, IFluidHandler.FluidAction.EXECUTE);
		return stow(container, containerHandler.getContainer(), inventory, player);
	}

	public static FluidActionResult tryFillContainerAndStow(ItemStack container, IFluidHandler source, IItemHandler inventory, int maxAmount, @Nullable PlayerEntity player, boolean doFill) {
		if (container.isEmpty()) {
			return FluidActionResult.FAILURE;
		}

		IFluidHandlerItem containerHandler = FluidHandlerLookup.getItem(container).orElse(null);
		if (containerHandler == null) {
			return FluidActionResult.FAILURE;
		}

		FluidStack drained = source.drain(maxAmount, IFluidHandler.FluidAction.SIMULATE);
		if (drained.isEmpty() || containerHandler.fill(drained, IFluidHandler.FluidAction.SIMULATE) <= 0) {
			return FluidActionResult.FAILURE;
		}

		if (!doFill) {
			return new FluidActionResult(container);
		}

		int filled = containerHandler.fill(drained, IFluidHandler.FluidAction.EXECUTE);
		source.drain(new FluidStack(drained, filled), IFluidHandler.FluidAction.EXECUTE);
		return stow(container, containerHandler.getContainer(), inventory, player);
	}

	private static FluidActionResult stow(ItemStack original, ItemStack result, IItemHandler inventory, @Nullable PlayerEntity player) {
		if (original.getCount() == 1) {
			return new FluidActionResult(result);
		}

		ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, result, false);
		if (!remainder.isEmpty()) {
			if (player == null) {
				return FluidActionResult.FAILURE;
			}
			ItemHandlerHelper.giveItemToPlayer(player, remainder);
		}

		ItemStack shrunk = original.copy();
		shrunk.shrink(1);
		return new FluidActionResult(shrunk);
	}

	public static boolean tryPlaceFluid(@Nullable PlayerEntity player, World world, Hand hand, BlockPos pos, IFluidHandler source, FluidStack resource) {
		if (resource.isEmpty() || resource.getAmount() < FluidAttributes.BUCKET) {
			return false;
		}

		Fluid fluid = resource.getFluid();
		if (!(fluid instanceof FlowingFluid)) {
			return false;
		}

		BlockState existing = world.getBlockState(pos);
		if (!existing.isAir() && !existing.getMaterial().isReplaceable()) {
			return false;
		}

		if (world.getDimension().doesWaterVaporize() && fluid.isIn(net.minecraft.tags.FluidTags.WATER)) {
			playVaporize(world, pos);
			source.drain(new FluidStack(fluid, FluidAttributes.BUCKET), IFluidHandler.FluidAction.EXECUTE);
			return true;
		}

		FluidStack drained = source.drain(new FluidStack(fluid, FluidAttributes.BUCKET), IFluidHandler.FluidAction.EXECUTE);
		if (drained.isEmpty()) {
			return false;
		}

		world.setBlockState(pos, ((FlowingFluid) fluid).getDefaultState().getBlockState(), 11);
		world.playSound(player, pos, getPlaceSound(fluid), SoundCategory.BLOCKS, 1.0F, 1.0F);
		return true;
	}

	private static SoundEvent getPlaceSound(Fluid fluid) {
		return fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
	}

	private static void playVaporize(World world, BlockPos pos) {
		world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
		for (int i = 0; i < 8; ++i) {
			world.addParticle(net.minecraft.particles.ParticleTypes.LARGE_SMOKE, pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
		}
	}
}
