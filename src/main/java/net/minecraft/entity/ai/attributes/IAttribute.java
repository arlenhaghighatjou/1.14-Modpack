package net.minecraft.entity.ai.attributes;

public interface IAttribute {
   String getName();

   double clampValue(double value);

   double getDefaultValue();

   boolean getShouldWatch();

   IAttribute getParent();
}
