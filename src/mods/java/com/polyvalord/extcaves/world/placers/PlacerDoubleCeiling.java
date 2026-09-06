package com.polyvalord.extcaves.world.placers;

import com.mojang.serialization.Codec;
import com.polyvalord.extcaves.blocks.basic.BlockTallCeilingWL;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.blockplacer.BlockPlacer;
import net.minecraft.world.gen.blockplacer.BlockPlacerType;

public class PlacerDoubleCeiling extends BlockPlacer {
    public static final Codec<PlacerDoubleCeiling> field_236443_b_;
    public static final PlacerDoubleCeiling field_236444_c_ = new PlacerDoubleCeiling();

    static {
        field_236443_b_ = Codec.unit(() ->
        {
            return field_236444_c_;
        });
    }

    @Override
    public void place(IWorld world, BlockPos pos, BlockState state, Random random) {
        ((BlockTallCeilingWL) state.getBlock()).placeAt(world, pos, 2);
    }

    @Override
    protected BlockPlacerType<?> getBlockPlacerType() {
        return BlockPlacerType.DOUBLE_PLANT;
    }
}