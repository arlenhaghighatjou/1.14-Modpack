package com.mojang.datafixers;

import net.minecraft.nbt.CompoundNBT;

public interface IDataFixer {
    CompoundNBT process(IFixType type, CompoundNBT compound, int versionIn);
}
