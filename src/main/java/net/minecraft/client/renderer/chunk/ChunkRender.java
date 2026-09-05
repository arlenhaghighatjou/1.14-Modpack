package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.HashSet;
import net.lax1dude.eaglercraft.Random;
import me.jellysquid.mods.sodium.client.util.rand.XoRoShiRoRandom;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChunkRender {
   private volatile World world;
   private final WorldRenderer renderGlobal;
   public static int renderChunksUpdated;
   public CompiledChunk compiledChunk = CompiledChunk.DUMMY;
   private static final Direction[] FACINGS = Direction.values();

   public static enum ShadowFrustumState {
      INSIDE, INTERSECT, OUTSIDE, OUTSIDE_BB
   }

   public int shadowLOD0FrameIndex = -1;
   public int shadowLOD1FrameIndex = -1;
   public int shadowLOD2FrameIndex = -1;
   public ShadowFrustumState shadowLOD0InFrustum = ShadowFrustumState.OUTSIDE;
   public ShadowFrustumState shadowLOD1InFrustum = ShadowFrustumState.OUTSIDE;
   public ShadowFrustumState shadowLOD2InFrustum = ShadowFrustumState.OUTSIDE;

   private ChunkRenderTask compileTask;
   private final Set<TileEntity> setTileEntities = Sets.newHashSetWithExpectedSize(8);
   private final VertexBuffer[] vertexBuffers = new VertexBuffer[BlockRenderLayer._VALUES.length];
   public AxisAlignedBB boundingBox;
   private int frameIndex = -1;
   private boolean needsUpdate = true;
   private final BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos(-1, -1, -1);
   private final BlockPos.MutableBlockPos scratchNeighborPos = new BlockPos.MutableBlockPos();
   private final BlockPos.MutableBlockPos[] mapEnumFacing = Util.make(new BlockPos.MutableBlockPos[6], (p_205125_0_) -> {
      for(int j = 0; j < p_205125_0_.length; ++j) {
         p_205125_0_[j] = new BlockPos.MutableBlockPos();
      }

   });
   private final ChunkRender[] neighborChunks = new ChunkRender[6];
   private static final boolean[] REBUILD_BOOLEAN_CACHE = new boolean[BlockRenderLayer._VALUES.length];
   private static final VisGraph REBUILD_VIS_GRAPH_CACHE = new VisGraph();
   private final BlockPos.MutableBlockPos chunkCachePos1 = new BlockPos.MutableBlockPos();
   private final BlockPos.MutableBlockPos chunkCachePos2 = new BlockPos.MutableBlockPos();
   private final Random rebuildRandom = new XoRoShiRoRandom();
   private boolean needsImmediateUpdate;

   public ChunkRender(World worldIn, WorldRenderer worldRendererIn) {
      this.world = worldIn;
      this.renderGlobal = worldRendererIn;
   }

   private static boolean isChunkEmpty(BlockPos pos, World worldIn) {
      return !worldIn.getChunk(pos.getX() >> 4, pos.getZ() >> 4).isEmpty();
   }

   public boolean shouldStayLoaded() {
      int i = 24;
      if (!(this.getDistanceSq() > 576.0D)) {
         return true;
      } else {
         World world = this.getWorld();
         return isChunkEmpty(this.mapEnumFacing[Direction.WEST.ordinal()], world) && isChunkEmpty(this.mapEnumFacing[Direction.NORTH.ordinal()], world) && isChunkEmpty(this.mapEnumFacing[Direction.EAST.ordinal()], world) && isChunkEmpty(this.mapEnumFacing[Direction.SOUTH.ordinal()], world);
      }
   }

   public boolean setFrameIndex(int frameIndexIn) {
      if (this.frameIndex == frameIndexIn) {
         return false;
      } else {
         this.frameIndex = frameIndexIn;
         return true;
      }
   }

   public VertexBuffer getVertexBufferByLayer(int layer) {
      VertexBuffer vertexBuffer = this.vertexBuffers[layer];
      if (vertexBuffer == null) {
         vertexBuffer = new VertexBuffer(DefaultVertexFormats.BLOCK);
         this.vertexBuffers[layer] = vertexBuffer;
      }
      return vertexBuffer;
   }

   public void setPosition(int x, int y, int z) {
      if (x != this.position.getX() || y != this.position.getY() || z != this.position.getZ()) {
         this.stopCompileTask();
         this.position.setPos(x, y, z);
         this.boundingBox = new AxisAlignedBB((double)x, (double)y, (double)z, (double)(x + 16), (double)(y + 16), (double)(z + 16));

                for(int f = 0; f < FACINGS.length; ++f) {
                   Direction direction = FACINGS[f];
                   this.mapEnumFacing[direction.ordinal()].setPos(this.position).move(direction, 16);
         }

      }
   }

   public void resortTransparency(float x, float y, float z, ChunkRenderTask generator) {
      CompiledChunk compiledchunk = generator.getCompiledChunk();
      if (compiledchunk.getState() != null && !compiledchunk.isLayerEmpty(BlockRenderLayer.TRANSLUCENT)) {
         this.preRenderBlocks(generator.getRegionRenderCacheBuilder().getBuilder(BlockRenderLayer.TRANSLUCENT), this.position);
         generator.getRegionRenderCacheBuilder().getBuilder(BlockRenderLayer.TRANSLUCENT).setVertexState(compiledchunk.getState());
         this.postRenderBlocks(BlockRenderLayer.TRANSLUCENT, x, y, z, generator.getRegionRenderCacheBuilder().getBuilder(BlockRenderLayer.TRANSLUCENT), compiledchunk);
      }
   }

   public void rebuildChunk(float x, float y, float z, ChunkRenderTask generator) {
      CompiledChunk compiledchunk = this.compiledChunk;
      if (compiledchunk == CompiledChunk.DUMMY) {
         compiledchunk = new CompiledChunk();
      } else {
         compiledchunk.reset();
      }
      BlockPos blockpos = this.position.toImmutable();
      World world = this.world;
      if (world != null) {
         if (generator.getStatus() != ChunkRenderTask.Status.COMPILING) {
            return;
         }
         generator.setCompiledChunk(compiledchunk);
         VisGraph visgraph = REBUILD_VIS_GRAPH_CACHE;
         visgraph.reset();
         Set<TileEntity> globalTileEntities = null;
         ChunkRenderCache cache = generator.takeChunkRenderCache();
         if (cache != null) {
            ++renderChunksUpdated;
            boolean[] aboolean = REBUILD_BOOLEAN_CACHE;
            java.util.Arrays.fill(aboolean, false);
            BlockModelRenderer.enableCache(blockpos);
            BlockRendererDispatcher blockrendererdispatcher = this.renderGlobal.getBlockRendererDispatcher();
            TextureAtlasSprite.beginAnimationCollection(compiledchunk.getAnimatedSprites());

            try {
               int baseX = blockpos.getX(), baseY = blockpos.getY(), baseZ = blockpos.getZ();
               int sectionIdx = baseY >> 4;
               int centralCx = (baseX >> 4) - cache.chunkStartX;
               int centralCz = (baseZ >> 4) - cache.chunkStartZ;
               Chunk centralChunk = cache.chunks[centralCx][centralCz];
               ChunkSection centralSection = centralChunk.getSections()[sectionIdx];

               if (centralSection != null && !centralSection.isEmpty()) {
                  BlockPos.MutableBlockPos blockpos2 = new BlockPos.MutableBlockPos();
                  int endX = baseX + 16, endY = baseY + 16, endZ = baseZ + 16;
                  for (int curY = baseY; curY < endY; ++curY) {
                     int subY = curY & 15;
                     for (int curZ = baseZ; curZ < endZ; ++curZ) {
                        int subZ = curZ & 15;
                        for (int curX = baseX; curX < endX; ++curX) {
                           int subX = curX & 15;
                           blockpos2.setPos(curX, curY, curZ);

                           BlockState blockstate = centralSection.getBlockState(subX, subY, subZ);
                           boolean isOpaque = blockstate.isOpaqueCube(cache, blockpos2);
                           if (isOpaque) {
                              visgraph.setOpaqueCube(blockpos2);
                           }

                           if (blockstate.getBlock().hasTileEntity()) {
                              TileEntity tileentity = cache.getTileEntity(blockpos2, Chunk.CreateEntityType.CHECK);
                              if (tileentity != null) {
                                 TileEntityRenderer<TileEntity> tileentityrenderer = TileEntityRendererDispatcher.instance.getRenderer(tileentity);
                                 if (tileentityrenderer != null) {
                                    compiledchunk.addTileEntity(tileentity);
                                    if (tileentityrenderer.isGlobalRenderer(tileentity)) {
                                       if (globalTileEntities == null) {
                                          globalTileEntities = Sets.newHashSetWithExpectedSize(4);
                                       }
                                       globalTileEntities.add(tileentity);
                                    }
                                 }
                              }
                           }

                           if (blockstate.isAir()) continue;

                           IFluidState ifluidstate = blockstate.getFluidState();
                           if (!ifluidstate.isEmpty()) {
                              BlockRenderLayer blockrenderlayer1 = ifluidstate.getRenderLayer();
                              int j = blockrenderlayer1.ordinal();
                              BufferBuilder bufferbuilder = generator.getRegionRenderCacheBuilder().getBuilder(j);
                              if (!compiledchunk.isLayerStarted(blockrenderlayer1)) {
                                 compiledchunk.setLayerStarted(blockrenderlayer1);
                                 this.preRenderBlocks(bufferbuilder, blockpos);
                              }
                              aboolean[j] |= blockrendererdispatcher.renderFluid(blockpos2, cache, bufferbuilder, ifluidstate);
                           }

                           if (blockstate.getRenderType() != BlockRenderType.INVISIBLE) {
                              if (isOpaque) {
                                 boolean allOpaque = true;
                                 for (int f2 = 0; f2 < FACINGS.length; ++f2) {
                                    Direction dir = FACINGS[f2];
                                    this.scratchNeighborPos.setPos(blockpos2).move(dir);
                                    if (!cache.getBlockState(this.scratchNeighborPos).isOpaqueCube(cache, this.scratchNeighborPos)) {
                                       allOpaque = false;
                                       break;
                                    }
                                 }
                                 if (allOpaque) continue;
                              }
                              BlockRenderLayer blockrenderlayer2 = blockstate.getBlock().getRenderLayer();
                              int k = blockrenderlayer2.ordinal();
                              BufferBuilder bufferbuilder1 = generator.getRegionRenderCacheBuilder().getBuilder(k);
                              if (!compiledchunk.isLayerStarted(blockrenderlayer2)) {
                                 compiledchunk.setLayerStarted(blockrenderlayer2);
                                 this.preRenderBlocks(bufferbuilder1, blockpos);
                              }
                              aboolean[k] |= blockrendererdispatcher.func_215330_a(blockstate, blockpos2, cache, bufferbuilder1, this.rebuildRandom);
                           }
                        }
                     }
                  }
               }

               for(BlockRenderLayer blockrenderlayer : BlockRenderLayer._VALUES) {
                  if (aboolean[blockrenderlayer.ordinal()]) {
                     compiledchunk.setLayerUsed(blockrenderlayer);
                  }
                  if (compiledchunk.isLayerStarted(blockrenderlayer)) {
                     this.postRenderBlocks(blockrenderlayer, x, y, z, generator.getRegionRenderCacheBuilder().getBuilder(blockrenderlayer), compiledchunk);
                  }
               }
            } finally {
               TextureAtlasSprite.endAnimationCollection();
               BlockModelRenderer.disableCache();
            }
         }

         visgraph.computeVisibility(compiledchunk.getVisibility());

         if (globalTileEntities == null) {
            if (!this.setTileEntities.isEmpty()) {
               Set<TileEntity> removed = new HashSet<>(this.setTileEntities);
               this.setTileEntities.clear();
               this.renderGlobal.updateTileEntities(removed, Collections.emptySet());
            }
         } else if (!this.setTileEntities.equals(globalTileEntities)) {
            Set<TileEntity> added = new HashSet<>(globalTileEntities);
            added.removeAll(this.setTileEntities);
            Set<TileEntity> removed = new HashSet<>(this.setTileEntities);
            removed.removeAll(globalTileEntities);
            this.setTileEntities.clear();
            this.setTileEntities.addAll(globalTileEntities);
            this.renderGlobal.updateTileEntities(removed, added);
         }

      }
   }

   protected void finishCompileTask() {

      try {
         if (this.compileTask != null && this.compileTask.getStatus() != ChunkRenderTask.Status.DONE) {
            this.compileTask.finish();
            this.compileTask = null;
         }
      } finally {
      }

   }

   public Object getLockCompileTask() {
      return null;
   }
   public boolean trySetEmptyCompiledChunk() {
      World currentWorld = this.world;
      if (currentWorld == null) {
         return false;
      }

      BlockPos blockpos = this.position;
      int sectionY = blockpos.getY() >> 4;
      Chunk chunk = currentWorld.getChunk(blockpos.getX() >> 4, blockpos.getZ() >> 4);
      ChunkSection[] sections = chunk.getSections();
      ChunkSection section = sectionY >= 0 && sectionY < sections.length
            ? sections[sectionY] : Chunk.EMPTY_SECTION;
      if (!ChunkSection.isEmpty(section)) {
         return false;
      }

      this.finishCompileTask();
      CompiledChunk compiledchunk = this.compiledChunk;
      if (compiledchunk == CompiledChunk.DUMMY) {
         compiledchunk = new CompiledChunk();
      } else {
         compiledchunk.reset();
      }
      compiledchunk.getVisibility().setAllVisible(true);

      if (!this.setTileEntities.isEmpty()) {
         Set<TileEntity> removed = new HashSet<>(this.setTileEntities);
         this.setTileEntities.clear();
         this.renderGlobal.updateTileEntities(removed, Collections.emptySet());
      }

      this.setCompiledChunk(compiledchunk);
      return true;
   }

   public ChunkRenderTask makeCompileTaskChunk() {

      ChunkRenderTask chunkrendertask;
      try {
         if (this.trySetEmptyCompiledChunk()) {
            return null;
         }

         this.finishCompileTask();
         BlockPos blockpos = this.position.toImmutable();
         this.chunkCachePos1.setPos(blockpos).move(-1, -1, -1);
         this.chunkCachePos2.setPos(blockpos).move(16, 16, 16);
         ChunkRenderCache chunkrendercache = ChunkRenderCache.generateCache(this.world, this.chunkCachePos1, this.chunkCachePos2, 1);

         this.compileTask = new ChunkRenderTask(this, ChunkRenderTask.Type.REBUILD_CHUNK, this.getDistanceSq(), chunkrendercache);
         chunkrendertask = this.compileTask;
      } finally {
      }

      return chunkrendertask;
   }

   public ChunkRenderTask makeCompileTaskTransparency() {

      ChunkRenderTask chunkrendertask;
      try {
         if (this.compileTask == null || this.compileTask.getStatus() != ChunkRenderTask.Status.PENDING) {
            if (this.compileTask != null && this.compileTask.getStatus() != ChunkRenderTask.Status.DONE) {
               this.compileTask.finish();
               this.compileTask = null;
            }

            this.compileTask = new ChunkRenderTask(this, ChunkRenderTask.Type.RESORT_TRANSPARENCY, this.getDistanceSq(), (ChunkRenderCache)null);
            this.compileTask.setCompiledChunk(this.compiledChunk);
            chunkrendertask = this.compileTask;
            return chunkrendertask;
         }

         chunkrendertask = null;
      } finally {
      }

      return chunkrendertask;
   }

   protected double getDistanceSq() {
      ActiveRenderInfo activerenderinfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
      double d0 = this.boundingBox.minX + 8.0D - activerenderinfo.getProjectedView().x;
      double d1 = this.boundingBox.minY + 8.0D - activerenderinfo.getProjectedView().y;
      double d2 = this.boundingBox.minZ + 8.0D - activerenderinfo.getProjectedView().z;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   private void preRenderBlocks(BufferBuilder bufferBuilderIn, BlockPos pos) {
      bufferBuilderIn.begin(7, DefaultVertexFormats.BLOCK);
      bufferBuilderIn.setTranslation((double)(-this.getBufferOriginX()), (double)(-this.getBufferOriginY()), (double)(-this.getBufferOriginZ()));
   }

   protected int getBufferOriginX() {
      return this.position.getX();
   }

   protected int getBufferOriginY() {
      return this.position.getY();
   }

   protected int getBufferOriginZ() {
      return this.position.getZ();
   }

   private void postRenderBlocks(BlockRenderLayer layer, float x, float y, float z, BufferBuilder bufferBuilderIn, CompiledChunk compiledChunkIn) {
      bufferBuilderIn.finishDrawing();
   }

   public CompiledChunk getCompiledChunk() {
      return this.compiledChunk;
   }

   public void setCompiledChunk(CompiledChunk compiledChunkIn) {

      try {
         this.compiledChunk = compiledChunkIn;
      } finally {
      }

   }

   public void stopCompileTask() {
      this.finishCompileTask();
      this.compiledChunk = CompiledChunk.DUMMY;
      this.needsUpdate = true;
   }

   public void deleteGlResources() {
      this.stopCompileTask();
      this.world = null;

      for(int i = 0; i < BlockRenderLayer._VALUES.length; ++i) {
         if (this.vertexBuffers[i] != null) {
            this.vertexBuffers[i].deleteGlBuffers();
         }
      }

   }

   public BlockPos getPosition() {
      return this.position;
   }

   public void setNeedsUpdate(boolean immediate) {
      if (this.needsUpdate) {
         immediate |= this.needsImmediateUpdate;
      }

      this.needsUpdate = true;
      this.needsImmediateUpdate = immediate;
   }

   public void clearNeedsUpdate() {
      this.needsUpdate = false;
      this.needsImmediateUpdate = false;
   }

   public boolean needsUpdate() {
      return this.needsUpdate;
   }

   public boolean needsImmediateUpdate() {
      return this.needsUpdate && this.needsImmediateUpdate;
   }

   public BlockPos getBlockPosOffset16(Direction facing) {
      return this.mapEnumFacing[facing.ordinal()];
   }

   public ChunkRender getNeighborChunk(Direction facing) {
      return this.neighborChunks[facing.ordinal()];
   }

   public void setNeighborChunk(Direction facing, ChunkRender chunk) {
      this.neighborChunks[facing.ordinal()] = chunk;
   }

   public World getWorld() {
      return this.world;
   }
}
