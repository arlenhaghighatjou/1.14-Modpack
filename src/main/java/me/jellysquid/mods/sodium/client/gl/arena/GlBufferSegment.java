package me.jellysquid.mods.sodium.client.gl.arena;

public class GlBufferSegment {
    private final GlBufferArena arena;
    private final int start;
    private final int len;
    private boolean freed;

    GlBufferSegment(GlBufferArena arena, int start, int len) {
        this.arena = arena;
        this.start = start;
        this.len = len;
    }

    public int getStart() {
        return this.start;
    }

    public int getLength() {
        return this.len;
    }

    public void delete() {
        if (this.freed) {
            throw new IllegalStateException("Segment already freed");
        }
        this.freed = true;
        this.arena.free(this);
    }
}
