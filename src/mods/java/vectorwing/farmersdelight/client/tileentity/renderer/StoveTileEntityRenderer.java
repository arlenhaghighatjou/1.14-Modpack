package vectorwing.farmersdelight.client.tileentity.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.Vec2f;
import vectorwing.farmersdelight.blocks.StoveBlock;
import vectorwing.farmersdelight.tile.StoveTileEntity;

public class StoveTileEntityRenderer extends TileEntityRenderer<StoveTileEntity>
{
	@Override
	public void render(StoveTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage)
	{
		Direction direction = tileEntityIn.getBlockState().get(StoveBlock.FACING).getOpposite();
		NonNullList<ItemStack> nonnulllist = tileEntityIn.getInventory();

		for(int i = 0; i < nonnulllist.size(); ++i) {
			ItemStack itemstack = nonnulllist.get(i);
			if (!itemstack.isEmpty()) {
				GlStateManager.pushMatrix();
				GlStateManager.translatef((float)x + 0.5F, (float)y + 1.02F, (float)z + 0.5F);
				GlStateManager.rotatef(-direction.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				Vec2f itemOffset = tileEntityIn.getStoveItemOffset(i);
				GlStateManager.translatef(itemOffset.x, itemOffset.y, 0.0F);
				GlStateManager.scalef(0.375F, 0.375F, 0.375F);
				Minecraft.getInstance().getItemRenderer().renderItem(itemstack, ItemCameraTransforms.TransformType.FIXED);
				GlStateManager.popMatrix();
			}
		}
	}
}
