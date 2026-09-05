package vectorwing.farmersdelight.setup;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.client.gui.CookingPotScreen;
import vectorwing.farmersdelight.client.gui.NourishedHungerOverlay;
import vectorwing.farmersdelight.client.particles.StarParticle;
import vectorwing.farmersdelight.client.tileentity.renderer.CuttingBoardTileEntityRenderer;
import vectorwing.farmersdelight.client.tileentity.renderer.StoveTileEntityRenderer;
import vectorwing.farmersdelight.registry.ModBlocks;
import vectorwing.farmersdelight.registry.ModContainerTypes;
import vectorwing.farmersdelight.registry.ModParticleTypes;
import vectorwing.farmersdelight.registry.ModTileEntityTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = FarmersDelight.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler
{
	public static final ResourceLocation EMPTY_CONTAINER_SLOT_BOWL = new ResourceLocation(FarmersDelight.MODID, "item/empty_container_slot_bowl");

	@SubscribeEvent
	public static void onStitchEvent(TextureStitchEvent.Pre event)
	{
		ResourceLocation stitching = event.getMap().getTextureLocation();
		if(!stitching.equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE))	{
			return;
		}
		boolean added = event.addSprite(EMPTY_CONTAINER_SLOT_BOWL);
	}

	public static void init(final FMLClientSetupEvent event) {
		RenderTypeLookup.setRenderLayer(ModBlocks.WILD_CABBAGES, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.WILD_ONIONS, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.WILD_TOMATOES, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.WILD_CARROTS, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.WILD_POTATOES, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.WILD_BEETROOTS, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.WILD_RICE, RenderType.getCutout());

		RenderTypeLookup.setRenderLayer(ModBlocks.ONION_CROP, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.CABBAGE_CROP, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.TOMATO_CROP, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.RICE_CROP, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.RICE_UPPER_CROP, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.TALL_RICE_CROP, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.BROWN_MUSHROOM_COLONY, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.RED_MUSHROOM_COLONY, RenderType.getCutout());

		RenderTypeLookup.setRenderLayer(ModBlocks.COOKING_POT, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.CUTTING_BOARD, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.BASKET, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.ROPE, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModBlocks.SAFETY_NET, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.FULL_TATAMI_MAT, RenderType.getCutout());

		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.STOVE_TILE,
				StoveTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.CUTTING_BOARD_TILE,
				CuttingBoardTileEntityRenderer::new);

		ScreenManager.registerFactory(ModContainerTypes.COOKING_POT, CookingPotScreen::new);

		NourishedHungerOverlay.init();
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void registerParticles(ParticleFactoryRegisterEvent event) {
		Minecraft.getInstance().particles.registerFactory(ModParticleTypes.STAR_PARTICLE, StarParticle.Factory::new);
	}
}
