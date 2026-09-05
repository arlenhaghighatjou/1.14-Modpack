package net.minecraft.client.network;

import com.google.common.collect.Lists;
import net.lax1dude.eaglercraft.internal.EnumServerRateLimit;
import net.lax1dude.eaglercraft.internal.QueryResponse;
import net.lax1dude.eaglercraft.socket.ServerQueryDispatch;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ServerPinger {
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<ServerData> pingDestinations = Collections.synchronizedList(Lists.newArrayList());

    public void ping(final ServerData server) throws UnknownHostException {
        if (server.pingSentTime <= 0l) {
            server.pingSentTime = Util.milliTime();
            server.pingToServer = -2l;
            String addr = server.serverIP;
            server.currentQuery = ServerQueryDispatch.sendServerQuery(addr, "MOTD");
            if (server.currentQuery == null) {
                server.pingToServer = -1l;
                server.hasPing = true;
            } else {
                this.pingDestinations.add(server);
            }
        }
    }

    public void pingPendingNetworks() {
        synchronized (this.pingDestinations) {
            Iterator<ServerData> iterator = this.pingDestinations.iterator();

            while (iterator.hasNext()) {
                ServerData dat = iterator.next();
                if (dat.currentQuery != null) {
                    dat.currentQuery.update();
                    if (!dat.hasPing) {
                        EnumServerRateLimit rateLimit = dat.currentQuery.getRateLimit();
                        if (rateLimit != EnumServerRateLimit.OK) {
                            dat.serverMOTD = TextFormatting.RED + "Too Many Requests!\nTry again later";
                            dat.pingToServer = -1l;
                            dat.hasPing = true;
                            iterator.remove();
                            continue;
                        }
                    }
                    if (dat.currentQuery.responsesAvailable() > 0) {
                        QueryResponse pkt;
                        do {
                            pkt = dat.currentQuery.getResponse();
                        } while (dat.currentQuery.responsesAvailable() > 0);
                        if (pkt.responseType.equalsIgnoreCase("MOTD") && pkt.isResponseJSON()) {
                            dat.setMOTDFromQuery(pkt);
                            if (!dat.hasPing) {
                                dat.pingToServer = pkt.ping;
                                dat.hasPing = true;
                            }
                        }
                    }
                    if (dat.currentQuery.binaryResponsesAvailable() > 0) {
                        byte[] r;
                        do {
                            r = dat.currentQuery.getBinaryResponse();
                        } while (dat.currentQuery.binaryResponsesAvailable() > 0);
                        dat.setIconPacket(r);
                    }
                    if (!dat.currentQuery.isOpen()) {
                        if (!dat.hasPing && dat.pingSentTime > 0l && (Util.milliTime() - dat.pingSentTime) > 2000l) {
                            dat.pingToServer = -1l;
                            dat.hasPing = true;
                        }
                        iterator.remove();
                    }
                } else {
                    iterator.remove();
                }
            }
        }
    }

    public void clearPendingNetworks() {
        synchronized (this.pingDestinations) {
            Iterator<ServerData> iterator = this.pingDestinations.iterator();

            while (iterator.hasNext()) {
                ServerData dat = iterator.next();
                if (dat.currentQuery != null && dat.currentQuery.isOpen()) {
                    dat.currentQuery.close();
                }
                dat.currentQuery = null;
                iterator.remove();
            }
        }
    }
}
