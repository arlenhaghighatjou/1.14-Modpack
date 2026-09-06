package com.polyvalord.extcaves.blocks.decoration;

import com.polyvalord.extcaves.blocks.basic.BlockGroundWL;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class BlockDecorationPot extends BlockGroundWL
{
	
	private static final VoxelShape SHAPE = VoxelShapes.or(
			Block.makeCuboidShape(5.5, 6, 5.5, 10.5, 7, 10.5),
			Block.makeCuboidShape(4.5, 1, 4.5, 11.5, 6, 11.5),
			Block.makeCuboidShape(5.5, 0, 5.5, 10.5, 1, 10.5),
			Block.makeCuboidShape(5.5, 8, 5.5, 10.5, 9, 10.5),
			Block.makeCuboidShape(6.5, 7, 6.5, 9.5, 8, 9.5)
			);

	public BlockDecorationPot(Properties properties) 
	{
		super(properties);
	}
	
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return SHAPE;
	}

}
