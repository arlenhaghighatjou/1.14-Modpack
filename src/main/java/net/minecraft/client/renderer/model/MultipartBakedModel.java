package net.minecraft.client.renderer.model;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import net.lax1dude.eaglercraft.Random;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class MultipartBakedModel implements IBakedModel {
    private final List<Pair<Predicate<BlockState>, IBakedModel>> selectors;
    protected final boolean ambientOcclusion;
    protected final boolean gui3D;
    protected final TextureAtlasSprite particleTexture;
    protected final ItemCameraTransforms cameraTransforms;
    protected final ItemOverrideList overrides;
    private final Map<BlockState, List<IBakedModel>> modelCache = new Object2ObjectOpenCustomHashMap<>(Util.identityHashStrategy());

    public MultipartBakedModel(List<Pair<Predicate<BlockState>, IBakedModel>> p_i48273_1_) {
        this.selectors = p_i48273_1_;
        IBakedModel ibakedmodel = p_i48273_1_.iterator().next().getRight();
        this.ambientOcclusion = ibakedmodel.isAmbientOcclusion();
        this.gui3D = ibakedmodel.isGui3d();
        this.particleTexture = ibakedmodel.getParticleTexture();
        this.cameraTransforms = ibakedmodel.getItemCameraTransforms();
        this.overrides = ibakedmodel.getOverrides();
    }

    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
        if (state == null) {
            return Collections.emptyList();
        }

        List<IBakedModel> models;

        synchronized (this.modelCache) {
            models = this.modelCache.get(state);

            if (models == null) {
                models = new ArrayList<>(this.selectors.size());

                for (Pair<Predicate<BlockState>, IBakedModel> pair : this.selectors) {
                    if ((pair.getLeft()).test(state)) {
                        models.add(pair.getRight());
                    }
                }

                this.modelCache.put(state, models);
            }
        }

        List<BakedQuad> list = new ArrayList<>();

        long seed = rand.nextLong();

        for (IBakedModel model : models) {
            rand.setSeed(seed);

            list.addAll(model.getQuads(state, side, rand));
        }

        return list;
    }

    public boolean isAmbientOcclusion() {
        return this.ambientOcclusion;
    }

    public boolean isGui3d() {
        return this.gui3D;
    }

    public boolean isBuiltInRenderer() {
        return false;
    }

    public TextureAtlasSprite getParticleTexture() {
        return this.particleTexture;
    }

    public ItemCameraTransforms getItemCameraTransforms() {
        return this.cameraTransforms;
    }

    public ItemOverrideList getOverrides() {
        return this.overrides;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Builder {
        private final List<Pair<Predicate<BlockState>, IBakedModel>> selectors = new ArrayList<>();

        public void putModel(Predicate<BlockState> predicate, IBakedModel model) {
            this.selectors.add(Pair.of(predicate, model));
        }

        public IBakedModel build() {
            return new MultipartBakedModel(this.selectors);
        }
    }
}
