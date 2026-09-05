package net.minecraft.network;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.network.handshake.ClientHandshakeNetHandler;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.network.handshake.ServerHandshakeNetHandler;
import net.minecraft.network.play.server.SDisconnectPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LazyLoadBase;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkSystem {
   private static final Logger LOGGER = LogManager.getLogger();

   private final MinecraftServer server;
   public volatile boolean isAlive;private final List<NetworkManager> networkManagers = Collections.synchronizedList(Lists.newArrayList());

   public NetworkSystem(MinecraftServer server) {
      this.server = server;
      this.isAlive = true;
   }

   public void addEndpoint( InetAddress address, int port) throws IOException {

   }

   public void terminateEndpoints() {

   }

   public void tick() {
      synchronized(this.networkManagers) {
         Iterator<NetworkManager> iterator = this.networkManagers.iterator();

         while(iterator.hasNext()) {
            NetworkManager networkmanager = iterator.next();
            if (!networkmanager.hasNoChannel()) {
               if (networkmanager.isChannelOpen()) {
                  try {
                     networkmanager.tick();
                  } catch (Exception exception) {
                     if (networkmanager.isLocalChannel()) {
                        CrashReport crashreport = CrashReport.makeCrashReport(exception, "Ticking memory connection");
                        CrashReportCategory crashreportcategory = crashreport.makeCategory("Ticking connection");
                        crashreportcategory.addDetail("Connection", networkmanager::toString);
                        throw new ReportedException(crashreport);
                     }

                     LOGGER.warn("Failed to handle packet for {}", networkmanager.getRemoteAddress(), exception);
                     ITextComponent itextcomponent = new StringTextComponent("Internal server error");
                     networkmanager.disableAutoRead();
                  }
               } else {
                  iterator.remove();
                  networkmanager.handleDisconnection();
               }
            }
         }

      }
   }

   public MinecraftServer getServer() {
      return this.server;
   }
}
