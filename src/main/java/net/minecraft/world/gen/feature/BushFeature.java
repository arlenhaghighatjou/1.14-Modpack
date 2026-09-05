package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import net.lax1dude.eaglercraft.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

import java.util.function.Function;

public class BushFeature extends Feature<BushConfig> {
    public BushFeature(Function<Dynamic<?>, ? extends BushConfig> p_i49908_1_) {
        super(p_i49908_1_);
    }

    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, BushConfig config) {
        int i = 0;
        BlockState blockstate = config.state;

        for (int j = 0; j < 64; ++j) {
            BlockPos blockpos = pos.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
            if (worldIn.isAirBlock(blockpos) && (!worldIn.getDimension().isNether() || blockpos.getY() < 255) && blockstate.isValidPosition(worldIn, blockpos)) {
                worldIn.setBlockState(blockpos, blockstate, 2);
                ++i;
            }
        }

        return i > 0;
    }
}
