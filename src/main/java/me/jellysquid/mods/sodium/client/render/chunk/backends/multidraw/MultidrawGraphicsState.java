package me.jellysquid.mods.sodium.client.render.chunk.backends.multidraw;

import me.jellysquid.mods.sodium.client.gl.arena.GlBufferSegment;
import me.jellysquid.mods.sodium.client.render.chunk.region.ChunkRegion;

public class MultidrawGraphicsState {
    private final ChunkRegion<MultidrawGraphicsState> region;

    private final GlBufferSegment segment;
    private final int elementOffset;
    private final int elementCount;

    public MultidrawGraphicsState(ChunkRegion<MultidrawGraphicsState> region, GlBufferSegment segment,
                                  int elementOffset, int elementCount) {
        this.region = region;
        this.segment = segment;
        this.elementOffset = elementOffset;
        this.elementCount = elementCount;
    }

    public void delete() {
        this.segment.delete();
    }

    public ChunkRegion<MultidrawGraphicsState> getRegion() {
        return this.region;
    }

    public GlBufferSegment getSegment() {
        return this.segment;
    }

    public int getElementOffset() {
        return this.elementOffset;
    }

    public int getElementCount() {
        return this.elementCount;
    }
}
