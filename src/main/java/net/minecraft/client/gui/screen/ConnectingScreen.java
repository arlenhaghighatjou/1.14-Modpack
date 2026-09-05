package net.minecraft.client.gui.screen;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.cookie.ServerCookieDataStore;
import net.lax1dude.eaglercraft.internal.EnumEaglerConnectionState;
import net.lax1dude.eaglercraft.internal.IWebSocketClient;
import net.lax1dude.eaglercraft.internal.IWebSocketFrame;
import net.lax1dude.eaglercraft.internal.PlatformNetworking;
import net.lax1dude.eaglercraft.profile.EaglerProfile;
import net.lax1dude.eaglercraft.socket.AddressResolver;
import net.lax1dude.eaglercraft.socket.RateLimitTracker;
import net.lax1dude.eaglercraft.socket.protocol.handshake.HandshakerHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ConnectingScreen extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();
    private IWebSocketClient webSocket;
    private HandshakerHandler handshaker;
    private String currentAddress;
    private String currentPassword;
    private boolean allowPlaintext;
    private boolean allowCookies;
    private boolean cancel;
    private final Screen previousGuiScreen;
    private ITextComponent field_209515_s = new TranslationTextComponent("connect.connecting");
    private long field_213000_g = -1L;
    private int timer = 0;

    public ConnectingScreen(Screen parent, Minecraft mcIn, ServerData serverDataIn) {
        this(parent, mcIn, serverDataIn, null, false);
    }

    public ConnectingScreen(Screen parent, Minecraft mcIn, ServerData serverDataIn, String password, boolean allowPlaintext) {
        super(NarratorChatListener.field_216868_a);
        this.mc = mcIn;
        this.previousGuiScreen = parent;
        String serveraddress = AddressResolver.resolveURI(serverDataIn);
        mcIn.func_213254_o();
        mcIn.setServerData(serverDataIn);
        if (RateLimitTracker.isLockedOut(serveraddress)) {
            LOGGER.error("Server locked this client out on a previous connection, will not attempt to reconnect");
        } else {
            this.connect(serveraddress, password, allowPlaintext,
                    serverDataIn.enableCookies && EagRuntime.getConfiguration().isEnableServerCookies());
        }
    }

    public ConnectingScreen(Screen parent, Minecraft mcIn, String hostName, int port) {
        super(NarratorChatListener.field_216868_a);
        this.mc = mcIn;
        this.previousGuiScreen = parent;
        mcIn.func_213254_o();
        this.connect(hostName, null, false,
                EagRuntime.getConfiguration().isEnableServerCookies());
    }

    public ConnectingScreen(Screen parent, Minecraft mcIn, String hostName, int port, String password) {
        super(NarratorChatListener.field_216868_a);
        this.mc = mcIn;
        this.previousGuiScreen = parent;
        mcIn.func_213254_o();
        this.connect(hostName, password, false,
                EagRuntime.getConfiguration().isEnableServerCookies());
    }

    public ConnectingScreen(Screen parent, Minecraft mcIn, String hostName, int port, String password, boolean allowPlaintext) {
        super(NarratorChatListener.field_216868_a);
        this.mc = mcIn;
        this.previousGuiScreen = parent;
        mcIn.func_213254_o();
        this.connect(hostName, password, allowPlaintext,
                EagRuntime.getConfiguration().isEnableServerCookies());
    }

    public Screen getPreviousGuiScreen() {
        return this.previousGuiScreen;
    }

    private void connect(String ip, String password, boolean allowPlaintext, boolean allowCookies) {
        this.currentAddress = ip;
        this.currentPassword = password;
        this.allowPlaintext = allowPlaintext;
        this.allowCookies = allowCookies;
    }

    public void func_209514_a(ITextComponent p_209514_1_) {
        this.field_209515_s = p_209514_1_;
    }

    public void tick() {
        ++timer;
        if (timer > 1) {
            if (this.currentAddress == null) {
                mc.displayGuiScreen(new DisconnectedScreen(previousGuiScreen, "connect.failed",
                        new StringTextComponent("Too Many Requests!\nTry again later")));
            } else if (webSocket == null) {
                LOGGER.info("Connecting to: {}", currentAddress);
                webSocket = PlatformNetworking.openWebSocket(currentAddress);
                if (webSocket == null) {
                    mc.displayGuiScreen(new DisconnectedScreen(previousGuiScreen, "connect.failed",
                            new StringTextComponent("Could not open WebSocket to \"" + currentAddress + "\"!")));
                }
            } else {
                EnumEaglerConnectionState connState = webSocket.getState();
                if (connState == EnumEaglerConnectionState.CONNECTED) {
                    if (handshaker == null) {

                        LOGGER.info("Logging in: {}", currentAddress);

                        byte[] cookieData = null;
                        if (allowCookies) {
                            ServerCookieDataStore.ServerCookie cookie = ServerCookieDataStore
                                    .loadCookie(currentAddress);
                            if (cookie != null) {
                                cookieData = cookie.cookie;
                            }
                        }

                        handshaker = new HandshakerHandler(this, webSocket, EaglerProfile.getName(), currentPassword,
                                allowPlaintext, allowCookies, cookieData);
                    }
                    handshaker.tick();
                } else {
                    if (handshaker != null) {
                        handshaker.tick();
                        if (connState == EnumEaglerConnectionState.FAILED) {
                            checkRatelimit();
                            if (mc.currentScreen == this) {
                                if (RateLimitTracker.isProbablyLockedOut(currentAddress)) {
                                    mc.displayGuiScreen(new DisconnectedScreen(previousGuiScreen, "connect.failed",
                                            new StringTextComponent("Too Many Requests!\nTry again later")));
                                } else {
                                    mc.displayGuiScreen(new DisconnectedScreen(previousGuiScreen, "connect.failed",
                                            new StringTextComponent("Connection Refused")));
                                }
                            }
                        }
                    }
                }
            }
            if (timer > 200) {
                if (webSocket != null) {
                    webSocket.close();
                }
                mc.displayGuiScreen(new DisconnectedScreen(previousGuiScreen, "connect.failed",
                        new StringTextComponent("Handshake timed out")));
            }
        }
    }

    private void checkRatelimit() {
        if (this.webSocket != null) {
            List<IWebSocketFrame> strFrames = webSocket.getNextStringFrames();
            if (strFrames != null) {
                for (int i = 0; i < strFrames.size(); ++i) {
                    String str = strFrames.get(i).getString();
                    if (str.equalsIgnoreCase("BLOCKED")) {
                        RateLimitTracker.registerBlock(currentAddress);
                        mc.displayGuiScreen(new DisconnectedScreen(previousGuiScreen, "connect.failed",
                                new StringTextComponent("Too Many Requests!\nTry again later")));
                        LOGGER.info("Handshake Failure: Too Many Requests!");
                    } else if (str.equalsIgnoreCase("LOCKED")) {
                        RateLimitTracker.registerLockOut(currentAddress);
                        mc.displayGuiScreen(new DisconnectedScreen(previousGuiScreen, "connect.failed",
                                new StringTextComponent("Too Many Requests!\nTry again later")));
                        LOGGER.info("Handshake Failure: Too Many Requests!");
                        LOGGER.info("Server has locked this client out");
                    }
                }
            }
        }
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    protected void init() {
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, I18n.format("gui.cancel"), (p_212999_1_) -> {
            this.cancel = true;
            if (this.webSocket != null) {
                this.webSocket.close();
            }
            this.mc.displayGuiScreen(this.previousGuiScreen);
        }));
    }

    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground();
        long i = Util.milliTime();
        if (i - this.field_213000_g > 2000L) {
            this.field_213000_g = i;
            NarratorChatListener.INSTANCE.func_216864_a((new TranslationTextComponent("narrator.joining")).getString());
        }

        if (this.handshaker == null) {
            this.drawCenteredString(this.font, I18n.format("connect.connecting"), this.width / 2, this.height / 2 - 50, 16777215);
        } else {
            this.drawCenteredString(this.font, I18n.format("connect.authorizing"), this.width / 2, this.height / 2 - 50, 16777215);
        }
        super.render(p_render_1_, p_render_2_, p_render_3_);
    }
}
