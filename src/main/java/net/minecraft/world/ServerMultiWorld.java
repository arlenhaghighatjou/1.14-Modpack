package net.minecraft.world;

import net.minecraft.profiler.IProfiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.SaveHandler;

import java.util.concurrent.Executor;

public class ServerMultiWorld extends ServerWorld {
    public ServerMultiWorld(ServerWorld p_i50708_1_, MinecraftServer p_i50708_2_, Executor p_i50708_3_, SaveHandler p_i50708_4_, DimensionType p_i50708_5_, IProfiler p_i50708_6_, IChunkStatusListener p_i50708_7_) {
        super(p_i50708_2_, p_i50708_3_, p_i50708_4_, new DerivedWorldInfo(p_i50708_1_.getWorldInfo()), p_i50708_5_, p_i50708_6_, p_i50708_7_);
        p_i50708_1_.getWorldBorder().addListener(new IBorderListener.Impl(this.getWorldBorder()));
    }

    protected void advanceTime() {
    }
}
