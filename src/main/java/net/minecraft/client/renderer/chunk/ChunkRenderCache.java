package net.minecraft.client.renderer.chunk;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChunkRenderCache implements IEnviromentBlockReader {
    protected final int chunkStartX;
    protected final int chunkStartZ;
    protected final BlockPos cacheStartPos;
    protected final int cacheSizeX;
    protected final int cacheSizeY;
    protected final int cacheSizeZ;
    protected final Chunk[][] chunks;
    protected final World world;

    public static ChunkRenderCache generateCache(World worldIn, BlockPos from, BlockPos to, int padding) {
        int i = from.getX() - padding >> 4;
        int j = from.getZ() - padding >> 4;
        int k = to.getX() + padding >> 4;
        int l = to.getZ() + padding >> 4;
        Chunk[][] achunk = new Chunk[k - i + 1][l - j + 1];

        for (int i1 = i; i1 <= k; ++i1) {
            for (int j1 = j; j1 <= l; ++j1) {
                achunk[i1 - i][j1 - j] = worldIn.getChunk(i1, j1);
            }
        }

        boolean flag = true;

        for (int l1 = from.getX() >> 4; l1 <= to.getX() >> 4; ++l1) {
            for (int k1 = from.getZ() >> 4; k1 <= to.getZ() >> 4; ++k1) {
                Chunk chunk = achunk[l1 - i][k1 - j];
                if (!chunk.isEmptyBetween(from.getY(), to.getY())) {
                    flag = false;
                }
            }
        }

        if (flag) {
            return null;
        } else {
            int i2 = 1;
            BlockPos blockpos = from.add(-1, -1, -1);
            BlockPos blockpos1 = to.add(1, 1, 1);
            return new ChunkRenderCache(worldIn, i, j, achunk, blockpos, blockpos1);
        }
    }

    public ChunkRenderCache(World worldIn, int chunkStartXIn, int chunkStartZIn, Chunk[][] chunksIn, BlockPos startPos, BlockPos endPos) {
        this.world = worldIn;
        this.chunkStartX = chunkStartXIn;
        this.chunkStartZ = chunkStartZIn;
        this.chunks = chunksIn;
        this.cacheStartPos = startPos;
        this.cacheSizeX = endPos.getX() - startPos.getX() + 1;
        this.cacheSizeY = endPos.getY() - startPos.getY() + 1;
        this.cacheSizeZ = endPos.getZ() - startPos.getZ() + 1;
    }

    public BlockState getBlockState(BlockPos pos) {
        int i = (pos.getX() >> 4) - this.chunkStartX;
        int j = (pos.getZ() >> 4) - this.chunkStartZ;
        return this.chunks[i][j].getBlockState(pos);
    }

    public IFluidState getFluidState(BlockPos pos) {
        int i = (pos.getX() >> 4) - this.chunkStartX;
        int j = (pos.getZ() >> 4) - this.chunkStartZ;
        return this.chunks[i][j].getFluidState(pos);
    }

    @OnlyIn(Dist.CLIENT)
    public int getCombinedLight(BlockPos pos, int minLight) {
        int i = this.getLightFor(LightType.SKY, pos);
        int j = this.getLightFor(LightType.BLOCK, pos);
        if (j < minLight) {
            j = minLight;
        }
        return i << 20 | j << 4;
    }

    public int getLightFor(LightType type, BlockPos pos) {
        return this.world.getLightFor(type, pos);
    }

    public Biome getBiome(BlockPos pos) {
        int i = (pos.getX() >> 4) - this.chunkStartX;
        int j = (pos.getZ() >> 4) - this.chunkStartZ;
        return this.chunks[i][j].getBiome(pos);
    }

    public TileEntity getTileEntity(BlockPos pos) {
        return this.getTileEntity(pos, Chunk.CreateEntityType.IMMEDIATE);
    }

    public TileEntity getTileEntity(BlockPos pos, Chunk.CreateEntityType creationType) {
        int i = (pos.getX() >> 4) - this.chunkStartX;
        int j = (pos.getZ() >> 4) - this.chunkStartZ;
        return this.chunks[i][j].getTileEntity(pos, creationType);
    }
}
