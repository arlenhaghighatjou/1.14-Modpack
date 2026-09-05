package net.blay09.mods.waystones.handler;

import net.blay09.mods.waystones.item.IResetUseOnDamage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class WarpDamageResetHandler {

    public static void onDamage(LivingEntity entity) {
        if (entity instanceof PlayerEntity && entity.getActiveItemStack().getItem() instanceof IResetUseOnDamage) {
            entity.stopActiveHand();
        }
    }

}
