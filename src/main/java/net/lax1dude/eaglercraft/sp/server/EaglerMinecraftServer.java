package net.lax1dude.eaglercraft.sp.server;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.sp.server.skins.IntegratedCapeService;
import net.lax1dude.eaglercraft.sp.server.skins.IntegratedSkinService;
import net.minecraft.command.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.Util;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.SaveFormat;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.function.BooleanSupplier;

public class EaglerMinecraftServer extends MinecraftServer {

    public static final Logger logger = EaglerIntegratedServerWorker.logger;
    public static int counterTicksPerSecond = 0;
    public static int counterChunkRead = 0;
    public static int counterChunkGenerate = 0;
    public static int counterChunkWrite = 0;
    public static int counterTileUpdate = 0;
    public static int counterLightUpdate = 0;
    public WorldSettings newWorldSettings;
    protected Difficulty difficulty;
    protected GameType gamemode;
    protected boolean isGamePaused = false;
    protected IntegratedSkinService skinService;
    protected IntegratedCapeService capeService;
    private long lastTPSUpdate = 0l;
    private long localTime;
    private int viewDistance;

    public EaglerMinecraftServer(String mcDataDir, String folderName, String worldName, String owner, int viewDistance, WorldSettings currentWorldSettings, boolean demo, SaveFormat format) {
        super(net.peyton.eagler.fs.WorldsDB.newVFile(""), (DataFixer) null, new Commands(false), new YggdrasilAuthenticationService(""), null, null, null, (p) -> new net.minecraft.world.chunk.listener.IChunkStatusListener() {
            private final java.util.Map<net.minecraft.util.math.ChunkPos, net.minecraft.world.chunk.ChunkStatus> statuses = new java.util.HashMap<>();
            private int totalChunks;
            private int loadedChunks;
            private long nextLogTime;
            private boolean tracking;
            private net.minecraft.util.math.ChunkPos center = new net.minecraft.util.math.ChunkPos(0, 0);
            private int eRadius = 11 + net.minecraft.world.chunk.ChunkStatus.func_222600_b();
            private int diameter = eRadius * 2 + 1;

            {
                int i = p * 2 + 1;
                this.totalChunks = i * i;
                this.loadedChunks = 0;
            }

            private char getStatusChar(net.minecraft.world.chunk.ChunkStatus s) {
                if (s == net.minecraft.world.chunk.ChunkStatus.EMPTY) return '0';
                if (s == net.minecraft.world.chunk.ChunkStatus.STRUCTURE_STARTS) return '1';
                if (s == net.minecraft.world.chunk.ChunkStatus.STRUCTURE_REFERENCES) return '2';
                if (s == net.minecraft.world.chunk.ChunkStatus.BIOMES) return '3';
                if (s == net.minecraft.world.chunk.ChunkStatus.NOISE) return '4';
                if (s == net.minecraft.world.chunk.ChunkStatus.SURFACE) return '5';
                if (s == net.minecraft.world.chunk.ChunkStatus.CARVERS) return '6';
                if (s == net.minecraft.world.chunk.ChunkStatus.LIQUID_CARVERS) return '7';
                if (s == net.minecraft.world.chunk.ChunkStatus.FEATURES) return '8';
                if (s == net.minecraft.world.chunk.ChunkStatus.LIGHT) return '9';
                if (s == net.minecraft.world.chunk.ChunkStatus.SPAWN) return 'A';
                if (s == net.minecraft.world.chunk.ChunkStatus.HEIGHTMAPS) return 'B';
                if (s == net.minecraft.world.chunk.ChunkStatus.FULL) return 'C';
                return ' ';
            }

            public void start(net.minecraft.util.math.ChunkPos center) {
                this.tracking = true;
                this.statuses.clear();
                this.loadedChunks = 0;
                this.center = center;
                this.nextLogTime = net.minecraft.util.Util.milliTime();
                EaglerIntegratedServerWorker.sendProgress("chk:" + center.x + ":" + center.z + ": ", 0.0f);
            }

            public void statusChanged(net.minecraft.util.math.ChunkPos pos, net.minecraft.world.chunk.ChunkStatus status) {
                if (!this.tracking) {
                    return;
                }
                if (status == net.minecraft.world.chunk.ChunkStatus.FULL) {
                    ++this.loadedChunks;
                }
                if (status == null) {
                    statuses.remove(pos);
                } else {
                    statuses.put(pos, status);
                }
                long current = net.minecraft.util.Util.milliTime();
                if (current > this.nextLogTime) {
                    this.nextLogTime = current + 50L;
                    StringBuilder sb = new StringBuilder("chk:");
                    sb.append(center.x).append(":").append(center.z).append(":");
                    for (int x = 0; x < diameter; ++x) {
                        for (int z = 0; z < diameter; ++z) {
                            net.minecraft.util.math.ChunkPos p2 = new net.minecraft.util.math.ChunkPos(x - eRadius + center.x, z - eRadius + center.z);
                            sb.append(getStatusChar(statuses.get(p2)));
                        }
                    }
                    EaglerIntegratedServerWorker.sendProgress(sb.toString(), (float) this.loadedChunks / (float) this.totalChunks);
                }
            }

            public void stop() {
                this.tracking = false;
                this.statuses.clear();
                EaglerIntegratedServerWorker.sendProgress("singleplayer.busy.startingIntegratedServer", 1.0f);
            }
        }, folderName);
        this.skinService = new IntegratedSkinService(
                new VFile2(new net.lax1dude.eaglercraft.internal.vfs2.VFile2(new net.lax1dude.eaglercraft.internal.vfs2.VFile2(mcDataDir, "worlds"), folderName), "eagler/skulls"));
        this.capeService = new IntegratedCapeService();
        this.setServerOwner(owner);
        logger.info("server owner: " + owner);
        this.setWorldName(worldName);
        this.setDemo(demo);
        this.setBuildLimit(256);
        this.newWorldSettings = currentWorldSettings;
        this.isGamePaused = false;
        this.viewDistance = viewDistance;
    }

    private static int countChunksLoaded(Iterable<ServerWorld> worlds) {
        int i = 0;
        for (ServerWorld world : worlds) {
            if (world != null) {
                i += world.getChunkProvider().getLoadedChunkCount();
            }
        }
        return i;
    }

    private static int countChunksTotal(Iterable<ServerWorld> worlds) {
        int i = 0;
        for (ServerWorld world : worlds) {
            if (world != null) {
                i += world.getChunkProvider().getLoadedChunkCount();
            }
        }
        return i;
    }

    private static int countEntities(Iterable<ServerWorld> worlds) {
        return 0;
    }

    private static int countTileEntities(Iterable<ServerWorld> worlds) {
        int i = 0;
        for (ServerWorld world : worlds) {
            if (world != null) {
                i += world.loadedTileEntityList.size();
            }
        }
        return i;
    }

    private static int countPlayerEntities(Iterable<ServerWorld> worlds) {
        int i = 0;
        for (ServerWorld world : worlds) {
            if (world != null) {
                i += world.getPlayers().size();
            }
        }
        return i;
    }

    public boolean isOnExecutionThread() {
        return true;
    }

    public boolean shouldDeferTasks() {
        return false;
    }

    @Override
    public boolean init() throws IOException {
        logger.info("Starting integrated eaglercraft server version 1.14.4");
        this.setOnlineMode(false);
        this.setCanSpawnAnimals(true);
        this.setCanSpawnNPCs(true);
        this.setAllowPvp(true);
        this.setAllowFlight(true);
        this.setPlayerList(new EaglerPlayerList(this, viewDistance));
        long seed = newWorldSettings != null ? newWorldSettings.getSeed() : 0L;
        WorldType worldType = newWorldSettings != null ? newWorldSettings.getTerrainType() : WorldType.DEFAULT;
        this.loadAllWorlds(this.getFolderName(), this.getWorldName(), seed, worldType, (JsonElement) null);
        this.setMOTD(this.getServerOwner() + " - " + this.getWorld(DimensionType.OVERWORLD).getWorldInfo().getWorldName());
        return true;
    }

    @Override
    protected void loadAllWorlds(String saveName, String worldNameIn, long seed, WorldType type, JsonElement generatorOptions) {
        this.convertMapIfNeeded(saveName);
        this.setUserMessage(new net.minecraft.util.text.TranslationTextComponent("menu.loadingLevel"));
        net.minecraft.world.storage.SaveHandler savehandler = this.getActiveAnvilConverter().getSaveLoader(saveName, this);
        this.setResourcePackFromWorld(this.getFolderName(), savehandler);
        net.minecraft.world.storage.WorldInfo worldinfo = savehandler.loadWorldInfo();
        WorldSettings worldsettings;
        if (worldinfo == null) {
            worldsettings = this.newWorldSettings;
            worldinfo = new net.minecraft.world.storage.WorldInfo(worldsettings, worldNameIn);
        } else {
            worldinfo.setWorldName(worldNameIn);
            worldsettings = new WorldSettings(worldinfo);
        }

        this.loadDataPacks(savehandler.getWorldDirectory(), worldinfo);
        net.minecraft.world.chunk.listener.IChunkStatusListener ichunkstatuslistener = this.chunkStatusListenerFactory.create(11);
        this.loadWorlds(savehandler, worldinfo, worldsettings, ichunkstatuslistener);
        this.setDifficultyForAllWorlds(this.getDifficulty(), true);
    }

    public IntegratedSkinService getSkinService() {
        return skinService;
    }

    public IntegratedCapeService getCapeService() {
        return capeService;
    }

    public void setBaseServerProperties(Difficulty difficulty, GameType gamemode) {
        this.difficulty = difficulty;
        this.gamemode = gamemode;
        this.setCanSpawnAnimals(true);
        this.setCanSpawnNPCs(true);
        this.setAllowPvp(true);
        this.setAllowFlight(true);
    }

    @Override
    public boolean allowLogging() {
        return false;
    }

    public void mainLoop(boolean singleThreadMode) {
        long k = Util.milliTime();
        if (isGamePaused) {
            localTime = k;
            this.sendTPSToClient(k);
            return;
        }

        long j = k - this.localTime;
        if ((j > (singleThreadMode ? 500L : 2000L))) {
            logger.warn(
                    "Can\'t keep up! Did the system time change, or is the server overloaded? Running {}ms behind, skipping {} tick(s)",
                    new Object[]{Long.valueOf(j), Long.valueOf(j / 50L)});
            j = 100L;
            this.localTime = k - 100l;
        }

        if (j < 0L) {
            logger.warn("Time ran backwards! Did the system time change?");
            j = 0L;
            this.localTime = k;
        }

        // A single-thread server may only be entered once per rendered frame. Catch
        // up within a small wall-time budget so chunk rendering cannot cap TPS to FPS.
        int ticksRemaining = singleThreadMode ? 10 : 1;
        long tickDeadline = singleThreadMode ? k + 25L : Long.MAX_VALUE;
        while (j >= 50L && ticksRemaining-- > 0) {
            this.localTime += 50L;
            this.tick(() -> true);
            this.drainScheduledTasks();
            ++counterTicksPerSecond;
            j = k - this.localTime;
            if (singleThreadMode && Util.milliTime() >= tickDeadline) {
                break;
            }
        }

        this.sendTPSToClient(Util.milliTime());
    }

    private void drainScheduledTasks() {
        this.runScheduledTasks();
    }

    @Override
    protected void runScheduledTasks() {
        long l = net.minecraft.util.Util.milliTime();
        while (this.driveOne()) {
            if (net.minecraft.util.Util.milliTime() - l > 50L) {
                break;
            }
        }
    }

    @Override
    public boolean driveOne() {
        boolean flag = super.driveOne();
        if (!flag) {
            for (net.minecraft.world.server.ServerWorld serverworld : this.getWorlds()) {
                if (serverworld.getChunkProvider().func_217234_d()) {
                    return true;
                }
            }
        }
        return flag;
    }

    @Override
    protected void updateTimeLightAndEntities(BooleanSupplier hasTimeLeft) {
        this.skinService.flushCache();
        super.updateTimeLightAndEntities(hasTimeLeft);
    }

    protected void sendTPSToClient(long millis) {
        if (lastTPSUpdate == 0L) {
            lastTPSUpdate = millis;
            return;
        }
        if (millis - lastTPSUpdate > 1000l) {
            lastTPSUpdate = millis;
            if (isServerRunning() && this.getWorld(DimensionType.OVERWORLD) != null) {
                List<String> lst = Lists.newArrayList("TPS: " + counterTicksPerSecond + "/20",
                        "Chunks: " + countChunksLoaded(this.getWorlds()) + "/" + countChunksTotal(this.getWorlds()),
                        "Entities: " + countEntities(this.getWorlds()) + "+" + countTileEntities(this.getWorlds()),
                        "R: " + counterChunkRead + ", G: " + counterChunkGenerate + ", W: " + counterChunkWrite,
                        "TU: " + counterTileUpdate + ", LU: " + counterLightUpdate);
                int players = countPlayerEntities(this.getWorlds());
                if (players > 1) {
                    lst.add("Players: " + players);
                }
                counterTicksPerSecond = counterChunkRead = counterChunkGenerate = 0;
                counterChunkWrite = counterTileUpdate = counterLightUpdate = 0;
                EaglerIntegratedServerWorker.reportTPS(lst);
            }
        }
    }

    public boolean getPaused() {
        return isGamePaused;
    }

    public void setPaused(boolean p) {
        isGamePaused = p;
        if (!p) {
            localTime = System.currentTimeMillis();
        }
    }

    @Override
    public boolean canStructuresSpawn() {
        ServerWorld overworld = getWorld(DimensionType.OVERWORLD);
        return overworld != null ? overworld.getWorldInfo().isMapFeaturesEnabled()
                : newWorldSettings.isMapFeaturesEnabled();
    }

    @Override
    public GameType getGameType() {
        ServerWorld overworld = getWorld(DimensionType.OVERWORLD);
        return overworld != null ? overworld.getWorldInfo().getGameType() : newWorldSettings.getGameType();
    }

    @Override
    public Difficulty getDifficulty() {
        return difficulty;
    }

    @Override
    public boolean isHardcore() {
        ServerWorld overworld = getWorld(DimensionType.OVERWORLD);
        return overworld != null ? overworld.getWorldInfo().isHardcore()
                : newWorldSettings.getHardcoreEnabled();
    }

    @Override
    public int getOpPermissionLevel() {
        return 4;
    }

    @Override
    public int func_223707_k() {
        return 4;
    }

    @Override
    public boolean allowLoggingRcon() {
        return false;
    }

    @Override
    public boolean isDedicatedServer() {
        return false;
    }

    @Override
    public boolean shouldUseNativeTransport() {
        return false;
    }

    @Override
    public boolean isCommandBlockEnabled() {
        return true;
    }

    @Override
    public boolean getPublic() {
        return true;
    }

    @Override
    public boolean shareToLAN(GameType type, boolean allowCheats, int port) {
        return true;
    }

    @Override
    public boolean func_213199_b(GameProfile profile) {
        return true;
    }

    public PlayerList getConfigurationManager() {
        return this.getPlayerList();
    }

    public void setDifficultyLockedForAllWorlds(boolean locked) {
        // stub for integrated server
    }

    public void saveAllWorlds(boolean suppressEvent) {
        for (net.minecraft.world.server.ServerWorld world : this.getWorlds()) {
            try {
                world.save(null, true, false);
            } catch (net.minecraft.world.storage.SessionLockException e) {
                // ignore
            }
        }
        net.minecraft.world.server.ServerWorld overworld = this.getWorld(net.minecraft.world.dimension.DimensionType.OVERWORLD);
        if (overworld != null) {
            net.minecraft.world.storage.WorldInfo worldinfo = overworld.getWorldInfo();
            overworld.getWorldBorder().copyTo(worldinfo);
            worldinfo.setCustomBossEvents(this.getCustomBossEvents().write());
            overworld.getSaveHandler().saveWorldInfoWithPlayer(worldinfo, this.getPlayerList().getHostPlayerData());
        }
    }

}
