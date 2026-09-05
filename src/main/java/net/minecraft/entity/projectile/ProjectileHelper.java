package net.minecraft.entity.projectile;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public final class ProjectileHelper {
    public static RayTraceResult func_221266_a(Entity p_221266_0_, boolean p_221266_1_, boolean p_221266_2_, Entity p_221266_3_, RayTraceContext.BlockMode p_221266_4_) {
        return func_221268_a(p_221266_0_, p_221266_1_, p_221266_2_, p_221266_3_, p_221266_4_, true, (p_221270_2_) -> {
            return !p_221270_2_.isSpectator() && p_221270_2_.canBeCollidedWith() && (p_221266_2_ || !p_221270_2_.isEntityEqual(p_221266_3_)) && !p_221270_2_.noClip;
        }, p_221266_0_.getBoundingBox().expand(p_221266_0_.getMotion()).grow(1.0D));
    }

    public static RayTraceResult func_221267_a(Entity p_221267_0_, AxisAlignedBB p_221267_1_, Predicate<Entity> p_221267_2_, RayTraceContext.BlockMode p_221267_3_, boolean p_221267_4_) {
        return func_221268_a(p_221267_0_, p_221267_4_, false, (Entity) null, p_221267_3_, false, p_221267_2_, p_221267_1_);
    }

    public static EntityRayTraceResult func_221271_a(World p_221271_0_, Entity p_221271_1_, Vec3d p_221271_2_, Vec3d p_221271_3_, AxisAlignedBB p_221271_4_, Predicate<Entity> p_221271_5_) {
        return func_221269_a(p_221271_0_, p_221271_1_, p_221271_2_, p_221271_3_, p_221271_4_, p_221271_5_, Double.MAX_VALUE);
    }

    private static RayTraceResult func_221268_a(Entity p_221268_0_, boolean p_221268_1_, boolean p_221268_2_, Entity p_221268_3_, RayTraceContext.BlockMode p_221268_4_, boolean p_221268_5_, Predicate<Entity> p_221268_6_, AxisAlignedBB p_221268_7_) {
        double d0 = p_221268_0_.posX;
        double d1 = p_221268_0_.posY;
        double d2 = p_221268_0_.posZ;
        Vec3d vec3d = p_221268_0_.getMotion();
        World world = p_221268_0_.world;
        Vec3d vec3d1 = new Vec3d(d0, d1, d2);
        if (p_221268_5_ && !world.isCollisionBoxesEmpty(p_221268_0_, p_221268_0_.getBoundingBox(), (Set<Entity>) (!p_221268_2_ && p_221268_3_ != null ? getEntityAndMount(p_221268_3_) : ImmutableSet.of()))) {
            return new BlockRayTraceResult(vec3d1, Direction.getFacingFromVector(vec3d.x, vec3d.y, vec3d.z), new BlockPos(p_221268_0_), false);
        } else {
            Vec3d vec3d2 = vec3d1.add(vec3d);
            RayTraceResult raytraceresult = world.rayTraceBlocks(new RayTraceContext(vec3d1, vec3d2, p_221268_4_, RayTraceContext.FluidMode.NONE, p_221268_0_));
            if (p_221268_1_) {
                if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
                    vec3d2 = raytraceresult.getHitVec();
                }

                RayTraceResult raytraceresult1 = func_221271_a(world, p_221268_0_, vec3d1, vec3d2, p_221268_7_, p_221268_6_);
                if (raytraceresult1 != null) {
                    raytraceresult = raytraceresult1;
                }
            }

            return raytraceresult;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static EntityRayTraceResult func_221273_a(Entity p_221273_0_, Vec3d p_221273_1_, Vec3d p_221273_2_, AxisAlignedBB p_221273_3_, Predicate<Entity> p_221273_4_, double p_221273_5_) {
        World world = p_221273_0_.world;
        List<Entity> candidates = world.getEntitiesInAABBexcluding(p_221273_0_, p_221273_3_, p_221273_4_);
        return func_221273_a(p_221273_0_, p_221273_1_, p_221273_2_, candidates, p_221273_5_);
    }

    @OnlyIn(Dist.CLIENT)
    public static EntityRayTraceResult func_221273_a(Entity source, Vec3d start, Vec3d end,
                                                       List<Entity> candidates, double maxDistanceSq) {
        double d0 = maxDistanceSq;
        Entity entity = null;
        double bestT = Double.NaN;
        double rayX = end.x - start.x;
        double rayY = end.y - start.y;
        double rayZ = end.z - start.z;
        double rayLengthSq = rayX * rayX + rayY * rayY + rayZ * rayZ;

        for (Entity entity1 : candidates) {
            AxisAlignedBB axisalignedbb = entity1.getBoundingBox();
            double border = (double)entity1.getCollisionBorderSize();
            if (start.x > axisalignedbb.minX - border && start.x < axisalignedbb.maxX + border
                    && start.y > axisalignedbb.minY - border && start.y < axisalignedbb.maxY + border
                    && start.z > axisalignedbb.minZ - border && start.z < axisalignedbb.maxZ + border) {
                if (d0 >= 0.0D) {
                    entity = entity1;
                    bestT = 0.0D;
                    d0 = 0.0D;
                }
            } else {
                double hitT = rayTraceParameter(axisalignedbb, border, start, rayX, rayY, rayZ);
                if (Double.isNaN(hitT)) {
                    continue;
                }
                double d1 = hitT * hitT * rayLengthSq;
                if (d1 < d0 || d0 == 0.0D) {
                    if (entity1.getLowestRidingEntity() == source.getLowestRidingEntity()) {
                        if (d0 == 0.0D) {
                            entity = entity1;
                            bestT = hitT;
                        }
                    } else {
                        entity = entity1;
                        bestT = hitT;
                        d0 = d1;
                    }
                }
            }
        }

        if (entity == null) {
            return null;
        } else {
            Vec3d hit = bestT == 0.0D ? start : new Vec3d(start.x + rayX * bestT,
                    start.y + rayY * bestT, start.z + rayZ * bestT);
            return new EntityRayTraceResult(entity, hit);
        }
    }

    private static double rayTraceParameter(AxisAlignedBB box, double border, Vec3d start,
                                            double dx, double dy, double dz) {
        double tMin = 0.0D;
        double tMax = 1.0D;

        if (Math.abs(dx) < 1.0E-7D) {
            if (start.x < box.minX - border || start.x > box.maxX + border) return Double.NaN;
        } else {
            double t1 = (box.minX - border - start.x) / dx;
            double t2 = (box.maxX + border - start.x) / dx;
            if (t1 > t2) { double t = t1; t1 = t2; t2 = t; }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMax < tMin) return Double.NaN;
        }
        if (Math.abs(dy) < 1.0E-7D) {
            if (start.y < box.minY - border || start.y > box.maxY + border) return Double.NaN;
        } else {
            double t1 = (box.minY - border - start.y) / dy;
            double t2 = (box.maxY + border - start.y) / dy;
            if (t1 > t2) { double t = t1; t1 = t2; t2 = t; }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMax < tMin) return Double.NaN;
        }
        if (Math.abs(dz) < 1.0E-7D) {
            if (start.z < box.minZ - border || start.z > box.maxZ + border) return Double.NaN;
        } else {
            double t1 = (box.minZ - border - start.z) / dz;
            double t2 = (box.maxZ + border - start.z) / dz;
            if (t1 > t2) { double t = t1; t1 = t2; t2 = t; }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMax < tMin) return Double.NaN;
        }
        return tMin > 0.0D && tMin < 1.0D ? tMin : Double.NaN;
    }

    public static EntityRayTraceResult func_221269_a(World p_221269_0_, Entity p_221269_1_, Vec3d p_221269_2_, Vec3d p_221269_3_, AxisAlignedBB p_221269_4_, Predicate<Entity> p_221269_5_, double p_221269_6_) {
        double d0 = p_221269_6_;
        Entity entity = null;

        for (Entity entity1 : p_221269_0_.getEntitiesInAABBexcluding(p_221269_1_, p_221269_4_, p_221269_5_)) {
            AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow((double) 0.3F);
            Optional<Vec3d> optional = axisalignedbb.rayTrace(p_221269_2_, p_221269_3_);
            if (optional.isPresent()) {
                double d1 = p_221269_2_.squareDistanceTo(optional.get());
                if (d1 < d0) {
                    entity = entity1;
                    d0 = d1;
                }
            }
        }

        if (entity == null) {
            return null;
        } else {
            return new EntityRayTraceResult(entity);
        }
    }

    private static Set<Entity> getEntityAndMount(Entity p_211325_0_) {
        Entity entity = p_211325_0_.getRidingEntity();
        return entity != null ? ImmutableSet.of(p_211325_0_, entity) : ImmutableSet.of(p_211325_0_);
    }

    public static final void rotateTowardsMovement(Entity projectile, float rotationSpeed) {
        Vec3d vec3d = projectile.getMotion();
        float f = MathHelper.sqrt(Entity.func_213296_b(vec3d));
        projectile.rotationYaw = (float) (MathHelper.atan2(vec3d.z, vec3d.x) * (double) (180F / (float) Math.PI)) + 90.0F;

        for (projectile.rotationPitch = (float) (MathHelper.atan2((double) f, vec3d.y) * (double) (180F / (float) Math.PI)) - 90.0F; projectile.rotationPitch - projectile.prevRotationPitch < -180.0F; projectile.prevRotationPitch -= 360.0F) {
            ;
        }

        while (projectile.rotationPitch - projectile.prevRotationPitch >= 180.0F) {
            projectile.prevRotationPitch += 360.0F;
        }

        while (projectile.rotationYaw - projectile.prevRotationYaw < -180.0F) {
            projectile.prevRotationYaw -= 360.0F;
        }

        while (projectile.rotationYaw - projectile.prevRotationYaw >= 180.0F) {
            projectile.prevRotationYaw += 360.0F;
        }

        projectile.rotationPitch = MathHelper.lerp(rotationSpeed, projectile.prevRotationPitch, projectile.rotationPitch);
        projectile.rotationYaw = MathHelper.lerp(rotationSpeed, projectile.prevRotationYaw, projectile.rotationYaw);
    }

    public static Hand getHandWith(LivingEntity living, Item itemIn) {
        return living.getHeldItemMainhand().getItem() == itemIn ? Hand.MAIN_HAND : Hand.OFF_HAND;
    }

    public static AbstractArrowEntity func_221272_a(LivingEntity p_221272_0_, ItemStack p_221272_1_, float p_221272_2_) {
        ArrowItem arrowitem = (ArrowItem) (p_221272_1_.getItem() instanceof ArrowItem ? p_221272_1_.getItem() : Items.ARROW);
        AbstractArrowEntity abstractarrowentity = arrowitem.createArrow(p_221272_0_.world, p_221272_1_, p_221272_0_);
        abstractarrowentity.setEnchantmentEffectsFromEntity(p_221272_0_, p_221272_2_);
        if (p_221272_1_.getItem() == Items.TIPPED_ARROW && abstractarrowentity instanceof ArrowEntity) {
            ((ArrowEntity) abstractarrowentity).setPotionEffect(p_221272_1_);
        }

        return abstractarrowentity;
    }
}
