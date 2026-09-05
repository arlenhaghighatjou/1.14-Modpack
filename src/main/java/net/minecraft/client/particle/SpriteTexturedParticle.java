package net.minecraft.client.particle;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class SpriteTexturedParticle extends TexturedParticle {
    protected TextureAtlasSprite sprite;

    @Override
    public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        if (this.sprite != null) {
            this.sprite.markActive();
        }
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }

    protected SpriteTexturedParticle(World p_i50998_1_, double p_i50998_2_, double p_i50998_4_, double p_i50998_6_) {
        super(p_i50998_1_, p_i50998_2_, p_i50998_4_, p_i50998_6_);
    }

    protected SpriteTexturedParticle(World p_i50999_1_, double p_i50999_2_, double p_i50999_4_, double p_i50999_6_, double p_i50999_8_, double p_i50999_10_, double p_i50999_12_) {
        super(p_i50999_1_, p_i50999_2_, p_i50999_4_, p_i50999_6_, p_i50999_8_, p_i50999_10_, p_i50999_12_);
    }

    protected void setSprite(TextureAtlasSprite sprite) {
        this.sprite = sprite;
    }

    protected float getMinU() {
        return this.sprite != null ? this.sprite.getMinU() : 0.0F;
    }

    protected float getMaxU() {
        return this.sprite != null ? this.sprite.getMaxU() : 1.0F;
    }

    protected float getMinV() {
        return this.sprite != null ? this.sprite.getMinV() : 0.0F;
    }

    protected float getMaxV() {
        return this.sprite != null ? this.sprite.getMaxV() : 1.0F;
    }

    public void selectSpriteRandomly(IAnimatedSprite p_217568_1_) {
        TextureAtlasSprite sprite = p_217568_1_.get(this.rand);
        if (sprite != null) {
            this.setSprite(sprite);
        }
    }

    public void selectSpriteWithAge(IAnimatedSprite p_217566_1_) {
        TextureAtlasSprite sprite = p_217566_1_.get(this.age, this.maxAge);
        if (sprite != null) {
            this.setSprite(sprite);
        }
    }
}
