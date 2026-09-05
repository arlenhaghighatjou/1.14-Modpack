package net.lax1dude.eaglercraft.sp.socket;

import net.lax1dude.eaglercraft.socket.protocol.GamePluginMessageConstants;
import net.lax1dude.eaglercraft.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.socket.protocol.client.GameProtocolMessageController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.login.IClientLoginNetHandler;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.login.server.SDisconnectLoginPacket;
import net.minecraft.network.login.server.SEnableCompressionPacket;
import net.minecraft.network.login.server.SEncryptionRequestPacket;
import net.minecraft.network.login.server.SLoginSuccessPacket;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Copyright (c) 2023-2024 lax1dude. All Rights Reserved.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
public class NetHandlerSingleplayerLogin implements IClientLoginNetHandler {

    private static final Logger logger = LogManager.getLogger("NetHandlerSingleplayerLogin");
    private final Minecraft mc;
    private final Screen previousGuiScreen;
    private final NetworkManager networkManager;

    public NetHandlerSingleplayerLogin(NetworkManager parNetworkManager, Minecraft mcIn, Screen parGuiScreen) {
        this.networkManager = parNetworkManager;
        this.mc = mcIn;
        this.previousGuiScreen = parGuiScreen;
    }

    public NetworkManager getNetworkManager() {
        return this.networkManager;
    }

    @Override
    public void onDisconnect(ITextComponent var1) {
        this.mc.displayGuiScreen(new DisconnectedScreen(this.previousGuiScreen, "connect.failed", var1));
    }

    @Override
    public void handleEncryptionRequest(SEncryptionRequestPacket var1) {

    }

    @Override
    public void handleLoginSuccess(SLoginSuccessPacket var1) {
        this.networkManager.setConnectionState(ProtocolType.PLAY);
        int p = var1.getSelectedProtocol();
        GamePluginMessageProtocol mp = GamePluginMessageProtocol.getByVersion(p);
        if (mp == null) {
            this.networkManager.closeChannel(new StringTextComponent("Unknown protocol selected: " + p));
            return;
        }
        logger.info("Server is using protocol: {}", p);
        ClientPlayNetHandler netHandler = new ClientPlayNetHandler(this.mc, this.previousGuiScreen, this.networkManager, var1.getProfile());
        netHandler.setEaglerMessageController(
                new GameProtocolMessageController(mp, GamePluginMessageConstants.CLIENT_TO_SERVER,
                        GameProtocolMessageController.createClientHandler(p, netHandler),
                        (ch, msg) -> netHandler.sendPacket(new CCustomPayloadPacket(new net.minecraft.util.ResourceLocation(GamePluginMessageConstants.toResourceLocation(ch)), msg))));
        this.networkManager.setNetHandler(netHandler);
    }

    @Override
    public void handleDisconnect(SDisconnectLoginPacket var1) {
        networkManager.closeChannel(var1.getReason());
    }

    @Override
    public void handleEnableCompression(SEnableCompressionPacket var1) {

    }

    @Override
    public void handleCustomPayloadLogin(net.minecraft.network.login.server.SCustomPayloadLoginPacket var1) {

    }

}
