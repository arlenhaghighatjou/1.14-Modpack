package net.minecraft.client.renderer.model;

import net.lax1dude.eaglercraft.Random;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public interface IBakedModel {
    List<BakedQuad> getQuads(BlockState state, Direction side, Random rand);

    boolean isAmbientOcclusion();

    boolean isGui3d();

    boolean isBuiltInRenderer();

    TextureAtlasSprite getParticleTexture();

    ItemCameraTransforms getItemCameraTransforms();

    ItemOverrideList getOverrides();
}
