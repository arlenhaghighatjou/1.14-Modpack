package net.minecraft.client.renderer.culling;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ICamera {
    boolean isBoundingBoxInFrustum(AxisAlignedBB p_78546_1_);

    boolean isBoxInFrustum(double minX, double minY, double minZ, double maxX, double maxY, double maxZ);

    void setPosition(double xIn, double yIn, double zIn);
}
