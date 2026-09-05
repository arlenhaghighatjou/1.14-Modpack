package vectorwing.farmersdelight.blocks;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vectorwing.farmersdelight.registry.ModBlocks;
import vectorwing.farmersdelight.utils.MathUtils;

import net.lax1dude.eaglercraft.Random;

public class RichSoilBlock extends Block
{
	public RichSoilBlock()
	{
		super(Properties.from(Blocks.DIRT));
	}

	public RichSoilBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public void tick(BlockState state, World worldIn, BlockPos pos, Random rand) {
		if (!worldIn.isRemote) {
			BlockState plant = worldIn.getBlockState(pos.up());
			if (plant.getBlock() instanceof TallFlowerBlock) {
				return;
			}
			if (plant.getBlock() == Blocks.BROWN_MUSHROOM) {
				worldIn.setBlockState(pos.up(), ModBlocks.BROWN_MUSHROOM_COLONY.getDefaultState().with(MushroomColonyBlock.COLONY_AGE, 0));
			}
			if (plant.getBlock() == Blocks.RED_MUSHROOM) {
				worldIn.setBlockState(pos.up(), ModBlocks.RED_MUSHROOM_COLONY.getDefaultState().with(MushroomColonyBlock.COLONY_AGE, 0));
			}
			if (plant.getBlock() instanceof IGrowable && MathUtils.RAND.nextInt(10) <= 2) {
				IGrowable growable = (IGrowable) plant.getBlock();
				if (growable.canGrow(worldIn, pos.up(), plant, false)) {
					growable.grow(worldIn, worldIn.rand, pos.up(), plant);
				}
			}
		}
	}
}
