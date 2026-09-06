package net.p3pp3rf1y.sophisticatedbackpacks.settings;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.item.DyeColor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.controls.ButtonBase;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.Dimension;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.GuiHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.TranslationHelper;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.GuiHelper.DEFAULT_BUTTON_BACKGROUND;
import static net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.GuiHelper.DEFAULT_BUTTON_HOVERED_BACKGROUND;

public class ColorToggleButton extends ButtonBase {
	private static final DyeColor[] DYE_VALUES = DyeColor.values();
	private static final List<ITextComponent> TOOLTIP = new ImmutableList.Builder<ITextComponent>()
			.add(new TranslationTextComponent(TranslationHelper.translSettingsButton("toggle_color")))
			.addAll(TranslationHelper.getTranslatedLines(TranslationHelper.translSettingsButton("toggle_color_detail"), null, TextFormatting.GRAY))
			.build();

	private final Supplier<DyeColor> getColor;
	private final Consumer<DyeColor> setColor;

	public ColorToggleButton(Position position, Supplier<DyeColor> getColor, Consumer<DyeColor> setColor) {
		super(position, Dimension.SQUARE_18, b -> {});
		this.getColor = getColor;
		this.setColor = setColor;
		setOnClick(this::onClick);
	}

	private void onClick(int button) {
		toggleColor(button);
	}

	private void toggleColor(int button) {
		if (button == 0) {
			setColor.accept(nextColor(getColor.get()));
		} else if (button == 1) {
			setColor.accept(previousColor(getColor.get()));
		}
	}

	private DyeColor nextColor(DyeColor color) {
		return DYE_VALUES[(color.ordinal() + 1) % DYE_VALUES.length];
	}

	private DyeColor previousColor(DyeColor color) {
		return DYE_VALUES[(color.ordinal() - 1 + DYE_VALUES.length) % DYE_VALUES.length];
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY) {
		if (isMouseOver(mouseX, mouseY)) {
			GuiHelper.blit(minecraft, x, y, DEFAULT_BUTTON_HOVERED_BACKGROUND);
		} else {
			GuiHelper.blit(minecraft, x, y, DEFAULT_BUTTON_BACKGROUND);
		}
	}

	@Override
	protected void renderWidget(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.disableDepthTest();
		GlStateManager.colorMask(true, true, true, false);
		int color = getColor.get().getColorValue() | (200 << 24);
		fillGradient(x + 3, y + 3, x + 15, y + 15, color, color);
		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.enableDepthTest();

		if (isMouseOver(mouseX, mouseY)) {
			GuiHelper.setTooltipToRender(TOOLTIP);
		}
	}
}
