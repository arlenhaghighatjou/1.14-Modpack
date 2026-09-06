package com.polyvalord.extcaves.blocks.decoration;

import com.polyvalord.extcaves.blocks.basic.BlockGroundFacingWL;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class BlockDecorationPotQuartzShort extends BlockGroundFacingWL
{
	
	private static final VoxelShape SHAPE_NS = VoxelShapes.or(
			Block.makeCuboidShape(5.5, 1, 5.5, 10.5, 6, 10.5),
			Block.makeCuboidShape(5.5, 7, 5.5, 10.5, 8, 10.5),
			Block.makeCuboidShape(6.5, 6, 6.5, 9.5, 7, 9.5),
			Block.makeCuboidShape(6.5, 0, 6.5, 9.5, 1, 9.5),
			Block.makeCuboidShape(3.5, 3, 7.5, 5.5, 6, 8.5),
			Block.makeCuboidShape(10.5, 3, 7.5, 12.5, 6, 8.5)
			);
	
	private static final VoxelShape SHAPE_EW = VoxelShapes.or(
			Block.makeCuboidShape(5.5, 1, 5.5, 10.5, 6, 10.5),
			Block.makeCuboidShape(5.5, 7, 5.5, 10.5, 8, 10.5),
			Block.makeCuboidShape(6.5, 6, 6.5, 9.5, 7, 9.5),
			Block.makeCuboidShape(6.5, 0, 6.5, 9.5, 1, 9.5),
			Block.makeCuboidShape(7.5, 3, 3.5, 8.5, 6, 5.5),
			Block.makeCuboidShape(7.5, 3, 10.5, 8.5, 6, 12.5)
			);

	public BlockDecorationPotQuartzShort(Properties properties) 
	{
		super(properties);
	}
	
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) 
	{
		return getVoxelShape(state);
	}

	public static VoxelShape getVoxelShape(BlockState blockstate)
	{
		if (blockstate.get(HORIZONTAL_FACING) == Direction.NORTH ||
			blockstate.get(HORIZONTAL_FACING) == Direction.SOUTH)
		{
			return SHAPE_NS;
		}
		else
		{
			return SHAPE_EW;
		}
	}

}
