package com.polyvalord.extcaves.blocks.vines;

import com.polyvalord.extcaves.blocks.RegBlocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractBodyPlantBlock;
import net.minecraft.block.AbstractTopPlantBlock;
import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;

public class BlockCaveVine extends AbstractBodyPlantBlock
{
	public static final VoxelShape SHAPE = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

	public BlockCaveVine(AbstractBlock.Properties properties)
	{
		super(properties, Direction.DOWN, SHAPE, false);
	}

	@Override
	protected AbstractTopPlantBlock getTopPlantBlock() {
		return (AbstractTopPlantBlock) RegBlocks.CAVE_VINE_END.get();
	}
}