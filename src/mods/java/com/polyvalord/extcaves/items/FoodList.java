package com.polyvalord.extcaves.items;

import net.minecraft.item.Food;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

@SuppressWarnings("deprecation")
public class FoodList {
    public static final Food FOOD_SWEETSHROOM = (new Food.Builder()).hunger(2).saturation(0.2F).build();
    public static final Food FOOD_SWEETSHROOM_COOKED = (new Food.Builder()).hunger(6).saturation(0.6F).build();
    public static final Food FOOD_GOLDISHROOM = (new Food.Builder()).hunger(2).saturation(0.2F).setAlwaysEdible().effect(new EffectInstance(Effects.HASTE, 600, 0), 1.0F).build();
    public static final Food FOOD_SHINYSHROOM = (new Food.Builder()).hunger(2).saturation(0.2F).setAlwaysEdible().effect(new EffectInstance(Effects.HASTE, 1200, 0), 1.0F).build();
    public static final Food FOOD_LUMISHROOM = (new Food.Builder()).hunger(2).saturation(0.2F).setAlwaysEdible().effect(new EffectInstance(Effects.BLINDNESS, 300, 0), 1.0F).build();
    public static final Food FOOD_FLUOSHROOM = (new Food.Builder()).hunger(2).saturation(0.2F).setAlwaysEdible().effect(new EffectInstance(Effects.NAUSEA, 300, 0), 1.0F).build();
    public static final Food FOOD_ROCKSHROOM = (new Food.Builder()).hunger(2).saturation(0.2F).setAlwaysEdible().effect(new EffectInstance(Effects.RESISTANCE, 600, 0), 1.0F).build();
    public static final Food FOOD_STEW_STICKY = (new Food.Builder()).hunger(6).saturation(0.6F).setAlwaysEdible().effect(new EffectInstance(Effects.HASTE, 3600, 1), 1.0F).build();
    public static final Food FOOD_STEW_FLUORESCENT = (new Food.Builder()).hunger(6).saturation(0.6F).setAlwaysEdible().effect(new EffectInstance(Effects.NIGHT_VISION, 6000, 0), 1.0F).build();
    public static final Food FOOD_STEW_HARD = (new Food.Builder()).hunger(6).saturation(0.6F).setAlwaysEdible().effect(new EffectInstance(Effects.RESISTANCE, 3600, 0), 1.0F).build();
}
