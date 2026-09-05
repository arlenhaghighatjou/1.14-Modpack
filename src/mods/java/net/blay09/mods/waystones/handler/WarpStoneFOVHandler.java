package net.blay09.mods.waystones.handler;

import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WarpStoneFOVHandler {

    public static float getFovModifier(PlayerEntity player, float fov) {
        ItemStack activeItemStack = player.getActiveItemStack();
        if (!activeItemStack.isEmpty() && activeItemStack.getItem() == ModItems.returnScroll) {
            return player.getItemInUseCount() / 64f * 2f + 0.5f;
        }

        return fov;
    }

}
