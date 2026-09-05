package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.model.GuardianModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuardianRenderer extends MobRenderer<GuardianEntity, GuardianModel> {
   private static final ResourceLocation GUARDIAN_TEXTURE = new ResourceLocation("textures/entity/guardian.png");
   private static final ResourceLocation GUARDIAN_BEAM_TEXTURE = new ResourceLocation("textures/entity/guardian_beam.png");

   public GuardianRenderer(EntityRendererManager renderManagerIn) {
      this(renderManagerIn, 0.5F);
   }

   protected GuardianRenderer(EntityRendererManager p_i50968_1_, float p_i50968_2_) {
      super(p_i50968_1_, new GuardianModel(), p_i50968_2_);
   }

   public boolean shouldRender(GuardianEntity livingEntity, ICamera camera, double camX, double camY, double camZ) {
      if (super.shouldRender(livingEntity, camera, camX, camY, camZ)) {
         return true;
      } else {
         if (livingEntity.hasTargetedEntity()) {
            LivingEntity livingentity = livingEntity.getTargetedEntity();
            if (livingentity != null) {
               double targetY = livingentity.posY + (double)livingentity.getHeight() * 0.5D;
               double guardianY = livingEntity.posY + (double)livingEntity.getEyeHeight();
               if (camera.isBoxInFrustum(Math.min(livingEntity.posX, livingentity.posX), Math.min(guardianY, targetY),
                     Math.min(livingEntity.posZ, livingentity.posZ), Math.max(livingEntity.posX, livingentity.posX),
                     Math.max(guardianY, targetY), Math.max(livingEntity.posZ, livingentity.posZ))) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public void doRender(GuardianEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
      LivingEntity livingentity = entity.getTargetedEntity();
      if (livingentity != null) {
         float f = entity.getAttackAnimationScale(partialTicks);
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         this.bindTexture(GUARDIAN_BEAM_TEXTURE);
         GlStateManager.texParameter(3553, 10242, 10497);
         GlStateManager.texParameter(3553, 10243, 10497);
         GlStateManager.disableLighting();
         GlStateManager.disableCull();
         GlStateManager.disableBlend();
         GlStateManager.depthMask(true);
         float f1 = 240.0F;
         GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 240.0F);
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         float f2 = (float)entity.world.getGameTime() + partialTicks;
         float f3 = f2 * 0.5F % 1.0F;
         float f4 = entity.getEyeHeight();
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)x, (float)y + f4, (float)z);
         double targetX = MathHelper.lerp((double)partialTicks, livingentity.lastTickPosX, livingentity.posX);
         double targetY = MathHelper.lerp((double)partialTicks, livingentity.lastTickPosY, livingentity.posY)
               + (double)livingentity.getHeight() * 0.5D;
         double targetZ = MathHelper.lerp((double)partialTicks, livingentity.lastTickPosZ, livingentity.posZ);
         double guardianX = MathHelper.lerp((double)partialTicks, entity.lastTickPosX, entity.posX);
         double guardianY = MathHelper.lerp((double)partialTicks, entity.lastTickPosY, entity.posY) + (double)f4;
         double guardianZ = MathHelper.lerp((double)partialTicks, entity.lastTickPosZ, entity.posZ);
         double beamX = targetX - guardianX;
         double beamY = targetY - guardianY;
         double beamZ = targetZ - guardianZ;
         double beamLength = (double)MathHelper.sqrt(beamX * beamX + beamY * beamY + beamZ * beamZ);
         double d0 = beamLength + 1.0D;
         float f5 = (float)Math.acos(beamLength < 1.0E-4D ? 0.0D : beamY / beamLength);
         float f6 = (float)Math.atan2(beamZ, beamX);
         GlStateManager.rotatef((((float)Math.PI / 2F) - f6) * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(f5 * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
         int i = 1;
         double d1 = (double)f2 * 0.05D * -1.5D;
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         float f7 = f * f;
         int j = 64 + (int)(f7 * 191.0F);
         int k = 32 + (int)(f7 * 191.0F);
         int l = 128 - (int)(f7 * 64.0F);
         double d2 = 0.2D;
         double d3 = 0.282D;
         double d4 = 0.0D + Math.cos(d1 + 2.356194490192345D) * 0.282D;
         double d5 = 0.0D + Math.sin(d1 + 2.356194490192345D) * 0.282D;
         double d6 = 0.0D + Math.cos(d1 + (Math.PI / 4D)) * 0.282D;
         double d7 = 0.0D + Math.sin(d1 + (Math.PI / 4D)) * 0.282D;
         double d8 = 0.0D + Math.cos(d1 + 3.9269908169872414D) * 0.282D;
         double d9 = 0.0D + Math.sin(d1 + 3.9269908169872414D) * 0.282D;
         double d10 = 0.0D + Math.cos(d1 + 5.497787143782138D) * 0.282D;
         double d11 = 0.0D + Math.sin(d1 + 5.497787143782138D) * 0.282D;
         double d12 = 0.0D + Math.cos(d1 + Math.PI) * 0.2D;
         double d13 = 0.0D + Math.sin(d1 + Math.PI) * 0.2D;
         double d14 = 0.0D + Math.cos(d1 + 0.0D) * 0.2D;
         double d15 = 0.0D + Math.sin(d1 + 0.0D) * 0.2D;
         double d16 = 0.0D + Math.cos(d1 + (Math.PI / 2D)) * 0.2D;
         double d17 = 0.0D + Math.sin(d1 + (Math.PI / 2D)) * 0.2D;
         double d18 = 0.0D + Math.cos(d1 + (Math.PI * 1.5D)) * 0.2D;
         double d19 = 0.0D + Math.sin(d1 + (Math.PI * 1.5D)) * 0.2D;
         double d20 = 0.0D;
         double d21 = 0.4999D;
         double d22 = (double)(-1.0F + f3);
         double d23 = d0 * 2.5D + d22;
         bufferbuilder.pos(d12, d0, d13).tex(0.4999D, d23).color(j, k, l, 255).endVertex();
         bufferbuilder.pos(d12, 0.0D, d13).tex(0.4999D, d22).color(j, k, l, 255).endVertex();
         bufferbuilder.pos(d14, 0.0D, d15).tex(0.0D, d22).color(j, k, l, 255).endVertex();
         bufferbuilder.pos(d14, d0, d15).tex(0.0D, d23).color(j, k, l, 255).endVertex();
         bufferbuilder.pos(d16, d0, d17).tex(0.4999D, d23).color(j, k, l, 255).endVertex();
         bufferbuilder.pos(d16, 0.0D, d17).tex(0.4999D, d22).color(j, k, l, 255).endVertex();
         bufferbuilder.pos(d18, 0.0D, d19).tex(0.0D, d22).color(j, k, l, 255).endVertex();
         bufferbuilder.pos(d18, d0, d19).tex(0.0D, d23).color(j, k, l, 255).endVertex();
         double d24 = 0.0D;
         if (entity.ticksExisted % 2 == 0) {
            d24 = 0.5D;
         }

         bufferbuilder.pos(d4, d0, d5).tex(0.5D, d24 + 0.5D).color(j, k, l, 255).endVertex();
         bufferbuilder.pos(d6, d0, d7).tex(1.0D, d24 + 0.5D).color(j, k, l, 255).endVertex();
         bufferbuilder.pos(d10, d0, d11).tex(1.0D, d24).color(j, k, l, 255).endVertex();
         bufferbuilder.pos(d8, d0, d9).tex(0.5D, d24).color(j, k, l, 255).endVertex();
         tessellator.draw();
         GlStateManager.popMatrix();
      }

   }

   protected ResourceLocation getEntityTexture(GuardianEntity entity) {
      return GUARDIAN_TEXTURE;
   }
}
