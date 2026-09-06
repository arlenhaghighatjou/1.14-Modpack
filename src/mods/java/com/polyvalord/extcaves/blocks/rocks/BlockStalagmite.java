package com.polyvalord.extcaves.blocks.rocks;

import com.polyvalord.extcaves.blocks.basic.BlockGroundWL;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class BlockStalagmite extends BlockGroundWL {

    private static final VoxelShape SHAPE = VoxelShapes.or(
            Block.makeCuboidShape(6, 0, 6, 10, 15, 10)
    );

    public BlockStalagmite(Properties properties) {
        super(properties);
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

}
