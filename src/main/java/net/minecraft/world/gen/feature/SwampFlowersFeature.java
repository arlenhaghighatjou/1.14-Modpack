package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import net.lax1dude.eaglercraft.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.function.Function;

public class SwampFlowersFeature extends FlowersFeature {
    public SwampFlowersFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51426_1_) {
        super(p_i51426_1_);
    }

    public BlockState getRandomFlower(Random random, BlockPos pos) {
        return Blocks.BLUE_ORCHID.getDefaultState();
    }
}
