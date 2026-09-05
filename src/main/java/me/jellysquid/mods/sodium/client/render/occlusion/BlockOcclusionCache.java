package me.jellysquid.mods.sodium.client.render.occlusion;

import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class BlockOcclusionCache {
    private static final byte UNCACHED_VALUE = (byte) 127;
    private static final int CACHE_SIZE = 2048;

    private final Object2ByteLinkedOpenHashMap<CachedOcclusionShapeTest> map =
            new Object2ByteLinkedOpenHashMap<CachedOcclusionShapeTest>(CACHE_SIZE, 0.5F) {
                protected void rehash(int newN) {
                }
            };
    private final CachedOcclusionShapeTest cachedTest = new CachedOcclusionShapeTest();
    private final BlockPos.MutableBlockPos cpos = new BlockPos.MutableBlockPos();

    public BlockOcclusionCache() {
        this.map.defaultReturnValue(UNCACHED_VALUE);
    }

    public boolean shouldDrawSide(BlockState selfState, IBlockReader view, BlockPos pos, Direction facing) {
        BlockPos.MutableBlockPos adjPos = this.cpos;
        adjPos.setPos(pos).move(facing);
        BlockState adjState = view.getBlockState(adjPos);

        if (selfState.isSideInvisible(adjState, facing)) {
            return false;
        } else if (selfState.isOpaqueCube(view, pos) && adjState.isOpaqueCube(view, adjPos)) {
            return false;
        } else if (adjState.isSolid()) {
            VoxelShape adjShape = adjState.func_215702_a(view, adjPos, facing.getOpposite());

            if (adjShape == VoxelShapes.fullCube()) {
                return false;
            }

            return this.calculate(selfState.func_215702_a(view, pos, facing), adjShape);
        } else {
            return true;
        }
    }

    private boolean calculate(VoxelShape selfShape, VoxelShape adjShape) {
        CachedOcclusionShapeTest cache = this.cachedTest;
        cache.a = selfShape;
        cache.b = adjShape;
        cache.updateHash();

        byte cached = this.map.getAndMoveToFirst(cache);

        if (cached != UNCACHED_VALUE) {
            return cached == 1;
        }

        boolean result = VoxelShapes.compare(selfShape, adjShape, IBooleanFunction.ONLY_FIRST);

        if (this.map.size() >= CACHE_SIZE) {
            this.map.removeLastByte();
        }

        this.map.putAndMoveToFirst(cache.copy(), (byte) (result ? 1 : 0));
        return result;
    }

    private static final class CachedOcclusionShapeTest {
        private VoxelShape a;
        private VoxelShape b;
        private int hash;

        private CachedOcclusionShapeTest() {
        }

        private CachedOcclusionShapeTest(VoxelShape a, VoxelShape b, int hash) {
            this.a = a;
            this.b = b;
            this.hash = hash;
        }

        private void updateHash() {
            int result = System.identityHashCode(this.a);
            this.hash = 31 * result + System.identityHashCode(this.b);
        }

        private CachedOcclusionShapeTest copy() {
            return new CachedOcclusionShapeTest(this.a, this.b, this.hash);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof CachedOcclusionShapeTest)) {
                return false;
            }

            CachedOcclusionShapeTest other = (CachedOcclusionShapeTest) obj;
            return this.a == other.a && this.b == other.b;
        }

        public int hashCode() {
            return this.hash;
        }
    }
}
