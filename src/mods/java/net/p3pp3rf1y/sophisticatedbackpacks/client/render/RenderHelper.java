package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.util.fluid.FluidAttributes;

public class RenderHelper {
	static final ResourceLocation BACKPACK_ENTITY_TEXTURE = new ResourceLocation(SophisticatedBackpacks.MOD_ID, "textures/entity/backpack.png");

	private static final float MODEL_SCALE = 0.0625F;

	private RenderHelper() {}

	public static void renderFluid(Fluid fluid, float fill, float xOffset, float yOffset, int fillYOffsetMultiplier, float zOffset) {
		if (fill <= 0.0F) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();
		minecraft.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
		TextureAtlasSprite still = minecraft.getTextureMap().getSprite(FluidAttributes.getStillTexture(fluid));

		int color = FluidAttributes.getColor(fluid);
		GlStateManager.color4f((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, 1.0F);

		float height = fill * 10.0F;
		float y = yOffset + fillYOffsetMultiplier * height;
		renderBox(xOffset, y, zOffset, 3.5F, height, 4.0F, still.getMinU(), still.getMinV(), still.getMaxU(), still.getMaxV());

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public static void renderBatteryCharge(float charge) {
		float pixels = (int) (charge * 4);
		if (pixels <= 0.0F) {
			return;
		}

		Minecraft.getInstance().getTextureManager().bindTexture(BACKPACK_ENTITY_TEXTURE);
		renderBox(-2.0F, -3.0F, -6.01F, pixels, 1.0F, 1.0F, 18 / 64F, 55 / 64F, 22 / 64F, 56 / 64F);
	}

	private static void renderBox(float x, float y, float z, float width, float height, float depth, float minU, float minV, float maxU, float maxV) {
		float x0 = x * MODEL_SCALE;
		float y0 = y * MODEL_SCALE;
		float z0 = z * MODEL_SCALE;
		float x1 = (x + width) * MODEL_SCALE;
		float y1 = (y + height) * MODEL_SCALE;
		float z1 = (z + depth) * MODEL_SCALE;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
		face(buffer, x0, y0, z1, x1, y1, z1, 0, 0, 1, minU, minV, maxU, maxV);
		face(buffer, x1, y0, z0, x0, y1, z0, 0, 0, -1, minU, minV, maxU, maxV);
		face(buffer, x0, y0, z0, x1, y0, z1, 0, -1, 0, minU, minV, maxU, maxV);
		face(buffer, x1, y1, z0, x0, y1, z1, 0, 1, 0, minU, minV, maxU, maxV);
		face(buffer, x0, y0, z0, x0, y1, z1, -1, 0, 0, minU, minV, maxU, maxV);
		face(buffer, x1, y0, z1, x1, y1, z0, 1, 0, 0, minU, minV, maxU, maxV);
		tessellator.draw();
	}

	private static void face(BufferBuilder buffer, float ax, float ay, float az, float bx, float by, float bz, int nx, int ny, int nz, float minU, float minV, float maxU, float maxV) {
		if (ny != 0) {
			buffer.pos(ax, ay, az).tex(minU, minV).normal(nx, ny, nz).endVertex();
			buffer.pos(ax, ay, bz).tex(minU, maxV).normal(nx, ny, nz).endVertex();
			buffer.pos(bx, by, bz).tex(maxU, maxV).normal(nx, ny, nz).endVertex();
			buffer.pos(bx, by, az).tex(maxU, minV).normal(nx, ny, nz).endVertex();
			return;
		}
		buffer.pos(ax, ay, az).tex(minU, maxV).normal(nx, ny, nz).endVertex();
		buffer.pos(bx, ay, bz).tex(maxU, maxV).normal(nx, ny, nz).endVertex();
		buffer.pos(bx, by, bz).tex(maxU, minV).normal(nx, ny, nz).endVertex();
		buffer.pos(ax, by, az).tex(minU, minV).normal(nx, ny, nz).endVertex();
	}
}
