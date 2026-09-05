package net.minecraft.client.network.login;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.math.BigInteger;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.login.client.CCustomPayloadLoginPacket;
import net.minecraft.network.login.client.CEncryptionResponsePacket;
import net.minecraft.network.login.server.SCustomPayloadLoginPacket;
import net.minecraft.network.login.server.SDisconnectLoginPacket;
import net.minecraft.network.login.server.SEnableCompressionPacket;
import net.minecraft.network.login.server.SEncryptionRequestPacket;
import net.minecraft.network.login.server.SLoginSuccessPacket;
import net.minecraft.util.CryptManager;
import net.minecraft.util.HTTPUtil;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ClientLoginNetHandler implements IClientLoginNetHandler {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft mc;

   private final Screen previousGuiScreen;
   private final Consumer<ITextComponent> statusMessageConsumer;
   private final NetworkManager networkManager;
   private GameProfile gameProfile;

   public ClientLoginNetHandler(NetworkManager p_i49527_1_, Minecraft p_i49527_2_,  Screen p_i49527_3_, Consumer<ITextComponent> p_i49527_4_) {
      this.networkManager = p_i49527_1_;
      this.mc = p_i49527_2_;
      this.previousGuiScreen = p_i49527_3_;
      this.statusMessageConsumer = p_i49527_4_;
   }

   public void handleEncryptionRequest(SEncryptionRequestPacket packetIn) {
      Object secretkey = CryptManager.createNewSharedKey();
      Object publickey = packetIn.getPublicKey();
      String s = (new BigInteger(CryptManager.getServerIdHash(packetIn.getServerId(), publickey, secretkey))).toString(16);
      CEncryptionResponsePacket cencryptionresponsepacket = new CEncryptionResponsePacket(secretkey, publickey, packetIn.getVerifyToken());
      this.statusMessageConsumer.accept(new TranslationTextComponent("connect.authorizing"));

   }

   private ITextComponent joinServer(String p_209522_1_) {
      try {
         this.getSessionService().joinServer(this.mc.getSession().getProfile(), this.mc.getSession().getToken(), p_209522_1_);
         return null;
      } catch (AuthenticationUnavailableException var3) {
         return new TranslationTextComponent("disconnect.loginFailedInfo", new TranslationTextComponent("disconnect.loginFailedInfo.serversUnavailable"));
      } catch (InvalidCredentialsException var4) {
         return new TranslationTextComponent("disconnect.loginFailedInfo", new TranslationTextComponent("disconnect.loginFailedInfo.invalidSession"));
      } catch (AuthenticationException authenticationexception) {
         return new TranslationTextComponent("disconnect.loginFailedInfo", authenticationexception.getMessage());
      }
   }

   private MinecraftSessionService getSessionService() {
      return this.mc.getSessionService();
   }

   public void handleLoginSuccess(SLoginSuccessPacket packetIn) {
      this.statusMessageConsumer.accept(new TranslationTextComponent("connect.joining"));
      this.gameProfile = packetIn.getProfile();
      this.networkManager.setConnectionState(ProtocolType.PLAY);
      ClientPlayNetHandler netHandler = new ClientPlayNetHandler(this.mc, this.previousGuiScreen, this.networkManager, this.gameProfile);
      int p = packetIn.getSelectedProtocol();
      net.lax1dude.eaglercraft.socket.protocol.GamePluginMessageProtocol mp = net.lax1dude.eaglercraft.socket.protocol.GamePluginMessageProtocol.getByVersion(p);
      if (mp != null) {
          netHandler.setEaglerMessageController(
                  new net.lax1dude.eaglercraft.socket.protocol.client.GameProtocolMessageController(mp, net.lax1dude.eaglercraft.socket.protocol.GamePluginMessageConstants.CLIENT_TO_SERVER,
                          net.lax1dude.eaglercraft.socket.protocol.client.GameProtocolMessageController.createClientHandler(p, netHandler),
                          (ch, msg) -> netHandler.sendPacket(new net.minecraft.network.play.client.CCustomPayloadPacket(new net.minecraft.util.ResourceLocation(net.lax1dude.eaglercraft.socket.protocol.GamePluginMessageConstants.toResourceLocation(ch)), msg))));
      }
      this.networkManager.setNetHandler(netHandler);
   }

   public void onDisconnect(ITextComponent reason) {
      this.mc.displayGuiScreen(new DisconnectedScreen(this.previousGuiScreen, "connect.failed", reason));
   }

   public NetworkManager getNetworkManager() {
      return this.networkManager;
   }

   public void handleDisconnect(SDisconnectLoginPacket packetIn) {
      this.networkManager.closeChannel(packetIn.getReason());
   }

   public void handleEnableCompression(SEnableCompressionPacket packetIn) {
      if (!this.networkManager.isLocalChannel()) {
         this.networkManager.setCompressionThreshold(packetIn.getCompressionThreshold());
      }

   }

   public void handleCustomPayloadLogin(SCustomPayloadLoginPacket p_209521_1_) {
      this.statusMessageConsumer.accept(new TranslationTextComponent("connect.negotiating"));
      this.networkManager.sendPacket(new CCustomPayloadLoginPacket(p_209521_1_.getTransaction(), (PacketBuffer)null));
   }
}
