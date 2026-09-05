package net.minecraft.util.math;

public abstract class RayTraceResult {
   protected final Vec3d hitResult;

   protected RayTraceResult(Vec3d hitVec) {
      this.hitResult = hitVec;
   }

   public abstract RayTraceResult.Type getType();

   public Vec3d getHitVec() {
      return this.hitResult;
   }

   public static enum Type {
      MISS,
      BLOCK,
      ENTITY;
   }
}
