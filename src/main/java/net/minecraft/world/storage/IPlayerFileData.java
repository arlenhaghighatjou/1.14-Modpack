package net.minecraft.world.storage;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public interface IPlayerFileData {
   void writePlayerData(PlayerEntity player);

   CompoundNBT readPlayerData(PlayerEntity player);
}
