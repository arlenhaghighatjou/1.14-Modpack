package me.jellysquid.mods.sodium.client.render.chunk.format;

public interface ModelVertexSink {
    void writeQuad(float x, float y, float z, int color, float u, float v, int light);
}
