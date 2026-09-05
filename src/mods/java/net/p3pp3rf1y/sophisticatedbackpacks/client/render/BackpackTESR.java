package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlock;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackTileEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackRenderInfo;

/**
 * The placed backpack only needs its moving pieces drawn here since the pouches, tanks and battery
 * all come out of the baked model.
 */
public class BackpackTESR extends TileEntityRenderer<BackpackTileEntity> {
	@Override
	public void render(BackpackTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
		BlockState state = tileEntityIn.getBlockState();
		Direction facing = state.get(BackpackBlock.FACING);
		boolean showBattery = state.get(BackpackBlock.BATTERY);
		BackpackRenderInfo renderInfo = tileEntityIn.getBackpackWrapper().getRenderInfo();

		GlStateManager.pushMatrix();
		GlStateManager.translated(x + 0.5D, y, z + 0.5D);
		GlStateManager.rotatef(-facing.getHorizontalAngle(), 0F, 1F, 0F);

		if (showBattery) {
			renderInfo.getBatteryRenderInfo().ifPresent(batteryRenderInfo -> {
				if (batteryRenderInfo.getChargeRatio() > 0.1F) {
					GlStateManager.pushMatrix();
					GlStateManager.rotatef(180F, 1F, 0F, 0F);
					RenderHelper.renderBatteryCharge(batteryRenderInfo.getChargeRatio());
					GlStateManager.popMatrix();
				}
			});
		}

		renderItemDisplay(renderInfo);
		GlStateManager.popMatrix();
	}

	private void renderItemDisplay(BackpackRenderInfo renderInfo) {
		BackpackRenderInfo.ItemDisplayRenderInfo itemDisplayRenderInfo = renderInfo.getItemDisplayRenderInfo();
		ItemStack displayItem = itemDisplayRenderInfo.getItem();
		if (displayItem.isEmpty()) {
			return;
		}

		GlStateManager.pushMatrix();
		GlStateManager.translated(0D, 0.6D, 0.25D);
		GlStateManager.scalef(0.5F, 0.5F, 0.5F);
		GlStateManager.rotatef(180F, 1F, 0F, 0F);
		GlStateManager.rotatef(180F + itemDisplayRenderInfo.getRotation(), 0F, 0F, 1F);
		Minecraft.getInstance().getItemRenderer().renderItem(displayItem, ItemCameraTransforms.TransformType.FIXED);
		GlStateManager.popMatrix();
	}
}
