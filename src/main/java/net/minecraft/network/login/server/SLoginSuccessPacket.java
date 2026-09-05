package net.minecraft.network.login.server;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.minecraft.client.network.login.IClientLoginNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SLoginSuccessPacket implements IPacket<IClientLoginNetHandler> {
   private GameProfile profile;
   private int selectedProtocol = 3;

   public SLoginSuccessPacket() {
   }

   public SLoginSuccessPacket(GameProfile profileIn) {
      this.profile = profileIn;
   }

   public SLoginSuccessPacket(GameProfile profileIn, int selectedProtocol) {
      this.profile = profileIn;
      this.selectedProtocol = selectedProtocol;
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      String s = buf.readString(36);
      String s1 = buf.readString(16);
      selectedProtocol = buf.readableBytes() > 0 ? buf.readShort() : 3;
      EaglercraftUUID uuid = EaglercraftUUID.fromString(s);
      this.profile = new GameProfile(uuid, s1);
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      EaglercraftUUID uuid = this.profile.getId();
      buf.writeString(uuid == null ? "" : uuid.toString());
      buf.writeString(this.profile.getName());
      if (selectedProtocol != 3) {
         buf.writeShort(selectedProtocol);
      }
   }

   public void processPacket(IClientLoginNetHandler handler) {
      handler.handleLoginSuccess(this);
   }

   @OnlyIn(Dist.CLIENT)
   public GameProfile getProfile() {
      return this.profile;
   }

   public int getSelectedProtocol() {
      return selectedProtocol;
   }
}
