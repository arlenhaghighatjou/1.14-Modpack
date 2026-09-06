package com.polyvalord.extcaves.blocks.mushrooms;

import java.util.Random;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;

public class BlockMushroom extends BushBlock {

	private final int SPREAD_SPEED;
	private final int SPREAD_MAX;

	public BlockMushroom(int spreadspeed, int spreadmax, AbstractBlock.Properties properties) {
		super(properties);
		this.SPREAD_SPEED = spreadspeed;
		this.SPREAD_MAX = spreadmax;
	}

	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		// spread speed
		if (random.nextInt(SPREAD_SPEED) == 0) {

			int i = SPREAD_MAX; // max number of neighbors
			int j = 4; // max spread distance

			for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-j, -1, -j), pos.add(j, 1, j))) {
				if (worldIn.getBlockState(blockpos).isIn(this)) {
					--i;
					if (i <= 0) {
						return;
					}
				}
			}

			BlockPos blockpos1 = pos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2),
					random.nextInt(3) - 1);

			for (int k = 0; k < 4; ++k) {
				if (worldIn.isAirBlock(blockpos1) && state.isValidPosition(worldIn, blockpos1)) {
					pos = blockpos1;
				}

				blockpos1 = pos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2),
						random.nextInt(3) - 1);
			}

			if (worldIn.isAirBlock(blockpos1) && state.isValidPosition(worldIn, blockpos1)) {
				worldIn.setBlockState(blockpos1, state, 2);
			}
		}

	}

	protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return state.isOpaqueCube(worldIn, pos);
	}

	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		BlockPos blockpos = pos.down();
		BlockState blockstate = worldIn.getBlockState(blockpos);
		if (!blockstate.isIn(Blocks.MYCELIUM) && !blockstate.isIn(Blocks.PODZOL)) {
			return worldIn.getLightSubtracted(pos, 0) < 13
					&& blockstate.canSustainPlant(worldIn, blockpos, net.minecraft.util.Direction.UP, this);
		} else {
			return true;
		}
	}

}