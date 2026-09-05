package me.jellysquid.mods.sodium.client.model.quad.properties;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.util.Direction;

/**
 * Cached properties of a baked model quad used by Sodium's light pipeline.
 */
public final class ModelQuadFlags {
    /** Indicates that the quad is aligned to the block grid. */
    public static final int IS_ALIGNED = 0b01;

    /** Indicates that the quad does not fully cover its model face. */
    public static final int IS_PARTIAL = 0b10;

    private ModelQuadFlags() {
    }

    public static boolean contains(int flags, int mask) {
        return (flags & mask) != 0;
    }

    /**
     * Calculates the properties of the given quad once when it is baked. This
     * lets the light pipeline skip bounds calculation and interpolation for the
     * overwhelmingly common aligned, full block face.
     */
    public static int getQuadFlags(BakedQuad bakedQuad) {
        int[] vertices = bakedQuad.getVertexData();
        Direction face = bakedQuad.getFace();
        int vertexSize = vertices.length / 4;

        float minX = 32.0F;
        float minY = 32.0F;
        float minZ = 32.0F;
        float maxX = -32.0F;
        float maxY = -32.0F;
        float maxZ = -32.0F;

        for (int i = 0; i < 4; ++i) {
            int offset = i * vertexSize;
            float x = Float.intBitsToFloat(vertices[offset]);
            float y = Float.intBitsToFloat(vertices[offset + 1]);
            float z = Float.intBitsToFloat(vertices[offset + 2]);

            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            minZ = Math.min(minZ, z);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            maxZ = Math.max(maxZ, z);
        }

        boolean partial = false;
        boolean aligned = false;

        switch (face) {
            case DOWN:
                aligned = minY == maxY && minY < 0.0001F;
                break;
            case UP:
                aligned = minY == maxY && maxY > 0.9999F;
                break;
            case NORTH:
                aligned = minZ == maxZ && minZ < 0.0001F;
                break;
            case SOUTH:
                aligned = minZ == maxZ && maxZ > 0.9999F;
                break;
            case WEST:
                aligned = minX == maxX && minX < 0.0001F;
                break;
            case EAST:
                aligned = minX == maxX && maxX > 0.9999F;
                break;
        }

        switch (face.getAxis()) {
            case X:
                partial = minY >= 0.0001F || minZ >= 0.0001F || maxY <= 0.9999F || maxZ <= 0.9999F;
                break;
            case Y:
                partial = minX >= 0.0001F || minZ >= 0.0001F || maxX <= 0.9999F || maxZ <= 0.9999F;
                break;
            case Z:
                partial = minX >= 0.0001F || minY >= 0.0001F || maxX <= 0.9999F || maxY <= 0.9999F;
                break;
        }

        int flags = 0;
        if (partial) {
            flags |= IS_PARTIAL;
        }
        if (aligned) {
            flags |= IS_ALIGNED;
        }
        return flags;
    }
}
