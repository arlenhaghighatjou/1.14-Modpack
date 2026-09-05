package me.jellysquid.mods.sodium.client.model.light.cache;

import java.util.Arrays;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;

/**
 * A light data cache which uses a flat-array to store the light data for the
 * blocks in a given chunk section and its direct neighbors. This is
 * considerably faster than using a hash table to look up values for a given
 * block position and can be re-used between chunk builds to avoid allocations.
 *
 * Ported from Sodium's ArrayLightDataCache. The packed word contains the two
 * values consumed by Minecraft 1.14.4's ambient-occlusion renderer: packed
 * light in the high word and ambient-occlusion brightness in the low word.
 */
public final class ArrayLightDataCache {
    private static final int NEIGHBOR_BLOCK_RADIUS = 2;
    private static final int BLOCK_LENGTH = 16 + (NEIGHBOR_BLOCK_RADIUS * 2);
    private static final int BLOCK_PLANE_SIZE = BLOCK_LENGTH * BLOCK_LENGTH;

    private final long[] light = new long[BLOCK_LENGTH * BLOCK_LENGTH * BLOCK_LENGTH];

    private int xOffset;
    private int yOffset;
    private int zOffset;

    public void reset(BlockPos origin) {
        this.xOffset = origin.getX() - NEIGHBOR_BLOCK_RADIUS;
        this.yOffset = origin.getY() - NEIGHBOR_BLOCK_RADIUS;
        this.zOffset = origin.getZ() - NEIGHBOR_BLOCK_RADIUS;
        Arrays.fill(this.light, 0L);
    }

    private int index(BlockPos pos) {
        int x = pos.getX() - this.xOffset;
        int y = pos.getY() - this.yOffset;
        int z = pos.getZ() - this.zOffset;
        return z * BLOCK_PLANE_SIZE + y * BLOCK_LENGTH + x;
    }

    public long get(BlockState state, IEnviromentBlockReader world, BlockPos pos) {
        int index = this.index(pos);
        long word = this.light[index];

        if (word == 0L) {
            int packedLight = state.getPackedLightmapCoords(world, pos);
            int ambientOcclusion = Float.floatToRawIntBits(state.func_215703_d(world, pos));
            this.light[index] = word = (long) packedLight << 32 | (long) ambientOcclusion & 4294967295L;
        }

        return word;
    }
}
