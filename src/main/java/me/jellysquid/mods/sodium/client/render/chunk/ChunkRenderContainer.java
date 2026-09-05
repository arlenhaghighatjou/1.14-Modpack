package me.jellysquid.mods.sodium.client.render.chunk;

import me.jellysquid.mods.sodium.client.render.chunk.backends.multidraw.MultidrawChunkRenderBackend;
import me.jellysquid.mods.sodium.client.render.chunk.backends.multidraw.MultidrawGraphicsState;
import me.jellysquid.mods.sodium.client.render.chunk.region.ChunkRegionManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.ChunkRender;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.World;

public class ChunkRenderContainer extends ChunkRender {
    private final MultidrawChunkRenderBackend backend;
    private final MultidrawGraphicsState[] graphicsStates = new MultidrawGraphicsState[BlockRenderLayer._VALUES.length];

    public ChunkRenderContainer(World world, WorldRenderer worldRenderer, MultidrawChunkRenderBackend backend) {
        super(world, worldRenderer);
        this.backend = backend;
    }

    public void upload(BlockRenderLayer layer, BufferBuilder builder) {
        int index = layer.ordinal();
        this.graphicsStates[index] = this.backend.upload(this, builder, this.graphicsStates[index]);
    }

    public MultidrawGraphicsState getGraphicsState(BlockRenderLayer layer) {
        return this.graphicsStates[layer.ordinal()];
    }

    private void deleteGraphicsState() {
        for (int i = 0; i < this.graphicsStates.length; ++i) {
            MultidrawGraphicsState state = this.graphicsStates[i];
            if (state != null) {
                this.backend.deleteGraphicsState(state);
                this.graphicsStates[i] = null;
            }
        }
    }

    @Override
    public void setPosition(int x, int y, int z) {
        if (x != this.getPosition().getX() || y != this.getPosition().getY() || z != this.getPosition().getZ()) {
            this.deleteGraphicsState();
        }
        super.setPosition(x, y, z);
    }

    @Override
    public void setCompiledChunk(CompiledChunk compiledChunk) {
        for (BlockRenderLayer layer : BlockRenderLayer._VALUES) {
            int index = layer.ordinal();
            MultidrawGraphicsState state = this.graphicsStates[index];
            if (compiledChunk.isLayerEmpty(layer) && state != null) {
                this.backend.deleteGraphicsState(state);
                this.graphicsStates[index] = null;
            }
        }
        super.setCompiledChunk(compiledChunk);
    }

    @Override
    protected int getBufferOriginX() {
        return ChunkRegionManager.getRegionOriginX(this.getPosition().getX() >> 4);
    }

    @Override
    protected int getBufferOriginY() {
        return ChunkRegionManager.getRegionOriginY(this.getPosition().getY() >> 4);
    }

    @Override
    protected int getBufferOriginZ() {
        return ChunkRegionManager.getRegionOriginZ(this.getPosition().getZ() >> 4);
    }

    @Override
    public void deleteGlResources() {
        this.deleteGraphicsState();
        super.deleteGlResources();
    }
}
