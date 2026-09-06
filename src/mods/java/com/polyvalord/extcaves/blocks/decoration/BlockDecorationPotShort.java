package com.polyvalord.extcaves.blocks.decoration;

import com.polyvalord.extcaves.blocks.basic.BlockGroundWL;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class BlockDecorationPotShort extends BlockGroundWL
{
	
	private static final VoxelShape SHAPE = VoxelShapes.or(
			Block.makeCuboidShape(5.5, 1, 5.5, 10.5, 6, 10.5),
			Block.makeCuboidShape(5.5, 7, 5.5, 10.5, 8, 10.5),
			Block.makeCuboidShape(6.5, 6, 6.5, 9.5, 7, 9.5),
			Block.makeCuboidShape(6.5, 0, 6.5, 9.5, 1, 9.5)
			);

	public BlockDecorationPotShort(Properties properties) 
	{
		super(properties);
	}
	
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return SHAPE;
	}

}
