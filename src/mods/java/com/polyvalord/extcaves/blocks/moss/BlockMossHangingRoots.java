package com.polyvalord.extcaves.blocks.moss;

import com.polyvalord.extcaves.blocks.basic.BlockCeiling;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class BlockMossHangingRoots extends BlockCeiling
{
	
	protected static final VoxelShape SHAPE = Block.makeCuboidShape(1.0D, 6.0D, 1.0D, 15.0D, 16.0D, 15.0D);

	public BlockMossHangingRoots(Properties properties) 
	{
		super(properties);
	}
	
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) 
	{
		return SHAPE;
	}

}
