package vectorwing.farmersdelight.client.tileentity.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.Direction;
import vectorwing.farmersdelight.blocks.CuttingBoardBlock;
import vectorwing.farmersdelight.tile.CuttingBoardTileEntity;

public class CuttingBoardTileEntityRenderer extends TileEntityRenderer<CuttingBoardTileEntity>
{
	@Override
	public void render(CuttingBoardTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage)
	{
		Direction direction = tileEntityIn.getBlockState().get(CuttingBoardBlock.FACING).getOpposite();
		ItemStack itemStack = tileEntityIn.getStoredItem();

		if (!itemStack.isEmpty()) {
			ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
			boolean blockItem = itemRenderer.getItemModelWithOverrides(itemStack, tileEntityIn.getWorld(), null).isGui3d();

			GlStateManager.pushMatrix();

			if (tileEntityIn.getIsItemCarvingBoard()) {
				renderItemCarved(x, y, z, direction, itemStack);
			} else if (blockItem) {
				renderBlock(x, y, z, direction);
			} else {
				renderItemLayingDown(x, y, z, direction);
			}

			itemRenderer.renderItem(itemStack, ItemCameraTransforms.TransformType.FIXED);
			GlStateManager.popMatrix();
		}
	}

	public void renderItemLayingDown(double x, double y, double z, Direction direction) {
		GlStateManager.translatef((float)x + 0.5F, (float)y + 0.08F, (float)z + 0.5F);
		GlStateManager.rotatef(-direction.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.scalef(0.6F, 0.6F, 0.6F);
	}

	public void renderBlock(double x, double y, double z, Direction direction) {
		GlStateManager.translatef((float)x + 0.5F, (float)y + 0.27F, (float)z + 0.5F);
		GlStateManager.rotatef(-direction.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
		GlStateManager.scalef(0.8F, 0.8F, 0.8F);
	}

	public void renderItemCarved(double x, double y, double z, Direction direction, ItemStack itemStack) {
		GlStateManager.translatef((float)x + 0.5F, (float)y + 0.25F, (float)z + 0.5F);
		GlStateManager.rotatef(-direction.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(itemStack.getItem() instanceof PickaxeItem || itemStack.getItem() instanceof HoeItem ? 225.0F : 180.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.scalef(0.6F, 0.6F, 0.6F);
	}
}
