package me.jellysquid.mods.sodium.client.render.chunk.backends.multidraw;

import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_ARRAY_BUFFER;
import static net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglBufferData;
import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_ELEMENT_ARRAY_BUFFER;
import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_MODELVIEW;
import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_STATIC_DRAW;
import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_STREAM_DRAW;
import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_TEXTURE;
import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_TEXTURE0;
import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_TEXTURE1;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.jellysquid.mods.sodium.client.gl.arena.GlBufferArena;
import me.jellysquid.mods.sodium.client.gl.arena.GlBufferSegment;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderContainer;
import me.jellysquid.mods.sodium.client.render.chunk.format.DefaultModelVertexFormats;
import me.jellysquid.mods.sodium.client.render.chunk.format.hfp.HFPModelVertexType;
import me.jellysquid.mods.sodium.client.render.chunk.region.ChunkRegion;
import me.jellysquid.mods.sodium.client.render.chunk.region.ChunkRegionManager;
import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.internal.IBufferGL;
import net.lax1dude.eaglercraft.internal.IVertexArrayGL;
import net.lax1dude.eaglercraft.internal.buffer.ByteBuffer;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.chunk.ChunkRender;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;

/**
 * Shader-based chunk renderer which makes use of a custom memory allocator on top of large buffer objects to allow
 * for draw call batching without buffer switching.
 *
 * The biggest bottleneck after setting up vertex attribute state is the sheer number of buffer switches and draw calls
 * being performed. In vanilla, the game uses one buffer for every chunk section, which means we need to bind, setup,
 * and draw every chunk individually.
 *
 * In order to reduce the number of these calls, we need to firstly reduce the number of buffer switches. We do this
 * through sub-dividing the world into larger "chunk regions" which then have one large buffer object in OpenGL. From
 * here, we can allocate slices of this buffer to each individual chunk and then only bind it once before drawing. Then,
 * our draw calls can simply point to individual sections within the buffer by manipulating the offset and count
 * parameters.
 *
 * However, an unfortunate consequence is that if we run out of space in a buffer, we need to re-allocate the entire
 * storage, which can take a ton of time! With old OpenGL 2.1 code, the only way to do this would be to copy the buffer's
 * memory from the graphics card over the host bus into CPU memory, allocate a new buffer, and then copy it back over
 * the bus and into graphics card. For reasons that should be obvious, this is extremely inefficient and requires the
 * CPU and GPU to be synchronized.
 *
 * If we make use of more modern OpenGL 3.0 features, we can avoid this transfer over the memory bus and instead just
 * perform the copy between buffers in GPU memory with the aptly named "copy buffer" function. It's still not blazing
 * fast, but it's much better than what we're stuck with in older versions. We can help prevent these re-allocations by
 * sizing our buffers to be a bit larger than what we expect all the chunk data to be, but this wastes memory.
 *
 * In the initial implementation, this solution worked fine enough, but the amount of time being spent on uploading
 * chunks to the large buffers was now a magnitude more than what it was before all of this and it made chunk updates
 * *very* slow. It took some tinkering to figure out what was going wrong here, but at least on the NVIDIA drivers, it
 * seems that updating sub-regions of buffer memory hits some kind of slow path. A workaround for this problem is to
 * create a scratch buffer object and upload the chunk data there *first*, re-allocating the storage each time. Then,
 * you can copy the contents of the scratch buffer into the chunk region buffer, rise and repeat. I'm not happy with
 * this solution, but it performs surprisingly well across all hardware I tried.
 *
 * With both of these changes, the amount of CPU time taken by rendering chunks linearly decreases with the reduction
 * in buffer bind/setup/draw calls. Using the default settings of 4x2x4 chunk region buffers, the number of calls can be
 * reduced up to a factor of ~32x.
 */
public class MultidrawChunkRenderBackend {
    private static final HFPModelVertexType VERTEX_FORMAT = DefaultModelVertexFormats.MODEL_VERTEX_HFP;
    private static final int VERTEX_STRIDE = VERTEX_FORMAT.getCustomVertexFormat().getStride();
    private static final int INDEX_GROWTH_VERTICES = 1 << 16;
    private static final float MODEL_ORIGIN = 8.0F;

    private final ChunkRegionManager<MultidrawGraphicsState> bufferManager;

    private final ObjectArrayList<ChunkRegion<MultidrawGraphicsState>>[] cachedBatches;
    private final int[] cachedRenderListRevisions;
    private final int[] cachedGeometryRevisions;

    private final IBufferGL uploadBuffer;
    private final IBufferGL indexBuffer;
    private final IVertexArrayGL indexBufferUploadVertexArray;
    private int indexVertexCapacity;

    private boolean needsRegionCleanup;
    private int geometryRevision;

    @SuppressWarnings("unchecked")
    public MultidrawChunkRenderBackend() {
        this.bufferManager = new ChunkRegionManager<>();
        this.uploadBuffer = EaglercraftGPU.createGLArrayBuffer();
        this.indexBuffer = EaglercraftGPU.createGLElementArrayBuffer();
        this.indexBufferUploadVertexArray = EaglercraftGPU.createGLVertexArray();
        int layerCount = BlockRenderLayer._VALUES.length;
        this.cachedBatches = new ObjectArrayList[layerCount];
        this.cachedRenderListRevisions = new int[layerCount];
        this.cachedGeometryRevisions = new int[layerCount];
        for (int i = 0; i < layerCount; ++i) {
            this.cachedBatches[i] = new ObjectArrayList<>();
            this.cachedRenderListRevisions[i] = Integer.MIN_VALUE;
            this.cachedGeometryRevisions[i] = Integer.MIN_VALUE;
        }
    }

    public MultidrawGraphicsState upload(ChunkRenderContainer render, BufferBuilder builder, MultidrawGraphicsState graphics) {
        if (graphics != null) {
            this.deleteGraphicsState(graphics);
        }

        int sourceVertices = builder.getVertexCount();
        if (sourceVertices <= 0) {
            builder.reset();
            return null;
        }
        if (!builder.isDirectChunkHfp() || builder.getDrawMode() != 7 || (sourceVertices & 3) != 0) {
            builder.reset();
            throw new IllegalStateException("Regional terrain upload requires direct 20-byte HFP quads");
        }

        int indexCount = sourceVertices + (sourceVertices >> 1);
        int uploadBytes = sourceVertices * VERTEX_STRIDE;
        ByteBuffer src = builder.getByteBuffer();
        src.position(0);
        src.limit(uploadBytes);

        EaglercraftGPU.bindGLArrayBuffer(this.uploadBuffer);
        _wglBufferData(GL_ARRAY_BUFFER, src, GL_STREAM_DRAW);

        BlockPos pos = render.getPosition();
        ChunkRegion<MultidrawGraphicsState> region = this.bufferManager.getOrCreateRegion(pos.getX() >> 4,
                pos.getY() >> 4, pos.getZ() >> 4);
        GlBufferSegment segment = region.getBufferArena().uploadBuffer(this.uploadBuffer, 0, uploadBytes);
        int baseVertex = segment.getStart() / VERTEX_STRIDE;
        this.ensureIndexBufferCapacity(baseVertex + sourceVertices);
        region.setIndexBuffer(this.indexBuffer);
        builder.reset();
        ++this.geometryRevision;

        return new MultidrawGraphicsState(region, segment, (baseVertex >> 2) * 24, indexCount);
    }

    private void ensureIndexBufferCapacity(int requiredVertices) {
        if (requiredVertices <= this.indexVertexCapacity) {
            return;
        }

        int vertexCapacity = (requiredVertices + INDEX_GROWTH_VERTICES - 1)
                & -INDEX_GROWTH_VERTICES;
        ByteBuffer indices = EagRuntime.allocateByteBuffer(vertexCapacity * 6);
        try {
            for (int vertex = 0; vertex < vertexCapacity; vertex += 4) {
                indices.putInt(vertex);
                indices.putInt(vertex + 1);
                indices.putInt(vertex + 2);
                indices.putInt(vertex);
                indices.putInt(vertex + 2);
                indices.putInt(vertex + 3);
            }
            indices.flip();

            EaglercraftGPU.bindGLVertexArray(this.indexBufferUploadVertexArray);
            EaglercraftGPU.bindVAOGLElementArrayBuffer(this.indexBuffer);
            _wglBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
            this.indexVertexCapacity = vertexCapacity;
        } finally {
            EagRuntime.freeByteBuffer(indices);
        }
    }

    public void render(List<ChunkRender> renders, BlockRenderLayer layer, double viewX, double viewY, double viewZ,
                       int renderListRevision) {
        if (this.needsRegionCleanup) {
            this.bufferManager.cleanup();
            this.needsRegionCleanup = false;
        }

        int layerIndex = layer.ordinal();
        ObjectArrayList<ChunkRegion<MultidrawGraphicsState>> batches = this.cachedBatches[layerIndex];
        if (this.cachedRenderListRevisions[layerIndex] != renderListRevision
                || this.cachedGeometryRevisions[layerIndex] != this.geometryRevision) {
            this.setupDrawBatches(renders, layer, batches);
            this.cachedRenderListRevisions[layerIndex] = renderListRevision;
            this.cachedGeometryRevisions[layerIndex] = this.geometryRevision;
        }

        if (batches.isEmpty()) {
            return;
        }

        this.begin();

        try {
            for (ChunkRegion<MultidrawGraphicsState> region : batches) {
                ChunkDrawCallBatcher batch = region.getDrawBatcher(layer);

                GlStateManager.pushMatrix();
                GlStateManager.translatef((float) ((double) region.getOriginX() - viewX) - MODEL_ORIGIN,
                        (float) ((double) region.getOriginY() - viewY) - MODEL_ORIGIN,
                        (float) ((double) region.getOriginZ() - viewZ) - MODEL_ORIGIN);
                GlStateManager.scalef(VERTEX_FORMAT.getModelScale(), VERTEX_FORMAT.getModelScale(),
                        VERTEX_FORMAT.getModelScale());
                EaglercraftGPU.drawChunkRegion(region.getBufferArena().getVertexArray(), batch.getFirstsBuffer(),
                        batch.getCountsBuffer(), batch.getCount());
                GlStateManager.popMatrix();
            }
        } finally {
            this.end();
        }

    }

    private void begin() {
        GlStateManager.activeTexture(GL_TEXTURE0);
        GlStateManager.matrixMode(GL_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.scalef(VERTEX_FORMAT.getTextureScale(), VERTEX_FORMAT.getTextureScale(), 1.0F);

        GlStateManager.activeTexture(GL_TEXTURE1);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();

        GlStateManager.activeTexture(GL_TEXTURE0);
        GlStateManager.matrixMode(GL_MODELVIEW);
    }

    private void end() {
        GlStateManager.activeTexture(GL_TEXTURE1);
        GlStateManager.matrixMode(GL_TEXTURE);
        GlStateManager.popMatrix();

        GlStateManager.activeTexture(GL_TEXTURE0);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL_MODELVIEW);
    }

    private void setupDrawBatches(List<ChunkRender> renders, BlockRenderLayer layer,
                                  ObjectArrayList<ChunkRegion<MultidrawGraphicsState>> batches) {
        batches.clear();

        for (int i = 0, len = renders.size(); i < len; ++i) {
            ChunkRenderContainer render = (ChunkRenderContainer) renders.get(i);
            MultidrawGraphicsState state = render.getGraphicsState(layer);
            if (state == null) {
                continue;
            }

            ChunkRegion<MultidrawGraphicsState> region = state.getRegion();
            ChunkDrawCallBatcher batch = region.getDrawBatcher(layer);

            if (!batch.isBuilding()) {
                batch.begin();

                batches.add(region);
            }

            batch.addIndirectDrawCall(state.getElementOffset(), state.getElementCount(), 0, 1);
        }

        for (int i = 0; i < batches.size(); ++i) {
            batches.get(i).getDrawBatcher(layer).end();
        }
    }

    public void deleteGraphicsState(MultidrawGraphicsState state) {
        state.delete();
        this.needsRegionCleanup = true;
        ++this.geometryRevision;
    }

    public void clear() {
        this.bufferManager.delete();
        for (int i = 0; i < this.cachedBatches.length; ++i) {
            this.cachedBatches[i].clear();
            this.cachedRenderListRevisions[i] = Integer.MIN_VALUE;
            this.cachedGeometryRevisions[i] = Integer.MIN_VALUE;
        }
        this.needsRegionCleanup = false;
        ++this.geometryRevision;
    }

    public void delete() {
        this.clear();
        EaglercraftGPU.destroyGLArrayBuffer(this.uploadBuffer);
        EaglercraftGPU.destroyGLVertexArray(this.indexBufferUploadVertexArray);
        EaglercraftGPU.destroyGLElementArrayBuffer(this.indexBuffer);

    }

    public static boolean isSupported() {
        return EaglercraftGPU.checkMultiDrawCapable();
    }

    public List<String> getDebugStrings() {
        List<String> list = new ArrayList<>();
        list.add(String.format("Active Buffers: %s", this.bufferManager.getAllocatedRegionCount()));
        list.add("Submission Mode: Multi Draw");

        return list;
    }
}
