package net.minecraft.block;

import net.lax1dude.eaglercraft.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public interface IGrowable {
    boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient);

    boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state);

    void grow(World worldIn, Random rand, BlockPos pos, BlockState state);
}
