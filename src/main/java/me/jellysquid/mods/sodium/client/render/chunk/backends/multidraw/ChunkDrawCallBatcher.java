package me.jellysquid.mods.sodium.client.render.chunk.backends.multidraw;

import java.nio.BufferUnderflowException;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.internal.buffer.IntBuffer;

/**
 * Provides a fixed-size buffer which can be used to batch chunk section draw calls.
 */
public class ChunkDrawCallBatcher {
    protected final int capacity;

    protected boolean isBuilding;
    protected int count;

    protected int arrayLength;

    private final IntBuffer firsts;
    private final IntBuffer counts;

    protected ChunkDrawCallBatcher(int capacity) {
        this.capacity = capacity;
        this.firsts = EagRuntime.allocateIntBuffer(capacity);
        this.counts = EagRuntime.allocateIntBuffer(capacity);
    }

    public static ChunkDrawCallBatcher create(int capacity) {
        return new ChunkDrawCallBatcher(capacity);
    }

    public void begin() {
        this.isBuilding = true;
        this.count = 0;
        this.arrayLength = 0;
        this.firsts.clear();
        this.counts.clear();
    }

    public void end() {
        this.isBuilding = false;

        this.arrayLength = this.count * 8;
        this.firsts.limit(this.count);
        this.firsts.position(0);
        this.counts.limit(this.count);
        this.counts.position(0);
    }

    public boolean isBuilding() {
        return this.isBuilding;
    }

    public void addIndirectDrawCall(int first, int count, int baseInstance, int instanceCount) {
        if (this.count >= this.capacity) {
            throw new BufferUnderflowException();
        }

        this.firsts.put(this.count, first);
        this.counts.put(this.count, count);
        this.count++;
    }

    public int getCount() {
        return this.count;
    }

    public int getArrayLength() {
        return this.arrayLength;
    }

    public IntBuffer getFirstsBuffer() {
        return this.firsts;
    }

    public IntBuffer getCountsBuffer() {
        return this.counts;
    }

    public void delete() {
        EagRuntime.freeIntBuffer(this.firsts);
        EagRuntime.freeIntBuffer(this.counts);
    }
}
