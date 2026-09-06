package com.polyvalord.extcaves.blocks.rocks;

import com.polyvalord.extcaves.blocks.basic.BlockTallGroundWL;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockStalagmiteTall extends BlockTallGroundWL {

    private static final VoxelShape SHAPE_LOWER = VoxelShapes.or(
            Block.makeCuboidShape(5, 0, 5, 11, 16, 11)
    );
    private static final VoxelShape SHAPE_UPPER = VoxelShapes.or(
            Block.makeCuboidShape(6, 0, 6, 10, 9, 10)
    );

    public BlockStalagmiteTall(Properties properties) {
        super(properties);
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return getVoxelShape(state);
    }

    public static VoxelShape getVoxelShape(BlockState blockstate) {
        if (blockstate.get(HALF) == DoubleBlockHalf.LOWER) {
            return SHAPE_LOWER;
        } else {
            return SHAPE_UPPER;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public long getPositionRandom(BlockState state, BlockPos pos) {
        return MathHelper.getCoordinateRandom(pos.getX(),
                pos.down(state.get(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
    }

}
