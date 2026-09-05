package net.lax1dude.eaglercraft.sp.server.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.Filesystem;
import net.lax1dude.eaglercraft.internal.IClientConfigAdapter;
import net.lax1dude.eaglercraft.internal.IEaglerFilesystem;
import net.lax1dude.eaglercraft.internal.IPCPacketData;
import net.lax1dude.eaglercraft.internal.lwjgl.DesktopClientConfigAdapter;
import net.lax1dude.eaglercraft.sp.server.IWASMCrashCallback;
import net.lax1dude.eaglercraft.sp.server.internal.lwjgl.MemoryConnection;

public class ServerPlatformSingleplayer {

	private static IEaglerFilesystem filesystem = null;

	public static void initializeContext() {
		if (filesystem == null) {
			filesystem = Filesystem.getHandleFor(getClientConfigAdapter().getWorldsDB());
		}
	}

	public static void initializeContextSingleThread(Consumer<IPCPacketData> packetSendCallback) {
		throw new UnsupportedOperationException();
	}

	public static IEaglerFilesystem getWorldsDatabase() {
		return filesystem;
	}

	public static void sendPacket(IPCPacketData packet) {
		synchronized (MemoryConnection.serverToClientQueue) {
			MemoryConnection.serverToClientQueue.add(packet);
		}
	}

	public static IPCPacketData recievePacket() {
		synchronized (MemoryConnection.clientToServerQueue) {
			if (MemoryConnection.clientToServerQueue.size() > 0) {
				return MemoryConnection.clientToServerQueue.remove(0);
			}
		}
		return null;
	}

	public static List<IPCPacketData> recieveAllPacket() {
		synchronized (MemoryConnection.clientToServerQueue) {
			if (MemoryConnection.clientToServerQueue.size() == 0) {
				return null;
			} else {
				List<IPCPacketData> ret = new ArrayList<>(MemoryConnection.clientToServerQueue);
				MemoryConnection.clientToServerQueue.clear();
				return ret;
			}
		}
	}

	public static IClientConfigAdapter getClientConfigAdapter() {
		return DesktopClientConfigAdapter.instance;
	}

	public static void immediateContinue() {

	}

	public static void platformShutdown() {
		filesystem = null;
	}

	public static boolean isSingleThreadMode() {
		return false;
	}

	public static void setCrashCallbackWASM(IWASMCrashCallback callback) {

	}

	public static boolean isTabAboutToCloseWASM() {
		return false;
	}

}
