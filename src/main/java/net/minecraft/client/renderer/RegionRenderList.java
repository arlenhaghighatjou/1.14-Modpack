package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;

import me.jellysquid.mods.sodium.client.render.chunk.backends.multidraw.MultidrawChunkRenderBackend;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RegionRenderList extends AbstractChunkRenderContainer {
    private final MultidrawChunkRenderBackend backend;

    public RegionRenderList(MultidrawChunkRenderBackend backend) {
        this.backend = backend;
    }

    @Override
    public void renderChunkLayer(BlockRenderLayer layer) {
        if (this.initialized) {
            this.backend.render(this.renderChunks, layer, this.getViewEntityX(), this.getViewEntityY(),
                    this.getViewEntityZ(), this.getRenderListRevision());
            GlStateManager.clearCurrentColor();
            this.renderChunks.clear();
        }
    }
}
