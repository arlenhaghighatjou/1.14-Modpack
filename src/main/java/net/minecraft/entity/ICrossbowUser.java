package net.minecraft.entity;

import net.minecraft.item.ItemStack;

public interface ICrossbowUser {
    void setCharging(boolean p_213671_1_);

    void shoot(LivingEntity p_213670_1_, ItemStack p_213670_2_, IProjectile p_213670_3_, float p_213670_4_);

    LivingEntity getAttackTarget();
}
