package net.minecraft.block.trees;

import net.lax1dude.eaglercraft.Random;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.MegaJungleFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;

public class JungleTree extends BigTree {

    protected AbstractTreeFeature<NoFeatureConfig> getTreeFeature(Random random) {
        return new TreeFeature(NoFeatureConfig::deserialize, true, 4 + random.nextInt(7), Blocks.JUNGLE_LOG.getDefaultState(), Blocks.JUNGLE_LEAVES.getDefaultState(), false);
    }

    protected AbstractTreeFeature<NoFeatureConfig> getBigTreeFeature(Random random) {
        return new MegaJungleFeature(NoFeatureConfig::deserialize, true, 10, 20, Blocks.JUNGLE_LOG.getDefaultState(), Blocks.JUNGLE_LEAVES.getDefaultState());
    }
}
