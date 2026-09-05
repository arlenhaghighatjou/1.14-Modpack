package com.mojang.datafixers;

import net.minecraft.nbt.CompoundNBT;

public interface IDataWalker {
    CompoundNBT process(IDataFixer fixer, CompoundNBT compound, int versionIn);
}
