package com.polyvalord.extcaves.blocks.vines;

import java.util.Random;

import com.polyvalord.extcaves.blocks.RegBlocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractTopPlantBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlockHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;

public class BlockCaveVineEnd extends AbstractTopPlantBlock
{
	protected static final VoxelShape SHAPE = Block.makeCuboidShape(4.0D, 2.0D, 4.0D, 12.0D, 16.0D, 12.0D);

	public BlockCaveVineEnd(AbstractBlock.Properties properties) {
		super(properties, Direction.DOWN, SHAPE, false, 0.1D);
	}

	@Override
	protected int getGrowthAmount(Random rand) {
		return PlantBlockHelper.getGrowthAmount(rand);
	}

	@Override
	protected boolean canGrowIn(BlockState state) {
		return PlantBlockHelper.isAir(state);
	}

	@Override
	protected Block getBodyPlantBlock() {
		return RegBlocks.CAVE_VINE.get();
	}
}