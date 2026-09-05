package net.minecraft.world.gen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class SurfaceBuilderConfig implements ISurfaceBuilderConfig {
   private final BlockState topMaterial;
   private final BlockState underMaterial;
   private final BlockState underWaterMaterial;

   public SurfaceBuilderConfig(BlockState topMaterial, BlockState underMaterial, BlockState underWaterMaterial) {
      this.topMaterial = topMaterial;
      this.underMaterial = underMaterial;
      this.underWaterMaterial = underWaterMaterial;
   }

   public BlockState getTop() {
      return this.topMaterial;
   }

   public BlockState getUnder() {
      return this.underMaterial;
   }

   public BlockState getUnderWaterMaterial() {
      return this.underWaterMaterial;
   }

   public static SurfaceBuilderConfig deserialize(Dynamic<?> p_215455_0_) {
      BlockState blockstate = BlockState.deserialize(p_215455_0_.get("top_material").orElseEmptyMap());
      BlockState blockstate1 = BlockState.deserialize(p_215455_0_.get("under_material").orElseEmptyMap());
      BlockState blockstate2 = BlockState.deserialize(p_215455_0_.get("underwater_material").orElseEmptyMap());
      return new SurfaceBuilderConfig(blockstate, blockstate1, blockstate2);
   }
}
