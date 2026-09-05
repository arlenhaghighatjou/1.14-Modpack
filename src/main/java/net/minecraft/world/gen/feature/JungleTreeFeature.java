package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import net.lax1dude.eaglercraft.Random;
import net.minecraft.block.BlockState;

import java.util.function.Function;

public class JungleTreeFeature extends TreeFeature {
    public JungleTreeFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51488_1_, boolean p_i51488_2_, int p_i51488_3_, BlockState p_i51488_4_, BlockState p_i51488_5_, boolean p_i51488_6_) {
        super(p_i51488_1_, p_i51488_2_, p_i51488_3_, p_i51488_4_, p_i51488_5_, p_i51488_6_);
    }

    protected int getHeight(Random random) {
        return this.minTreeHeight + random.nextInt(7);
    }
}
