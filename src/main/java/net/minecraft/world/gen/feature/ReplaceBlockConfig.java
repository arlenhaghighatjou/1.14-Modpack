package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;

public class ReplaceBlockConfig implements IFeatureConfig {
    public final BlockState target;
    public final BlockState state;

    public ReplaceBlockConfig(BlockState target, BlockState state) {
        this.target = target;
        this.state = state;
    }

    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("target"), BlockState.serialize(ops, this.target).getValue(), ops.createString("state"), BlockState.serialize(ops, this.state).getValue())));
    }

    public static <T> ReplaceBlockConfig deserialize(Dynamic<T> p_214657_0_) {
        BlockState blockstate = BlockState.deserialize(p_214657_0_.get("target").orElseEmptyMap());
        BlockState blockstate1 = BlockState.deserialize(p_214657_0_.get("state").orElseEmptyMap());
        return new ReplaceBlockConfig(blockstate, blockstate1);
    }
}
