package net.minecraft.profiler;

import com.google.common.collect.Maps;
import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

public class Snooper {
    private final Map<String, Object> snooperStats = Maps.newHashMap();
    private final Map<String, Object> clientStats = Maps.newHashMap();
    private final String uniqueID = EaglercraftUUID.randomUUID().toString();
    private final ISnooperInfo playerStatsCollector;
    private final Object syncLock = new Object();
    private final long minecraftStartTimeMilis;
    private boolean isRunning;

    public Snooper(String side, ISnooperInfo playerStatCollector, long startTime) {
        this.playerStatsCollector = playerStatCollector;
        this.minecraftStartTimeMilis = startTime;
    }

    public void start() {
        if (!this.isRunning) {
            ;
        }

    }

    public void addMemoryStatsToSnooper() {
        this.addStatToSnooper("memory_total", EagRuntime.totalMemory());
        this.addStatToSnooper("memory_free", EagRuntime.freeMemory());
        this.playerStatsCollector.fillSnooper(this);
    }

    public void addClientStat(String statName, Object statValue) {
        synchronized (this.syncLock) {
            this.clientStats.put(statName, statValue);
        }
    }

    public void addStatToSnooper(String statName, Object statValue) {
        synchronized (this.syncLock) {
            this.snooperStats.put(statName, statValue);
        }
    }

    public boolean isSnooperRunning() {
        return this.isRunning;
    }

    public void stop() {
    }

    @OnlyIn(Dist.CLIENT)
    public String getUniqueID() {
        return this.uniqueID;
    }

    public long getMinecraftStartTimeMillis() {
        return this.minecraftStartTimeMilis;
    }
}
