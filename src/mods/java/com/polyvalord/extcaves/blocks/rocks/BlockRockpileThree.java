package com.polyvalord.extcaves.blocks.rocks;

import com.polyvalord.extcaves.blocks.basic.BlockGroundWL;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class BlockRockpileThree extends BlockGroundWL {

    private static final VoxelShape SHAPE = VoxelShapes.or(
            Block.makeCuboidShape(2.5, 0, 2.5, 13.5, 4, 13.5)
    );

    public BlockRockpileThree(Properties properties) {
        super(properties);
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

}
