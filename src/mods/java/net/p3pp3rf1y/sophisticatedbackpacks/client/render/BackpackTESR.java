package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.client.renderer.Vector3f;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IRenderedTankUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlock;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackTileEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackRenderInfo;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.TankPosition;

public class BackpackTESR extends TileEntityRenderer<BackpackTileEntity> {
	public BackpackTESR(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(BackpackTileEntity tileEntityIn, float partialTicks, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		BlockState state = tileEntityIn.getBlockState();
		Direction facing = state.get(BackpackBlock.FACING);
		boolean showLeftTank = state.get(BackpackBlock.LEFT_TANK);
		boolean showRightTank = state.get(BackpackBlock.RIGHT_TANK);
		boolean showBattery = state.get(BackpackBlock.BATTERY);
		BackpackRenderInfo renderInfo = tileEntityIn.getBackpackWrapper().getRenderInfo();
		matrixStack.pushPose();
		GlStateManager.translated(0.5, 0, 0.5);
		matrixStack.mulPose(Vector3f.YN.rotationDegrees(facing.toYRot()));
		matrixStack.pushPose();
		GlStateManager.scalef(6 / 10f, 6 / 10f, 6 / 10f);
		if (showLeftTank) {
			IRenderedTankUpgrade.TankRenderInfo tankRenderInfo = renderInfo.getTankRenderInfos().get(TankPosition.LEFT);
			if (tankRenderInfo != null) {
				tankRenderInfo.getFluid().ifPresent(fluid -> RenderHelper.renderFluid(buffer, combinedLight, fluid, tankRenderInfo.getFillRatio(), -12.2F, 2.5F, 0, -2F));
			}
		}
		if (showRightTank) {
			IRenderedTankUpgrade.TankRenderInfo tankRenderInfo = renderInfo.getTankRenderInfos().get(TankPosition.RIGHT);
			if (tankRenderInfo != null) {
				tankRenderInfo.getFluid().ifPresent(fluid -> RenderHelper.renderFluid(buffer, combinedLight, fluid, tankRenderInfo.getFillRatio(), 8.7F, 2.5F, 0, -2F));
			}
		}
		matrixStack.popPose();
		if (showBattery) {
			renderInfo.getBatteryRenderInfo().ifPresent(batteryRenderInfo -> {
				if (batteryRenderInfo.getChargeRatio() > 0.1f) {
					matrixStack.pushPose();
					matrixStack.mulPose(Vector3f.XN.rotationDegrees(180));
					RenderHelper.renderBatteryCharge(buffer, combinedLight, batteryRenderInfo.getChargeRatio());
					matrixStack.popPose();
				}
			});
		}
		renderItemDisplay(buffer, combinedLight, combinedOverlay, renderInfo);
		matrixStack.popPose();
	}

	private void renderItemDisplay(IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay, BackpackRenderInfo renderInfo) {
		BackpackRenderInfo.ItemDisplayRenderInfo itemDisplayRenderInfo = renderInfo.getItemDisplayRenderInfo();
		matrixStack.pushPose();
		GlStateManager.translated(0, 0.6, 0.25);
		GlStateManager.scalef(0.5f, 0.5f, 0.5f);
		matrixStack.mulPose(Vector3f.XN.rotationDegrees(180));
		matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180f + itemDisplayRenderInfo.getRotation()));
		Minecraft.getInstance().getItemRenderer().renderStatic(itemDisplayRenderInfo.getItem(), ItemCameraTransforms.TransformType.FIXED, combinedLight, combinedOverlay, matrixStack, buffer);
		matrixStack.popPose();
	}
}
