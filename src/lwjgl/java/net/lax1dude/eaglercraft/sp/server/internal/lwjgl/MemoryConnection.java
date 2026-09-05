package net.lax1dude.eaglercraft.sp.server.internal.lwjgl;

import java.util.LinkedList;
import java.util.List;

import net.lax1dude.eaglercraft.internal.IPCPacketData;

public class MemoryConnection {

	public static final List<IPCPacketData> clientToServerQueue = new LinkedList<>();
	public static final List<IPCPacketData> serverToClientQueue = new LinkedList<>();

}
