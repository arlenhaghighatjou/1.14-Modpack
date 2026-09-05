package net.minecraft.server.integrated;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import java.io.IOException;
import java.net.InetAddress;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import java.util.function.BooleanSupplier;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.LanServerPingThread;
import net.minecraft.command.Commands;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.profiler.DebugProfiler;
import net.minecraft.profiler.Snooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.CryptManager;
import net.minecraft.util.SharedConstants;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.chunk.listener.IChunkStatusListenerFactory;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class IntegratedServer extends MinecraftServer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft mc;
   private final WorldSettings worldSettings;
   private boolean isGamePaused;
   private int serverPort = -1;
   private LanServerPingThread lanServerPing;
   private EaglercraftUUID playerUuid;

   public IntegratedServer(Minecraft p_i50895_1_, String worldName, String p_i50895_3_, WorldSettings p_i50895_4_, YggdrasilAuthenticationService p_i50895_5_, MinecraftSessionService p_i50895_6_, GameProfileRepository p_i50895_7_, PlayerProfileCache p_i50895_8_, IChunkStatusListenerFactory p_i50895_9_) {
      super(new VFile2(p_i50895_1_.gameDir, "saves"), p_i50895_1_.getDataFixer(), new Commands(false), p_i50895_5_, p_i50895_6_, p_i50895_7_, p_i50895_8_, p_i50895_9_, worldName);
      this.setServerOwner(p_i50895_1_.getSession().getUsername());
      this.setWorldName(p_i50895_3_);
      this.setDemo(p_i50895_1_.isDemo());
      this.canCreateBonusChest(p_i50895_4_.isBonusChestEnabled());
      this.setBuildLimit(256);
      this.setPlayerList(new IntegratedPlayerList(this));
      this.mc = p_i50895_1_;
      this.worldSettings = this.isDemo() ? MinecraftServer.DEMO_WORLD_SETTINGS : p_i50895_4_;
   }

   public void loadAllWorlds(String saveName, String worldNameIn, long seed, WorldType type, JsonElement generatorOptions) {
      this.convertMapIfNeeded(saveName);
      SaveHandler savehandler = this.getActiveAnvilConverter().getSaveLoader(saveName, this);
      this.setResourcePackFromWorld(this.getFolderName(), savehandler);
      WorldInfo worldinfo = savehandler.loadWorldInfo();
      if (worldinfo == null) {
         worldinfo = new WorldInfo(this.worldSettings, worldNameIn);
      } else {
         worldinfo.setWorldName(worldNameIn);
      }

      this.loadDataPacks(savehandler.getWorldDirectory(), worldinfo);
      IChunkStatusListener ichunkstatuslistener = this.chunkStatusListenerFactory.create(11);
      this.loadWorlds(savehandler, worldinfo, this.worldSettings, ichunkstatuslistener);
      if (this.getWorld(DimensionType.OVERWORLD).getWorldInfo().getDifficulty() == null) {
         this.setDifficultyForAllWorlds(this.mc.gameSettings.difficulty, true);
      }

      this.loadInitialChunks(ichunkstatuslistener);
   }

   public boolean init() throws IOException {
      LOGGER.info("Starting integrated minecraft server version " + SharedConstants.getVersion().getName());
      this.setOnlineMode(true);
      this.setCanSpawnAnimals(true);
      this.setCanSpawnNPCs(true);
      this.setAllowPvp(true);
      this.setAllowFlight(true);
      LOGGER.info("Generating keypair");
      this.setKeyPair(CryptManager.generateKeyPair());
      this.loadAllWorlds(this.getFolderName(), this.getWorldName(), this.worldSettings.getSeed(), this.worldSettings.getTerrainType(), this.worldSettings.getGeneratorOptions());
      this.setMOTD(this.getServerOwner() + " - " + this.getWorld(DimensionType.OVERWORLD).getWorldInfo().getWorldName());
      return true;
   }

   public void tick(BooleanSupplier hasTimeLeft) {
      boolean flag = this.isGamePaused;
      this.isGamePaused = Minecraft.getInstance().getConnection() != null && Minecraft.getInstance().isGamePaused();
      DebugProfiler debugprofiler = this.getProfiler();
      if (!flag && this.isGamePaused) {
         debugprofiler.startSection("autoSave");
         LOGGER.info("Saving and pausing game...");
         this.getPlayerList().saveAllPlayerData();
         this.save(false, false, false);
         debugprofiler.endSection();
      }

      if (!this.isGamePaused) {
         super.tick(hasTimeLeft);
         int i = Math.max(2, this.mc.gameSettings.renderDistanceChunks + -2);
         if (i != this.getPlayerList().getViewDistance()) {
            LOGGER.info("Changing view distance to {}, from {}", i, this.getPlayerList().getViewDistance());
            this.getPlayerList().setViewDistance(i);
         }

      }
   }

   public boolean canStructuresSpawn() {
      return false;
   }

   public GameType getGameType() {
      return this.worldSettings.getGameType();
   }

   public Difficulty getDifficulty() {
      return this.mc.world.getWorldInfo().getDifficulty();
   }

   public boolean isHardcore() {
      return this.worldSettings.getHardcoreEnabled();
   }

   public boolean allowLoggingRcon() {
      return true;
   }

   public boolean allowLogging() {
      return true;
   }

   public VFile2 getDataDirectory() {
      return this.mc.gameDir;
   }

   public boolean isDedicatedServer() {
      return false;
   }

   public boolean shouldUseNativeTransport() {
      return false;
   }

   public void finalTick(CrashReport report) {
      this.mc.crashed(report);
   }

   public CrashReport addServerInfoToCrashReport(CrashReport report) {
      report = super.addServerInfoToCrashReport(report);
      report.getCategory().addDetail("Type", "Integrated Server (map_client.txt)");
      report.getCategory().addDetail("Is Modded", () -> {
         String s = ClientBrandRetriever.getClientModName();
         if (!s.equals("vanilla")) {
            return "Definitely; Client brand changed to '" + s + "'";
         } else {
            s = this.getServerModName();
            if (!"vanilla".equals(s)) {
               return "Definitely; Server brand changed to '" + s + "'";
            } else {
               return Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and both client + server brands are untouched.";
            }
         }
      });
      return report;
   }

   public void fillSnooper(Snooper snooper) {
      super.fillSnooper(snooper);
      snooper.addClientStat("snooper_partner", this.mc.getSnooper().getUniqueID());
   }

   public boolean shareToLAN(GameType gameMode, boolean cheats, int port) {
      try {
         this.getNetworkSystem().addEndpoint((InetAddress)null, port);
         LOGGER.info("Started serving on {}", (int)port);
         this.serverPort = port;
         this.lanServerPing = new LanServerPingThread(this.getMOTD(), port + "");
         this.lanServerPing.start();
         this.getPlayerList().setGameType(gameMode);
         this.getPlayerList().setCommandsAllowedForAll(cheats);
         int i = this.getPermissionLevel(this.mc.player.getGameProfile());
         this.mc.player.setPermissionLevel(i);

         for(ServerPlayerEntity serverplayerentity : this.getPlayerList().getPlayers()) {
            this.getCommandManager().send(serverplayerentity);
         }

         return true;
      } catch (IOException var7) {
         return false;
      }
   }

   public void stopServer() {
      super.stopServer();
      if (this.lanServerPing != null) {
         this.lanServerPing.interrupt();
         this.lanServerPing = null;
      }

   }

   public void initiateShutdown(boolean p_71263_1_) {
      this.runImmediately(() -> {
         for(ServerPlayerEntity serverplayerentity : Lists.newArrayList(this.getPlayerList().getPlayers())) {
            if (!serverplayerentity.getUniqueID().equals(this.playerUuid)) {
               this.getPlayerList().playerLoggedOut(serverplayerentity);
            }
         }

      });
      super.initiateShutdown(p_71263_1_);
      if (this.lanServerPing != null) {
         this.lanServerPing.interrupt();
         this.lanServerPing = null;
      }

   }

   public boolean getPublic() {
      return this.serverPort > -1;
   }

   public int getServerPort() {
      return this.serverPort;
   }

   public void setGameType(GameType gameMode) {
      super.setGameType(gameMode);
      this.getPlayerList().setGameType(gameMode);
   }

   public boolean isCommandBlockEnabled() {
      return true;
   }

   public int getOpPermissionLevel() {
      return 2;
   }

   public int func_223707_k() {
      return 2;
   }

   public void setPlayerUuid(EaglercraftUUID uuid) {
      this.playerUuid = uuid;
   }

   public boolean func_213199_b(GameProfile p_213199_1_) {
      return p_213199_1_.getName().equalsIgnoreCase(this.getServerOwner());
   }
}
