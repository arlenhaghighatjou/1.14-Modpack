package com.polyvalord.extcaves.blocks.rocks;

import com.polyvalord.extcaves.blocks.basic.BlockGroundWL;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;

public class BlockPebble extends BlockGroundWL {

    private static final VoxelShape SHAPE = VoxelShapes.or(
            Block.makeCuboidShape(6, 0, 6, 10, 2, 10)
    );

    public BlockPebble(Properties properties) {
        super(properties);
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Vector3d vector3d = state.getOffset(worldIn, pos);
        return SHAPE.withOffset(vector3d.x, vector3d.y, vector3d.z);
    }

    public AbstractBlock.OffsetType getOffsetType() {
        return AbstractBlock.OffsetType.XZ;
    }

}
