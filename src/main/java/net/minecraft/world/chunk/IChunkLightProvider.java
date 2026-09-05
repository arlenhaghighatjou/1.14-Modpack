package net.minecraft.world.chunk;

import net.minecraft.util.math.SectionPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;

public interface IChunkLightProvider {

   IBlockReader getChunkForLight(int chunkX, int chunkZ);

   default void markLightChanged(LightType type, SectionPos pos) {
   }

   IBlockReader getWorld();
}
