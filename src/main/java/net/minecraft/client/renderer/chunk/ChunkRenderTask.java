package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ChunkRenderTask {
    private final ChunkRender renderChunk;
    private final List<Runnable> listFinishRunnables = Lists.newArrayList();
    private final ChunkRenderTask.Type type;
    private final double distanceSq;

    private ChunkRenderCache chunkRenderCache;
    private RegionRenderCacheBuilder regionRenderCacheBuilder;
    private CompiledChunk compiledChunk;
    private ChunkRenderTask.Status status = ChunkRenderTask.Status.PENDING;
    private boolean finished;

    public ChunkRenderTask(ChunkRender chunkRenderIn, ChunkRenderTask.Type typeIn, double distanceSqIn, ChunkRenderCache chunkRenderCacheIn) {
        this.renderChunk = chunkRenderIn;
        this.type = typeIn;
        this.distanceSq = distanceSqIn;
        this.chunkRenderCache = chunkRenderCacheIn;
    }

    public ChunkRenderTask.Status getStatus() {
        return this.status;
    }

    public ChunkRender getRenderChunk() {
        return this.renderChunk;
    }

    public ChunkRenderCache takeChunkRenderCache() {
        ChunkRenderCache chunkrendercache = this.chunkRenderCache;
        this.chunkRenderCache = null;
        return chunkrendercache;
    }

    public CompiledChunk getCompiledChunk() {
        return this.compiledChunk;
    }

    public void setCompiledChunk(CompiledChunk compiledChunkIn) {
        this.compiledChunk = compiledChunkIn;
    }

    public RegionRenderCacheBuilder getRegionRenderCacheBuilder() {
        return this.regionRenderCacheBuilder;
    }

    public void setRegionRenderCacheBuilder(RegionRenderCacheBuilder regionRenderCacheBuilderIn) {
        this.regionRenderCacheBuilder = regionRenderCacheBuilderIn;
    }

    public void setStatus(ChunkRenderTask.Status statusIn) {

        try {
            this.status = statusIn;
        } finally {
        }

    }

    public void finish() {

        try {
            this.chunkRenderCache = null;
            if (this.type == ChunkRenderTask.Type.REBUILD_CHUNK && this.status != ChunkRenderTask.Status.DONE) {
                this.renderChunk.setNeedsUpdate(false);
            }

            this.finished = true;
            this.status = ChunkRenderTask.Status.DONE;

            for (Runnable runnable : this.listFinishRunnables) {
                runnable.run();
            }
        } finally {
        }

    }

    public void addFinishRunnable(Runnable runnable) {

        try {
            this.listFinishRunnables.add(runnable);
            if (this.finished) {
                runnable.run();
            }
        } finally {
        }

    }

    public ChunkRenderTask.Type getType() {
        return this.type;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public double getDistanceSq() {
        return this.distanceSq;
    }

    @OnlyIn(Dist.CLIENT)
    public static enum Status {
        PENDING,
        COMPILING,
        UPLOADING,
        DONE;
    }

    @OnlyIn(Dist.CLIENT)
    public static enum Type {
        REBUILD_CHUNK,
        RESORT_TRANSPARENCY;
    }
}
