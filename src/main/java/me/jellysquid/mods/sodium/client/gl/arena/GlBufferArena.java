package me.jellysquid.mods.sodium.client.gl.arena;

import static net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglBindBuffer;
import static net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglBufferData;
import static net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglCopyBufferSubData;
import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_ARRAY_BUFFER;
import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_DYNAMIC_DRAW;

import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import me.jellysquid.mods.sodium.client.render.chunk.format.hfp.HFPModelVertexType;
import net.lax1dude.eaglercraft.internal.IBufferGL;
import net.lax1dude.eaglercraft.internal.IVertexArrayGL;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;

public class GlBufferArena {
    private static final int BUFFER_USAGE = GL_DYNAMIC_DRAW;
    private static final int GL_COPY_READ_BUFFER = 0x8F36;
    private static final int GL_COPY_WRITE_BUFFER = 0x8F37;

    private final int resizeIncrement;

    private final Set<GlBufferSegment> freeRegions = new ObjectLinkedOpenHashSet<>();

    private IBufferGL vertexBuffer;
    private IVertexArrayGL vertexArray;

    private int position;
    private int capacity;
    private int allocCount;

    public GlBufferArena(int initialSize, int resizeIncrement) {
        this.vertexBuffer = EaglercraftGPU.createGLArrayBuffer();
        this.vertexArray = EaglercraftGPU.createGLVertexArray();
        EaglercraftGPU.bindGLArrayBuffer(this.vertexBuffer);
        _wglBufferData(GL_ARRAY_BUFFER, initialSize, BUFFER_USAGE);
        EaglercraftGPU.setupChunkRegionVertexArray(this.vertexArray, this.vertexBuffer, HFPModelVertexType.VERTEX_FORMAT);

        this.resizeIncrement = resizeIncrement;
        this.capacity = initialSize;
    }

    private void resize(int newCapacity) {
        IBufferGL src = this.vertexBuffer;
        IBufferGL dst = EaglercraftGPU.createGLArrayBuffer();

        _wglBindBuffer(GL_COPY_WRITE_BUFFER, dst);
        _wglBufferData(GL_COPY_WRITE_BUFFER, newCapacity, BUFFER_USAGE);
        _wglBindBuffer(GL_COPY_READ_BUFFER, src);
        _wglCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, 0, 0, this.position);
        EaglercraftGPU.setupChunkRegionVertexArray(this.vertexArray, dst, HFPModelVertexType.VERTEX_FORMAT);
        EaglercraftGPU.destroyGLArrayBuffer(src);

        this.vertexBuffer = dst;
        this.capacity = newCapacity;
    }

    public void prepareBuffer(int bytes) {
        if (this.position + bytes > this.capacity) {
            this.resize(this.getNextSize(bytes));
        }
    }

    public GlBufferSegment uploadBuffer(IBufferGL readBuffer, int readOffset, int byteCount) {
        GlBufferSegment segment = this.alloc(byteCount);

        _wglBindBuffer(GL_COPY_READ_BUFFER, readBuffer);
        _wglBindBuffer(GL_COPY_WRITE_BUFFER, this.vertexBuffer);
        _wglCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, readOffset, segment.getStart(), byteCount);

        return segment;
    }

    private int getNextSize(int len) {
        return Math.max(this.capacity + this.resizeIncrement, this.capacity + len);
    }

    public void free(GlBufferSegment segment) {
        if (!this.freeRegions.add(segment)) {
            throw new IllegalArgumentException("Segment already freed");
        }

        this.allocCount--;
    }

    private GlBufferSegment alloc(int len) {
        GlBufferSegment segment = this.allocReuse(len);

        if (segment == null) {
            this.prepareBuffer(len);
            segment = new GlBufferSegment(this, this.position, len);

            this.position += len;
        }

        this.allocCount++;

        return segment;
    }

    private GlBufferSegment allocReuse(int len) {
        GlBufferSegment bestSegment = null;

        for (GlBufferSegment segment : this.freeRegions) {
            if (segment.getLength() < len) {
                continue;
            }

            if (bestSegment == null || bestSegment.getLength() > segment.getLength()) {
                bestSegment = segment;
            }
        }

        if (bestSegment == null) {
            return null;
        }

        this.freeRegions.remove(bestSegment);

        int excess = bestSegment.getLength() - len;

        if (excess > 0) {
            this.freeRegions.add(new GlBufferSegment(this, bestSegment.getStart() + len, excess));
        }

        return new GlBufferSegment(this, bestSegment.getStart(), len);
    }

    public void delete() {
        EaglercraftGPU.destroyGLVertexArray(this.vertexArray);
        EaglercraftGPU.destroyGLArrayBuffer(this.vertexBuffer);
        this.vertexArray = null;
        this.vertexBuffer = null;
        this.freeRegions.clear();
    }

    public boolean isEmpty() {
        return this.allocCount <= 0;
    }

    public IBufferGL getBuffer() {
        return this.vertexBuffer;
    }

    public IVertexArrayGL getVertexArray() {
        return this.vertexArray;
    }

    public void setIndexBuffer(IBufferGL indexBuffer) {
        EaglercraftGPU.bindGLVertexArray(this.vertexArray);
        EaglercraftGPU.bindVAOGLElementArrayBuffer(indexBuffer);
    }
}
