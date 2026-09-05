package net.minecraft.util.math;

import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ChunkPos {
   public static final long SENTINEL = asLong(1875016, 1875016);
   public final int x;
   public final int z;

   public ChunkPos(int x, int z) {
      this.x = x;
      this.z = z;
   }

   public ChunkPos(BlockPos pos) {
      this.x = pos.getX() >> 4;
      this.z = pos.getZ() >> 4;
   }

   public ChunkPos(long longIn) {
      this.x = (int)longIn;
      this.z = (int)(longIn >> 32);
   }

   public long asLong() {
      return asLong(this.x, this.z);
   }

   public static long asLong(int x, int z) {
      return (long)x & 4294967295L | ((long)z & 4294967295L) << 32;
   }

   public static int getX(long p_212578_0_) {
      return (int)(p_212578_0_ & 4294967295L);
   }

   public static int getZ(long p_212579_0_) {
      return (int)(p_212579_0_ >>> 32 & 4294967295L);
   }

   public int hashCode() {
      int i = 1664525 * this.x + 1013904223;
      int j = 1664525 * (this.z ^ -559038737) + 1013904223;
      return i ^ j;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof ChunkPos)) {
         return false;
      } else {
         ChunkPos chunkpos = (ChunkPos)p_equals_1_;
         return this.x == chunkpos.x && this.z == chunkpos.z;
      }
   }

   public int getXStart() {
      return this.x << 4;
   }

   public int getZStart() {
      return this.z << 4;
   }

   public int getXEnd() {
      return (this.x << 4) + 15;
   }

   public int getZEnd() {
      return (this.z << 4) + 15;
   }

   public int getRegionCoordX() {
      return this.x >> 5;
   }

   public int getRegionCoordZ() {
      return this.z >> 5;
   }

   public int getRegionPositionX() {
      return this.x & 31;
   }

   public int getRegionPositionZ() {
      return this.z & 31;
   }

   public BlockPos getBlock(int x, int y, int z) {
      return new BlockPos((this.x << 4) + x, y, (this.z << 4) + z);
   }

   public String toString() {
      return "[" + this.x + ", " + this.z + "]";
   }

   public BlockPos asBlockPos() {
      return new BlockPos(this.x << 4, 0, this.z << 4);
   }

   public static Stream<ChunkPos> getAllInBox(ChunkPos center, int radius) {
      return getAllInBox(new ChunkPos(center.x - radius, center.z - radius), new ChunkPos(center.x + radius, center.z + radius));
   }

   public static Stream<ChunkPos> getAllInBox(final ChunkPos p_222239_0_, final ChunkPos p_222239_1_) {
      int i = Math.abs(p_222239_0_.x - p_222239_1_.x) + 1;
      int j = Math.abs(p_222239_0_.z - p_222239_1_.z) + 1;
      final int k = p_222239_0_.x < p_222239_1_.x ? 1 : -1;
      final int l = p_222239_0_.z < p_222239_1_.z ? 1 : -1;
      java.util.List<ChunkPos> list = new java.util.ArrayList<>(i * j);
      ChunkPos current = p_222239_0_;
      while (true) {
         list.add(current);
         int i1 = current.x;
         int j1 = current.z;
         if (i1 == p_222239_1_.x && j1 == p_222239_1_.z) {
            break;
         }
         if (i1 == p_222239_1_.x) {
            current = new ChunkPos(p_222239_0_.x, j1 + l);
         } else {
            current = new ChunkPos(i1 + k, j1);
         }
      }
      return list.stream();
   }
}
