package com.polyvalord.extcaves.blocks.basic;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class BlockFacingWL extends BlockWL
{
	
	public static final DirectionProperty HORIZONTAL_FACING = HorizontalBlock.HORIZONTAL_FACING;

	public BlockFacingWL(Properties properties) 
	{
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH).with(WATERLOGGED, false));
	}
	
	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext context)
	{
		BlockState blockstate = this.getDefaultState();
		FluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
		IWorldReader iworldreader = context.getWorld();
		BlockPos blockpos = context.getPos();
		Direction[] adirection = context.getNearestLookingDirections();

		for(Direction direction : adirection)
		{
			if (direction.getAxis().isHorizontal())
			{
				Direction direction1 = direction.getOpposite();
				blockstate = blockstate.with(HORIZONTAL_FACING, direction1).with(WATERLOGGED, Boolean.valueOf(ifluidstate.isTagged(FluidTags.WATER) && ifluidstate.getLevel() == 8));
				if (blockstate.isValidPosition(iworldreader, blockpos))
				{
					return blockstate;
				}
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
	{
		if (stateIn.get(WATERLOGGED))
		{
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}
		
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(HORIZONTAL_FACING, WATERLOGGED);
	}

}
