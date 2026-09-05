package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ListenableFuture;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Queue;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.VertexBufferUploader;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderContainer;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ChunkRenderDispatcher {
   private static final Logger LOGGER = LogManager.getLogger();

   private static final int TASK_QUEUE_LIMIT_PER_WORKER = 2;

   private final int countRenderBuilders;
   private final List<Thread> listWorkerThreads = Lists.newArrayList();
   private final List<ChunkRenderWorker> listThreadedWorkers = Lists.newArrayList();
   private final Queue<ChunkRenderTask> queueChunkUpdates = new java.util.ArrayDeque<>();
   private final Queue<RegionRenderCacheBuilder> queueFreeRenderBuilders;
   private final WorldVertexBufferUploader worldVertexUploader = new WorldVertexBufferUploader();
   private final VertexBufferUploader vertexUploader = new VertexBufferUploader();
   private final Queue<ChunkRenderDispatcher.PendingUpload> queueChunkUploads = Queues.newPriorityQueue();
   private final ChunkRenderWorker renderWorker;
   private Vec3d field_217672_l = Vec3d.ZERO;

   public ChunkRenderDispatcher(boolean p_i51518_1_) {
      this.renderWorker = new ChunkRenderWorker(this, new RegionRenderCacheBuilder());
      this.countRenderBuilders = 0;
      this.queueFreeRenderBuilders = new java.util.LinkedList<>();
   }

   public String getDebugInfo() {
      return this.listWorkerThreads.isEmpty() ? String.format("pC: %03d, single-threaded", this.queueChunkUpdates.size()) : String.format("pC: %03d, pU: %02d, aB: %02d", this.queueChunkUpdates.size(), this.queueChunkUploads.size(), this.queueFreeRenderBuilders.size());
   }

   public void func_217669_a(Vec3d p_217669_1_) {
      this.field_217672_l = p_217669_1_;
   }

   public Vec3d func_217671_b() {
      return this.field_217672_l;
   }

   public int getSchedulingBudget() {
      return Math.max(0, TASK_QUEUE_LIMIT_PER_WORKER - this.queueChunkUpdates.size());
   }

   public boolean runChunkUploads(long finishTimeNano) {
      boolean flag = false;
      int processedTasks = 0;

      while(true) {
         boolean flag1 = false;
         if (this.listWorkerThreads.isEmpty()) {
            ChunkRenderTask chunkrendertask = this.pollPendingChunkUpdate();
            if (chunkrendertask != null) {
               try {
                  this.renderWorker.processTask(chunkrendertask);
                  flag1 = true;
                  flag = true;
                  ++processedTasks;
               } catch (InterruptedException var9) {
                  LOGGER.warn("Skipped task due to interrupt");
               }
            }
         }

         int i = 0;
         synchronized(this.queueChunkUploads) {
            while(i < 10) {
               ChunkRenderDispatcher.PendingUpload chunkrenderdispatcher$pendingupload = this.queueChunkUploads.poll();
               if (chunkrenderdispatcher$pendingupload == null) {
                  break;
               }

               chunkrenderdispatcher$pendingupload.uploadTask.run();
               flag1 = true;
               flag = true;
               ++i;
            }
         }

         if (finishTimeNano == 0L || !flag1 || finishTimeNano < Util.nanoTime() || processedTasks >= 4) {
            break;
         }
      }

      return flag;
   }

   public boolean updateChunkLater(ChunkRender chunkRenderer) {

      if (chunkRenderer.trySetEmptyCompiledChunk()) {
         return true;
      }

      if (this.getSchedulingBudget() <= 0) {
         return false;
      }

      boolean flag1;
      try {
         ChunkRenderTask chunkrendertask = chunkRenderer.makeCompileTaskChunk();
         if (chunkrendertask == null) {
            return true;
         }
         boolean flag = this.queueChunkUpdates.offer(chunkrendertask);
         if (!flag) {
            chunkrendertask.finish();
         }

         flag1 = flag;
      } finally {
      }

      return flag1;
   }

   public boolean updateChunkNow(ChunkRender chunkRenderer) {

      boolean flag;
      try {
         ChunkRenderTask chunkrendertask = chunkRenderer.makeCompileTaskChunk();
         if (chunkrendertask == null) {
            return true;
         }

         try {
            this.renderWorker.processTask(chunkrendertask);
         } catch (InterruptedException var7) {
            ;
         }

         flag = true;
      } finally {
      }

      return flag;
   }

   public void stopChunkUpdates() {
      this.clearChunkUpdates();
      List<RegionRenderCacheBuilder> list = Lists.newArrayList();

      while(list.size() != this.countRenderBuilders) {
         this.runChunkUploads(Long.MAX_VALUE);

         try {
            list.add(this.allocateRenderBuilder());
         } catch (InterruptedException var3) {
            ;
         }
      }

      this.queueFreeRenderBuilders.addAll(list);
   }

   public void freeRenderBuilder(RegionRenderCacheBuilder builder) {
      this.queueFreeRenderBuilders.add(builder);
   }

   public RegionRenderCacheBuilder allocateRenderBuilder() throws InterruptedException {
      return this.queueFreeRenderBuilders.poll();
   }

   public ChunkRenderTask getNextChunkUpdate() throws InterruptedException {
      return this.pollPendingChunkUpdate();
   }

   private ChunkRenderTask pollPendingChunkUpdate() {
      ChunkRenderTask task;
      while ((task = this.queueChunkUpdates.poll()) != null && task.isFinished()) {
      }
      return task;
   }

   public boolean updateTransparencyLater(ChunkRender chunkRenderer) {

      if (this.getSchedulingBudget() <= 0) {
         return false;
      }

      boolean flag;
      try {
         ChunkRenderTask chunkrendertask = chunkRenderer.makeCompileTaskTransparency();
         if (chunkrendertask == null) {
            flag = true;
            return flag;
         }

         flag = this.queueChunkUpdates.offer(chunkrendertask);
      } finally {
      }

      return flag;
   }

   public ListenableFuture<Void> uploadChunk(BlockRenderLayer layerIn, BufferBuilder builderIn, ChunkRender renderChunkIn, CompiledChunk compiledChunkIn, double distanceSqIn) {
      if (Minecraft.getInstance().isOnExecutionThread()) {
         if (renderChunkIn instanceof ChunkRenderContainer) {
            ((ChunkRenderContainer) renderChunkIn).upload(layerIn, builderIn);
         } else if (GLX.useVbo()) {
            this.uploadVertexBuffer(builderIn, renderChunkIn.getVertexBufferByLayer(layerIn.ordinal()));
         } else {
            this.func_217670_a(builderIn, ((ListedChunkRender)renderChunkIn).getDisplayList(layerIn, compiledChunkIn));
         }

         builderIn.setTranslation(0.0D, 0.0D, 0.0D);
         return null;
      } else {
         Runnable uploadTask = () -> {
            this.uploadChunk(layerIn, builderIn, renderChunkIn, compiledChunkIn, distanceSqIn);
         };
         synchronized(this.queueChunkUploads) {
            this.queueChunkUploads.add(new ChunkRenderDispatcher.PendingUpload(uploadTask, distanceSqIn));
         }
         return null;
      }
   }

   private void func_217670_a(BufferBuilder p_217670_1_, int p_217670_2_) {
      if (p_217670_2_ != -1) {
         GlStateManager.newList(p_217670_2_, 4864);
         this.worldVertexUploader.draw(p_217670_1_);
         GlStateManager.endList();
      } else {
         p_217670_1_.reset();
      }
   }

   private void uploadVertexBuffer(BufferBuilder bufferBuilderIn, VertexBuffer vertexBufferIn) {
      this.vertexUploader.setVertexBuffer(vertexBufferIn);
      this.vertexUploader.draw(bufferBuilderIn);
   }

   public void clearChunkUpdates() {
      while(!this.queueChunkUpdates.isEmpty()) {
         ChunkRenderTask chunkrendertask = this.queueChunkUpdates.poll();
         if (chunkrendertask != null) {
            chunkrendertask.finish();
         }
      }

   }

   public boolean hasNoChunkUpdates() {
      return this.queueChunkUpdates.isEmpty() && this.queueChunkUploads.isEmpty();
   }

   public void stopWorkerThreads() {
      this.clearChunkUpdates();

      for(ChunkRenderWorker chunkrenderworker : this.listThreadedWorkers) {
         chunkrenderworker.notifyToStop();
      }

      this.queueFreeRenderBuilders.clear();
   }

   @OnlyIn(Dist.CLIENT)
   class PendingUpload implements Comparable<ChunkRenderDispatcher.PendingUpload> {
      public final Runnable uploadTask;
      private final double distanceSq;

      public PendingUpload(Runnable uploadTaskIn, double distanceSqIn) {
         this.uploadTask = uploadTaskIn;
         this.distanceSq = distanceSqIn;
      }

      public int compareTo(ChunkRenderDispatcher.PendingUpload p_compareTo_1_) {
         return Double.compare(this.distanceSq, p_compareTo_1_.distanceSq);
      }
   }
}
