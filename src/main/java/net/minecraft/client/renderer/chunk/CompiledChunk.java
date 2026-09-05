package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class CompiledChunk {
    public static final CompiledChunk DUMMY = new CompiledChunk() {
        protected void setLayerUsed(BlockRenderLayer layer) {
            throw new UnsupportedOperationException();
        }

        public void setLayerStarted(BlockRenderLayer layer) {
            throw new UnsupportedOperationException();
        }

        public boolean isVisible(Direction facing, Direction facing2) {
            return false;
        }
    };
    private final boolean[] layersUsed = new boolean[BlockRenderLayer.values().length];
    private final boolean[] layersStarted = new boolean[BlockRenderLayer.values().length];
    private boolean empty = true;
    private final List<TileEntity> tileEntities = Lists.newArrayList();
    private final List<TextureAtlasSprite> animatedSprites = new ArrayList<>(4);
    private final SetVisibility setVisibility = new SetVisibility();
    private BufferBuilder.State state;

    public boolean isEmpty() {
        return this.empty;
    }

    protected void setLayerUsed(BlockRenderLayer layer) {
        this.empty = false;
        this.layersUsed[layer.ordinal()] = true;
    }

    public boolean isLayerEmpty(BlockRenderLayer layer) {
        return !this.layersUsed[layer.ordinal()];
    }

    public void setLayerStarted(BlockRenderLayer layer) {
        this.layersStarted[layer.ordinal()] = true;
    }

    public boolean isLayerStarted(BlockRenderLayer layer) {
        return this.layersStarted[layer.ordinal()];
    }

    public List<TileEntity> getTileEntities() {
        return this.tileEntities;
    }

    public void addTileEntity(TileEntity tileEntityIn) {
        this.tileEntities.add(tileEntityIn);
    }

    public List<TextureAtlasSprite> getAnimatedSprites() {
        return this.animatedSprites;
    }

    public void markAnimatedSpritesActive() {
        for (int i = 0, len = this.animatedSprites.size(); i < len; ++i) {
            this.animatedSprites.get(i).markActive();
        }
    }

    public boolean isVisible(Direction facing, Direction facing2) {
        return this.setVisibility.isVisible(facing, facing2);
    }

    public SetVisibility getVisibility() {
        return this.setVisibility;
    }

    public BufferBuilder.State getState() {
        return this.state;
    }

    public void setState(BufferBuilder.State stateIn) {
        this.state = stateIn;
    }

    public void reset() {
        this.empty = true;
        for (int i = 0; i < this.layersUsed.length; ++i) {
            this.layersUsed[i] = false;
            this.layersStarted[i] = false;
        }
        this.tileEntities.clear();
        this.animatedSprites.clear();
        this.setVisibility.setAllVisible(false);
        this.state = null;
    }
}
