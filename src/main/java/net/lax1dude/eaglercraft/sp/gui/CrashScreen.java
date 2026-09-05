package net.lax1dude.eaglercraft.sp.gui;

import net.lax1dude.eaglercraft.sp.internal.ClientPlatformSingleplayer;

public class CrashScreen {

    public static void showCrashReportOverlay(String report, int x, int y, int w, int h) {
        ClientPlatformSingleplayer.showCrashReportOverlay(report, x, y, w, h);
    }

    public static void hideCrashReportOverlay() {
        ClientPlatformSingleplayer.hideCrashReportOverlay();
    }

}
