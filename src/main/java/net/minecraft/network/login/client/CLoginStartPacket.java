package net.minecraft.network.login.client;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.IServerLoginNetHandler;

public class CLoginStartPacket implements IPacket<IServerLoginNetHandler> {
   private GameProfile profile;
   private byte[] skin;
   private byte[] cape;
   private byte[] protocols;
   private EaglercraftUUID brandUUID;

   public CLoginStartPacket() {
   }

   public CLoginStartPacket(GameProfile profileIn, byte[] skin, byte[] cape, byte[] protocols, EaglercraftUUID brandUUID) {
      this.profile = profileIn;
      this.skin = skin;
      this.cape = cape;
      this.protocols = protocols;
      this.brandUUID = brandUUID;
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.profile = new GameProfile((EaglercraftUUID)null, buf.readString(16));
      this.skin = buf.readByteArray();
      this.cape = buf.readableBytes() > 0 ? buf.readByteArray() : null;
      this.protocols = buf.readableBytes() > 0 ? buf.readByteArray() : null;
      this.brandUUID = buf.readableBytes() > 0 ? uuidToEagler(buf.readUniqueId()) : null;
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeString(this.profile.getName());
      buf.writeByteArray(this.skin);
      buf.writeByteArray(this.cape);
      buf.writeByteArray(this.protocols);
      buf.writeUniqueId(brandUUID != null ? new EaglercraftUUID(brandUUID.msb, brandUUID.lsb) : new EaglercraftUUID(0L, 0L));
   }

   public void processPacket(IServerLoginNetHandler handler) {
      handler.processLoginStart(this);
   }

   public GameProfile getProfile() {
      return this.profile;
   }

   public byte[] getSkin() {
      return this.skin;
   }

   public byte[] getCape() {
      return this.cape;
   }

   public byte[] getProtocols() {
      return this.protocols;
   }

   public EaglercraftUUID getBrandUUID() {
      return this.brandUUID;
   }

   private static EaglercraftUUID uuidToEagler(EaglercraftUUID uuid) {
      return uuid != null ? new EaglercraftUUID(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()) : null;
   }
}
