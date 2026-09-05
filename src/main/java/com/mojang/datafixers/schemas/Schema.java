package com.mojang.datafixers.schemas;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.TypeReference;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Schema {
    protected final int versionKey;
    protected final Schema parent;
    private final Map<String, Supplier<Map<String, ?>>> typeChoices = new HashMap<>();

    public Schema(int versionKey, Schema parent) {
        this.versionKey = versionKey;
        this.parent = parent;
        this.registerTypes(this, this.typeChoices, this.typeChoices);
    }

    protected void registerTypes(Schema schema, Map<String, Supplier<Map<String, ?>>> entityTypes, Map<String, Supplier<Map<String, ?>>> blockTypes) {
        if (this.parent != null) {
            this.parent.registerTypes(schema, entityTypes, blockTypes);
        }
    }

    public void registerType(boolean isEntity, TypeReference type, Supplier<Map<String, ?>> choiceMap) {
        this.typeChoices.put(type.typeName(), choiceMap);
    }

    public Object getChoiceType(TypeReference type, String id) {
        if (!this.typeChoices.containsKey(type.typeName()) && this.parent != null) {
            return this.parent.getChoiceType(type, id);
        }

        Supplier<Map<String, ?>> supplier = this.typeChoices.get(type.typeName());
        if (supplier != null) {
            Map<String, ?> choices = supplier.get();
            if (choices.containsKey(id)) {
                return new Object();
            }
            ResourceLocation resourcelocation = ResourceLocation.tryCreate(id);
            if (resourcelocation != null && choices.containsKey(resourcelocation.toString())) {
                return new Object();
            }
            throw new IllegalStateException("Unknown choice type '" + id + "' for type '" + type.typeName() + "'");
        }

        throw new IllegalStateException("Unknown type '" + type.typeName() + "'");
    }

    public int getVersionKey() {
        return this.versionKey;
    }
}
