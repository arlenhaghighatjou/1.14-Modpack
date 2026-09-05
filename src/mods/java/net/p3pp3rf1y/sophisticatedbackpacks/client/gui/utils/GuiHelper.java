package net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.controls.ToggleButton;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiHelper {
	public static final ResourceLocation GUI_CONTROLS = new ResourceLocation(SophisticatedBackpacks.MOD_ID, "textures/gui/gui_controls.png");
	private static final int GUI_CONTROLS_TEXTURE_WIDTH = 256;
	private static final int GUI_CONTROLS_TEXTURE_HEIGHT = 256;
	private static final int TOOLTIP_BACKGROUND_COLOR = 0xF0100010;
	private static final int TOOLTIP_BORDER_COLOR_START = 0x505000FF;
	private static final int TOOLTIP_BORDER_COLOR_END = 0x5028007F;
	public static final TextureBlitData BAR_BACKGROUND_BOTTOM = new TextureBlitData(GUI_CONTROLS, Dimension.SQUARE_256, new UV(29, 66), Dimension.SQUARE_18);
	public static final TextureBlitData BAR_BACKGROUND_MIDDLE = new TextureBlitData(GUI_CONTROLS, Dimension.SQUARE_256, new UV(29, 48), Dimension.SQUARE_18);
	public static final TextureBlitData BAR_BACKGROUND_TOP = new TextureBlitData(GUI_CONTROLS, Dimension.SQUARE_256, new UV(29, 30), Dimension.SQUARE_18);
	public static final ResourceLocation ICONS = new ResourceLocation(SophisticatedBackpacks.MOD_ID, "textures/gui/icons.png");
	public static final TextureBlitData CRAFTING_RESULT_SLOT = new TextureBlitData(GUI_CONTROLS, new UV(71, 216), new Dimension(26, 26));
	public static final TextureBlitData DEFAULT_BUTTON_HOVERED_BACKGROUND = new TextureBlitData(GUI_CONTROLS, new UV(47, 0), Dimension.SQUARE_18);
	public static final TextureBlitData DEFAULT_BUTTON_BACKGROUND = new TextureBlitData(GUI_CONTROLS, new UV(29, 0), Dimension.SQUARE_18);
	public static final TextureBlitData SMALL_BUTTON_BACKGROUND = new TextureBlitData(GuiHelper.GUI_CONTROLS, Dimension.SQUARE_256, new UV(29, 18), Dimension.SQUARE_12);
	public static final TextureBlitData SMALL_BUTTON_HOVERED_BACKGROUND = new TextureBlitData(GuiHelper.GUI_CONTROLS, Dimension.SQUARE_256, new UV(41, 18), Dimension.SQUARE_12);
	public static final ResourceLocation SLOTS_BACKGROUND = new ResourceLocation(SophisticatedBackpacks.MOD_ID, "textures/gui/slots_background.png");

	private static final Map<Integer, TextureBlitData> SLOTS_BACKGROUNDS = new HashMap<>();

	private GuiHelper() {}

	public static void renderItemInGUI(Minecraft minecraft, ItemStack stack, int xPosition, int yPosition) {
		renderItemInGUI(minecraft, stack, xPosition, yPosition, false);
	}

	public static void renderSlotsBackground(Minecraft minecraft, int x, int y, int slotWidth, int slotHeight) {
		int key = getSlotsBackgroundKey(slotWidth, slotHeight);
		blit(minecraft, x, y, SLOTS_BACKGROUNDS.computeIfAbsent(key, k ->
				new TextureBlitData(SLOTS_BACKGROUND, Dimension.SQUARE_256, new UV(0, 0), new Dimension(slotWidth * 18, slotHeight * 18))
		));
	}

	private static int getSlotsBackgroundKey(int slotWidth, int slotHeight) {
		return slotWidth * 31 + slotHeight;
	}

	public static void renderItemInGUI(Minecraft minecraft, ItemStack stack, int xPosition, int yPosition, boolean renderOverlay) {
		renderItemInGUI(minecraft, stack, xPosition, yPosition, renderOverlay, null);
	}

	public static void renderItemInGUI(Minecraft minecraft, ItemStack stack, int xPosition, int yPosition, boolean renderOverlay,
			@Nullable String countText) {
		ItemRenderer itemRenderer = minecraft.getItemRenderer();
		float originalZLevel = itemRenderer.zLevel;
		itemRenderer.zLevel += getZOffset();
		itemRenderer.renderItemAndEffectIntoGUI(stack, xPosition, yPosition);
		if (renderOverlay) {
			itemRenderer.renderItemOverlayIntoGUI(minecraft.fontRenderer, stack, xPosition, yPosition, countText);
		}
		itemRenderer.zLevel = originalZLevel;
	}

	private static int getZOffset() {
		return 0;
	}

	public static void blit(Minecraft minecraft, int x, int y, TextureBlitData texData) {
		minecraft.getTextureManager().bindTexture(texData.getTextureName());
		AbstractGui.blit(x + texData.getXOffset(), y + texData.getYOffset(), texData.getU(), texData.getV(), texData.getWidth(), texData.getHeight(), texData.getTextureWidth(), texData.getTextureHeight());
	}

	public static void coloredBlit(int x, int y, TextureBlitData texData, int color) {
		float red = (color >> 16 & 255) / 255F;
		float green = (color >> 8 & 255) / 255F;
		float blue = (color & 255) / 255F;
		float alpha = (color >> 24 & 255) / 255F;

		int xMin = x + texData.getXOffset();
		int yMin = y + texData.getYOffset();
		int xMax = xMin + texData.getWidth();
		int yMax = yMin + texData.getHeight();

		float minU = (float) texData.getU() / texData.getTextureWidth();
		float maxU = minU + ((float) texData.getWidth() / texData.getTextureWidth());
		float minV = (float) texData.getV() / texData.getTextureHeight();
		float maxV = minV + ((float) texData.getHeight() / texData.getTextureWidth());

		BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.pos(xMin, yMax, 0).tex(minU, maxV).color(red, green, blue, alpha).endVertex();
		bufferbuilder.pos(xMax, yMax, 0).tex(maxU, maxV).color(red, green, blue, alpha).endVertex();
		bufferbuilder.pos(xMax, yMin, 0).tex(maxU, minV).color(red, green, blue, alpha).endVertex();
		bufferbuilder.pos(xMin, yMin, 0).tex(minU, minV).color(red, green, blue, alpha).endVertex();
		Tessellator.getInstance().draw();
	}

	private static List<? extends ITextComponent> tooltipToRender = Collections.emptyList();

	public static void setTooltipToRender(List<? extends ITextComponent> tooltip) {
		tooltipToRender = tooltip;
	}

	public static void renderTooltip(Minecraft minecraft, int mouseX, int mouseY) {
		if (tooltipToRender.isEmpty()) {
			return;
		}

		renderTooltip(minecraft, tooltipToRender, mouseX, mouseY, ITooltipRenderPart.EMPTY, null, ItemStack.EMPTY, 200);
		tooltipToRender = Collections.emptyList();
	}

	public static void renderTooltip(Minecraft minecraft, List<? extends ITextComponent> textLines, int mouseX, int mouseY,
			ITooltipRenderPart additionalRender, @Nullable FontRenderer tooltipRenderFont, ItemStack stack) {
		renderTooltip(minecraft, textLines, mouseX, mouseY, additionalRender, tooltipRenderFont, stack, 0);
	}

	public static void renderTooltip(Minecraft minecraft, List<? extends ITextComponent> textLines, int mouseX, int mouseY,
			ITooltipRenderPart additionalRender, @Nullable FontRenderer tooltipRenderFont, ItemStack stack, int maxTextWidth) {

		FontRenderer font = tooltipRenderFont == null ? minecraft.fontRenderer : tooltipRenderFont;

		int windowWidth = minecraft.mainWindow.getScaledWidth();
		int windowHeight = minecraft.mainWindow.getScaledHeight();

		int tooltipWidth = getMaxLineWidth(textLines, font);

		if (maxTextWidth > 0 && tooltipWidth > maxTextWidth) {
			tooltipWidth = maxTextWidth;
		}

		int wrappedTooltipWidth = 0;
		List<ITextComponent> wrappedTextLines = new ArrayList<>();
		for (ITextComponent textLine : textLines) {
			for (String line : font.listFormattedStringToWidth(textLine.getFormattedText(), tooltipWidth)) {
				int lineWidth = font.getStringWidth(line);
				if (lineWidth > wrappedTooltipWidth) {wrappedTooltipWidth = lineWidth;}
				wrappedTextLines.add(new StringTextComponent(line));
			}
		}
		tooltipWidth = wrappedTooltipWidth;
		tooltipWidth = Math.max(tooltipWidth, additionalRender.getWidth());

		textLines = wrappedTextLines;

		int leftX = mouseX + 12;
		if (leftX + tooltipWidth > windowWidth) {
			leftX -= 28 + tooltipWidth;
		}

		int topY = mouseY - 12;
		int tooltipHeight = 8;
		if (textLines.size() > 1) {
			tooltipHeight += 2 + (textLines.size() - 1) * 10;
		}
		tooltipHeight += additionalRender.getHeight();

		if (topY + tooltipHeight + 6 > windowHeight) {
			topY = windowHeight - tooltipHeight - 6;
		}

		GlStateManager.pushMatrix();
		renderTooltipBackground(tooltipWidth, leftX, topY, tooltipHeight, TOOLTIP_BACKGROUND_COLOR, TOOLTIP_BORDER_COLOR_START, TOOLTIP_BORDER_COLOR_END);

		GlStateManager.translated(0.0D, 0.0D, 400.0D);
		topY = writeTooltipLines(textLines, font, leftX, topY, -1);
		additionalRender.render(leftX, topY, font);
		GlStateManager.popMatrix();
	}

	public static void renderTooltipBackground(int tooltipWidth, int leftX, int topY, int tooltipHeight, int backgroundColor, int borderColorStart, int borderColorEnd) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);

		fillGradient(bufferbuilder, leftX - 3, topY - 4, leftX + tooltipWidth + 3, topY - 3, 400, backgroundColor, backgroundColor);
		fillGradient(bufferbuilder, leftX - 3, topY + tooltipHeight + 3, leftX + tooltipWidth + 3, topY + tooltipHeight + 4, 400, backgroundColor, backgroundColor);
		fillGradient(bufferbuilder, leftX - 3, topY - 3, leftX + tooltipWidth + 3, topY + tooltipHeight + 3, 400, backgroundColor, backgroundColor);
		fillGradient(bufferbuilder, leftX - 4, topY - 3, leftX - 3, topY + tooltipHeight + 3, 400, backgroundColor, backgroundColor);
		fillGradient(bufferbuilder, leftX + tooltipWidth + 3, topY - 3, leftX + tooltipWidth + 4, topY + tooltipHeight + 3, 400, backgroundColor, backgroundColor);
		fillGradient(bufferbuilder, leftX - 3, topY - 3 + 1, leftX - 3 + 1, topY + tooltipHeight + 3 - 1, 400, borderColorStart, borderColorEnd);
		fillGradient(bufferbuilder, leftX + tooltipWidth + 2, topY - 3 + 1, leftX + tooltipWidth + 3, topY + tooltipHeight + 3 - 1, 400, borderColorStart, borderColorEnd);
		fillGradient(bufferbuilder, leftX - 3, topY - 3, leftX + tooltipWidth + 3, topY - 3 + 1, 400, borderColorStart, borderColorStart);
		fillGradient(bufferbuilder, leftX - 3, topY + tooltipHeight + 2, leftX + tooltipWidth + 3, topY + tooltipHeight + 3, 400, borderColorEnd, borderColorEnd);
		GlStateManager.enableDepthTest();
		GlStateManager.disableTexture();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.shadeModel(7425);
		Tessellator.getInstance().draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableTexture();
	}

	private static int getMaxLineWidth(List<? extends ITextComponent> tooltips, FontRenderer font) {
		int maxLineWidth = 0;
		for (ITextComponent line : tooltips) {
			int lineWidth = font.getStringWidth(line.getFormattedText());
			if (lineWidth > maxLineWidth) {
				maxLineWidth = lineWidth;
			}
		}
		return maxLineWidth;
	}

	public static int writeTooltipLines(List<? extends ITextComponent> textLines, FontRenderer font, float leftX, int topY, int color) {
		for (int i = 0; i < textLines.size(); ++i) {
			ITextComponent line = textLines.get(i);
			if (line != null) {
				font.drawStringWithShadow(line.getFormattedText(), leftX, topY, color);
			}

			if (i == 0) {
				topY += 2;
			}

			topY += 10;
		}
		return topY;
	}

	private static void fillGradient(BufferBuilder builder, int x1, int y1, int x2, int y2, int z, int colorA, int colorB) {
		float f = (colorA >> 24 & 255) / 255.0F;
		float f1 = (colorA >> 16 & 255) / 255.0F;
		float f2 = (colorA >> 8 & 255) / 255.0F;
		float f3 = (colorA & 255) / 255.0F;
		float f4 = (colorB >> 24 & 255) / 255.0F;
		float f5 = (colorB >> 16 & 255) / 255.0F;
		float f6 = (colorB >> 8 & 255) / 255.0F;
		float f7 = (colorB & 255) / 255.0F;
		builder.pos(x2, y1, z).color(f1, f2, f3, f).endVertex();
		builder.pos(x1, y1, z).color(f1, f2, f3, f).endVertex();
		builder.pos(x1, y2, z).color(f5, f6, f7, f4).endVertex();
		builder.pos(x2, y2, z).color(f5, f6, f7, f4).endVertex();
	}

	public static ToggleButton.StateData getButtonStateData(UV uv, Dimension dimension, Position offset, ITextComponent... tooltip) {
		return getButtonStateData(uv, dimension, offset, Arrays.asList(tooltip));
	}

	public static ToggleButton.StateData getButtonStateData(UV uv, String tooltip, Dimension dimension) {
		return getButtonStateData(uv, tooltip, dimension, new Position(0, 0));
	}

	public static ToggleButton.StateData getButtonStateData(UV uv, String tooltip, Dimension dimension, Position offset) {
		return new ToggleButton.StateData(new TextureBlitData(ICONS, offset, Dimension.SQUARE_256, uv, dimension),
				new TranslationTextComponent(tooltip)
		);
	}

	public static ToggleButton.StateData getButtonStateData(UV uv, Dimension dimension, Position offset, List<? extends ITextComponent> tooltip) {
		return new ToggleButton.StateData(new TextureBlitData(ICONS, offset, Dimension.SQUARE_256, uv, dimension), tooltip);
	}

	public static void renderSlotsBackground(Minecraft minecraft, int x, int y, int slotsInRow, int fullSlotRows, int extraRowSlots) {
		renderSlotsBackground(minecraft, x, y, slotsInRow, fullSlotRows);
		if (extraRowSlots > 0) {
			renderSlotsBackground(minecraft, x, y + fullSlotRows * 18, extraRowSlots, 1);
		}
	}

	public static void renderTiledFluidTextureAtlas(TextureAtlasSprite sprite, int color, int x, int y, int height, Minecraft minecraft) {
		minecraft.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
		BufferBuilder builder = Tessellator.getInstance().getBuffer();
		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

		float u1 = sprite.getMinU();
		float v1 = sprite.getMinV();
		int spriteHeight = sprite.getHeight();
		int spriteWidth = sprite.getWidth();
		int startY = y;
		float red = (color >> 16 & 255) / 255.0F;
		float green = (color >> 8 & 255) / 255.0F;
		float blue = (color & 255) / 255.0F;
		do {
			int renderHeight = Math.min(spriteHeight, height);
			height -= renderHeight;
			float v2 = sprite.getInterpolatedV((16f * renderHeight) / spriteHeight);
			float u2 = sprite.getInterpolatedU((16f * 16) / spriteWidth);

			builder.pos(x, (float) startY + renderHeight, 100).tex(u1, v2).color(red, green, blue, 1).endVertex();
			builder.pos((float) x + 16, (float) startY + renderHeight, 100).tex(u2, v2).color(red, green, blue, 1).endVertex();
			builder.pos((float) x + 16, startY, 100).tex(u2, v1).color(red, green, blue, 1).endVertex();
			builder.pos(x, startY, 100).tex(u1, v1).color(red, green, blue, 1).endVertex();

			startY += renderHeight;
		} while (height > 0);

		GlStateManager.enableAlphaTest();
		Tessellator.getInstance().draw();
	}

	public static void renderControlBackground(Minecraft minecraft, int x, int y, int renderWidth, int renderHeight) {
		minecraft.getTextureManager().bindTexture(GUI_CONTROLS);

		int u = 29;
		int v = 146;
		int textureBgWidth = 66;
		int textureBgHeight = 56;
		int halfWidth = renderWidth / 2;
		int halfHeight = renderHeight / 2;
		AbstractGui.blit(x, y, u, v, halfWidth, halfHeight, GUI_CONTROLS_TEXTURE_WIDTH, GUI_CONTROLS_TEXTURE_HEIGHT);
		AbstractGui.blit(x, y + halfHeight, u, (float) v + textureBgHeight - halfHeight, halfWidth, halfHeight, GUI_CONTROLS_TEXTURE_WIDTH, GUI_CONTROLS_TEXTURE_HEIGHT);
		AbstractGui.blit(x + halfWidth, y, (float) u + textureBgWidth - halfWidth, v, halfWidth, halfHeight, GUI_CONTROLS_TEXTURE_WIDTH, GUI_CONTROLS_TEXTURE_HEIGHT);
		AbstractGui.blit(x + halfWidth, y + halfHeight, (float) u + textureBgWidth - halfWidth, (float) v + textureBgHeight - halfHeight, halfWidth, halfHeight, GUI_CONTROLS_TEXTURE_WIDTH, GUI_CONTROLS_TEXTURE_HEIGHT);
	}

	public interface ITooltipRenderPart {
		ITooltipRenderPart EMPTY = new ITooltipRenderPart() {
			@Override
			public int getWidth() {
				return 0;
			}

			@Override
			public int getHeight() {
				return 0;
			}

			@Override
			public void render(int leftX, int topY, FontRenderer font) {
				//noop
			}
		};

		int getWidth();

		int getHeight();

		void render(int leftX, int topY, FontRenderer font);
	}

	public static void tryRenderGuiItem(ItemRenderer itemRenderer, TextureManager textureManager,
			@Nullable LivingEntity livingEntity, ItemStack stack, int x, int y, int rotation) {
		if (!stack.isEmpty()) {
			itemRenderer.zLevel += 50.0F;

			try {
				renderGuiItem(itemRenderer, textureManager, stack, x, y, itemRenderer.getItemModelWithOverrides(stack, null, livingEntity), rotation);
			}
			catch (Throwable throwable) {
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering item");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being rendered");
				crashreportcategory.addDetail("Item Type", () -> String.valueOf(stack.getItem()));
				crashreportcategory.addDetail("Registry Name", () -> String.valueOf(Registry.ITEM.getKey(stack.getItem())));
				crashreportcategory.addDetail("Item Damage", () -> String.valueOf(stack.getDamage()));
				crashreportcategory.addDetail("Item NBT", () -> String.valueOf(stack.getTag()));
				crashreportcategory.addDetail("Item Foil", () -> String.valueOf(stack.hasEffect()));
				throw new ReportedException(crashreport);
			}

			itemRenderer.zLevel -= 50.0F;
		}
	}

	private static void renderGuiItem(ItemRenderer itemRenderer, TextureManager textureManager, ItemStack stack, int x, int y, IBakedModel bakedModel, int rotation) {
		GlStateManager.pushMatrix();
		textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
		textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableAlphaTest();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.translatef((float) x, (float) y, 100.0F + itemRenderer.zLevel);
		GlStateManager.translatef(8.0F, 8.0F, 0.0F);
		if (rotation != 0) {
			GlStateManager.rotatef(rotation, 0, 0, 1);
		}
		GlStateManager.scalef(1.0F, -1.0F, 1.0F);
		GlStateManager.scalef(16.0F, 16.0F, 16.0F);

		boolean flat = !bakedModel.isGui3d();
		if (flat) {
			RenderHelper.disableStandardItemLighting();
		} else {
			GlStateManager.enableLighting();
		}

		bakedModel.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GUI);
		GlStateManager.enableDepthTest();
		itemRenderer.renderItem(stack, bakedModel);
		if (flat) {
			RenderHelper.enableGUIStandardItemLighting();
		}

		GlStateManager.disableAlphaTest();
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
		textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
	}
}
