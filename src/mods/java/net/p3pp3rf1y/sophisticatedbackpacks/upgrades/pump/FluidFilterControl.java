package net.p3pp3rf1y.sophisticatedbackpacks.upgrades.pump;

import net.p3pp3rf1y.sophisticatedbackpacks.util.fluid.FluidAttributes;

import net.minecraft.client.renderer.texture.AtlasTexture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.controls.BackpackWidget;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.GuiHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.Position;

import java.util.Optional;

public class FluidFilterControl extends BackpackWidget {
	private final FluidFilterContainer container;

	protected FluidFilterControl(Position position, FluidFilterContainer container) {
		super(position);
		this.container = container;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY) {
		GuiHelper.renderSlotsBackground(minecraft, x, y, container.getNumberOfFluidFilters(), 1);
	}

	@Override
	protected void renderWidget(int mouseX, int mouseY, float partialTicks) {
		for (int i = 0; i < container.getNumberOfFluidFilters(); i++) {
			Fluid fluid = container.getFluid(i);
			if (fluid != Fluids.EMPTY) {
				ResourceLocation texture = FluidAttributes.getStillTexture(fluid);
				TextureAtlasSprite still = minecraft.getTextureMap().apply(texture);
				GuiHelper.renderTiledFluidTextureAtlas(still, FluidAttributes.getColor(fluid), x + i * 18 + 1, y + 1, 16, minecraft);
			}
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int pButton) {
		if (!isMouseOver(mouseX, mouseY)) {
			return false;
		}

		getSlotClicked(mouseX, mouseY).ifPresent(container::slotClick);

		return true;
	}

	private Optional<Integer> getSlotClicked(double mouseX, double mouseY) {
		if (mouseY < y + 1 || mouseY >= y + 17) {
			return Optional.empty();
		}
		int index = (int) ((mouseX - x) / 18);
		return Optional.of(index);
	}

	@Override
	public int getWidth() {
		return container.getNumberOfFluidFilters() * 18;
	}

	@Override
	public int getHeight() {
		return 18;
	}
}
