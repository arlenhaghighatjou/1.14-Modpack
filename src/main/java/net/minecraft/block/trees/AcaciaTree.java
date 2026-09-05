package net.minecraft.block.trees;

import net.lax1dude.eaglercraft.Random;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.SavannaTreeFeature;

public class AcaciaTree extends Tree {

   protected AbstractTreeFeature<NoFeatureConfig> getTreeFeature(Random random) {
      return new SavannaTreeFeature(NoFeatureConfig::deserialize, true);
   }
}
