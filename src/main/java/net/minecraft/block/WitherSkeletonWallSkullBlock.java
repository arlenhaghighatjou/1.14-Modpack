package net.minecraft.block;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WitherSkeletonWallSkullBlock extends WallSkullBlock {
   protected WitherSkeletonWallSkullBlock(Block.Properties properties) {
      super(SkullBlock.Types.WITHER_SKELETON, properties);
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state,  LivingEntity placer, ItemStack stack) {
      Blocks.WITHER_SKELETON_SKULL.onBlockPlacedBy(worldIn, pos, state, placer, stack);
   }
}
