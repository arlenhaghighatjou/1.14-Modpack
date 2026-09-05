package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;

public class AbstractMapItem extends Item {
    public AbstractMapItem(Item.Properties builder) {
        super(builder);
    }

    public boolean isComplex() {
        return true;
    }

    public IPacket<?> getUpdatePacket(ItemStack stack, World worldIn, PlayerEntity player) {
        return null;
    }
}
