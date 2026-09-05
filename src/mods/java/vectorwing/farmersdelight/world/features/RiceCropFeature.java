package vectorwing.farmersdelight.world.features;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.BushConfig;
import net.minecraft.world.gen.feature.Feature;
import vectorwing.farmersdelight.blocks.WildRiceBlock;

import net.lax1dude.eaglercraft.Random;
import java.util.function.Function;

public class RiceCropFeature extends Feature<BushConfig> {
	private static final int TRY_COUNT = 64;
	private static final int SPREAD = 4;

	public RiceCropFeature(Function<Dynamic<?>, ? extends BushConfig> configFactoryIn) {
		super(configFactoryIn);
	}

	@Override
	public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, BushConfig config) {
		BlockPos blockpos = worldIn.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, pos);

		int i = 0;
		BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

		for(int j = 0; j < TRY_COUNT; ++j) {
			blockpos$mutable.setPos(blockpos).move(
					rand.nextInt(SPREAD + 1) - rand.nextInt(SPREAD + 1),
					0,
					rand.nextInt(SPREAD + 1) - rand.nextInt(SPREAD + 1));

			if (worldIn.getBlockState(blockpos$mutable).getBlock() == Blocks.WATER && worldIn.getBlockState(blockpos$mutable.up()).getBlock() == Blocks.AIR) {
				BlockState bottomRiceState = config.state.with(WildRiceBlock.HALF, DoubleBlockHalf.LOWER);
				if (bottomRiceState.isValidPosition(worldIn, blockpos$mutable)) {
					((WildRiceBlock)bottomRiceState.getBlock()).placeAt(worldIn, blockpos$mutable, 2);
					++i;
				}
			}
		}

		return i > 0;
	}
}
