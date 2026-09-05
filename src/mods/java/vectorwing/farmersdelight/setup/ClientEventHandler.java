package vectorwing.farmersdelight.setup;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vectorwing.farmersdelight.client.gui.CookingPotScreen;
import vectorwing.farmersdelight.client.particles.StarParticle;
import vectorwing.farmersdelight.client.tileentity.renderer.CuttingBoardTileEntityRenderer;
import vectorwing.farmersdelight.client.tileentity.renderer.StoveTileEntityRenderer;
import vectorwing.farmersdelight.registry.ModContainerTypes;
import vectorwing.farmersdelight.registry.ModParticleTypes;
import vectorwing.farmersdelight.tile.CuttingBoardTileEntity;
import vectorwing.farmersdelight.tile.StoveTileEntity;

@OnlyIn(Dist.CLIENT)
public class ClientEventHandler
{
	public static void init() {
		TileEntityRendererDispatcher.instance.register(StoveTileEntity.class, new StoveTileEntityRenderer());
		TileEntityRendererDispatcher.instance.register(CuttingBoardTileEntity.class, new CuttingBoardTileEntityRenderer());

		ScreenManager.registerFactory(ModContainerTypes.COOKING_POT, CookingPotScreen::new);

		Minecraft.getInstance().particles.registerFactory(ModParticleTypes.STAR_PARTICLE, StarParticle.Factory::new);
	}
}
