package me.jellysquid.mods.sodium.client.render.chunk.region;

import me.jellysquid.mods.sodium.client.gl.arena.GlBufferArena;
import me.jellysquid.mods.sodium.client.render.chunk.backends.multidraw.ChunkDrawCallBatcher;
import net.lax1dude.eaglercraft.internal.IBufferGL;
import net.minecraft.util.BlockRenderLayer;

public class ChunkRegion<T> {
    private static final int EXPECTED_CHUNK_SIZE = 4 * 1024;
    private static final int INITIAL_BUFFER_SIZE = 256 * 1024;

    private final GlBufferArena arena;
    private final ChunkDrawCallBatcher[] batches;
    private IBufferGL indexBuffer;

    private final int originX;
    private final int originY;
    private final int originZ;

    public ChunkRegion(int size, int originX, int originY, int originZ) {
        int arenaSize = EXPECTED_CHUNK_SIZE * size;

        this.arena = new GlBufferArena(Math.min(arenaSize, INITIAL_BUFFER_SIZE), arenaSize);

        this.batches = new ChunkDrawCallBatcher[BlockRenderLayer._VALUES.length];
        for (int i = 0; i < this.batches.length; ++i) {
            this.batches[i] = ChunkDrawCallBatcher.create(size);
        }
        this.originX = originX;
        this.originY = originY;
        this.originZ = originZ;
    }

    public GlBufferArena getBufferArena() {
        return this.arena;
    }

    public boolean isArenaEmpty() {
        return this.arena.isEmpty();
    }

    public void setIndexBuffer(IBufferGL indexBuffer) {
        if (this.indexBuffer != indexBuffer) {
            this.arena.setIndexBuffer(indexBuffer);
            this.indexBuffer = indexBuffer;
        }
    }

    public void deleteResources() {
        this.arena.delete();
        this.indexBuffer = null;
        for (int i = 0; i < this.batches.length; ++i) {
            this.batches[i].delete();
        }
    }

    public ChunkDrawCallBatcher getDrawBatcher(BlockRenderLayer layer) {
        return this.batches[layer.ordinal()];
    }

    public int getOriginX() {
        return this.originX;
    }

    public int getOriginY() {
        return this.originY;
    }

    public int getOriginZ() {
        return this.originZ;
    }
}
