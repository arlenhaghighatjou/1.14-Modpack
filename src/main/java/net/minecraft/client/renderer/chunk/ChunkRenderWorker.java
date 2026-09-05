package net.minecraft.client.renderer.chunk;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ChunkRenderWorker implements Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ChunkRenderDispatcher chunkRenderDispatcher;
   private final RegionRenderCacheBuilder regionRenderCacheBuilder;
   private boolean shouldRun = true;

   public ChunkRenderWorker(ChunkRenderDispatcher chunkRenderDispatcherIn) {
      this(chunkRenderDispatcherIn, (RegionRenderCacheBuilder)null);
   }

   public ChunkRenderWorker(ChunkRenderDispatcher chunkRenderDispatcherIn,  RegionRenderCacheBuilder regionRenderCacheBuilderIn) {
      this.chunkRenderDispatcher = chunkRenderDispatcherIn;
      this.regionRenderCacheBuilder = regionRenderCacheBuilderIn;
   }

   public void run() {
      while(this.shouldRun) {
         try {
            this.processTask(this.chunkRenderDispatcher.getNextChunkUpdate());
         } catch (InterruptedException var3) {
            LOGGER.debug("Stopping chunk worker due to interrupt");
            return;
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Batching chunks");
            Minecraft.getInstance().crashed(Minecraft.getInstance().addGraphicsAndWorldToCrashReport(crashreport));
            return;
         }
      }

   }

   void processTask(final ChunkRenderTask generator) throws InterruptedException {
      ChunkRender chunk = generator.getRenderChunk();
      generator.setStatus(ChunkRenderTask.Status.COMPILING);
      RegionRenderCacheBuilder builder = this.getRegionRenderCacheBuilder();
      generator.setRegionRenderCacheBuilder(builder);

      Vec3d cameraPos = this.chunkRenderDispatcher.func_217671_b();
      float camX = (float) cameraPos.x;
      float camY = (float) cameraPos.y;
      float camZ = (float) cameraPos.z;
      ChunkRenderTask.Type type = generator.getType();
      if (type == ChunkRenderTask.Type.REBUILD_CHUNK) {
         chunk.rebuildChunk(camX, camY, camZ, generator);
      } else if (type == ChunkRenderTask.Type.RESORT_TRANSPARENCY) {
         chunk.resortTransparency(camX, camY, camZ, generator);
      }

      generator.setStatus(ChunkRenderTask.Status.UPLOADING);
      CompiledChunk compiled = generator.getCompiledChunk();
      if (compiled != null) {
         if (type == ChunkRenderTask.Type.REBUILD_CHUNK) {
            for (BlockRenderLayer layer : BlockRenderLayer._VALUES) {
               if (compiled.isLayerStarted(layer)) {
                  this.chunkRenderDispatcher.uploadChunk(layer, builder.getBuilder(layer), chunk, compiled, generator.getDistanceSq());
               }
            }
         } else if (type == ChunkRenderTask.Type.RESORT_TRANSPARENCY) {
            this.chunkRenderDispatcher.uploadChunk(BlockRenderLayer.TRANSLUCENT, builder.getBuilder(BlockRenderLayer.TRANSLUCENT), chunk, compiled, generator.getDistanceSq());
         }
         chunk.setCompiledChunk(compiled);
      }

      this.freeRenderBuilder(builder);
      generator.setStatus(ChunkRenderTask.Status.DONE);
      generator.finish();
   }

   private RegionRenderCacheBuilder getRegionRenderCacheBuilder() throws InterruptedException {
      return this.regionRenderCacheBuilder != null ? this.regionRenderCacheBuilder : this.chunkRenderDispatcher.allocateRenderBuilder();
   }

   private void freeRenderBuilder(RegionRenderCacheBuilder builder) {
      if (builder != this.regionRenderCacheBuilder) {
         this.chunkRenderDispatcher.freeRenderBuilder(builder);
      }
   }

   public void notifyToStop() {
      this.shouldRun = false;
   }
}
