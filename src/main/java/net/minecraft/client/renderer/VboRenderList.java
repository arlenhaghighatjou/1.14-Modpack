package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.lax1dude.eaglercraft.opengl.FixedFunctionPipeline;
import net.lax1dude.eaglercraft.opengl.FixedFunctionShader.FixedFunctionState;
import net.minecraft.client.renderer.chunk.ChunkRender;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VboRenderList extends AbstractChunkRenderContainer {

   private static final int ATTRIB_BITS =
         FixedFunctionState.STATE_HAS_ATTRIB_TEXTURE |
         FixedFunctionState.STATE_HAS_ATTRIB_COLOR |
         FixedFunctionState.STATE_HAS_ATTRIB_LIGHTMAP;

   public void renderChunkLayer(BlockRenderLayer layer) {
      if (this.initialized) {
         for(ChunkRender chunkrender : this.renderChunks) {
            VertexBuffer vertexbuffer = chunkrender.getVertexBufferByLayer(layer.ordinal());
            GlStateManager.pushMatrix();
            this.preRenderChunk(chunkrender);
            FixedFunctionPipeline.bindExtensionPipelineForVBO(ATTRIB_BITS);
            vertexbuffer.bindBuffer();
            this.setupArrayPointers();
            vertexbuffer.drawArrays(7);
            GlStateManager.popMatrix();
         }

         VertexBuffer.unbindBuffer();
         GlStateManager.clearCurrentColor();
         this.renderChunks.clear();
      }
   }

   private void setupArrayPointers() {
      GlStateManager.vertexPointer(3, 5126, 28, 0);
      GlStateManager.colorPointer(4, 5121, 28, 12);
      GlStateManager.texCoordPointer(2, 5126, 28, 16);
      GLX.glClientActiveTexture(GLX.GL_TEXTURE1);
      GlStateManager.texCoordPointer(2, 5122, 28, 24);
      GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
   }
}
