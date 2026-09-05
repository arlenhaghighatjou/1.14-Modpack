package vectorwing.farmersdelight.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.registry.ModEffects;
import vectorwing.farmersdelight.setup.Configuration;

/**
 * Credits to squeek502 (AppleSkin) for the implementation reference!
 * https://www.curseforge.com/minecraft/mc-mods/appleskin
 */

@OnlyIn(Dist.CLIENT)
public class NourishedHungerOverlay {

	private static final ResourceLocation modIcons = new ResourceLocation(FarmersDelight.MODID, "textures/gui/nourished.png");

	public void onRender(int left, int top) {
		if (!Configuration.NOURISHED_HUNGER_OVERLAY)
			return;

		Minecraft mc = Minecraft.getInstance();
		PlayerEntity player = mc.player;
		FoodStats stats = player.getFoodStats();

		boolean isPlayerHealingWithSaturation =
				player.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)
						&& player.shouldHeal()
						&& stats.getSaturationLevel() > 0.0F
						&& stats.getFoodLevel() >= 20;

		if (player.getActivePotionEffect(ModEffects.NOURISHED) != null && player.getActivePotionEffect(Effects.HUNGER) == null) {
			drawNourishedOverlay(stats.getFoodLevel(), mc, left, top, isPlayerHealingWithSaturation);
		}
	}

	public static void drawNourishedOverlay(int foodLevel, Minecraft mc, int left, int top, boolean naturalHealing)
	{
		mc.getTextureManager().bindTexture(modIcons);

		for (int j = 0; j < 10; ++j)
		{
			int x = left - j * 8 - 9;

			// Background texture
			mc.ingameGUI.blit(x, top, 0, 0, 11, 11);

			float effectiveHungerOfBar = (foodLevel) / 2.0F - j;
			int naturalHealingOffset = naturalHealing ? 18 : 0;

			// Gilded hunger icons
			if (effectiveHungerOfBar >= 1)
				mc.ingameGUI.blit(x, top, 18 + naturalHealingOffset, 0, 9, 9);
			else if (effectiveHungerOfBar >= .5)
				mc.ingameGUI.blit(x, top, 9 + naturalHealingOffset, 0, 9, 9);
		}

		mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
	}
}
