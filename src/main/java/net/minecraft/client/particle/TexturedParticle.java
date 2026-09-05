package net.minecraft.client.particle;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class TexturedParticle extends Particle {
    protected float particleScale = 0.1F * (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F;

    protected TexturedParticle(World p_i51011_1_, double p_i51011_2_, double p_i51011_4_, double p_i51011_6_) {
        super(p_i51011_1_, p_i51011_2_, p_i51011_4_, p_i51011_6_);
    }

    protected TexturedParticle(World p_i51012_1_, double p_i51012_2_, double p_i51012_4_, double p_i51012_6_, double p_i51012_8_, double p_i51012_10_, double p_i51012_12_) {
        super(p_i51012_1_, p_i51012_2_, p_i51012_4_, p_i51012_6_, p_i51012_8_, p_i51012_10_, p_i51012_12_);
    }

    public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        float f = this.getScale(partialTicks);
        float f1 = this.getMinU();
        float f2 = this.getMaxU();
        float f3 = this.getMinV();
        float f4 = this.getMaxV();
        float f5 = (float) (MathHelper.lerp((double) partialTicks, this.prevPosX, this.posX) - interpPosX);
        float f6 = (float) (MathHelper.lerp((double) partialTicks, this.prevPosY, this.posY) - interpPosY);
        float f7 = (float) (MathHelper.lerp((double) partialTicks, this.prevPosZ, this.posZ) - interpPosZ);
        int i = this.getBrightnessForRender(partialTicks);
        if (this.particleAngle == 0.0F) {
            buffer.addParticleVertex(f5 + (-rotationX * f - rotationXY * f), f6 + (-rotationZ * f),
                    f7 + (-rotationYZ * f - rotationXZ * f), f2, f4,
                    this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha, i);
            buffer.addParticleVertex(f5 + (-rotationX * f + rotationXY * f), f6 + (rotationZ * f),
                    f7 + (-rotationYZ * f + rotationXZ * f), f2, f3,
                    this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha, i);
            buffer.addParticleVertex(f5 + (rotationX * f + rotationXY * f), f6 + (rotationZ * f),
                    f7 + (rotationYZ * f + rotationXZ * f), f1, f3,
                    this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha, i);
            buffer.addParticleVertex(f5 + (rotationX * f - rotationXY * f), f6 + (-rotationZ * f),
                    f7 + (rotationYZ * f - rotationXZ * f), f1, f4,
                    this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha, i);
            return;
        }
        float angle = MathHelper.lerp(partialTicks, this.prevParticleAngle, this.particleAngle);
        float qw = MathHelper.cos(angle * 0.5F);
        float sin = MathHelper.sin(angle * 0.5F);
        Vec3d look = entityIn.getLookDirection();
        float qx = sin * (float) look.x;
        float qy = sin * (float) look.y;
        float qz = sin * (float) look.z;
        float vectorScale = qw * qw - qx * qx - qy * qy - qz * qz;
        float twiceW = 2.0F * qw;

        this.renderRotatedVertex(buffer, -rotationX * f - rotationXY * f, -rotationZ * f, -rotationYZ * f - rotationXZ * f, f5, f6, f7, f2, f4, qx, qy, qz, vectorScale, twiceW, i);
        this.renderRotatedVertex(buffer, -rotationX * f + rotationXY * f, rotationZ * f, -rotationYZ * f + rotationXZ * f, f5, f6, f7, f2, f3, qx, qy, qz, vectorScale, twiceW, i);
        this.renderRotatedVertex(buffer, rotationX * f + rotationXY * f, rotationZ * f, rotationYZ * f + rotationXZ * f, f5, f6, f7, f1, f3, qx, qy, qz, vectorScale, twiceW, i);
        this.renderRotatedVertex(buffer, rotationX * f - rotationXY * f, -rotationZ * f, rotationYZ * f - rotationXZ * f, f5, f6, f7, f1, f4, qx, qy, qz, vectorScale, twiceW, i);
    }

    private void renderRotatedVertex(BufferBuilder buffer, float x, float y, float z, float centerX, float centerY,
                                     float centerZ, float u, float v, float qx, float qy, float qz,
                                     float vectorScale, float twiceW, int light) {
        float twiceDot = 2.0F * (qx * x + qy * y + qz * z);
        float rotatedX = twiceDot * qx + vectorScale * x + twiceW * (qy * z - qz * y);
        float rotatedY = twiceDot * qy + vectorScale * y + twiceW * (qz * x - qx * z);
        float rotatedZ = twiceDot * qz + vectorScale * z + twiceW * (qx * y - qy * x);
        buffer.addParticleVertex(centerX + rotatedX, centerY + rotatedY, centerZ + rotatedZ, u, v,
                this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha, light);
    }

    public float getScale(float p_217561_1_) {
        return this.particleScale;
    }

    public Particle multipleParticleScaleBy(float scale) {
        this.particleScale *= scale;
        return super.multipleParticleScaleBy(scale);
    }

    protected abstract float getMinU();

    protected abstract float getMaxU();

    protected abstract float getMinV();

    protected abstract float getMaxV();
}
