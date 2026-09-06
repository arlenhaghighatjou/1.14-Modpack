package com.polyvalord.extcaves.blocks.moss;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class BlockMossFire extends BlockMossDry
{
	
	public BlockMossFire(Properties properties) 
	{
		super(properties);
	}
	
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) 
	{
		if (entityIn instanceof LivingEntity && entityIn.getType() != EntityType.BLAZE) 
		{
			entityIn.setMotionMultiplier(state, new Vector3d((double) 0.9F, 1.0D, (double) 0.9F));
			if (!worldIn.isRemote && (entityIn.lastTickPosX != entityIn.getPosX() || entityIn.lastTickPosZ != entityIn.getPosZ())) 
			{
				double d0 = Math.abs(entityIn.getPosX() - entityIn.lastTickPosX);
				double d1 = Math.abs(entityIn.getPosZ() - entityIn.lastTickPosZ);
				if (d0 >= (double) 0.003F || d1 >= (double) 0.003F) 
				{
					entityIn.attackEntityFrom(DamageSource.ON_FIRE, 1.0F);
				}
			}

		}
	}

}
