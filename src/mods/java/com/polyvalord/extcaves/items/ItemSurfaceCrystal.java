package com.polyvalord.extcaves.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ItemSurfaceCrystal extends Item {
    public ItemSurfaceCrystal(Item.Properties builder) {
        super(builder);
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        ServerWorld serverworld = (ServerWorld) worldIn;
        ServerPlayerEntity serverplayer = (ServerPlayerEntity) playerIn;
        BlockPos blockpos = serverplayer.getBedPosition().get();
        serverplayer.teleport(serverworld, blockpos.getX(), blockpos.getY(), blockpos.getZ(), 0.0F, 0.0F);

        playerIn.getCooldownTracker().setCooldown(this, 20);
        return ActionResult.func_233538_a_(itemstack, worldIn.isRemote());
        
        /*boolean tele = false;
		if (!worldIn.isRemote && playerIn.isAlive())
		{
			double sl = worldIn.getSeaLevel();
			if (playerIn.getPosY() < sl)
			{
				
				double d0 = playerIn.getPosX();
				double d1 = playerIn.getPosY();
				double d2 = playerIn.getPosZ();
				
				for (int i = 0; i < 16; i++)
				{
					d0 = playerIn.getPosX();
					d1 = (double) (sl + i);
					d2 = playerIn.getPosZ();
					
					if (playerIn.attemptTeleport(d0, d1, d2, false))
					{
						tele = true;
						SoundEvent soundevent = SoundEvents.BLOCK_GLASS_BREAK;
						worldIn.playSound((PlayerEntity) null, d0, d1, d2, soundevent, SoundCategory.PLAYERS, 1.0F, 1.0F);
						playerIn.addStat(Stats.ITEM_USED.get(this));
						if (!playerIn.abilities.isCreativeMode)
						{
							itemstack.shrink(1);
						}
						break;
					}
				}
			}
			else
			{
				playerIn.sendStatusMessage(new TranslationTextComponent("item.extcaves.surface_crystal_cancel"), true);
				return ActionResult.resultFail(itemstack);
			}
		}
		
		if (tele == false)
		{
			playerIn.getCooldownTracker().setCooldown(this, 20);
			return ActionResult.resultFail(itemstack);
		}
		else
		{
			playerIn.getCooldownTracker().setCooldown(this, 60);
			return ActionResult.resultConsume(itemstack);
		}*/
    }
}