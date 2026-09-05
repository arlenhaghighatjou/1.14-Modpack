package net.minecraft.client.renderer;

import com.google.common.primitives.Floats;
import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.internal.buffer.ByteBuffer;
import net.lax1dude.eaglercraft.internal.buffer.FloatBuffer;
import net.lax1dude.eaglercraft.internal.buffer.IntBuffer;
import net.lax1dude.eaglercraft.internal.buffer.ShortBuffer;
import me.jellysquid.mods.sodium.client.render.chunk.format.ModelVertexUtil;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.BitSet;

@OnlyIn(Dist.CLIENT)
public class BufferBuilder {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final boolean LITTLE_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
    private static final int HFP_VERTEX_STRIDE = 20;
    private static final float HFP_MODEL_ORIGIN = 8.0F;
    private final boolean directChunkHfp;
    private ByteBuffer byteBuffer;
    private IntBuffer rawIntBuffer;
    private ShortBuffer rawShortBuffer;
    private FloatBuffer rawFloatBuffer;
    private int vertexCount;
    private VertexFormatElement vertexFormatElement;
    private int vertexFormatIndex;
    private boolean noColor;
    private int drawMode;
    private double xOffset;
    private double yOffset;
    private double zOffset;
    private VertexFormat vertexFormat;
    private boolean isDrawing;

    public BufferBuilder(int bufferSizeIn) {
        this(bufferSizeIn, false);
    }

    public BufferBuilder(int bufferSizeIn, boolean directChunkHfp) {
        this.directChunkHfp = directChunkHfp;
        this.byteBuffer = GLAllocation.createDirectByteBuffer(bufferSizeIn * 4);
        this.rawIntBuffer = this.byteBuffer.asIntBuffer();
        this.rawShortBuffer = this.byteBuffer.asShortBuffer();
        this.rawFloatBuffer = this.byteBuffer.asFloatBuffer();
    }

    private void growBuffer(int increaseAmount) {
        if (this.getBufferSizeBytes() + increaseAmount > this.byteBuffer.capacity()) {
            int capacity = this.byteBuffer.capacity();

            int newCapacity = Math.max(capacity * 2, capacity + increaseAmount);
            LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", capacity, newCapacity);
            int k = this.rawIntBuffer.position();
            ByteBuffer oldBuffer = this.byteBuffer;
            ByteBuffer bytebuffer = GLAllocation.createDirectByteBuffer(newCapacity);
            oldBuffer.position(0);
            bytebuffer.put(oldBuffer);
            bytebuffer.rewind();
            this.byteBuffer = bytebuffer;
            this.rawIntBuffer = this.byteBuffer.asIntBuffer();
            this.rawIntBuffer.position(k);
            this.rawShortBuffer = this.byteBuffer.asShortBuffer();
            this.rawShortBuffer.position(k << 1);
            this.rawFloatBuffer = this.byteBuffer.asFloatBuffer();
            this.rawFloatBuffer.position(k);
            EagRuntime.freeByteBuffer(oldBuffer);
        }
    }

    public void sortVertexData(float cameraX, float cameraY, float cameraZ) {
        int quadCount = this.vertexCount / 4;
        float[] distanceArray = new float[quadCount];
        int[] indicesArray = new int[quadCount];

        float centerX = (float) ((double) cameraX + this.xOffset);
        float centerY = (float) ((double) cameraY + this.yOffset);
        float centerZ = (float) ((double) cameraZ + this.zOffset);
        for (int quadIdx = 0; quadIdx < quadCount; ++quadIdx) {
            if (this.directChunkHfp) {
                distanceArray[quadIdx] = getHfpDistanceSq(this.byteBuffer, centerX + HFP_MODEL_ORIGIN,
                        centerY + HFP_MODEL_ORIGIN, centerZ + HFP_MODEL_ORIGIN,
                        quadIdx * HFP_VERTEX_STRIDE * 4);
            } else {
                distanceArray[quadIdx] = getDistanceSq(this.rawFloatBuffer, centerX, centerY, centerZ,
                        this.vertexFormat.getIntegerSize(), quadIdx * this.vertexFormat.getSize());
            }
            indicesArray[quadIdx] = quadIdx;
        }

        mergeSort(indicesArray, distanceArray);
        BitSet bitset = new BitSet();
        int l = this.getStoredVertexStride();
        int[] aint = new int[l];
        int[] tempQuad = new int[l];

        for (int i1 = bitset.nextClearBit(0); i1 < indicesArray.length; i1 = bitset.nextClearBit(i1 + 1)) {
            int j1 = indicesArray[i1];
            if (j1 != i1) {
                this.rawIntBuffer.limit(j1 * l + l);
                this.rawIntBuffer.position(j1 * l);
                this.rawIntBuffer.get(aint);
                int k1 = j1;

                for (int l1 = indicesArray[j1]; k1 != i1; l1 = indicesArray[l1]) {
                    this.rawIntBuffer.limit(l1 * l + l);
                    this.rawIntBuffer.position(l1 * l);
                    this.rawIntBuffer.get(tempQuad);
                    this.rawIntBuffer.limit(k1 * l + l);
                    this.rawIntBuffer.position(k1 * l);
                    this.rawIntBuffer.put(tempQuad);
                    bitset.set(k1);
                    k1 = l1;
                }

                this.rawIntBuffer.limit(i1 * l + l);
                this.rawIntBuffer.position(i1 * l);
                this.rawIntBuffer.put(aint);
            }

            bitset.set(i1);
        }

    }

    private static void mergeSort(int[] indicesArray, float[] distanceArray) {
        mergeSort(indicesArray, 0, indicesArray.length, distanceArray,
                Arrays.copyOf(indicesArray, indicesArray.length));
    }

    private static void mergeSort(final int[] a, final int from, final int to, float[] dist, final int[] supp) {
        int len = to - from;

        if (len < 16) {
            insertionSort(a, from, to, dist);
            return;
        }
        final int mid = (from + to) >>> 1;
        mergeSort(supp, from, mid, dist, a);
        mergeSort(supp, mid, to, dist, a);

        if (Floats.compare(dist[supp[mid]], dist[supp[mid - 1]]) <= 0) {
            System.arraycopy(supp, from, a, from, len);
            return;
        }

        for (int i = from, p = from, q = mid; i < to; i++) {
            if (q >= to || p < mid && Floats.compare(dist[supp[q]], dist[supp[p]]) <= 0) {
                a[i] = supp[p++];
            } else {
                a[i] = supp[q++];
            }
        }
    }

    private static void insertionSort(final int[] a, final int from, final int to, final float[] dist) {
        for (int i = from; ++i < to; ) {
            int t = a[i];
            int j = i;

            for (int u = a[j - 1]; Floats.compare(dist[u], dist[t]) < 0; u = a[--j - 1]) {
                a[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
            }

            a[j] = t;
        }
    }

    public BufferBuilder.State getVertexState() {
        this.rawIntBuffer.rewind();
        int i = this.getBufferSize();
        this.rawIntBuffer.limit(i);
        int[] aint = new int[i];
        this.rawIntBuffer.get(aint);
        this.rawIntBuffer.limit(this.rawIntBuffer.capacity());
        this.rawIntBuffer.position(i);
        return new BufferBuilder.State(aint, new VertexFormat(this.vertexFormat), this.getStoredVertexStride() >> 2,
                this.directChunkHfp);
    }

    private int getBufferSize() {
        return this.getBufferSizeBytes() >> 2;
    }

    private int getBufferSizeBytes() {
        return this.vertexCount * this.getStoredVertexStride();
    }

    private int getStoredVertexStride() {
        return this.directChunkHfp ? HFP_VERTEX_STRIDE : this.vertexFormat.getSize();
    }

    private static float getDistanceSq(FloatBuffer buffer, float xCenter, float yCenter, float zCenter, int stride, int start) {
        int vertexBase = start;
        float x1 = buffer.get(vertexBase);
        float y1 = buffer.get(vertexBase + 1);
        float z1 = buffer.get(vertexBase + 2);

        vertexBase += stride;
        float x2 = buffer.get(vertexBase);
        float y2 = buffer.get(vertexBase + 1);
        float z2 = buffer.get(vertexBase + 2);

        vertexBase += stride;
        float x3 = buffer.get(vertexBase);
        float y3 = buffer.get(vertexBase + 1);
        float z3 = buffer.get(vertexBase + 2);

        vertexBase += stride;
        float x4 = buffer.get(vertexBase);
        float y4 = buffer.get(vertexBase + 1);
        float z4 = buffer.get(vertexBase + 2);

        float xDist = ((x1 + x2 + x3 + x4) * 0.25F) - xCenter;
        float yDist = ((y1 + y2 + y3 + y4) * 0.25F) - yCenter;
        float zDist = ((z1 + z2 + z3 + z4) * 0.25F) - zCenter;

        return (xDist * xDist) + (yDist * yDist) + (zDist * zDist);
    }

    private static float getHfpDistanceSq(ByteBuffer buffer, float xCenter, float yCenter, float zCenter, int start) {
        float x = 0.0F;
        float y = 0.0F;
        float z = 0.0F;
        for (int vertex = 0; vertex < 4; ++vertex) {
            int offset = start + vertex * HFP_VERTEX_STRIDE;
            x += (float) (buffer.getShort(offset) & 65535) * (1.0F / 256.0F);
            y += (float) (buffer.getShort(offset + 2) & 65535) * (1.0F / 256.0F);
            z += (float) (buffer.getShort(offset + 4) & 65535) * (1.0F / 256.0F);
        }
        float xDist = x * 0.25F - xCenter;
        float yDist = y * 0.25F - yCenter;
        float zDist = z * 0.25F - zCenter;
        return xDist * xDist + yDist * yDist + zDist * zDist;
    }

    public void setVertexState(BufferBuilder.State state) {
        if (state.isDirectChunkHfp() != this.directChunkHfp) {
            throw new IllegalArgumentException("Mismatched chunk vertex storage format");
        }
        this.rawIntBuffer.clear();
        this.growBuffer(state.getRawBuffer().length * 4);
        this.rawIntBuffer.put(state.getRawBuffer());
        this.vertexCount = state.getVertexCount();
        this.vertexFormat = new VertexFormat(state.getVertexFormat());
    }

    public void reset() {
        this.vertexCount = 0;
        this.vertexFormatElement = null;
        this.vertexFormatIndex = 0;
    }

    public void begin(int glMode, VertexFormat format) {
        if (this.isDrawing) {
            throw new IllegalStateException("Already building!");
        } else {
            this.isDrawing = true;
            this.reset();
            this.drawMode = glMode;
            this.vertexFormat = format;
            this.vertexFormatElement = format.getElement(this.vertexFormatIndex);
            this.noColor = false;
            this.byteBuffer.limit(this.byteBuffer.capacity());
        }
    }

    public BufferBuilder tex(double u, double v) {
        int i = this.vertexCount * this.vertexFormat.getSize() + this.vertexFormat.getOffset(this.vertexFormatIndex);
        switch (this.vertexFormatElement.getType()) {
            case FLOAT:
                this.byteBuffer.putFloat(i, (float) u);
                this.byteBuffer.putFloat(i + 4, (float) v);
                break;
            case UINT:
            case INT:
                this.byteBuffer.putInt(i, (int) u);
                this.byteBuffer.putInt(i + 4, (int) v);
                break;
            case USHORT:
            case SHORT:
                this.byteBuffer.putShort(i, (short) ((int) v));
                this.byteBuffer.putShort(i + 2, (short) ((int) u));
                break;
            case UBYTE:
            case BYTE:
                this.byteBuffer.put(i, (byte) ((int) v));
                this.byteBuffer.put(i + 1, (byte) ((int) u));
        }

        this.nextVertexFormatIndex();
        return this;
    }

    public BufferBuilder lightmap(int skyLight, int blockLight) {
        int i = this.vertexCount * this.vertexFormat.getSize() + this.vertexFormat.getOffset(this.vertexFormatIndex);
        switch (this.vertexFormatElement.getType()) {
            case FLOAT:
                this.byteBuffer.putFloat(i, (float) skyLight);
                this.byteBuffer.putFloat(i + 4, (float) blockLight);
                break;
            case UINT:
            case INT:
                this.byteBuffer.putInt(i, skyLight);
                this.byteBuffer.putInt(i + 4, blockLight);
                break;
            case USHORT:
            case SHORT:
                this.byteBuffer.putShort(i, (short) blockLight);
                this.byteBuffer.putShort(i + 2, (short) skyLight);
                break;
            case UBYTE:
            case BYTE:
                this.byteBuffer.put(i, (byte) blockLight);
                this.byteBuffer.put(i + 1, (byte) skyLight);
        }

        this.nextVertexFormatIndex();
        return this;
    }

    public void putBrightness4(int vertex0, int vertex1, int vertex2, int vertex3) {
        int i = this.vertexFormat.getSize();
        int j = i >> 2;
        int k = (this.vertexCount - 4) * j + this.vertexFormat.getUvOffsetById(1) / 4;
        this.rawIntBuffer.put(k, vertex0);
        this.rawIntBuffer.put(k + j, vertex1);
        this.rawIntBuffer.put(k + j * 2, vertex2);
        this.rawIntBuffer.put(k + j * 3, vertex3);
    }

    public void putPosition(double x, double y, double z) {
        int i = this.vertexFormat.getIntegerSize();
        int j = (this.vertexCount - 4) * i;

        for (int k = 0; k < 4; ++k) {
            int l = j + k * i;
            int i1 = l + 1;
            int j1 = i1 + 1;
            this.rawIntBuffer.put(l, Float.floatToRawIntBits((float) (x + this.xOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(l))));
            this.rawIntBuffer.put(i1, Float.floatToRawIntBits((float) (y + this.yOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(i1))));
            this.rawIntBuffer.put(j1, Float.floatToRawIntBits((float) (z + this.zOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(j1))));
        }

    }

    private int getColorIndex(int vertexIndex) {
        return ((this.vertexCount - vertexIndex) * this.vertexFormat.getSize() + this.vertexFormat.getColorOffset()) / 4;
    }

    public void putColorMultiplier(float red, float green, float blue, int vertexIndex) {
        int i = this.getColorIndex(vertexIndex);
        int j = -1;
        if (!this.noColor) {
            j = this.rawIntBuffer.get(i);
            if (LITTLE_ENDIAN) {
                int k = (int) ((float) (j & 255) * red);
                int l = (int) ((float) (j >> 8 & 255) * green);
                int i1 = (int) ((float) (j >> 16 & 255) * blue);
                j = j & -16777216;
                j = j | i1 << 16 | l << 8 | k;
            } else {
                int j1 = (int) ((float) (j >> 24 & 255) * red);
                int k1 = (int) ((float) (j >> 16 & 255) * green);
                int l1 = (int) ((float) (j >> 8 & 255) * blue);
                j = j & 255;
                j = j | j1 << 24 | k1 << 16 | l1 << 8;
            }
        }

        this.rawIntBuffer.put(i, j);
    }

    private void putColor(int argb, int vertexIndex) {
        int i = this.getColorIndex(vertexIndex);
        int j = argb >> 16 & 255;
        int k = argb >> 8 & 255;
        int l = argb & 255;
        this.putColorRGBA(i, j, k, l);
    }

    public void putColorRGB_F(float red, float green, float blue, int vertexIndex) {
        int i = this.getColorIndex(vertexIndex);
        int j = func_216567_a((int) (red * 255.0F), 0, 255);
        int k = func_216567_a((int) (green * 255.0F), 0, 255);
        int l = func_216567_a((int) (blue * 255.0F), 0, 255);
        this.putColorRGBA(i, j, k, l);
    }

    private static int func_216567_a(int p_216567_0_, int p_216567_1_, int p_216567_2_) {
        if (p_216567_0_ < p_216567_1_) {
            return p_216567_1_;
        } else {
            return p_216567_0_ > p_216567_2_ ? p_216567_2_ : p_216567_0_;
        }
    }

    private void putColorRGBA(int index, int red, int green, int blue) {
        if (LITTLE_ENDIAN) {
            this.rawIntBuffer.put(index, -16777216 | blue << 16 | green << 8 | red);
        } else {
            this.rawIntBuffer.put(index, red << 24 | green << 16 | blue << 8 | 255);
        }

    }

    public void noColor() {
        this.noColor = true;
    }

    public BufferBuilder color(float red, float green, float blue, float alpha) {
        return this.color((int) (red * 255.0F), (int) (green * 255.0F), (int) (blue * 255.0F), (int) (alpha * 255.0F));
    }

    public BufferBuilder color(int red, int green, int blue, int alpha) {
        if (this.noColor) {
            return this;
        } else {
            int i = this.vertexCount * this.vertexFormat.getSize() + this.vertexFormat.getOffset(this.vertexFormatIndex);
            switch (this.vertexFormatElement.getType()) {
                case FLOAT:
                    this.byteBuffer.putFloat(i, (float) red / 255.0F);
                    this.byteBuffer.putFloat(i + 4, (float) green / 255.0F);
                    this.byteBuffer.putFloat(i + 8, (float) blue / 255.0F);
                    this.byteBuffer.putFloat(i + 12, (float) alpha / 255.0F);
                    break;
                case UINT:
                case INT:
                    this.byteBuffer.putFloat(i, (float) red);
                    this.byteBuffer.putFloat(i + 4, (float) green);
                    this.byteBuffer.putFloat(i + 8, (float) blue);
                    this.byteBuffer.putFloat(i + 12, (float) alpha);
                    break;
                case USHORT:
                case SHORT:
                    this.byteBuffer.putShort(i, (short) red);
                    this.byteBuffer.putShort(i + 2, (short) green);
                    this.byteBuffer.putShort(i + 4, (short) blue);
                    this.byteBuffer.putShort(i + 6, (short) alpha);
                    break;
                case UBYTE:
                case BYTE:
                    if (LITTLE_ENDIAN) {
                        this.byteBuffer.put(i, (byte) red);
                        this.byteBuffer.put(i + 1, (byte) green);
                        this.byteBuffer.put(i + 2, (byte) blue);
                        this.byteBuffer.put(i + 3, (byte) alpha);
                    } else {
                        this.byteBuffer.put(i, (byte) alpha);
                        this.byteBuffer.put(i + 1, (byte) blue);
                        this.byteBuffer.put(i + 2, (byte) green);
                        this.byteBuffer.put(i + 3, (byte) red);
                    }
            }

            this.nextVertexFormatIndex();
            return this;
        }
    }

    public void addVertexData(int[] vertexData) {
        int i = this.vertexFormat.getSize();
        this.growBuffer(vertexData.length * 4 + i);
        this.rawIntBuffer.position(this.getBufferSize());
        this.rawIntBuffer.put(vertexData);
        this.vertexCount += vertexData.length / (i >> 2);
    }

    public void addQuadOptimized(int[] vertexData, float xOffset, float yOffset, float zOffset, int[] vertexBrightness, float[] colorMultR, float[] colorMultG, float[] colorMultB) {
        if (this.directChunkHfp) {
            this.addQuadHfp(vertexData, xOffset, yOffset, zOffset, vertexBrightness,
                    colorMultR, colorMultG, colorMultB);
            return;
        }

        int vertexSizeInts = this.vertexFormat.getIntegerSize();
        int quadInts = vertexSizeInts * 4;
        this.growBuffer(quadInts * 4 + this.vertexFormat.getSize());
        int offset = this.getBufferSize();
        float finalXOffset = xOffset + (float) this.xOffset;
        float finalYOffset = yOffset + (float) this.yOffset;
        float finalZOffset = zOffset + (float) this.zOffset;

        for (int i = 0; i < 4; ++i) {
            int vIdx = i * vertexSizeInts;
            int destIdx = offset + vIdx;

            this.rawIntBuffer.put(destIdx, Float.floatToRawIntBits(Float.intBitsToFloat(vertexData[vIdx]) + finalXOffset));
            this.rawIntBuffer.put(destIdx + 1, Float.floatToRawIntBits(Float.intBitsToFloat(vertexData[vIdx + 1]) + finalYOffset));
            this.rawIntBuffer.put(destIdx + 2, Float.floatToRawIntBits(Float.intBitsToFloat(vertexData[vIdx + 2]) + finalZOffset));

            int origColor = vertexData[vIdx + 3];
            float cr = colorMultR[i];
            float cg = colorMultG[i];
            float cb = colorMultB[i];

            if (LITTLE_ENDIAN) {
                int r = (int) ((float) (origColor & 255) * cr);
                int g = (int) ((float) (origColor >> 8 & 255) * cg);
                int b = (int) ((float) (origColor >> 16 & 255) * cb);
                this.rawIntBuffer.put(destIdx + 3, origColor & -16777216 | b << 16 | g << 8 | r);
            } else {
                int r = (int) ((float) (origColor >> 24 & 255) * cr);
                int g = (int) ((float) (origColor >> 16 & 255) * cg);
                int b = (int) ((float) (origColor >> 8 & 255) * cb);
                this.rawIntBuffer.put(destIdx + 3, origColor & 255 | r << 24 | g << 16 | b << 8);
            }

            this.rawIntBuffer.put(destIdx + 4, vertexData[vIdx + 4]);
            this.rawIntBuffer.put(destIdx + 5, vertexData[vIdx + 5]);

            this.rawIntBuffer.put(destIdx + 6, vertexBrightness[i]);

            for (int j = 7; j < vertexSizeInts; j++) {
                this.rawIntBuffer.put(destIdx + j, vertexData[vIdx + j]);
            }
        }

        this.vertexCount += 4;
    }

    private void addQuadHfp(int[] vertexData, float xOffset, float yOffset, float zOffset,
                            int[] vertexBrightness, float[] colorMultR, float[] colorMultG, float[] colorMultB) {
        int sourceStride = this.vertexFormat.getIntegerSize();
        this.growBuffer(HFP_VERTEX_STRIDE * 4);
        int offset = this.getBufferSizeBytes();
        float finalXOffset = xOffset + (float) this.xOffset;
        float finalYOffset = yOffset + (float) this.yOffset;
        float finalZOffset = zOffset + (float) this.zOffset;

        for (int i = 0; i < 4; ++i) {
            int source = i * sourceStride;
            int color = vertexData[source + 3];
            if (LITTLE_ENDIAN) {
                int red = (int) ((float) (color & 255) * colorMultR[i]);
                int green = (int) ((float) (color >> 8 & 255) * colorMultG[i]);
                int blue = (int) ((float) (color >> 16 & 255) * colorMultB[i]);
                color = color & -16777216 | blue << 16 | green << 8 | red;
            } else {
                int red = (int) ((float) (color >> 24 & 255) * colorMultR[i]);
                int green = (int) ((float) (color >> 16 & 255) * colorMultG[i]);
                int blue = (int) ((float) (color >> 8 & 255) * colorMultB[i]);
                color = color & 255 | red << 24 | green << 16 | blue << 8;
            }

            this.putHfpVertex(offset,
                    Float.intBitsToFloat(vertexData[source]) + finalXOffset + HFP_MODEL_ORIGIN,
                    Float.intBitsToFloat(vertexData[source + 1]) + finalYOffset + HFP_MODEL_ORIGIN,
                    Float.intBitsToFloat(vertexData[source + 2]) + finalZOffset + HFP_MODEL_ORIGIN,
                    color, Float.intBitsToFloat(vertexData[source + 4]),
                    Float.intBitsToFloat(vertexData[source + 5]), vertexBrightness[i]);
            offset += HFP_VERTEX_STRIDE;
        }

        this.vertexCount += 4;
    }

    public void addFluidQuad(
            float x0, float y0, float z0, float u0, float v0,
            float x1, float y1, float z1, float u1, float v1,
            float x2, float y2, float z2, float u2, float v2,
            float x3, float y3, float z3, float u3, float v3,
            float r, float g, float b, int lightmap) {
        int red = (int) (r * 255.0F) & 255;
        int green = (int) (g * 255.0F) & 255;
        int blue = (int) (b * 255.0F) & 255;
        int color = LITTLE_ENDIAN
                ? red | green << 8 | blue << 16 | 255 << 24
                : 255 | red << 24 | green << 16 | blue << 8;

        if (this.directChunkHfp) {
            this.growBuffer(HFP_VERTEX_STRIDE * 4);
            int offset = this.getBufferSizeBytes();
            this.putHfpVertex(offset, (float) ((double) x0 + this.xOffset) + HFP_MODEL_ORIGIN,
                    (float) ((double) y0 + this.yOffset) + HFP_MODEL_ORIGIN,
                    (float) ((double) z0 + this.zOffset) + HFP_MODEL_ORIGIN, color, u0, v0, lightmap);
            offset += HFP_VERTEX_STRIDE;
            this.putHfpVertex(offset, (float) ((double) x1 + this.xOffset) + HFP_MODEL_ORIGIN,
                    (float) ((double) y1 + this.yOffset) + HFP_MODEL_ORIGIN,
                    (float) ((double) z1 + this.zOffset) + HFP_MODEL_ORIGIN, color, u1, v1, lightmap);
            offset += HFP_VERTEX_STRIDE;
            this.putHfpVertex(offset, (float) ((double) x2 + this.xOffset) + HFP_MODEL_ORIGIN,
                    (float) ((double) y2 + this.yOffset) + HFP_MODEL_ORIGIN,
                    (float) ((double) z2 + this.zOffset) + HFP_MODEL_ORIGIN, color, u2, v2, lightmap);
            offset += HFP_VERTEX_STRIDE;
            this.putHfpVertex(offset, (float) ((double) x3 + this.xOffset) + HFP_MODEL_ORIGIN,
                    (float) ((double) y3 + this.yOffset) + HFP_MODEL_ORIGIN,
                    (float) ((double) z3 + this.zOffset) + HFP_MODEL_ORIGIN, color, u3, v3, lightmap);
            this.vertexCount += 4;
            return;
        }

        int vertexSizeInts = this.vertexFormat.getIntegerSize();
        this.growBuffer(this.vertexFormat.getSize() * 4);
        int offset = this.getBufferSize();

        this.putBlockVertex(offset, (float) ((double) x0 + this.xOffset),
                (float) ((double) y0 + this.yOffset), (float) ((double) z0 + this.zOffset), color, u0, v0, lightmap);
        offset += vertexSizeInts;
        this.putBlockVertex(offset, (float) ((double) x1 + this.xOffset),
                (float) ((double) y1 + this.yOffset), (float) ((double) z1 + this.zOffset), color, u1, v1, lightmap);
        offset += vertexSizeInts;
        this.putBlockVertex(offset, (float) ((double) x2 + this.xOffset),
                (float) ((double) y2 + this.yOffset), (float) ((double) z2 + this.zOffset), color, u2, v2, lightmap);
        offset += vertexSizeInts;
        this.putBlockVertex(offset, (float) ((double) x3 + this.xOffset),
                (float) ((double) y3 + this.yOffset), (float) ((double) z3 + this.zOffset), color, u3, v3, lightmap);

        this.vertexCount += 4;
    }

    private void putHfpVertex(int offset, float x, float y, float z, int color, float u, float v, int light) {
        this.byteBuffer.putShort(offset, ModelVertexUtil.denormalizeVertexPositionFloatAsShort(x));
        this.byteBuffer.putShort(offset + 2, ModelVertexUtil.denormalizeVertexPositionFloatAsShort(y));
        this.byteBuffer.putShort(offset + 4, ModelVertexUtil.denormalizeVertexPositionFloatAsShort(z));
        this.byteBuffer.putInt(offset + 8, color);
        this.byteBuffer.putShort(offset + 12, ModelVertexUtil.denormalizeVertexTextureFloatAsShort(u));
        this.byteBuffer.putShort(offset + 14, ModelVertexUtil.denormalizeVertexTextureFloatAsShort(v));
        this.byteBuffer.putInt(offset + 16, ModelVertexUtil.encodeLightMapTexCoord(light));
    }

    private void putBlockVertex(int offset, float x, float y, float z, int color, float u, float v, int light) {
        this.rawFloatBuffer.put(offset, x);
        this.rawFloatBuffer.put(offset + 1, y);
        this.rawFloatBuffer.put(offset + 2, z);
        this.rawIntBuffer.put(offset + 3, color);
        this.rawFloatBuffer.put(offset + 4, u);
        this.rawFloatBuffer.put(offset + 5, v);
        this.rawIntBuffer.put(offset + 6, light);
    }

    public void addParticleVertex(float x, float y, float z, float u, float v,
                                  float r, float g, float b, float a, int light) {
        this.growBuffer(this.vertexFormat.getSize());
        int offset = this.getBufferSize();

        int red = (int) (r * 255.0F) & 255;
        int green = (int) (g * 255.0F) & 255;
        int blue = (int) (b * 255.0F) & 255;
        int alpha = (int) (a * 255.0F) & 255;
        int color = LITTLE_ENDIAN
                ? red | green << 8 | blue << 16 | alpha << 24
                : alpha | red << 24 | green << 16 | blue << 8;

        this.rawFloatBuffer.put(offset, (float) ((double) x + this.xOffset));
        this.rawFloatBuffer.put(offset + 1, (float) ((double) y + this.yOffset));
        this.rawFloatBuffer.put(offset + 2, (float) ((double) z + this.zOffset));
        this.rawFloatBuffer.put(offset + 3, u);
        this.rawFloatBuffer.put(offset + 4, v);
        this.rawIntBuffer.put(offset + 5, color);
        this.rawIntBuffer.put(offset + 6, light);
        ++this.vertexCount;
    }

    public void endVertex() {
        ++this.vertexCount;
        this.growBuffer(this.vertexFormat.getSize());
    }

    public BufferBuilder pos(double x, double y, double z) {
        int i = this.vertexCount * this.vertexFormat.getSize() + this.vertexFormat.getOffset(this.vertexFormatIndex);
        switch (this.vertexFormatElement.getType()) {
            case FLOAT:
                this.byteBuffer.putFloat(i, (float) (x + this.xOffset));
                this.byteBuffer.putFloat(i + 4, (float) (y + this.yOffset));
                this.byteBuffer.putFloat(i + 8, (float) (z + this.zOffset));
                break;
            case UINT:
            case INT:
                this.byteBuffer.putInt(i, Float.floatToRawIntBits((float) (x + this.xOffset)));
                this.byteBuffer.putInt(i + 4, Float.floatToRawIntBits((float) (y + this.yOffset)));
                this.byteBuffer.putInt(i + 8, Float.floatToRawIntBits((float) (z + this.zOffset)));
                break;
            case USHORT:
            case SHORT:
                this.byteBuffer.putShort(i, (short) ((int) (x + this.xOffset)));
                this.byteBuffer.putShort(i + 2, (short) ((int) (y + this.yOffset)));
                this.byteBuffer.putShort(i + 4, (short) ((int) (z + this.zOffset)));
                break;
            case UBYTE:
            case BYTE:
                this.byteBuffer.put(i, (byte) ((int) (x + this.xOffset)));
                this.byteBuffer.put(i + 1, (byte) ((int) (y + this.yOffset)));
                this.byteBuffer.put(i + 2, (byte) ((int) (z + this.zOffset)));
        }

        this.nextVertexFormatIndex();
        return this;
    }

    public void putNormal(float x, float y, float z) {
        int i = (byte) ((int) (x * 127.0F)) & 255;
        int j = (byte) ((int) (y * 127.0F)) & 255;
        int k = (byte) ((int) (z * 127.0F)) & 255;
        int l = i | j << 8 | k << 16;
        int i1 = this.vertexFormat.getSize() >> 2;
        int j1 = (this.vertexCount - 4) * i1 + this.vertexFormat.getNormalOffset() / 4;
        this.rawIntBuffer.put(j1, l);
        this.rawIntBuffer.put(j1 + i1, l);
        this.rawIntBuffer.put(j1 + i1 * 2, l);
        this.rawIntBuffer.put(j1 + i1 * 3, l);
    }

    private void nextVertexFormatIndex() {
        int elementCount = this.vertexFormat.getElementCount();

        do {
            if (++this.vertexFormatIndex >= elementCount) {
                this.vertexFormatIndex -= elementCount;
            }

            this.vertexFormatElement = this.vertexFormat.getElement(this.vertexFormatIndex);
        } while (this.vertexFormatElement.getUsage() == VertexFormatElement.Usage.PADDING);
    }

    public BufferBuilder normal(float x, float y, float z) {
        int i = this.vertexCount * this.vertexFormat.getSize() + this.vertexFormat.getOffset(this.vertexFormatIndex);
        switch (this.vertexFormatElement.getType()) {
            case FLOAT:
                this.byteBuffer.putFloat(i, x);
                this.byteBuffer.putFloat(i + 4, y);
                this.byteBuffer.putFloat(i + 8, z);
                break;
            case UINT:
            case INT:
                this.byteBuffer.putInt(i, (int) x);
                this.byteBuffer.putInt(i + 4, (int) y);
                this.byteBuffer.putInt(i + 8, (int) z);
                break;
            case USHORT:
            case SHORT:
                this.byteBuffer.putShort(i, (short) ((int) x * 32767 & '\uffff'));
                this.byteBuffer.putShort(i + 2, (short) ((int) y * 32767 & '\uffff'));
                this.byteBuffer.putShort(i + 4, (short) ((int) z * 32767 & '\uffff'));
                break;
            case UBYTE:
            case BYTE:
                this.byteBuffer.put(i, (byte) ((int) x * 127 & 255));
                this.byteBuffer.put(i + 1, (byte) ((int) y * 127 & 255));
                this.byteBuffer.put(i + 2, (byte) ((int) z * 127 & 255));
        }

        this.nextVertexFormatIndex();
        return this;
    }

    public void setTranslation(double x, double y, double z) {
        this.xOffset = x;
        this.yOffset = y;
        this.zOffset = z;
    }

    public void finishDrawing() {
        if (!this.isDrawing) {
            throw new IllegalStateException("Not building!");
        } else {
            this.isDrawing = false;
            this.byteBuffer.position(0);
            this.byteBuffer.limit(this.getBufferSizeBytes());
        }
    }

    public ByteBuffer getByteBuffer() {
        return this.byteBuffer;
    }

    public VertexFormat getVertexFormat() {
        return this.vertexFormat;
    }

    public int getVertexCount() {
        return this.vertexCount;
    }

    public int getDrawMode() {
        return this.drawMode;
    }

    public boolean isDirectChunkHfp() {
        return this.directChunkHfp;
    }

    public void putColor4(int argb) {
        for (int i = 0; i < 4; ++i) {
            this.putColor(argb, i + 1);
        }

    }

    public void putColorRGB_F4(float red, float green, float blue) {
        for (int i = 0; i < 4; ++i) {
            this.putColorRGB_F(red, green, blue, i + 1);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public class State {
        private final int[] stateRawBuffer;
        private final VertexFormat stateVertexFormat;
        private final int stateVertexStrideInts;
        private final boolean stateDirectChunkHfp;

        public State(int[] buffer, VertexFormat format, int vertexStrideInts, boolean directChunkHfp) {
            this.stateRawBuffer = buffer;
            this.stateVertexFormat = format;
            this.stateVertexStrideInts = vertexStrideInts;
            this.stateDirectChunkHfp = directChunkHfp;
        }

        public int[] getRawBuffer() {
            return this.stateRawBuffer;
        }

        public int getVertexCount() {
            return this.stateRawBuffer.length / this.stateVertexStrideInts;
        }

        public VertexFormat getVertexFormat() {
            return this.stateVertexFormat;
        }

        public boolean isDirectChunkHfp() {
            return this.stateDirectChunkHfp;
        }
    }
}
