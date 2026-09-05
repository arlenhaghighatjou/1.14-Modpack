package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;

public class BlockBlobConfig implements IFeatureConfig {
    public final BlockState state;
    public final int startRadius;

    public BlockBlobConfig(BlockState state, int startRadius) {
        this.state = state;
        this.startRadius = startRadius;
    }

    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("state"), BlockState.serialize(ops, this.state).getValue(), ops.createString("start_radius"), ops.createInt(this.startRadius))));
    }

    public static <T> BlockBlobConfig deserialize(Dynamic<T> p_214682_0_) {
        BlockState blockstate = BlockState.deserialize(p_214682_0_.get("state").orElseEmptyMap());
        int i = p_214682_0_.get("start_radius").asInt(0);
        return new BlockBlobConfig(blockstate, i);
    }
}
