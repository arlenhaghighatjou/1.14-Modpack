package net.lax1dude.eaglercraft.sp.server;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.EagUtils;
import net.lax1dude.eaglercraft.internal.IPCPacketData;
import net.lax1dude.eaglercraft.internal.PlatformAssets;
import net.lax1dude.eaglercraft.sp.SingleplayerServerController;
import net.lax1dude.eaglercraft.sp.ipc.*;
import net.lax1dude.eaglercraft.sp.server.internal.ServerPlatformSingleplayer;
import net.lax1dude.eaglercraft.sp.server.socket.IntegratedServerPlayerNetworkManager;
import net.minecraft.crash.ReportedException;
import net.minecraft.network.ProtocolType;
import net.minecraft.server.network.NetHandlerLoginServer;
import net.minecraft.util.registry.Bootstrap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.SaveFormat;
import net.peyton.eagler.fs.FileUtils;
import org.apache.logging.log4j.ILogRedirector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EaglerIntegratedServerWorker {

    public static final Logger logger = LogManager.getLogger("EaglerIntegratedServer");
    private static final Map<String, IntegratedServerPlayerNetworkManager> openChannels = new HashMap();
    private static final IPCPacketManager packetManagerInstance = new IPCPacketManager();
    public static SaveFormat anvilConverter;
    private static EaglerMinecraftServer currentProcess = null;
    private static WorldSettings newWorldSettings = null;

    private static void processAsyncMessageQueue() {
        List<IPCPacketData> pktList = ServerPlatformSingleplayer.recieveAllPacket();
        if (pktList != null) {
            IPCPacketData packetData;
            for (int i = 0, l = pktList.size(); i < l; ++i) {
                packetData = pktList.get(i);
                if (packetData.channel.equals(SingleplayerServerController.IPC_CHANNEL)) {
                    if (packetData.contents == null || packetData.contents.length == 0) {
                        logger.error("Recieved empty IPC packet from client, skipping");
                        continue;
                    }
                    IPCPacketBase ipc;
                    try {
                        ipc = packetManagerInstance.IPCDeserialize(packetData.contents);
                    } catch (IOException ex) {
                        throw new RuntimeException("Failed to deserialize IPC packet", ex);
                    }
                    handleIPCPacket(ipc);
                } else {
                    IntegratedServerPlayerNetworkManager netHandler = openChannels.get(packetData.channel);
                    if (netHandler != null) {
                        netHandler.addRecievedPacket(packetData.contents);
                    } else {
                        logger.error("Recieved packet on channel that does not exist: \"{}\"", packetData.channel);
                    }
                }
            }
        }

        if (!ServerPlatformSingleplayer.isSingleThreadMode() && ServerPlatformSingleplayer.isTabAboutToCloseWASM()
                && !isServerStopped()) {
            logger.info("Autosaving worlds because the tab is about to close!");
            currentProcess.getPlayerList().saveAllPlayerData();
            currentProcess.saveAllWorlds(false);
        }
    }

    public static void tick() {
        List<IntegratedServerPlayerNetworkManager> ocs = new ArrayList<>(openChannels.values());
        for (int i = 0, l = ocs.size(); i < l; ++i) {
            ocs.get(i).tick();
        }
    }

    public static EaglerMinecraftServer getServer() {
        return currentProcess;
    }

    public static boolean getChannelExists(String channel) {
        return openChannels.containsKey(channel);
    }

    public static void closeChannel(String channel) {
        IntegratedServerPlayerNetworkManager netmanager = openChannels.remove(channel);
        if (netmanager != null) {
            netmanager.closeChannel(new StringTextComponent("End of stream"));
            sendIPCPacket(new IPCPacket0CPlayerChannel(channel, false));
        }
    }

    private static void startPlayerConnnection(String channel) {
        if (openChannels.containsKey(channel)) {
            logger.error("Tried opening player channel that already exists: {}", channel);
            return;
        }
        if (currentProcess == null) {
            logger.error("Tried opening player channel while server is stopped: {}", channel);
            return;
        }
        IntegratedServerPlayerNetworkManager networkmanager = new IntegratedServerPlayerNetworkManager(channel);
        networkmanager.setConnectionState(ProtocolType.LOGIN);
        networkmanager.setNetHandler(new NetHandlerLoginServer(currentProcess, networkmanager));
        openChannels.put(channel, networkmanager);
    }

    private static void handleIPCPacket(IPCPacketBase ipc) {
        int id = ipc.id();
        try {
            switch (id) {
                case IPCPacket00StartServer.ID: {
                    IPCPacket00StartServer pkt = (IPCPacket00StartServer) ipc;

                    if (currentProcess != null) {
                        currentProcess.stopServer();
                    }

                    Bootstrap.register();

                    currentProcess = new EaglerMinecraftServer(pkt.mcDataDir, pkt.folderName, pkt.worldName, pkt.ownerName,
                            pkt.initialViewDistance, newWorldSettings, pkt.demoMode, anvilConverter);
                    currentProcess.setBaseServerProperties(Difficulty.byId(pkt.initialDifficulty),
                            newWorldSettings == null ? GameType.SURVIVAL : newWorldSettings.getGameType());
                    currentProcess.init();

                    String[] worlds = FileUtils.worldsList.getAllLines();
                    if (worlds == null || (worlds.length == 1 && worlds[0].trim().isEmpty())) {
                        FileUtils.worldsList.setAllChars(pkt.folderName);
                    } else {
                        String[] s = new String[worlds.length + 1];
                        s[0] = pkt.folderName;
                        System.arraycopy(worlds, 0, s, 1, worlds.length);
                        FileUtils.worldsList.setAllChars(String.join("\n", s));
                        FileUtils.formatWorldList(FileUtils.worldsList.getAllLines());
                    }

                    sendIPCPacket(new IPCPacketFFProcessKeepAlive(IPCPacket00StartServer.ID));
                    break;
                }
                case IPCPacketMapAssets.ID: {
                    IPCPacketMapAssets epk = (IPCPacketMapAssets) ipc;
                    Map<String, byte[]> existing = PlatformAssets.getServerAssetMap();
                    if (existing != null && !existing.isEmpty()) {
                        existing.putAll(epk.assets);
                    } else {
                        PlatformAssets.setServerAssetMap(epk.assets);
                    }
                    break;
                }
                case IPCPacket01StopServer.ID: {
                    if (currentProcess != null) {
                        currentProcess.stopServer();
                        currentProcess = null;
                    }
                    sendIPCPacket(new IPCPacketFFProcessKeepAlive(IPCPacket01StopServer.ID));
                    break;
                }
                case IPCPacket02InitWorld.ID: {
                    tryStopServer();
                    IPCPacket02InitWorld pkt = (IPCPacket02InitWorld) ipc;
                    newWorldSettings = new WorldSettings(pkt.seed, GameType.getByID(pkt.gamemode), pkt.structures,
                            pkt.hardcore, WorldType.WORLD_TYPES[pkt.worldType]);
                    newWorldSettings.setGeneratorOptions(pkt.worldArgs != null ? new com.google.gson.JsonParser().parse(pkt.worldArgs) : null);
                    if (pkt.bonusChest) {
                        newWorldSettings.enableBonusChest();
                    }
                    if (pkt.cheats) {
                        newWorldSettings.enableCommands();
                    }
                    break;
                }
                case IPCPacket03DeleteWorld.ID: {
                    break;
                }
                case IPCPacket05RequestData.ID: {
                    break;
                }
                case IPCPacket06RenameWorldNBT.ID: {
                    break;
                }
                case IPCPacket07ImportWorld.ID: {
                    tryStopServer();
                    anvilConverter = new EaglerSaveFormat(net.peyton.eagler.fs.WorldsDB.newVFile(""), net.minecraft.util.datafix.DataFixesManager.getDataFixer());
                    IPCPacket07ImportWorld pkt = (IPCPacket07ImportWorld) ipc;
                    try {
                        if (pkt.worldFormat == IPCPacket07ImportWorld.WORLD_FORMAT_EAG) {
                            net.lax1dude.eaglercraft.sp.server.export.WorldConverterEPK.importWorld(pkt.worldData, pkt.worldName);
                        } else if (pkt.worldFormat == IPCPacket07ImportWorld.WORLD_FORMAT_MCA) {
                            net.lax1dude.eaglercraft.sp.server.export.WorldConverterMCA.importWorld(pkt.worldData, pkt.worldName);
                        } else {
                            throw new IOException("Client requested an unsupported export format!");
                        }
                        sendIPCPacket(new IPCPacketFFProcessKeepAlive(IPCPacket07ImportWorld.ID));
                    } catch (IOException ex) {
                        sendIPCPacket(new IPCPacket15Crashed("COULD NOT IMPORT WORLD \"" + pkt.worldName + "\"!!!\n\n" + EagRuntime.getStackTrace(ex) + "\n\nFile is probably corrupt, try a different world"));
                        sendTaskFailed();
                    }
                    break;
                }
                case IPCPacket0ASetWorldDifficulty.ID: {
                    IPCPacket0ASetWorldDifficulty pkt = (IPCPacket0ASetWorldDifficulty) ipc;
                    if (!isServerStopped()) {
                        if (pkt.difficulty == (byte) -1) {
                            currentProcess.setDifficultyLockedForAllWorlds(true);
                        } else {
                            currentProcess.setDifficultyForAllWorlds(Difficulty.byId(pkt.difficulty), true);
                        }
                    } else {
                        logger.warn("Client tried to set difficulty while server was stopped");
                    }
                    break;
                }
                case IPCPacket0BPause.ID: {
                    IPCPacket0BPause pkt = (IPCPacket0BPause) ipc;
                    if (!isServerStopped()) {
                        currentProcess.setPaused(pkt.pause);
                        sendIPCPacket(new IPCPacketFFProcessKeepAlive(IPCPacket0BPause.ID));
                    } else {
                        logger.error("Client tried to {} while server was stopped", pkt.pause ? "pause" : "unpause");
                        sendTaskFailed();
                    }
                    break;
                }
                case IPCPacket0CPlayerChannel.ID: {
                    IPCPacket0CPlayerChannel pkt = (IPCPacket0CPlayerChannel) ipc;
                    if (!isServerStopped()) {
                        if (pkt.open) {
                            startPlayerConnnection(pkt.channel);
                        } else {
                            closeChannel(pkt.channel);
                        }
                    } else {
                        logger.error("Client tried to {} channel server was stopped", pkt.open ? "open" : "close");
                    }
                    break;
                }
                case IPCPacket0EListWorlds.ID: {
                    break;
                }
                case IPCPacket14StringList.ID: {
                    IPCPacket14StringList pkt = (IPCPacket14StringList) ipc;
                    switch (pkt.opCode) {
                        case IPCPacket14StringList.LOCALE:
                            LanguageMap.initServer(pkt.stringList);
                            break;
                        default:
                            logger.error("Strange string list 0x{} with length{} recieved", Integer.toHexString(pkt.opCode),
                                    pkt.stringList.size());
                            break;
                    }
                    break;
                }
                case IPCPacket17ConfigureLAN.ID: {
                    break;
                }
                case IPCPacket18ClearPlayers.ID: {
                    break;
                }
                case IPCPacket19Autosave.ID: {
                    if (!isServerStopped()) {
                        currentProcess.getPlayerList().saveAllPlayerData();
                        currentProcess.saveAllWorlds(false);
                        sendIPCPacket(new IPCPacketFFProcessKeepAlive(IPCPacket19Autosave.ID));
                    } else {
                        logger.error("Client tried to autosave while server was stopped");
                        sendTaskFailed();
                    }
                    break;
                }
                case IPCPacket1BEnableLogging.ID: {
                    enableLoggingRedirector(((IPCPacket1BEnableLogging) ipc).enable);
                    break;
                }
                default:
                    logger.error("IPC packet type 0x{} class \"{}\" was not handled", Integer.toHexString(id),
                            ipc.getClass().getSimpleName());
                    sendTaskFailed();
                    break;
            }
        } catch (Throwable t) {
            logger.error("IPC packet type 0x{} class \"{}\" was not processed correctly", Integer.toHexString(id),
                    ipc.getClass().getSimpleName());
            logger.error(t);
            t.printStackTrace();
            sendIPCPacket(new IPCPacket15Crashed(
                    "IPC packet type 0x" + Integer.toHexString(id) + " class \"" + ipc.getClass().getSimpleName()
                            + "\" was not processed correctly!\n\n" + EagRuntime.getStackTrace(t)));
            sendTaskFailed();
        }
    }

    public static void enableLoggingRedirector(boolean en) {
        LogManager.logRedirector = en ? new ILogRedirector() {
            @Override
            public void log(String txt, boolean err) {
                sendLogMessagePacket(txt, err);
            }
        } : null;
    }

    public static void sendLogMessagePacket(String txt, boolean err) {
        sendIPCPacket(new IPCPacket1ALoggerMessage(txt, err));
    }

    public static void sendIPCPacket(IPCPacketBase ipc) {
        byte[] pkt;
        try {
            pkt = packetManagerInstance.IPCSerialize(ipc);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to serialize IPC packet", ex);
        }
        ServerPlatformSingleplayer.sendPacket(new IPCPacketData(SingleplayerServerController.IPC_CHANNEL, pkt));
    }

    public static void sendTaskFailed() {
        sendIPCPacket(new IPCPacketFFProcessKeepAlive(IPCPacketFFProcessKeepAlive.FAILURE));
    }

    public static void sendProgress(String updateMessage, float updateProgress) {
        sendIPCPacket(new IPCPacket0DProgressUpdate(updateMessage, updateProgress));
    }

    private static boolean isServerStopped() {
        return currentProcess == null || !currentProcess.isServerRunning();
    }

    private static void tryStopServer() {
        if (currentProcess != null) {
            currentProcess.stopServer();
        }
        currentProcess = null;
    }

    private static void mainLoop(boolean singleThreadMode) {
        processAsyncMessageQueue();

        if (currentProcess != null) {
            if (currentProcess.isServerRunning()) {
                currentProcess.mainLoop(singleThreadMode);
            }
            if (!currentProcess.isServerRunning()) {
                currentProcess.stopServer();
                currentProcess = null;
                sendIPCPacket(new IPCPacketFFProcessKeepAlive(IPCPacket01StopServer.ID));
            }
        } else {
            if (!singleThreadMode) {
                EagUtils.sleep(50);
            }
        }
    }

    public static void serverMain() {
        try {
            currentProcess = null;
            logger.info("Starting EaglercraftX integrated server worker...");

//			if(ServerPlatformSingleplayer.getWorldsDatabase().isRamdisk()) {
//				sendIPCPacket(new IPCPacket1CIssueDetected(IPCPacket1CIssueDetected.ISSUE_RAMDISK_MODE));
//			}

            // signal thread startup successful
            sendIPCPacket(new IPCPacketFFProcessKeepAlive(0xFF));

            ServerPlatformSingleplayer.setCrashCallbackWASM(EaglerIntegratedServerWorker::sendIntegratedServerCrashWASMCB);

            while (true) {
                mainLoop(false);
                ServerPlatformSingleplayer.immediateContinue();
            }
        } catch (Throwable tt) {
            if (tt instanceof ReportedException) {
                String fullReport = ((ReportedException) tt).getCrashReport().getCompleteReport();
                logger.error(fullReport);
                sendIPCPacket(new IPCPacket15Crashed(fullReport));
            } else {
                logger.error("Server process encountered a fatal error!");
                tt.printStackTrace();
                String stackTrace = EagRuntime.getStackTrace(tt);
                logger.error(stackTrace);
                sendIPCPacket(new IPCPacket15Crashed("SERVER PROCESS EXITED!\n\n" + stackTrace));
            }
        } finally {
            if (!isServerStopped()) {
                try {
                    currentProcess.stopServer();
                } catch (Throwable t) {
                    logger.error("Encountered exception while stopping server!");
                    logger.error(EagRuntime.getStackTrace(t));
                }
            }
            logger.error("Server process exited!");
            sendIPCPacket(new IPCPacketFFProcessKeepAlive(IPCPacketFFProcessKeepAlive.EXITED));
        }
    }

    public static void singleThreadMain() {
        logger.info("Starting EaglercraftX integrated server worker...");
        if (ServerPlatformSingleplayer.getWorldsDatabase().isRamdisk()) {
            sendIPCPacket(new IPCPacket1CIssueDetected(IPCPacket1CIssueDetected.ISSUE_RAMDISK_MODE));
        }
        sendIPCPacket(new IPCPacketFFProcessKeepAlive(0xFF));
    }

    public static void singleThreadUpdate() {
        mainLoop(true);
    }

    public static void sendIntegratedServerCrashWASMCB(String stringValue, boolean terminated) {
        sendIPCPacket(new IPCPacket15Crashed(stringValue));
        if (terminated) {
            sendIPCPacket(new IPCPacketFFProcessKeepAlive(IPCPacketFFProcessKeepAlive.EXITED));
        }
    }

    public static void reportTPS(List<String> texts) {
        sendIPCPacket(new IPCPacket14StringList(IPCPacket14StringList.SERVER_TPS, texts));
    }

}
