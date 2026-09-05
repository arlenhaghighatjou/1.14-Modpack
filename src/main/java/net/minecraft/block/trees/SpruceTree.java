package net.minecraft.block.trees;

import net.lax1dude.eaglercraft.Random;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.MegaPineTree;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.TallTaigaTreeFeature;

public class SpruceTree extends BigTree {

    protected AbstractTreeFeature<NoFeatureConfig> getTreeFeature(Random random) {
        return new TallTaigaTreeFeature(NoFeatureConfig::deserialize, true);
    }

    protected AbstractTreeFeature<NoFeatureConfig> getBigTreeFeature(Random random) {
        return new MegaPineTree(NoFeatureConfig::deserialize, false, random.nextBoolean());
    }
}
