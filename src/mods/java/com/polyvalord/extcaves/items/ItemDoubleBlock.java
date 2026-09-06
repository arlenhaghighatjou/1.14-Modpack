package com.polyvalord.extcaves.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;

public class ItemDoubleBlock extends BlockItem {
    private boolean IS_UP = true;

    public ItemDoubleBlock(boolean side, Block blockIn, Item.Properties builder) {
        super(blockIn, builder);
        this.IS_UP = side;
    }

    protected boolean placeBlock(BlockItemUseContext context, BlockState state) {
        BlockPos isup;
        if (IS_UP == true) {
            isup = context.getPos().up();
        } else {
            isup = context.getPos().down();
        }
        context.getWorld().setBlockState(isup, Blocks.AIR.getDefaultState(), 27);
        return super.placeBlock(context, state);
    }
}