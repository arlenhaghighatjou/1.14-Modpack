package net.minecraft.block.trees;

import net.lax1dude.eaglercraft.Random;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DarkOakTreeFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class DarkOakTree extends BigTree {

   protected AbstractTreeFeature<NoFeatureConfig> getTreeFeature(Random random) {
      return null;
   }

   protected AbstractTreeFeature<NoFeatureConfig> getBigTreeFeature(Random random) {
      return new DarkOakTreeFeature(NoFeatureConfig::deserialize, true);
   }
}
