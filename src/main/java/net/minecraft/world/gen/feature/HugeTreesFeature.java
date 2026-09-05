package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import net.lax1dude.eaglercraft.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.IWorldGenerationReader;

import java.util.Set;
import java.util.function.Function;

public abstract class HugeTreesFeature<T extends IFeatureConfig> extends AbstractTreeFeature<T> {
    protected final int baseHeight;
    protected final BlockState trunk;
    protected final BlockState leaf;
    protected final int extraRandomHeight;

    public HugeTreesFeature(Function<Dynamic<?>, ? extends T> configFactoryIn, boolean doBlockNotifyOnPlace, int baseHeightIn, int extraRandomHeightIn, BlockState trunkIn, BlockState leafIn) {
        super(configFactoryIn, doBlockNotifyOnPlace);
        this.baseHeight = baseHeightIn;
        this.extraRandomHeight = extraRandomHeightIn;
        this.trunk = trunkIn;
        this.leaf = leafIn;
    }

    protected int getHeight(Random rand) {
        int i = rand.nextInt(3) + this.baseHeight;
        if (this.extraRandomHeight > 1) {
            i += rand.nextInt(this.extraRandomHeight);
        }

        return i;
    }

    private boolean isSpaceAt(IWorldGenerationBaseReader worldIn, BlockPos leavesPos, int height) {
        boolean flag = true;
        if (leavesPos.getY() >= 1 && leavesPos.getY() + height + 1 <= 256) {
            for (int i = 0; i <= 1 + height; ++i) {
                int j = 2;
                if (i == 0) {
                    j = 1;
                } else if (i >= 1 + height - 2) {
                    j = 2;
                }

                for (int k = -j; k <= j && flag; ++k) {
                    for (int l = -j; l <= j && flag; ++l) {
                        if (leavesPos.getY() + i < 0 || leavesPos.getY() + i >= 256 || !func_214587_a(worldIn, leavesPos.add(k, i, l))) {
                            flag = false;
                        }
                    }
                }
            }

            return flag;
        } else {
            return false;
        }
    }

    private boolean func_202405_b(IWorldGenerationReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.down();
        if (isDirtOrGrassBlock(worldIn, blockpos) && pos.getY() >= 2) {
            this.func_214584_a(worldIn, blockpos);
            this.func_214584_a(worldIn, blockpos.east());
            this.func_214584_a(worldIn, blockpos.south());
            this.func_214584_a(worldIn, blockpos.south().east());
            return true;
        } else {
            return false;
        }
    }

    protected boolean func_203427_a(IWorldGenerationReader worldIn, BlockPos p_203427_2_, int p_203427_3_) {
        return this.isSpaceAt(worldIn, p_203427_2_, p_203427_3_) && this.func_202405_b(worldIn, p_203427_2_);
    }

    protected void func_222839_a(IWorldGenerationReader worldIn, BlockPos p_222839_2_, int p_222839_3_, MutableBoundingBox p_222839_4_, Set<BlockPos> p_222839_5_) {
        int i = p_222839_3_ * p_222839_3_;

        for (int j = -p_222839_3_; j <= p_222839_3_ + 1; ++j) {
            for (int k = -p_222839_3_; k <= p_222839_3_ + 1; ++k) {
                int l = Math.min(Math.abs(j), Math.abs(j - 1));
                int i1 = Math.min(Math.abs(k), Math.abs(k - 1));
                if (l + i1 < 7 && l * l + i1 * i1 <= i) {
                    BlockPos blockpos = p_222839_2_.add(j, 0, k);
                    if (isAirOrLeaves(worldIn, blockpos)) {
                        this.setLogState(p_222839_5_, worldIn, blockpos, this.leaf, p_222839_4_);
                    }
                }
            }
        }

    }

    protected void func_222838_b(IWorldGenerationReader worldIn, BlockPos p_222838_2_, int p_222838_3_, MutableBoundingBox p_222838_4_, Set<BlockPos> p_222838_5_) {
        int i = p_222838_3_ * p_222838_3_;

        for (int j = -p_222838_3_; j <= p_222838_3_; ++j) {
            for (int k = -p_222838_3_; k <= p_222838_3_; ++k) {
                if (j * j + k * k <= i) {
                    BlockPos blockpos = p_222838_2_.add(j, 0, k);
                    if (isAirOrLeaves(worldIn, blockpos)) {
                        this.setLogState(p_222838_5_, worldIn, blockpos, this.leaf, p_222838_4_);
                    }
                }
            }
        }

    }
}
