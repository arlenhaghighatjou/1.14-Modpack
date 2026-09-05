package me.jellysquid.mods.sodium.client.render.chunk.region;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.util.math.SectionPos;

public class ChunkRegionManager<T> {
    public static final int BUFFER_WIDTH = 8;
    public static final int BUFFER_HEIGHT = 4;
    public static final int BUFFER_LENGTH = 8;

    public static final int BUFFER_SIZE = BUFFER_WIDTH * BUFFER_HEIGHT * BUFFER_LENGTH;

    private static final int BUFFER_WIDTH_SH = Integer.bitCount(BUFFER_WIDTH - 1);
    private static final int BUFFER_HEIGHT_SH = Integer.bitCount(BUFFER_HEIGHT - 1);
    private static final int BUFFER_LENGTH_SH = Integer.bitCount(BUFFER_LENGTH - 1);

    private final Long2ObjectOpenHashMap<ChunkRegion<T>> regions = new Long2ObjectOpenHashMap<>();

    public ChunkRegion<T> getRegion(int x, int y, int z) {
        return this.regions.get(getRegionKey(x, y, z));
    }

    public ChunkRegion<T> getOrCreateRegion(int x, int y, int z) {
        long key = getRegionKey(x, y, z);

        ChunkRegion<T> region = this.regions.get(key);

        if (region == null) {
            this.regions.put(key, region = new ChunkRegion<>(BUFFER_SIZE,
                    getRegionOriginX(x), getRegionOriginY(y), getRegionOriginZ(z)));
        }

        return region;
    }

    public static long getRegionKey(int x, int y, int z) {
        return SectionPos.asLong(x >> BUFFER_WIDTH_SH, y >> BUFFER_HEIGHT_SH, z >> BUFFER_LENGTH_SH);
    }

    public static int getRegionOriginX(int x) {
        return (x >> BUFFER_WIDTH_SH) << (BUFFER_WIDTH_SH + 4);
    }

    public static int getRegionOriginY(int y) {
        return (y >> BUFFER_HEIGHT_SH) << (BUFFER_HEIGHT_SH + 4);
    }

    public static int getRegionOriginZ(int z) {
        return (z >> BUFFER_LENGTH_SH) << (BUFFER_LENGTH_SH + 4);
    }

    public void delete() {
        for (ChunkRegion<T> region : this.regions.values()) {
            region.deleteResources();
        }

        this.regions.clear();
    }

    public void cleanup() {
        for (ObjectIterator<ChunkRegion<T>> iterator = this.regions.values().iterator(); iterator.hasNext(); ) {
            ChunkRegion<T> region = iterator.next();

            if (region.isArenaEmpty()) {
                region.deleteResources();

                iterator.remove();
            }
        }
    }

    public int getAllocatedRegionCount() {
        return this.regions.size();
    }
}
