package com.mojang.datafixers;

import net.minecraft.nbt.CompoundNBT;

public interface IFixableData {
    int getFixVersion();

    CompoundNBT fixTagCompound(CompoundNBT compound);
}
