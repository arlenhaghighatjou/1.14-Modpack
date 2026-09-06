package com.polyvalord.extcaves.blocks;

import com.polyvalord.extcaves.config.Config;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockBrokenstone extends Block {

    public BlockBrokenstone(Properties properties) {
        super(properties);
    }

    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        // ROVER: Disable falling through if brokenstone is disabled after the world has been generated
        if (Config.gen_block_brokenstone.get() && entityIn.getType() == EntityType.PLAYER) {
            worldIn.destroyBlock(pos, false);
        }
    }
}
