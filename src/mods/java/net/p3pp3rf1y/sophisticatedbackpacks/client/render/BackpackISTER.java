package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackRenderInfo;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapperLookup;

/**
 * Draws the backpack model and then whatever item the display upgrade puts on its front.
 */
public class BackpackISTER {
	public static final BackpackISTER INSTANCE = new BackpackISTER();

	private BackpackISTER() {}

	public void renderByItem(ItemStack stack) {
		Minecraft minecraft = Minecraft.getInstance();
		ItemRenderer itemRenderer = minecraft.getItemRenderer();
		IBakedModel model = itemRenderer.getItemModelWithOverrides(stack, null, minecraft.player);
		itemRenderer.renderModel(model, stack);

		BackpackWrapperLookup.get(stack).ifPresent(backpackWrapper -> {
			BackpackRenderInfo.ItemDisplayRenderInfo itemDisplayRenderInfo = backpackWrapper.getRenderInfo().getItemDisplayRenderInfo();
			ItemStack displayItem = itemDisplayRenderInfo.getItem();
			if (displayItem.isEmpty()) {
				return;
			}

			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.5F, 0.6F, 0.25F);
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.rotatef(itemDisplayRenderInfo.getRotation(), 0F, 0F, 1F);
			itemRenderer.renderItem(displayItem, ItemCameraTransforms.TransformType.FIXED);
			GlStateManager.popMatrix();
		});
	}
}
