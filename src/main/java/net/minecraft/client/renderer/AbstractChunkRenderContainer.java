package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.chunk.ChunkRender;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractChunkRenderContainer {
    private double viewEntityX;
    private double viewEntityY;
    private double viewEntityZ;
    private int renderListRevision;
    protected final List<ChunkRender> renderChunks = Lists.newArrayListWithCapacity(2048);
    protected boolean initialized;

    public void initialize(double viewEntityXIn, double viewEntityYIn, double viewEntityZIn) {
        this.initialized = true;
        this.renderChunks.clear();
        this.viewEntityX = viewEntityXIn;
        this.viewEntityY = viewEntityYIn;
        this.viewEntityZ = viewEntityZIn;
    }

    public void preRenderChunk(ChunkRender renderChunkIn) {
        BlockPos blockpos = renderChunkIn.getPosition();
        GlStateManager.translatef((float) ((double) blockpos.getX() - this.viewEntityX), (float) ((double) blockpos.getY() - this.viewEntityY), (float) ((double) blockpos.getZ() - this.viewEntityZ));
    }

    public void addRenderChunk(ChunkRender renderChunkIn, BlockRenderLayer layer) {
        this.renderChunks.add(renderChunkIn);
    }

    public int getRenderChunksSize() {
        return this.renderChunks.size();
    }

    public void setRenderListRevision(int renderListRevision) {
        this.renderListRevision = renderListRevision;
    }

    protected int getRenderListRevision() {
        return this.renderListRevision;
    }

    protected double getViewEntityX() {
        return this.viewEntityX;
    }

    protected double getViewEntityY() {
        return this.viewEntityY;
    }

    protected double getViewEntityZ() {
        return this.viewEntityZ;
    }

    public abstract void renderChunkLayer(BlockRenderLayer layer);
}
