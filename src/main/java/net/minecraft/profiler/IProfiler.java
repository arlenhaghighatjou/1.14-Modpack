package net.minecraft.profiler;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public interface IProfiler {
    void startTick();

    void endTick();

    void startSection(String name);

    void startSection(Supplier<String> nameSupplier);

    void endSection();

    void endStartSection(String name);

    @OnlyIn(Dist.CLIENT)
    void endStartSection(Supplier<String> nameSupplier);
}
