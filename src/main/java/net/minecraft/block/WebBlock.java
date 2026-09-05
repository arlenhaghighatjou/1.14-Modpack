package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WebBlock extends Block {
    public WebBlock(Block.Properties properties) {
        super(properties);
    }

    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {

        if (entityIn instanceof ItemEntity) {
            entityIn.setMotionMultiplier(state, new Vec3d(0.25D, 0.8D, 0.25D));
        } else {
            entityIn.setMotionMultiplier(state, new Vec3d(0.25D, (double) 0.05F, 0.25D));
        }
    }

    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
