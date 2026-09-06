package com.polyvalord.extcaves.blocks.moss;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class BlockMossFrozen extends BlockMossDry
{
	
	public BlockMossFrozen(Properties properties) 
	{
		super(properties);
	}
	
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) 
	{
		if (entityIn instanceof LivingEntity && entityIn.getType() != EntityType.STRAY) 
		{
			entityIn.setMotionMultiplier(state, new Vector3d((double) 0.8F, 1.0D, (double) 0.8F));
		}
	}

}
