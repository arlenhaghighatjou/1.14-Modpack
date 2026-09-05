package net.minecraft.client.renderer.entity;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AreaEffectCloudRenderer extends EntityRenderer<AreaEffectCloudEntity> {
   public AreaEffectCloudRenderer(EntityRendererManager manager) {
      super(manager);
   }

   protected ResourceLocation getEntityTexture(AreaEffectCloudEntity entity) {
      return null;
   }
}
