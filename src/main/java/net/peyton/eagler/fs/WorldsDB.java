package net.peyton.eagler.fs;

import net.lax1dude.eaglercraft.internal.IEaglerFilesystem;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.sp.server.internal.ServerPlatformSingleplayer;

import java.util.function.Supplier;

public class WorldsDB {

    private static Supplier<IEaglerFilesystem> fsGetter = WorldsDB::getServerWorldsDB;

    private static IEaglerFilesystem getServerWorldsDB() {
        return ServerPlatformSingleplayer.getWorldsDatabase();
    }

    /**
     * Override the filesystem provider for WorldsDB VFile2 objects.
     * This is used on the WASM-GC main thread where ServerPlatformSingleplayer
     * is not initialized (it only runs in the worker).
     */
    public static void setWorldsDBProvider(Supplier<IEaglerFilesystem> provider) {
        fsGetter = provider;
    }
    public static VFile2 newVFile(Object... path) {
        return VFile2.create(fsGetter, path);
    }

}
