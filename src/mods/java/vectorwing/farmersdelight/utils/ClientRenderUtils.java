package vectorwing.farmersdelight.utils;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;

/**
 * Util for helping with rendering elements across the mod, when vanilla methods don't expose enough to use.
 */
public class ClientRenderUtils
{
	/**
	 * Renders an Item into the GUI, allowing the size to be defined instead of hardcoded.
	 */
	public static void renderItemIntoGUIScalable(ItemStack stack, int width, int height, IBakedModel bakedmodel, ItemRenderer renderer, TextureManager textureManager) {
		GlStateManager.pushMatrix();
		textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
		textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableAlphaTest();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.translatef(0.0F, 0.0F, 100.0F + renderer.zLevel);
		GlStateManager.translatef(width / 2.0F, height / 2.0F, 0.0F);
		GlStateManager.scalef(1.0F, -1.0F, 1.0F);
		GlStateManager.scalef(width * 3.0F, height * 3.0F, 16.0F);

		if (!bakedmodel.isGui3d()) {
			RenderHelper.enableGUIStandardItemLighting();
		}

		bakedmodel.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GUI);
		GlStateManager.enableDepthTest();
		renderer.renderItem(stack, bakedmodel);

		if (!bakedmodel.isGui3d()) {
			RenderHelper.enableGUIStandardItemLighting();
		}

		GlStateManager.disableAlphaTest();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();
		GlStateManager.popMatrix();
		textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
		textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
	}
}
