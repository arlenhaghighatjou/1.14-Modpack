package net.minecraft.block;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;

import java.util.List;

public class SkullWallPlayerBlock extends WallSkullBlock {
    protected SkullWallPlayerBlock(Block.Properties properties) {
        super(SkullBlock.Types.PLAYER, properties);
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        Blocks.PLAYER_HEAD.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return Blocks.PLAYER_HEAD.getDrops(state, builder);
    }
}
