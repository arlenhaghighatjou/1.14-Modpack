package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.IWorld;

import java.util.function.Function;

public class IceBlockPileFeature extends BlockPileFeature {
    public IceBlockPileFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49862_1_) {
        super(p_i49862_1_);
    }

    protected BlockState getRandomBlock(IWorld worldIn) {
        return worldIn.getRandom().nextInt(7) == 0 ? Blocks.BLUE_ICE.getDefaultState() : Blocks.PACKED_ICE.getDefaultState();
    }
}
