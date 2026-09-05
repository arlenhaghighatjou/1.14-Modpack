package vectorwing.farmersdelight.enchantments;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.registry.ModEnchantments;

public class BackstabbingEnchantment extends Enchantment
{
	public BackstabbingEnchantment(Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType... slots)
	{
		super(rarityIn, typeIn, slots);
	}

	public int getMinLevel() {
		return 1;
	}

	public int getMaxLevel() {
		return 3;
	}

	public int getMinEnchantability(int enchantmentLevel) {
		return 15 + (enchantmentLevel - 1) * 9;
	}

	public int getMaxEnchantability(int enchantmentLevel) {
		return super.getMinEnchantability(enchantmentLevel) + 50;
	}

	/**
	 * Determines whether the attacker is facing a 90-100 degree cone behind the target's looking direction.
	 */
	public static boolean isLookingBehindTarget(LivingEntity target, Vec3d attackerLocation) {
		if (attackerLocation != null) {
			Vec3d vec3d = target.getLook(1.0F);
			Vec3d vec3d1 = attackerLocation.subtract(target.getPositionVec()).normalize();
			vec3d1 = new Vec3d(vec3d1.x, 0.0D, vec3d1.z);
			return vec3d1.dotProduct(vec3d) < -0.5D;
		}
		return false;
	}

	public static float getBackstabbingDamagePerLevel(float amount, int level) {
		float multiplier = ((level * 0.4F) + 1.0F);
		return amount * multiplier;
	}

	public static float onLivingHurt(LivingEntity target, DamageSource source, float amount) {
		Entity attacker = source.getTrueSource();
		if (attacker instanceof PlayerEntity) {
			ItemStack weapon = ((PlayerEntity) attacker).getHeldItemMainhand();
			int level = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.BACKSTABBING, weapon);
			if (level > 0 && isLookingBehindTarget(target, source.getDamageLocation())) {
				World world = target.getEntityWorld();
				if (!world.isRemote) {
					world.playSound(null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
					return getBackstabbingDamagePerLevel(amount, level);
				}
			}
		}

		return amount;
	}

}
