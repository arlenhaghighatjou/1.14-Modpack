package net.lax1dude.eaglercraft.sp.internal;

import net.lax1dude.eaglercraft.internal.IPCPacketData;
import net.lax1dude.eaglercraft.internal.PlatformRuntime;
import net.lax1dude.eaglercraft.sp.server.internal.lwjgl.CrashScreenPopup;
import net.lax1dude.eaglercraft.sp.server.internal.lwjgl.DesktopIntegratedServer;
import net.lax1dude.eaglercraft.sp.server.internal.lwjgl.MemoryConnection;

import java.util.ArrayList;
import java.util.List;

public class ClientPlatformSingleplayer {

    private static CrashScreenPopup crashOverlay = null;

    public static void startIntegratedServer(boolean forceSingleThread) {
        DesktopIntegratedServer.startIntegratedServer();
    }

    public static void sendPacket(IPCPacketData packet) {
        synchronized (MemoryConnection.clientToServerQueue) {
            MemoryConnection.clientToServerQueue.add(packet);
        }
    }

    public static IPCPacketData recievePacket() {
        synchronized (MemoryConnection.serverToClientQueue) {
            if (MemoryConnection.serverToClientQueue.size() > 0) {
                return MemoryConnection.serverToClientQueue.remove(0);
            }
        }
        return null;
    }

    public static List<IPCPacketData> recieveAllPacket() {
        synchronized (MemoryConnection.serverToClientQueue) {
            if (MemoryConnection.serverToClientQueue.size() == 0) {
                return null;
            } else {
                List<IPCPacketData> ret = new ArrayList<>(MemoryConnection.serverToClientQueue);
                MemoryConnection.serverToClientQueue.clear();
                return ret;
            }
        }
    }

    public static boolean canKillWorker() {
        return false;
    }

    public static void killWorker() {
        throw new IllegalStateException("Cannot kill worker thread on desktop! (memleak)");
    }

    public static boolean isRunningSingleThreadMode() {
        return false;
    }

    public static boolean isSingleThreadModeSupported() {
        return false;
    }

    public static void updateSingleThreadMode() {

    }

    public static void showCrashReportOverlay(String report, int x, int y, int w, int h) {
        if (crashOverlay == null) {
            crashOverlay = new CrashScreenPopup();
        }
        int[] wx = new int[1];
        int[] wy = new int[1];
        PlatformRuntime.getWindowXY(wx, wy);
        crashOverlay.setBounds(wx[0] + x, wy[0] + y, w, h);
        crashOverlay.setCrashText(report);
        crashOverlay.setVisible(true);
        crashOverlay.requestFocus();
    }

    public static void hideCrashReportOverlay() {
        crashOverlay.setVisible(false);
    }

}
