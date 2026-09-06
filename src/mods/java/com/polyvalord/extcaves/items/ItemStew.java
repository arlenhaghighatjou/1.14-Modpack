package com.polyvalord.extcaves.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class ItemStew extends Item {
    public ItemStew(Item.Properties properties) {
        super(properties);
    }

    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        PlayerEntity playerentity = (PlayerEntity) entityLiving;
        if (!playerentity.abilities.isCreativeMode) {
            playerentity.inventory.addItemStackToInventory(new ItemStack(Items.BOWL));
        }
        super.onItemUseFinish(stack, worldIn, playerentity);
        return stack;
    }
}