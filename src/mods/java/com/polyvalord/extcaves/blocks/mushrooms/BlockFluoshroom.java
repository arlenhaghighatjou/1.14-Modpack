package com.polyvalord.extcaves.blocks.mushrooms;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class BlockFluoshroom extends BlockMushroom {

	protected static final VoxelShape SHAPE = Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 11.0D, 13.0D);

	public BlockFluoshroom(int spreadspeed, int spreadmax, Properties properties) {
		super(spreadspeed, spreadmax, properties);
	}

	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

}
