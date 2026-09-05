package net.minecraft.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

public class SpecialRecipeSerializer<T extends IRecipe<?>> extends IRecipeSerializer<T> {
    private final Function<ResourceLocation, T> field_222176_t;

    public SpecialRecipeSerializer(Function<ResourceLocation, T> p_i50024_1_) {
        this.field_222176_t = p_i50024_1_;
    }

    public T read(ResourceLocation recipeId, JsonObject json) {
        return (T) (this.field_222176_t.apply(recipeId));
    }

    public T read(ResourceLocation recipeId, PacketBuffer buffer) {
        return (T) (this.field_222176_t.apply(recipeId));
    }

    public void write(PacketBuffer buffer, T recipe) {
    }
}
