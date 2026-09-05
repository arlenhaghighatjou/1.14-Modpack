package me.jellysquid.mods.sodium.client.render.chunk.format.hfp;

import me.jellysquid.mods.sodium.client.gl.attribute.GlVertexAttributeFormat;
import me.jellysquid.mods.sodium.client.gl.attribute.GlVertexFormat;
import me.jellysquid.mods.sodium.client.render.chunk.format.ChunkMeshAttribute;

/**
 * Uses half-precision floating point numbers to represent position coordinates and normalized unsigned shorts for
 * texture coordinates. All texel positions in the block diffuse texture atlas can be exactly mapped (including
 * their centering offset), as the
 */
public class HFPModelVertexType {
    public static final GlVertexFormat<ChunkMeshAttribute> VERTEX_FORMAT = GlVertexFormat.builder(ChunkMeshAttribute.class, 20)
            .addElement(ChunkMeshAttribute.POSITION, 0, GlVertexAttributeFormat.UNSIGNED_SHORT, 3, false)
            .addElement(ChunkMeshAttribute.COLOR, 8, GlVertexAttributeFormat.UNSIGNED_BYTE, 4, true)
            .addElement(ChunkMeshAttribute.TEXTURE, 12, GlVertexAttributeFormat.UNSIGNED_SHORT, 2, false)
            .addElement(ChunkMeshAttribute.LIGHT, 16, GlVertexAttributeFormat.UNSIGNED_SHORT, 2, true)
            .build();

    public static final float MODEL_SCALE = (256.0f / 65536.0f);
    public static final float TEXTURE_SCALE = (1.0f / 32768.0f);

    public GlVertexFormat<ChunkMeshAttribute> getCustomVertexFormat() {
        return VERTEX_FORMAT;
    }

    public float getModelScale() {
        return MODEL_SCALE;
    }

    public float getTextureScale() {
        return TEXTURE_SCALE;
    }
}
