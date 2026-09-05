package net.minecraft.client.entity.player;

import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.profile.SkinModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.renderer.DownloadImageBuffer;
import net.minecraft.client.renderer.texture.DownloadingTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractClientPlayerEntity extends PlayerEntity {
   private NetworkPlayerInfo playerInfo;
   public long eaglerHighPolyAnimationTick = EagRuntime.steadyTimeMillis();
   public float eaglerHighPolyAnimationFloat1 = 0.0f;
   public float eaglerHighPolyAnimationFloat2 = 0.0f;
   public float eaglerHighPolyAnimationFloat3 = 0.0f;
   public float eaglerHighPolyAnimationFloat4 = 0.0f;
   public float eaglerHighPolyAnimationFloat5 = 0.0f;
   public float eaglerHighPolyAnimationFloat6 = 0.0f;
   public float rotateElytraX;
   public float rotateElytraY;
   public float rotateElytraZ;
   public final ClientWorld field_213837_d;
   public net.lax1dude.eaglercraft.EaglercraftUUID clientBrandUUIDCache;

   public AbstractClientPlayerEntity(ClientWorld p_i50991_1_, GameProfile p_i50991_2_) {
      super(p_i50991_1_, p_i50991_2_);
      this.field_213837_d = p_i50991_1_;
   }

   public boolean isSpectator() {
      Minecraft mc = Minecraft.getInstance();
      if (this == mc.player && mc.playerController != null && mc.playerController.isSpectatorMode()) {
         return true;
      }
      NetworkPlayerInfo networkplayerinfo = mc.getConnection().getPlayerInfo(this.getGameProfile().getId());
      return networkplayerinfo != null && networkplayerinfo.getGameType() == GameType.SPECTATOR;
   }

   public boolean isCreative() {
      Minecraft mc = Minecraft.getInstance();
      if (this == mc.player && mc.playerController != null && mc.playerController.getCurrentGameType() == GameType.CREATIVE) {
         return true;
      }
      NetworkPlayerInfo networkplayerinfo = mc.getConnection().getPlayerInfo(this.getGameProfile().getId());
      return networkplayerinfo != null && networkplayerinfo.getGameType() == GameType.CREATIVE;
   }

   public boolean hasPlayerInfo() {
      return this.getPlayerInfo() != null;
   }

   protected NetworkPlayerInfo getPlayerInfo() {
      if (this.playerInfo == null) {
         this.playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(this.getUniqueID());
      }

      return this.playerInfo;
   }

   public boolean hasSkin() {
      NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
      return networkplayerinfo != null && networkplayerinfo.hasLocationSkin();
   }

   public ResourceLocation getLocationSkin() {
      if (net.lax1dude.eaglercraft.profile.EaglerProfile.getPlayerUUID().equals(this.getUniqueID()) || this == Minecraft.getInstance().player) {
         return net.lax1dude.eaglercraft.profile.EaglerProfile.getActiveSkinResourceLocation();
      }
      NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
      return networkplayerinfo == null ? DefaultPlayerSkin.getDefaultSkin(this.getUniqueID()) : networkplayerinfo.getLocationSkin();
   }

   public ResourceLocation getLocationCape() {
      if (net.lax1dude.eaglercraft.profile.EaglerProfile.getPlayerUUID().equals(this.getUniqueID()) || this == Minecraft.getInstance().player) {
          return net.lax1dude.eaglercraft.profile.EaglerProfile.getActiveCapeResourceLocation();
      }
      NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
      return networkplayerinfo == null ? null : networkplayerinfo.getLocationCape();
   }

   public boolean isPlayerInfoSet() {
      return this.getPlayerInfo() != null;
   }

   public ResourceLocation getLocationElytra() {
      NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
      return networkplayerinfo == null ? null : networkplayerinfo.getLocationElytra();
   }

   public static DownloadingTexture getDownloadImageSkin(ResourceLocation resourceLocationIn, String username) {
      TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
      ITextureObject itextureobject = texturemanager.getTexture(resourceLocationIn);
      if (itextureobject == null) {
         itextureobject = new DownloadingTexture((VFile2)null, net.lax1dude.eaglercraft.HString.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringUtils.stripControlCodes(username)), DefaultPlayerSkin.getDefaultSkin(getOfflineUUID(username)), new DownloadImageBuffer());
         texturemanager.loadTexture(resourceLocationIn, itextureobject);
      }

      return (DownloadingTexture)itextureobject;
   }

   public static ResourceLocation getLocationSkin(String username) {
       return new ResourceLocation("skins/" + Integer.toHexString(StringUtils.stripControlCodes(username).hashCode()));
   }

   public String getSkinType() {
      if (net.lax1dude.eaglercraft.profile.EaglerProfile.getPlayerUUID().equals(this.getUniqueID()) || this == Minecraft.getInstance().player) {
          return net.lax1dude.eaglercraft.profile.EaglerProfile.getActiveSkinModel().profileSkinType;
      } else {
          NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
          return networkplayerinfo == null ? DefaultPlayerSkin.getSkinType(this.getUniqueID()) : networkplayerinfo.getSkinType();
      }
   }

   public SkinModel getEaglerSkinModel() {
      if (net.lax1dude.eaglercraft.profile.EaglerProfile.getPlayerUUID().equals(this.getUniqueID()) || this == Minecraft.getInstance().player) {
         return net.lax1dude.eaglercraft.profile.EaglerProfile.getActiveSkinModel();
      }
      NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
      return networkplayerinfo == null ? SkinModel.STEVE : networkplayerinfo.getEaglerSkinModel();
   }

   public float getFovModifier() {
      float f = 1.0F;
      if (this.abilities.isFlying) {
         f *= 1.1F;
      }

      IAttributeInstance iattributeinstance = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
      f = (float)((double)f * ((iattributeinstance.getValue() / (double)this.abilities.getWalkSpeed() + 1.0D) / 2.0D));
      if (this.abilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
         f = 1.0F;
      }

      if (this.isHandActive() && this.getActiveItemStack().getItem() == Items.BOW) {
         int i = this.getItemInUseMaxCount();
         float f1 = (float)i / 20.0F;
         if (f1 > 1.0F) {
            f1 = 1.0F;
         } else {
            f1 = f1 * f1;
         }

         f *= 1.0F - f1 * 0.15F;
      }

      return f;
   }
}
