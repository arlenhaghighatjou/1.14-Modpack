package net.minecraft.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public interface ITileEntityProvider {

    TileEntity createNewTileEntity(IBlockReader worldIn);
}
