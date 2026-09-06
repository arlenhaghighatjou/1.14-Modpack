package com.polyvalord.extcaves.blocks.treasures;

import com.polyvalord.extcaves.blocks.basic.BlockGroundFacingWL;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class BlockTreasurePotLong extends BlockGroundFacingWL 
{

	private static final VoxelShape SHAPE_NORTH = VoxelShapes.or(
			Block.makeCuboidShape(4.5, 1, 7.5, 9.5, 9, 12.5),
			Block.makeCuboidShape(4.5, 10, 7.5, 9.5, 11, 12.5),
			Block.makeCuboidShape(5.5, 9, 8.5, 8.5, 10, 11.5),
			Block.makeCuboidShape(5.5, 0, 8.5, 8.5, 1, 11.5)
			);
	private static final VoxelShape SHAPE_EAST = VoxelShapes.or(
			Block.makeCuboidShape(3.5, 1, 4.5, 8.5, 9, 9.5),
			Block.makeCuboidShape(3.5, 10, 4.5, 8.5, 11, 9.5),
			Block.makeCuboidShape(4.5, 9, 5.5, 7.5, 10, 8.5),
			Block.makeCuboidShape(4.5, 0, 5.5, 7.5, 1, 8.5)
			);
	private static final VoxelShape SHAPE_SOUTH = VoxelShapes.or(
			Block.makeCuboidShape(6.5, 1, 3.5, 11.5, 9, 8.5),
			Block.makeCuboidShape(6.5, 10, 3.5, 11.5, 11, 8.5),
			Block.makeCuboidShape(7.5, 9, 4.5, 10.5, 10, 7.5),
			Block.makeCuboidShape(7.5, 0, 4.5, 10.5, 1, 7.5)
			);
	private static final VoxelShape SHAPE_WEST = VoxelShapes.or(
			Block.makeCuboidShape(7.5, 1, 6.5, 12.5, 9, 11.5),
			Block.makeCuboidShape(7.5, 10, 6.5, 12.5, 11, 11.5),
			Block.makeCuboidShape(8.5, 9, 7.5, 11.5, 10, 10.5),
			Block.makeCuboidShape(8.5, 0, 7.5, 11.5, 1, 10.5)
			);

	public BlockTreasurePotLong(Properties properties) 
	{
		super(properties);
	}

	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) 
	{
		return getVoxelShape(state);
	}

	public static VoxelShape getVoxelShape(BlockState blockstate)
	{
		switch(blockstate.get(HORIZONTAL_FACING))
		{
			case NORTH:
				return SHAPE_NORTH;
			case EAST:
				return SHAPE_EAST;
			case SOUTH:
				return SHAPE_SOUTH;
			case WEST:
				return SHAPE_WEST;
			default:
				return null;
		}
	}

}
