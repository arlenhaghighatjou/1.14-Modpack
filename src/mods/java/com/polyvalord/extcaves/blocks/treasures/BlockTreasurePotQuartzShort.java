package com.polyvalord.extcaves.blocks.treasures;

import com.polyvalord.extcaves.blocks.basic.BlockGroundFacingWL;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class BlockTreasurePotQuartzShort extends BlockGroundFacingWL 
{

	private static final VoxelShape SHAPE_NORTH = VoxelShapes.or(
			Block.makeCuboidShape(4.5, 1, 7.5, 9.5, 6, 12.5),
			Block.makeCuboidShape(4.5, 7, 7.5, 9.5, 8, 12.5),
			Block.makeCuboidShape(5.5, 6, 8.5, 8.5, 7, 11.5),
			Block.makeCuboidShape(5.5, 0, 8.5, 8.5, 1, 11.5),
			Block.makeCuboidShape(2.5, 3, 9.5, 4.5, 6, 10.5),
			Block.makeCuboidShape(9.5, 3, 9.5, 11.5, 6, 10.5)
			);
	private static final VoxelShape SHAPE_EAST = VoxelShapes.or(
			Block.makeCuboidShape(3.5, 1, 4.5, 8.5, 6, 9.5),
			Block.makeCuboidShape(3.5, 7, 4.5, 8.5, 8, 9.5),
			Block.makeCuboidShape(4.5, 6, 5.5, 7.5, 7, 8.5),
			Block.makeCuboidShape(4.5, 0, 5.5, 7.5, 1, 8.5),
			Block.makeCuboidShape(5.5, 3, 2.5, 6.5, 6, 4.5),
			Block.makeCuboidShape(5.5, 3, 9.5, 6.5, 6, 11.5)
			);
	private static final VoxelShape SHAPE_SOUTH = VoxelShapes.or(
			Block.makeCuboidShape(6.5, 1, 3.5, 11.5, 6, 8.5),
			Block.makeCuboidShape(6.5, 7, 3.5, 11.5, 8, 8.5),
			Block.makeCuboidShape(7.5, 6, 4.5, 10.5, 7, 7.5),
			Block.makeCuboidShape(7.5, 0, 4.5, 10.5, 1, 7.5),
			Block.makeCuboidShape(11.5, 3, 5.5, 13.5, 6, 6.5),
			Block.makeCuboidShape(4.5, 3, 5.5, 6.5, 6, 6.5)
			);
	private static final VoxelShape SHAPE_WEST = VoxelShapes.or(
			Block.makeCuboidShape(7.5, 1, 6.5, 12.5, 6, 11.5),
			Block.makeCuboidShape(7.5, 7, 6.5, 12.5, 8, 11.5),
			Block.makeCuboidShape(8.5, 6, 7.5, 11.5, 7, 10.5),
			Block.makeCuboidShape(8.5, 0, 7.5, 11.5, 1, 10.5),
			Block.makeCuboidShape(9.5, 3, 11.5, 10.5, 6, 13.5),
			Block.makeCuboidShape(9.5, 3, 4.5, 10.5, 6, 6.5)
			);

	public BlockTreasurePotQuartzShort(Properties properties) 
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
