package net.minecraft.profiler;

import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import java.util.Collections;
import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EmptyProfileResult implements IProfileResult {
   public static final EmptyProfileResult field_219926_a = new EmptyProfileResult();

   @OnlyIn(Dist.CLIENT)
   public List<DataPoint> getDataPoints(String sectionPath) {
      return Collections.emptyList();
   }

   public boolean writeToFile(VFile2 p_219919_1_) {
      return false;
   }

   public long timeStop() {
      return 0L;
   }

   public int ticksStop() {
      return 0;
   }

   public long timeStart() {
      return 0L;
   }

   public int ticksStart() {
      return 0;
   }

   public String format() {
      return "";
   }
}
