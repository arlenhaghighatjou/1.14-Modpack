package net.minecraft.client.renderer.model;

import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public interface IUnbakedModel {
    Collection<ResourceLocation> getDependencies();

    Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors);

    IBakedModel bake(ModelBakery p_217641_1_, Function<ResourceLocation, TextureAtlasSprite> p_217641_2_, ISprite p_217641_3_);
}
