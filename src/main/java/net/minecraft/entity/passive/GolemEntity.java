package net.minecraft.entity.passive;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public abstract class GolemEntity extends CreatureEntity {
   protected GolemEntity(EntityType<? extends GolemEntity> type, World worldIn) {
      super(type, worldIn);
   }

   public void fall(float distance, float damageMultiplier) {
   }

   protected SoundEvent getAmbientSound() {
      return null;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return null;
   }

   protected SoundEvent getDeathSound() {
      return null;
   }

   public int getTalkInterval() {
      return 120;
   }

   public boolean canDespawn(double distanceToClosestPlayer) {
      return false;
   }
}
