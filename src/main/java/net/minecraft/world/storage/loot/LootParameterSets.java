package net.minecraft.world.storage.loot;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public class LootParameterSets {
    private static final BiMap<ResourceLocation, LootParameterSet> REGISTRY = HashBiMap.create();
    public static final LootParameterSet EMPTY = register("empty", (p_216249_0_) -> {
    });
    public static final LootParameterSet CHEST = register("chest", (p_216259_0_) -> {
        p_216259_0_.required(LootParameters.POSITION).optional(LootParameters.THIS_ENTITY);
    });
    public static final LootParameterSet FISHING = register("fishing", (p_216250_0_) -> {
        p_216250_0_.required(LootParameters.POSITION).required(LootParameters.TOOL);
    });
    public static final LootParameterSet ENTITY = register("entity", (p_216254_0_) -> {
        p_216254_0_.required(LootParameters.THIS_ENTITY).required(LootParameters.POSITION).required(LootParameters.DAMAGE_SOURCE).optional(LootParameters.KILLER_ENTITY).optional(LootParameters.DIRECT_KILLER_ENTITY).optional(LootParameters.LAST_DAMAGE_PLAYER);
    });
    public static final LootParameterSet GIFT = register("gift", (p_216258_0_) -> {
        p_216258_0_.required(LootParameters.POSITION).required(LootParameters.THIS_ENTITY);
    });
    public static final LootParameterSet ADVANCEMENT = register("advancement_reward", (p_216251_0_) -> {
        p_216251_0_.required(LootParameters.THIS_ENTITY).required(LootParameters.POSITION);
    });
    public static final LootParameterSet GENERIC = register("generic", (p_216255_0_) -> {
        p_216255_0_.required(LootParameters.THIS_ENTITY).required(LootParameters.LAST_DAMAGE_PLAYER).required(LootParameters.DAMAGE_SOURCE).required(LootParameters.KILLER_ENTITY).required(LootParameters.DIRECT_KILLER_ENTITY).required(LootParameters.POSITION).required(LootParameters.BLOCK_STATE).required(LootParameters.BLOCK_ENTITY).required(LootParameters.TOOL).required(LootParameters.EXPLOSION_RADIUS);
    });
    public static final LootParameterSet BLOCK = register("block", (p_216252_0_) -> {
        p_216252_0_.required(LootParameters.BLOCK_STATE).required(LootParameters.POSITION).required(LootParameters.TOOL).optional(LootParameters.THIS_ENTITY).optional(LootParameters.BLOCK_ENTITY).optional(LootParameters.EXPLOSION_RADIUS);
    });

    private static LootParameterSet register(String p_216253_0_, Consumer<LootParameterSet.Builder> p_216253_1_) {
        LootParameterSet.Builder lootparameterset$builder = new LootParameterSet.Builder();
        p_216253_1_.accept(lootparameterset$builder);
        LootParameterSet lootparameterset = lootparameterset$builder.build();
        ResourceLocation resourcelocation = new ResourceLocation(p_216253_0_);
        LootParameterSet lootparameterset1 = REGISTRY.put(resourcelocation, lootparameterset);
        if (lootparameterset1 != null) {
            throw new IllegalStateException("Loot table parameter set " + resourcelocation + " is already registered");
        } else {
            return lootparameterset;
        }
    }

    public static LootParameterSet getValue(ResourceLocation p_216256_0_) {
        return REGISTRY.get(p_216256_0_);
    }

    public static ResourceLocation getKey(LootParameterSet p_216257_0_) {
        return REGISTRY.inverse().get(p_216257_0_);
    }
}
