package com.polyvalord.extcaves.items;

import com.polyvalord.extcaves.config.Config;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class ItemMedicine extends Item {
    private float HEALTH = 0;

    public ItemMedicine(float health, Item.Properties builder) {
        super(builder);
        this.HEALTH = health;
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        worldIn.playSound((PlayerEntity) null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(),
                SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.NEUTRAL, 0.5F, 1.0F);
        playerIn.getCooldownTracker().setCooldown(this, (Config.item_medecine_cooldown.get() * 20));
        if (!worldIn.isRemote) {
            playerIn.setHealth(playerIn.getHealth() + HEALTH);
        }

        playerIn.addStat(Stats.ITEM_USED.get(this));
        if (!playerIn.abilities.isCreativeMode) {
            itemstack.shrink(1);
        }

        return ActionResult.func_233538_a_(itemstack, worldIn.isRemote());
    }
}