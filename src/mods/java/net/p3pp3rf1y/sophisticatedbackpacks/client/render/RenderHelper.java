package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.util.fluid.FluidStack;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;

public class RenderHelper {
	static final ResourceLocation BACKPACK_ENTITY_TEXTURE = new ResourceLocation(SophisticatedBackpacks.MOD_ID, "textures/entity/backpack.png");

	private RenderHelper() {}

	public static void renderFluid(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, Fluid fluid, float fill, float xOffset, float yOffset, int fillYOffsetMultiplier, float zOffset) {
		if (MathHelper.equal(fill, 0.0f)) {
			return;
		}

		ResourceLocation texture = fluid.getAttributes().getStillTexture(new FluidStack(fluid, 5000));
		TextureAtlasSprite still = Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(texture);
		IVertexBuilder vertexBuilder = buffer.getBuffer(RenderType.entityTranslucent(AtlasTexture.LOCATION_BLOCKS_TEXTURE));
		int atlasWidth = (int) (still.getWidth() / (still.getMaxU() - still.getMinU()));
		int atlasHeight = (int) (still.getHeight() / (still.getMaxV() - still.getMinV()));
		ModelRenderer fluidBox = new ModelRenderer(atlasWidth, atlasHeight, (int) (atlasWidth * still.getMinU()), (int) (atlasHeight * still.getMinV()));
		fluidBox.addBox(xOffset, yOffset + fillYOffsetMultiplier * fill * 10.0F, zOffset, 3.5F, fill * 10.0F, 4.0F, 0.0F, false);
		int color = fluid.getAttributes().getColor();
		float red = (color >> 16 & 255) / 255.0F;
		float green = (color >> 8 & 255) / 255.0F;
		float blue = (color & 255) / 255.0F;
		fluidBox.render(matrixStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, red, green, blue, 1);
	}

	public static void renderBatteryCharge(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, float charge) {
		int pixels = (int) (charge * 4);

		ModelRenderer chargeBox = new ModelRenderer(64, 64, 18, 55);
		chargeBox.addBox(-2.0F, -3.0F, -6.01F, pixels, 1.0F, 1.0F, 0.0F, false);
		IVertexBuilder vertexBuilder = buffer.getBuffer(RenderType.entityTranslucent(BACKPACK_ENTITY_TEXTURE));

		chargeBox.render(matrixStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY);
	}
}
