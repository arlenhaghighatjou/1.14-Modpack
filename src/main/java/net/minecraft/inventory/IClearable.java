package net.minecraft.inventory;

public interface IClearable {
   void clear();

   static void clearObj( Object object) {
      if (object instanceof IClearable) {
         ((IClearable)object).clear();
      }

   }
}
