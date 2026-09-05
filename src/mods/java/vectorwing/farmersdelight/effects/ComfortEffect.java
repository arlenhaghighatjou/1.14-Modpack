package vectorwing.farmersdelight.effects;

import com.google.common.collect.Sets;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import vectorwing.farmersdelight.registry.ModEffects;

import java.util.Set;

public class ComfortEffect extends Effect {
	public static final Set<Effect> COMFORT_IMMUNITIES = Sets.newHashSet(Effects.SLOWNESS, Effects.WEAKNESS, Effects.HUNGER);
	/**
	 * This effect makes the player immune to negative effects related to cold and sickness.
	 * It also instantly heals the equivalent effects when first applied.
	 * The effect runs entirely on events, which I assumed to be more efficient than constantly ticking over the entity's effect list.
	 * Current targets: Slowness, Weakness and Hunger.
	 */
	public ComfortEffect() {
		super(EffectType.BENEFICIAL, 0);
	}

	public static boolean isPotionApplicable(LivingEntity entity, EffectInstance effect) {
		return entity.getActivePotionEffect(ModEffects.COMFORT) == null || !COMFORT_IMMUNITIES.contains(effect.getPotion());
	}

	public static void onNewPotionEffect(LivingEntity entity, EffectInstance addedEffect) {
		if (addedEffect.getPotion().equals(ModEffects.COMFORT)) {
			for (Effect effect : COMFORT_IMMUNITIES) {
				entity.removePotionEffect(effect);
			}
		}
	}

	public boolean isReady(int duration, int amplifier) {
		return true;
	}

}
