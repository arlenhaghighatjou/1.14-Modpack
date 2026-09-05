package net.minecraft.client.renderer.culling;

import com.mojang.blaze3d.platform.GlStateManager;
import net.lax1dude.eaglercraft.internal.buffer.FloatBuffer;
import net.lax1dude.eaglercraft.opengl.ext.deferred.BetterFrustum;
import net.lax1dude.eaglercraft.vector.Matrix4f;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Frustum implements ICamera {
    private double x;
    private double y;
    private double z;

    private final BetterFrustum betterFrustum = new BetterFrustum();
    private static final FloatBuffer matrixBuffer = GLAllocation.createDirectFloatBuffer(16);
    private static final Matrix4f projMatrix = new Matrix4f();
    private static final Matrix4f modelMatrix = new Matrix4f();
    private static final Matrix4f mvpMatrix = new Matrix4f();

    public Frustum() {
    }

    public void update() {
        matrixBuffer.clear();
        GlStateManager.getMatrix(2983, matrixBuffer);
        matrixBuffer.flip().limit(16);
        projMatrix.load(matrixBuffer);

        matrixBuffer.clear();
        GlStateManager.getMatrix(2982, matrixBuffer);
        matrixBuffer.flip().limit(16);
        modelMatrix.load(matrixBuffer);

        Matrix4f.mul(projMatrix, modelMatrix, mvpMatrix);
        betterFrustum.set(mvpMatrix, false);
    }

    public Frustum(ClippingHelper clippingHelperIn) {
        this(); 
    }

    public void setPosition(double xIn, double yIn, double zIn) {
        this.x = xIn;
        this.y = yIn;
        this.z = zIn;
    }

    public boolean isBoxInFrustum(double p_78548_1_, double p_78548_3_, double p_78548_5_, double p_78548_7_, double p_78548_9_, double p_78548_11_) {
        return this.betterFrustum.testAab((float) (p_78548_1_ - this.x), (float) (p_78548_3_ - this.y), (float) (p_78548_5_ - this.z), (float) (p_78548_7_ - this.x), (float) (p_78548_9_ - this.y), (float) (p_78548_11_ - this.z));
    }

    public boolean isBoundingBoxInFrustum(AxisAlignedBB p_78546_1_) {
        return this.isBoxInFrustum(p_78546_1_.minX, p_78546_1_.minY, p_78546_1_.minZ, p_78546_1_.maxX, p_78546_1_.maxY, p_78546_1_.maxZ);
    }
}
