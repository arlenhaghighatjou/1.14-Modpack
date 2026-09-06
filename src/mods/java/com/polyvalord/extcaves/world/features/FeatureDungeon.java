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

public class FeatureDungeon extends Feature<NoFeatureConfig> {
    private int DUNGEON_TYPE = 0;
    private int DUNGEON_MOB = 0;
    private boolean DUNGEON_TALL = false;

    public FeatureDungeon(int dungeontype, int dungeonmob, boolean dungeontall, Codec<NoFeatureConfig> p_i231955_1_) {
        super(p_i231955_1_);
        this.DUNGEON_TYPE = dungeontype;
        this.DUNGEON_MOB = dungeonmob;
        this.DUNGEON_TALL = dungeontall;
    }

    @Override
    public boolean func_241855_a(ISeedReader seedreader, ChunkGenerator p_230362_3_,
                                 Random rand, BlockPos pos, NoFeatureConfig p_230362_6_) {
        Rotation rotation = Rotation.randomRotation(rand);
        TemplateManager templatemanager = ((ServerWorld) seedreader.getWorld()).getServer().func_240792_aT_();
        Template template = templatemanager.getTemplateDefaulted(this.getFeatureLocation(DUNGEON_TYPE));
        ChunkPos chunkpos = new ChunkPos(pos);
        MutableBoundingBox mutableboundingbox = new MutableBoundingBox(chunkpos.getXStart(), 0, chunkpos.getZStart(),
                chunkpos.getXEnd(), 256, chunkpos.getZEnd());
        PlacementSettings placementsettings = (new PlacementSettings()).setRotation(rotation)
                .setBoundingBox(mutableboundingbox).setRandom(rand)
                .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
        BlockPos blockpos = template.transformedSize(rotation);

        int d = this.getDungeonOffset(DUNGEON_TYPE);
        int j = rand.nextInt(16 - blockpos.getX());
        int k = rand.nextInt(16 - blockpos.getZ());
        int k1 = this.getDungeonMinHeight(DUNGEON_TYPE) + rand.nextInt(this.getDungeonMaxHeight(DUNGEON_TYPE));
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

            // structure
            template.func_237146_a_(seedreader, blockpos1, blockpos1, placementsettings, rand, 4);

            // platform under the structure
            int px, pz;
            int baseoffset = this.getPlatformBaseOffset(DUNGEON_TYPE);
            int minoffset = this.getPlatformMinOffset(DUNGEON_TYPE);
            int maxoffset = this.getPlatformMaxOffset(DUNGEON_TYPE);
            BlockState platformblock = this.getPlatformBlock(DUNGEON_TYPE).getDefaultState();
            for (px = (minoffset - baseoffset); px < (maxoffset + baseoffset); px++) {
                for (pz = (minoffset - baseoffset); pz < (maxoffset + baseoffset); pz++) {
                    seedreader.setBlockState(blockpos2.add(px - d, -1, pz - d), platformblock, 2);
                }
            }

            // treasures
            BlockState treasureblock = RegBlocks.TREASURE_POT.get().getDefaultState();
            int t, t2;
            for (px = minoffset + 1; px < maxoffset - 1; px++) {
                for (pz = minoffset + 1; pz < maxoffset - 1; pz++) {
                    // normal treasure
                    t = rand.nextInt(9);
                    if (t == 0) {
                        t2 = rand.nextInt(2);
                        switch (t2) {
                            case 0:
                                treasureblock = RegBlocks.TREASURE_POT_SHORT.get().getDefaultState()
                                        .with(BlockGroundFacingWL.HORIZONTAL_FACING, getRandomDir(rand));
                                break;
                            case 1:
                                treasureblock = RegBlocks.TREASURE_POT_LONG.get().getDefaultState()
                                        .with(BlockGroundFacingWL.HORIZONTAL_FACING, getRandomDir(rand));
                                break;
                        }
                        seedreader.setBlockState(blockpos2.add(px - d, 1, pz - d), treasureblock, 2);
                    }

                    // rare treasure
                    t = rand.nextInt(11);
                    if (t == 0) {
                        t2 = rand.nextInt(2);
                        switch (t2) {
                            case 0:
                                treasureblock = RegBlocks.TREASURE_POT_QUARTZ_SHORT.get().getDefaultState()
                                        .with(BlockGroundFacingWL.HORIZONTAL_FACING, getRandomDir(rand));
                                break;
                            case 1:
                                treasureblock = RegBlocks.TREASURE_POT_QUARTZ_LONG.get().getDefaultState()
                                        .with(BlockGroundFacingWL.HORIZONTAL_FACING, getRandomDir(rand));
                                break;
                        }
                        seedreader.setBlockState(blockpos2.add(px - d, 1, pz - d), treasureblock, 2);
                    }
                }
            }

            // spawner
            BlockPos blockpos3 = blockpos2.up();
            if (DUNGEON_TALL == true) {
                blockpos3 = blockpos2.up(6);
            }
            Template templatespawner = templatemanager.getTemplateDefaulted(this.getRandomSpawner(DUNGEON_MOB, rand));
            templatespawner.func_237146_a_(seedreader, blockpos3, blockpos3, placementsettings, rand, 4);

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

    public int getDungeonOffset(int dungeontype) {
        int r = 4;
        switch (dungeontype) {
            case 3:
                r = 5;
                break;

            case 4:
                r = 5;
                break;
        }
        return r;
    }

    public int getDungeonMinHeight(int dungeontype) {
        int r = 16;
        switch (dungeontype) {
            case 3:
                r = 8;
                break;

            case 4:
                r = 8;
                break;
        }
        return r;
    }

    public int getDungeonMaxHeight(int dungeontype) {
        int r = 40;
        switch (dungeontype) {
            case 3:
                r = 8;
                break;

            case 4:
                r = 8;
                break;
        }
        return r;
    }

    public int getPlatformBaseOffset(int dungeontype) {
        int r = 0;
        switch (dungeontype) {
            case 3:
                r = 1;
                break;

            case 4:
                r = 1;
                break;
        }
        return r;
    }

    public int getPlatformMinOffset(int dungeontype) {
        int r = 1;
        switch (dungeontype) {
            case 3:
                r = 2;
                break;

            case 4:
                r = 2;
                break;
        }
        return r;
    }

    public int getPlatformMaxOffset(int dungeontype) {
        int r = 8;
        switch (dungeontype) {
            case 3:
                r = 9;
                break;

            case 4:
                r = 9;
                break;
        }
        return r;
    }

    public Block getPlatformBlock(int dungeontype) {
        Block r = Blocks.COBBLESTONE;
        switch (dungeontype) {
            case 2:
                r = RegBlocks.BRICKS_SNOW.get();
                break;

            case 3:
                r = RegBlocks.LAVASTONE.get();
                break;

            case 4:
                r = RegBlocks.LAVASTONE.get();
                break;
        }
        return r;
    }

    public ResourceLocation getFeatureLocation(int dungeontype) {
        ResourceLocation r = null;
        switch (dungeontype) {
            case 0:
                r = new ResourceLocation("extcaves:dungeons/dungeon_cobblestone");
                break;

            case 1:
                r = new ResourceLocation("extcaves:dungeons/dungeon_tall_cobblestone");
                break;

            case 2:
                r = new ResourceLocation("extcaves:dungeons/dungeon_ice");
                break;

            case 3:
                r = new ResourceLocation("extcaves:dungeons/dungeon_lavastone");
                break;

            case 4:
                r = new ResourceLocation("extcaves:dungeons/dungeon_tall_lavastone");
                break;
        }

        return r;
    }

    public ResourceLocation getRandomSpawner(int dungeonmob, Random rand) {

        ResourceLocation r = new ResourceLocation("extcaves:spawners/zombie_default");
        int t1 = rand.nextInt(2);
        int t2 = 0;

        switch (dungeonmob) {
            // default
            case 0:
                switch (t1) {
                    // zombies
                    case 0:
                        r = new ResourceLocation("extcaves:spawners/zombie_default");
                        break;

                    // skeletons
                    case 1:
                        r = new ResourceLocation("extcaves:spawners/skeleton_default");
                        break;
                }
                break;

            // ice
            case 1:
                r = new ResourceLocation("extcaves:spawners/stray_default");
                break;

            // special
            case 2:
                t2 = rand.nextInt(4);
                switch (t1) {
                    // zombies
                    case 0:
                        switch (t2) {
                            case 0:
                                r = new ResourceLocation("extcaves:spawners/zombie_butcher");
                                break;

                            case 1:
                                r = new ResourceLocation("extcaves:spawners/zombie_chef");
                                break;

                            case 2:
                                r = new ResourceLocation("extcaves:spawners/zombie_gourmet");
                                break;

                            case 3:
                                r = new ResourceLocation("extcaves:spawners/zombie_miner");
                                break;
                        }

                        break;

                    // skeletons
                    case 1:
                        switch (t2) {
                            case 0:
                                r = new ResourceLocation("extcaves:spawners/skeleton_bandit");
                                break;

                            case 1:
                                r = new ResourceLocation("extcaves:spawners/skeleton_gourmet");
                                break;

                            case 2:
                                r = new ResourceLocation("extcaves:spawners/skeleton_rich");
                                break;

                            case 3:
                                r = new ResourceLocation("extcaves:spawners/skeleton_warrior");
                                break;
                        }

                        break;
                }
                break;

        }

        return r;
    }
}