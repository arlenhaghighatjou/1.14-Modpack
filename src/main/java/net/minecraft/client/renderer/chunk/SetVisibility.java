package net.minecraft.client.renderer.chunk;

import java.util.Set;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SetVisibility {
   private static final Direction[] FACINGS = Direction.values();
   private static final int COUNT_FACES = 6;
   private long bitSet;

   public void setManyVisible(Set<Direction> facing) {
      for(Direction direction : facing) {
         for(Direction direction1 : facing) {
            this.setVisible(direction, direction1, true);
         }
      }

   }

   public void setManyVisible(int facingMask) {
      for (int i = 0; i < COUNT_FACES; ++i) {
         if ((facingMask & 1 << i) == 0) continue;
         for (int j = 0; j < COUNT_FACES; ++j) {
            if ((facingMask & 1 << j) != 0) {
               this.setVisible(FACINGS[i], FACINGS[j], true);
            }
         }
      }
   }

   public void setVisible(Direction facing, Direction facing2, boolean value) {
      int bit1 = facing.ordinal() + facing2.ordinal() * COUNT_FACES;
      int bit2 = facing2.ordinal() + facing.ordinal() * COUNT_FACES;
      if (value) {
          this.bitSet |= (1L << bit1);
          this.bitSet |= (1L << bit2);
      } else {
          this.bitSet &= ~(1L << bit1);
          this.bitSet &= ~(1L << bit2);
      }
   }

   public void setAllVisible(boolean visible) {
      if (visible) {
          this.bitSet = (1L << 36) - 1;
      } else {
          this.bitSet = 0L;
      }
   }

   public boolean isVisible(Direction facing, Direction facing2) {
      int bit = facing.ordinal() + facing2.ordinal() * COUNT_FACES;
      return (this.bitSet & (1L << bit)) != 0;
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append(' ');

      for(Direction direction : Direction.values()) {
         stringbuilder.append(' ').append(direction.toString().toUpperCase().charAt(0));
      }

      stringbuilder.append('\n');

      for(Direction direction2 : Direction.values()) {
         stringbuilder.append(direction2.toString().toUpperCase().charAt(0));

         for(Direction direction1 : Direction.values()) {
            if (direction2 == direction1) {
               stringbuilder.append("  ");
            } else {
               boolean flag = this.isVisible(direction2, direction1);
               stringbuilder.append(' ').append((char)(flag ? 'Y' : 'n'));
            }
         }

         stringbuilder.append('\n');
      }

      return stringbuilder.toString();
   }
}
