package net.minecraft.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public abstract class IRecipeSerializer<T extends IRecipe<?>> {
   public static IRecipeSerializer<ShapedRecipe> CRAFTING_SHAPED = register("crafting_shaped", new ShapedRecipe.Serializer());
   public static IRecipeSerializer<ShapelessRecipe> CRAFTING_SHAPELESS = register("crafting_shapeless", new ShapelessRecipe.Serializer());
   public static SpecialRecipeSerializer<ArmorDyeRecipe> CRAFTING_SPECIAL_ARMORDYE = register("crafting_special_armordye", new SpecialRecipeSerializer<>(ArmorDyeRecipe::new));
   public static SpecialRecipeSerializer<BookCloningRecipe> CRAFTING_SPECIAL_BOOKCLONING = register("crafting_special_bookcloning", new SpecialRecipeSerializer<>(BookCloningRecipe::new));
   public static SpecialRecipeSerializer<MapCloningRecipe> CRAFTING_SPECIAL_MAPCLONING = register("crafting_special_mapcloning", new SpecialRecipeSerializer<>(MapCloningRecipe::new));
   public static SpecialRecipeSerializer<MapExtendingRecipe> CRAFTING_SPECIAL_MAPEXTENDING = register("crafting_special_mapextending", new SpecialRecipeSerializer<>(MapExtendingRecipe::new));
   public static SpecialRecipeSerializer<FireworkRocketRecipe> CRAFTING_SPECIAL_FIREWORK_ROCKET = register("crafting_special_firework_rocket", new SpecialRecipeSerializer<>(FireworkRocketRecipe::new));
   public static SpecialRecipeSerializer<FireworkStarRecipe> CRAFTING_SPECIAL_FIREWORK_STAR = register("crafting_special_firework_star", new SpecialRecipeSerializer<>(FireworkStarRecipe::new));
   public static SpecialRecipeSerializer<FireworkStarFadeRecipe> CRAFTING_SPECIAL_FIREWORK_STAR_FADE = register("crafting_special_firework_star_fade", new SpecialRecipeSerializer<>(FireworkStarFadeRecipe::new));
   public static SpecialRecipeSerializer<TippedArrowRecipe> CRAFTING_SPECIAL_TIPPEDARROW = register("crafting_special_tippedarrow", new SpecialRecipeSerializer<>(TippedArrowRecipe::new));
   public static SpecialRecipeSerializer<BannerDuplicateRecipe> CRAFTING_SPECIAL_BANNERDUPLICATE = register("crafting_special_bannerduplicate", new SpecialRecipeSerializer<>(BannerDuplicateRecipe::new));
   public static SpecialRecipeSerializer<ShieldRecipes> CRAFTING_SPECIAL_SHIELD = register("crafting_special_shielddecoration", new SpecialRecipeSerializer<>(ShieldRecipes::new));
   public static SpecialRecipeSerializer<ShulkerBoxColoringRecipe> CRAFTING_SPECIAL_SHULKERBOXCOLORING = register("crafting_special_shulkerboxcoloring", new SpecialRecipeSerializer<>(ShulkerBoxColoringRecipe::new));
   public static SpecialRecipeSerializer<SuspiciousStewRecipe> CRAFTING_SPECIAL_SUSPICIOUSSTEW = register("crafting_special_suspiciousstew", new SpecialRecipeSerializer<>(SuspiciousStewRecipe::new));
   public static SpecialRecipeSerializer<RepairItemRecipe> field_223550_o = register("crafting_special_repairitem", new SpecialRecipeSerializer<>(RepairItemRecipe::new));
   public static CookingRecipeSerializer<FurnaceRecipe> SMELTING = register("smelting", new CookingRecipeSerializer<>(FurnaceRecipe::new, 200));
   public static CookingRecipeSerializer<BlastingRecipe> BLASTING = register("blasting", new CookingRecipeSerializer<>(BlastingRecipe::new, 100));
   public static CookingRecipeSerializer<SmokingRecipe> SMOKING = register("smoking", new CookingRecipeSerializer<>(SmokingRecipe::new, 100));
   public static CookingRecipeSerializer<CampfireCookingRecipe> CAMPFIRE_COOKING = register("campfire_cooking", new CookingRecipeSerializer<>(CampfireCookingRecipe::new, 100));
   public static IRecipeSerializer<StonecuttingRecipe> STONECUTTING = register("stonecutting", new SingleItemRecipe.Serializer<>(StonecuttingRecipe::new));

   public abstract T read(ResourceLocation recipeId, JsonObject json);

   public abstract T read(ResourceLocation recipeId, PacketBuffer buffer);

   public abstract void write(PacketBuffer buffer, T recipe);

   public static <S extends IRecipeSerializer<T>, T extends IRecipe<?>> S register(String key, S recipeSerializer) {
      return (S)(Registry.<IRecipeSerializer<?>>register(Registry.RECIPE_SERIALIZER, key, recipeSerializer));
   }

   protected IRecipeSerializer() {
   }
}
