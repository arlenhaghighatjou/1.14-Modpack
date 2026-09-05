package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;

public class GrassFeatureConfig implements IFeatureConfig {
    public final BlockState state;

    public GrassFeatureConfig(BlockState state) {
        this.state = state;
    }

    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("state"), BlockState.serialize(ops, this.state).getValue())));
    }

    public static <T> GrassFeatureConfig deserialize(Dynamic<T> p_214707_0_) {
        BlockState blockstate = BlockState.deserialize(p_214707_0_.get("state").orElseEmptyMap());
        return new GrassFeatureConfig(blockstate);
    }
}
