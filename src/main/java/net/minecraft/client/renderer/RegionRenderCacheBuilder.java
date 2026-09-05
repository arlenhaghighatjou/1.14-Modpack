package net.minecraft.client.renderer;

import net.minecraft.util.BlockRenderLayer;
import me.jellysquid.mods.sodium.client.render.chunk.backends.multidraw.MultidrawChunkRenderBackend;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RegionRenderCacheBuilder {
    private final BufferBuilder[] builders = new BufferBuilder[BlockRenderLayer.values().length];

    public RegionRenderCacheBuilder() {
        boolean directHfp = MultidrawChunkRenderBackend.isSupported();
        this.builders[BlockRenderLayer.SOLID.ordinal()] = new BufferBuilder(524288, directHfp);
        this.builders[BlockRenderLayer.CUTOUT.ordinal()] = new BufferBuilder(131072, directHfp);
        this.builders[BlockRenderLayer.CUTOUT_MIPPED.ordinal()] = new BufferBuilder(131072, directHfp);
        this.builders[BlockRenderLayer.TRANSLUCENT.ordinal()] = new BufferBuilder(262144, directHfp);
    }

    public BufferBuilder getBuilder(BlockRenderLayer layer) {
        return this.builders[layer.ordinal()];
    }

    public BufferBuilder getBuilder(int id) {
        return this.builders[id];
    }
}
