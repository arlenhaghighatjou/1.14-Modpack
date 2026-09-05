package net.minecraft.client.renderer.texture;

import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ISprite {
   default ModelRotation getRotation() {
      return ModelRotation.X0_Y0;
   }

   default boolean isUvLock() {
      return false;
   }
}
