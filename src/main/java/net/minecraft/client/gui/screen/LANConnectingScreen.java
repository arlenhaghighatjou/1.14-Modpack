package net.minecraft.client.gui.screen;

import net.lax1dude.eaglercraft.internal.PlatformWebRTC;
import net.lax1dude.eaglercraft.sp.lan.LANClientNetworkManager;
import net.lax1dude.eaglercraft.sp.relay.RelayManager;
import net.lax1dude.eaglercraft.sp.relay.RelayServer;
import net.lax1dude.eaglercraft.sp.relay.RelayServerSocket;
import net.lax1dude.eaglercraft.sp.gui.ScreenNoRelays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.login.client.CLoginStartPacket;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.io.IOException;

public class LANConnectingScreen extends Screen {

    private final Screen parent;
    private final String code;
    private final RelayServer relay;

    private boolean completed = false;

    private LANClientNetworkManager networkManager = null;

    private int renderCount = 0;

    public LANConnectingScreen(Screen parent, String code) {
        super(new TranslationTextComponent("lanServer.title"));
        this.parent = parent;
        this.code = code;
        this.relay = null;
    }

    public LANConnectingScreen(Screen parent, String code, RelayServer relay) {
        super(new TranslationTextComponent("lanServer.title"));
        this.parent = parent;
        this.code = code;
        this.relay = relay;
        Minecraft.getInstance().setServerData(new ServerData("Shared World", "shared:" + relay.address, false));
    }

    public boolean isPauseScreen() {
        return false;
    }

    public void tick() {
        if(networkManager != null) {
            if (networkManager.isChannelOpen()) {
                try {
                    networkManager.processReceivedPackets();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                if (networkManager.checkDisconnected()) {
                    if (mc.currentScreen == this) {
                        mc.loadWorld(null);
                        mc.displayGuiScreen(new DisconnectedScreen(parent, "connect.failed", new StringTextComponent("LAN Connection Refused")));
                    }
                }
            }
        }
    }

    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        if(completed) {
            String message = I18n.format("connect.authorizing");
            this.drawCenteredString(font, message, this.width / 2, this.height / 3 + 10, 0xFFFFFF);
        }else {
            String message = I18n.format("lanServer.pleaseWait");
            this.drawCenteredString(font, message, this.width / 2, this.height / 3 + 10, 0xFFFFFF);

            PlatformWebRTC.supported();
            PlatformWebRTC.startRTCLANClient();

            if(++renderCount > 1) {
                RelayServerSocket sock;
                if(relay == null) {
                    sock = RelayManager.relayManager.getWorkingRelay((str) -> {}, 0x02, code);
                }else {
                    sock = RelayManager.relayManager.connectHandshake(relay, 0x02, code);
                }
                if(sock == null) {
                    this.mc.displayGuiScreen(new ScreenNoRelays(parent));
                    return;
                }

                networkManager = LANClientNetworkManager.connectToWorld(sock, code, sock.getURI());
                if(networkManager == null) {
                    this.mc.displayGuiScreen(new DisconnectedScreen(parent, "connect.failed", new StringTextComponent(I18n.format("noRelay.worldFail").replace("$code$", code))));
                    return;
                }

                completed = true;

                networkManager.setConnectionState(ProtocolType.LOGIN);
                networkManager.setNetHandler(new net.minecraft.client.network.login.ClientLoginNetHandler(networkManager, mc, parent, (str) -> {}));
                networkManager.sendPacket(new CLoginStartPacket(this.mc.getSession().getProfile(), new byte[0], new byte[0], new byte[0], net.lax1dude.eaglercraft.EaglercraftUUID.randomUUID()));
            }
        }
        super.render(mouseX, mouseY, partialTicks);
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }
}
