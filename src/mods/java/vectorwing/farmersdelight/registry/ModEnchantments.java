package vectorwing.farmersdelight.registry;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.enchantments.BackstabbingEnchantment;

public class ModEnchantments
{
	public static Enchantment BACKSTABBING;

	public static void registerEnchantments()
	{
		BACKSTABBING = Registry.register(Registry.ENCHANTMENT, new ResourceLocation(FarmersDelight.MODID, "backstabbing"), new BackstabbingEnchantment(Enchantment.Rarity.UNCOMMON, EnchantmentType.KNIFE, EquipmentSlotType.MAINHAND));
	}
}
