package com.polyvalord.extcaves.world.features;

import com.mojang.serialization.Codec;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.StructureManager;

public class FeatureRandomPatch extends Feature<BlockClusterFeatureConfig> {
    public final boolean isUp;

    public FeatureRandomPatch(boolean isup, Codec<BlockClusterFeatureConfig> p_i231979_1_) {
        super(p_i231979_1_);
        this.isUp = isup;
    }

    @Override
    public boolean func_241855_a(ISeedReader seedReader, ChunkGenerator chunkGenerator, Random rand, BlockPos pos, BlockClusterFeatureConfig featureConfig) {
        BlockState blockstate = featureConfig.stateProvider.getBlockState(rand, pos);
        BlockPos blockpos;
        if (featureConfig.field_227298_k_) {
            blockpos = seedReader.getHeight(Heightmap.Type.WORLD_SURFACE_WG, pos);
        } else {
            blockpos = pos;
        }

        int i = 0;
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for (int j = 0; j < featureConfig.tryCount; ++j) {
            blockpos$mutable.setAndOffset(blockpos,
                    rand.nextInt(featureConfig.xSpread + 1) - rand.nextInt(featureConfig.xSpread + 1),
                    rand.nextInt(featureConfig.ySpread + 1) - rand.nextInt(featureConfig.ySpread + 1),
                    rand.nextInt(featureConfig.zSpread + 1) - rand.nextInt(featureConfig.zSpread + 1));
            BlockPos blockpos1;
            if (isUp == true) {
                blockpos1 = blockpos$mutable.up();
            } else {
                blockpos1 = blockpos$mutable.down();
            }
            BlockState blockstate1 = seedReader.getBlockState(blockpos1);
            if ((seedReader.isAirBlock(blockpos$mutable) || featureConfig.isReplaceable
                    && seedReader.getBlockState(blockpos$mutable).getMaterial().isReplaceable())
                    && blockstate.isValidPosition(seedReader, blockpos$mutable)
                    && (featureConfig.whitelist.isEmpty() || featureConfig.whitelist.contains(blockstate1.getBlock()))
                    && !featureConfig.blacklist.contains(blockstate1)
                    && (!featureConfig.requiresWater
                    || seedReader.getFluidState(blockpos1.west()).isTagged(FluidTags.WATER)
                    || seedReader.getFluidState(blockpos1.east()).isTagged(FluidTags.WATER)
                    || seedReader.getFluidState(blockpos1.north()).isTagged(FluidTags.WATER)
                    || seedReader.getFluidState(blockpos1.south()).isTagged(FluidTags.WATER))) {
                featureConfig.blockPlacer.place(seedReader, blockpos$mutable, blockstate, rand);
                ++i;
            }
        }

        return i > 0;
    }
}