package me.jellysquid.mods.sodium.client.render.chunk.format.hfp;

import me.jellysquid.mods.sodium.client.render.chunk.format.ModelVertexSink;
import me.jellysquid.mods.sodium.client.render.chunk.format.ModelVertexUtil;
import net.lax1dude.eaglercraft.internal.buffer.ByteBuffer;

public class HFPModelVertexBufferWriterNio implements ModelVertexSink {
    private ByteBuffer byteBuffer;
    private int writeOffset;

    public void reset(ByteBuffer buffer) {
        this.byteBuffer = buffer;
        this.writeOffset = 0;
    }

    @Override
    public void writeQuad(float x, float y, float z, int color, float u, float v, int light) {
        this.writeQuadInternal(
                ModelVertexUtil.denormalizeVertexPositionFloatAsShort(x),
                ModelVertexUtil.denormalizeVertexPositionFloatAsShort(y),
                ModelVertexUtil.denormalizeVertexPositionFloatAsShort(z),
                color,
                ModelVertexUtil.denormalizeVertexTextureFloatAsShort(u),
                ModelVertexUtil.denormalizeVertexTextureFloatAsShort(v),
                ModelVertexUtil.encodeLightMapTexCoord(light)
        );
    }

    private void writeQuadInternal(short x, short y, short z, int color, short u, short v, int light) {
        int i = this.writeOffset;

        ByteBuffer buffer = this.byteBuffer;
        buffer.putShort(i, x);
        buffer.putShort(i + 2, y);
        buffer.putShort(i + 4, z);
        buffer.putInt(i + 8, color);
        buffer.putShort(i + 12, u);
        buffer.putShort(i + 14, v);
        buffer.putInt(i + 16, light);

        this.writeOffset += HFPModelVertexType.VERTEX_FORMAT.getStride();
    }

    public int getWriteOffset() {
        return this.writeOffset;
    }
}
