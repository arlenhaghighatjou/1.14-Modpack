package com.polyvalord.extcaves.blocks.basic;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class BlockFacing extends Block {

	public static final DirectionProperty HORIZONTAL_FACING = HorizontalBlock.HORIZONTAL_FACING;

	public BlockFacing(Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH));
	}

	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext context) 
	{
		BlockState blockstate = this.getDefaultState();
		IWorldReader iworldreader = context.getWorld();
		BlockPos blockpos = context.getPos();
		Direction[] adirection = context.getNearestLookingDirections();

		for (Direction direction : adirection) 
		{
			if (direction.getAxis().isHorizontal()) 
			{
				Direction direction1 = direction.getOpposite();
				blockstate = blockstate.with(HORIZONTAL_FACING, direction1);
				if (blockstate.isValidPosition(iworldreader, blockpos)) 
				{
					return blockstate;
				}
			}
		}

		return null;
	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) 
	{
		builder.add(HORIZONTAL_FACING);
	}

}
