package com.polyvalord.extcaves.blocks.rocks;

import com.polyvalord.extcaves.blocks.basic.BlockCeilingWL;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class BlockStalactite extends BlockCeilingWL {

    private static final VoxelShape SHAPE = VoxelShapes.or(
            Block.makeCuboidShape(6, 1, 6, 10, 16, 10)
    );

    public BlockStalactite(Properties properties) {
        super(properties);
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

}
