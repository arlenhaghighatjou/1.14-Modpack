package net.minecraft.world.biome;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.CubeCoordinateIterator;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BiomeColors {
   private static final BiomeColors.IColorResolver GRASS_COLOR = Biome::getGrassColor;
   private static final BiomeColors.IColorResolver FOLIAGE_COLOR = Biome::getFoliageColor;
   private static final BiomeColors.IColorResolver WATER_COLOR = (p_210280_0_, p_210280_1_) -> {
      return p_210280_0_.getWaterColor();
   };
   private static final BiomeColors.IColorResolver WATER_FOG_COLOR = (p_210279_0_, p_210279_1_) -> {
      return p_210279_0_.getWaterFogColor();
   };

   private static final ThreadLocal<BlockPos.MutableBlockPos> MUTABLE_POS = new ThreadLocal<BlockPos.MutableBlockPos>() {
      @Override
      protected BlockPos.MutableBlockPos initialValue() {
         return new BlockPos.MutableBlockPos();
      }
   };

   private static int getColor(IEnviromentBlockReader reader, BlockPos pos, BiomeColors.IColorResolver resolver) {
      int i = 0;
      int j = 0;
      int k = 0;
      int l = Minecraft.getInstance().gameSettings.biomeBlendRadius;
      if (l == 0) {
         return resolver.getColor(reader.getBiome(pos), pos);
      } else {
         int i1 = (l * 2 + 1) * (l * 2 + 1);
         BlockPos.MutableBlockPos mutPos = MUTABLE_POS.get();

         int minX = pos.getX() - l;
         int minY = pos.getY();
         int minZ = pos.getZ() - l;
         int maxX = pos.getX() + l;
         int maxZ = pos.getZ() + l;

         int j1;
         for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
               mutPos.setPos(x, minY, z);
               j1 = resolver.getColor(reader.getBiome(mutPos), mutPos);
               i += (j1 & 16711680) >> 16;
               j += (j1 & 65280) >> 8;
               k += j1 & 255;
            }
         }

         return (i / i1 & 255) << 16 | (j / i1 & 255) << 8 | k / i1 & 255;
      }
   }

   public static int getGrassColor(IEnviromentBlockReader reader, BlockPos pos) {
      return getColor(reader, pos, GRASS_COLOR);
   }

   public static int getFoliageColor(IEnviromentBlockReader reader, BlockPos pos) {
      return getColor(reader, pos, FOLIAGE_COLOR);
   }

   public static int getWaterColor(IEnviromentBlockReader reader, BlockPos pos) {
      return getColor(reader, pos, WATER_COLOR);
   }

   @OnlyIn(Dist.CLIENT)
   interface IColorResolver {
      int getColor(Biome p_getColor_1_, BlockPos p_getColor_2_);
   }
}
