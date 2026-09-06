package com.polyvalord.extcaves.world.features;

import com.mojang.serialization.Codec;
import com.polyvalord.extcaves.blocks.RegBlocks;
import com.polyvalord.extcaves.blocks.basic.BlockGroundFacingWL;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.IntegrityProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;

public class FeatureCabin extends Feature<NoFeatureConfig> {
    private int CABIN_TYPE = 0;

    public FeatureCabin(int type, Codec<NoFeatureConfig> p_i231955_1_) {
        super(p_i231955_1_);
        this.CABIN_TYPE = type;
    }

    @Override
    public boolean func_241855_a(ISeedReader seedreader, ChunkGenerator p_241855_2_, Random rand, BlockPos pos, NoFeatureConfig p_241855_5_) {
        Rotation rotation = Rotation.randomRotation(rand);
        TemplateManager templatemanager = ((ServerWorld) seedreader.getWorld()).getServer().func_240792_aT_();
        Template template = templatemanager.getTemplateDefaulted(this.getFeatureLocation(CABIN_TYPE));
        ChunkPos chunkpos = new ChunkPos(pos);
        MutableBoundingBox mutableboundingbox = new MutableBoundingBox(chunkpos.getXStart(), 0, chunkpos.getZStart(),
                chunkpos.getXEnd(), 256, chunkpos.getZEnd());
        PlacementSettings placementsettings = (new PlacementSettings()).setRotation(rotation)
                .setBoundingBox(mutableboundingbox).setRandom(rand)
                .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
        BlockPos blockpos = template.transformedSize(rotation);

        int d = 3;
        int j = rand.nextInt(16 - blockpos.getX());
        int k = rand.nextInt(16 - blockpos.getZ());
        int k1 = 16 + rand.nextInt(48);
        BlockPos blockpos1 = template.getZeroPositionWithTransform(pos.add(j, k1, k), Mirror.NONE, rotation);

        // get the center
        int dx = -1;
        int dz = -1;
        switch (rotation) {
            case CLOCKWISE_180:
                break;
            case CLOCKWISE_90:
                dz *= -1;
                break;
            case COUNTERCLOCKWISE_90:
                dx *= -1;
                break;
            case NONE:
                dx *= -1;
                dz *= -1;
                break;
        }

        BlockPos blockpos2 = blockpos1.add((dx * d), 0, (dz * d));
        BlockPos blockposA = blockpos2.add(+d, 0, +d);
        BlockPos blockposB = blockpos2.add(-d, 0, -d);
        BlockPos blockposC = blockpos2.add(+d, 0, -d);
        BlockPos blockposD = blockpos2.add(-d, 0, +d);

        // place feature
        if (seedreader.getBlockState(blockpos2.up()).getBlock() == Blocks.CAVE_AIR
                && (seedreader.getBlockState(blockposA.down()).getBlock() != Blocks.CAVE_AIR
                && seedreader.getBlockState(blockposB.down()).getBlock() != Blocks.CAVE_AIR)
                && (seedreader.getBlockState(blockposC.down()).getBlock() != Blocks.CAVE_AIR
                || seedreader.getBlockState(blockposD.down()).getBlock() != Blocks.CAVE_AIR)) {
            IntegrityProcessor integrityprocessor = new IntegrityProcessor(1.0F);
            placementsettings.clearProcessors().addProcessor(integrityprocessor);

            template.func_237146_a_(seedreader, blockpos1, blockpos1, placementsettings, rand, 4);

            // platform under the structure
            int px, pz;
            BlockState platformblock = this.getPlatformBlock(this.CABIN_TYPE).getDefaultState();
            for (px = 1; px < 6; px++) {
                for (pz = 1; pz < 6; pz++) {
                    seedreader.setBlockState(blockpos2.add(px - d, -1, pz - d), platformblock, 2);
                }
            }

            // treasures
            BlockState treasureblock = RegBlocks.TREASURE_POT.get().getDefaultState();
            int t, t2, t3;
            for (px = 2; px < 5; px++) {
                for (pz = 2; pz < 5; pz++) {
                    t = rand.nextInt(3);
                    if (t == 0) {
                        t2 = rand.nextInt(2);
                        switch (t2) {
                            case 0:
                                treasureblock = RegBlocks.TREASURE_POT.get().getDefaultState()
                                        .with(BlockGroundFacingWL.HORIZONTAL_FACING, getRandomDir(rand));
                                break;
                            case 1:
                                t3 = rand.nextInt(2);
                                switch (t3) {
                                    case 0:
                                        treasureblock = RegBlocks.TREASURE_POT_SHORT.get().getDefaultState()
                                                .with(BlockGroundFacingWL.HORIZONTAL_FACING, getRandomDir(rand));
                                        break;
                                    case 1:
                                        treasureblock = RegBlocks.TREASURE_POT_LONG.get().getDefaultState()
                                                .with(BlockGroundFacingWL.HORIZONTAL_FACING, getRandomDir(rand));
                                        break;
                                }
                                break;
                        }
                        seedreader.setBlockState(blockpos2.add(px - d, 1, pz - d), treasureblock, 2);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public Direction getRandomDir(Random rand) {
        int t = rand.nextInt(4);
        Direction dir = Direction.NORTH;
        switch (t) {
            case 0:
                dir = Direction.NORTH;
                break;
            case 1:
                dir = Direction.SOUTH;
                break;
            case 2:
                dir = Direction.EAST;
                break;
            case 3:
                dir = Direction.WEST;
                break;
        }
        return dir;
    }

    public ResourceLocation getFeatureLocation(int type) {
        ResourceLocation t = null;
        switch (type) {
            case 0:
                t = new ResourceLocation("extcaves:cabins/cabin_oak");
                break;

            case 1:
                t = new ResourceLocation("extcaves:cabins/cabin_cobblestone");
                break;

            case 2:
                t = new ResourceLocation("extcaves:cabins/cabin_bricks");
                break;

            case 3:
                t = new ResourceLocation("extcaves:cabins/cabin_spruce");
                break;

            case 4:
                t = new ResourceLocation("extcaves:cabins/cabin_jungle");
                break;

            case 5:
                t = new ResourceLocation("extcaves:cabins/cabin_acacia");
                break;

        }

        return t;
    }

    public Block getPlatformBlock(int type) {
        Block b = Blocks.COBBLESTONE;
        switch (type) {
            case 4:
                b = RegBlocks.DIRTSTONE_COBBLE.get();
                break;
        }
        return b;
    }
}