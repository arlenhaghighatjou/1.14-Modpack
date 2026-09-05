/*
 * Copyright (c) 2023 lax1dude. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.lax1dude.eaglercraft.opengl.ext.deferred.gui;

import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.opengl.ext.deferred.EaglerDeferredConfig;
import net.lax1dude.eaglercraft.opengl.ext.deferred.ShaderPackInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GuiShaderConfigList {

	public static final ResourceLocation shaderPackIcon = new ResourceLocation("eagler:glsl/deferred/shader_pack_icon.png");

	private final GuiShaderConfig screen;
	private final List<GuiShaderConfigEntry> entries = new ArrayList<>();

	private static abstract class ShaderOption {

		private final String label;
		private final List<String> desc;

		private ShaderOption(String label, List<String> desc) {
			this.label = label;
			this.desc = desc;
		}

		protected abstract String getDisplayValue();

		protected abstract void toggleOption(int dir);

		protected abstract boolean getDirty();

	}

	public GuiShaderConfigList(GuiShaderConfig screenIn, Minecraft mcIn) {
		this.screen = screenIn;
		this.entries.add(new ListEntryHeader("Current Shader Pack:"));
		this.entries.add(new ListEntryPackInfo());
		this.entries.add(new ListEntrySpacing());
		this.entries.add(new ListEntrySpacing());
		this.entries.add(new ListEntryHeader(I18n.format("shaders.gui.headerTier1")));
		List<ShaderOption> opts = new ArrayList<>();
		EaglerDeferredConfig conf = mcIn.gameSettings.deferredShaderConf;
		if(conf.shaderPackInfo != null && conf.shaderPackInfo.WAVING_BLOCKS) {
			opts.add(new ShaderOption(GuiShaderConfig.loadShaderLbl("WAVING_BLOCKS"), GuiShaderConfig.loadShaderDesc("WAVING_BLOCKS")) {
				private final boolean originalValue = conf.wavingBlocks;
				@Override
				protected String getDisplayValue() {
					return GuiShaderConfig.getColoredOnOff(conf.wavingBlocks, TextFormatting.GREEN, TextFormatting.RED);
				}
				@Override
				protected void toggleOption(int dir) {
					conf.wavingBlocks = !conf.wavingBlocks;
				}
				@Override
				protected boolean getDirty() {
					return conf.wavingBlocks != originalValue;
				}
			});
		}
		if(conf.shaderPackInfo != null && conf.shaderPackInfo.DYNAMIC_LIGHTS) {
			opts.add(new ShaderOption(GuiShaderConfig.loadShaderLbl("DYNAMIC_LIGHTS"), GuiShaderConfig.loadShaderDesc("DYNAMIC_LIGHTS")) {
				private final boolean originalValue = conf.dynamicLights;
				@Override
				protected String getDisplayValue() {
					return GuiShaderConfig.getColoredOnOff(conf.dynamicLights, TextFormatting.GREEN, TextFormatting.RED);
				}
				@Override
				protected void toggleOption(int dir) {
					conf.dynamicLights = !conf.dynamicLights;
				}
				@Override
				protected boolean getDirty() {
					return conf.dynamicLights != originalValue;
				}
			});
		}
		if(conf.shaderPackInfo != null && conf.shaderPackInfo.GLOBAL_AMBIENT_OCCLUSION) {
			opts.add(new ShaderOption(GuiShaderConfig.loadShaderLbl("GLOBAL_AMBIENT_OCCLUSION"), GuiShaderConfig.loadShaderDesc("GLOBAL_AMBIENT_OCCLUSION")) {
				private final boolean originalValue = conf.ssao;
				@Override
				protected String getDisplayValue() {
					return GuiShaderConfig.getColoredOnOff(conf.ssao, TextFormatting.GREEN, TextFormatting.RED);
				}
				@Override
				protected void toggleOption(int dir) {
					conf.ssao = !conf.ssao;
				}
				@Override
				protected boolean getDirty() {
					return conf.ssao != originalValue;
				}
			});
		}
		if(conf.shaderPackInfo != null && conf.shaderPackInfo.SHADOWS_SUN) {
			opts.add(new ShaderOption(GuiShaderConfig.loadShaderLbl("SHADOWS_SUN"), GuiShaderConfig.loadShaderDesc("SHADOWS_SUN")) {
				private final int originalValue = conf.shadowsSun;
				@Override
				protected String getDisplayValue() {
					return conf.shadowsSun == 0 ? "" + TextFormatting.RED + "0" : "" + TextFormatting.YELLOW + (1 << (conf.shadowsSun + 3));
				}
				@Override
				protected void toggleOption(int dir) {
					conf.shadowsSun = (conf.shadowsSun + dir + 5) % 5;
				}
				@Override
				protected boolean getDirty() {
					return conf.shadowsSun != originalValue;
				}
			});
		}
		if(conf.shaderPackInfo != null && conf.shaderPackInfo.REFLECTIONS_PARABOLOID) {
			opts.add(new ShaderOption(GuiShaderConfig.loadShaderLbl("REFLECTIONS_PARABOLOID"), GuiShaderConfig.loadShaderDesc("REFLECTIONS_PARABOLOID")) {
				private final boolean originalValue = conf.useEnvMap;
				@Override
				protected String getDisplayValue() {
					return GuiShaderConfig.getColoredOnOff(conf.useEnvMap, TextFormatting.GREEN, TextFormatting.RED);
				}
				@Override
				protected void toggleOption(int dir) {
					conf.useEnvMap = !conf.useEnvMap;
				}
				@Override
				protected boolean getDirty() {
					return conf.useEnvMap != originalValue;
				}
			});
		}
		if(conf.shaderPackInfo != null && conf.shaderPackInfo.POST_LENS_DISTORION) {
			opts.add(new ShaderOption(GuiShaderConfig.loadShaderLbl("POST_LENS_DISTORION"), GuiShaderConfig.loadShaderDesc("POST_LENS_DISTORION")) {
				private final boolean originalValue = conf.lensDistortion;
				@Override
				protected String getDisplayValue() {
					return GuiShaderConfig.getColoredOnOff(conf.lensDistortion, TextFormatting.GREEN, TextFormatting.RED);
				}
				@Override
				protected void toggleOption(int dir) {
					conf.lensDistortion = !conf.lensDistortion;
				}
				@Override
				protected boolean getDirty() {
					return conf.lensDistortion != originalValue;
				}
			});
		}
		if(conf.shaderPackInfo != null && conf.shaderPackInfo.SUBSURFACE_SCATTERING) {
			opts.add(new ShaderOption(GuiShaderConfig.loadShaderLbl("SUBSURFACE_SCATTERING"), GuiShaderConfig.loadShaderDesc("SUBSURFACE_SCATTERING")) {
				private final boolean originalValue = conf.subsurfaceScattering;
				@Override
				protected String getDisplayValue() {
					return GuiShaderConfig.getColoredOnOff(conf.subsurfaceScattering, TextFormatting.GREEN, TextFormatting.RED);
				}
				@Override
				protected void toggleOption(int dir) {
					conf.subsurfaceScattering = !conf.subsurfaceScattering;
				}
				@Override
				protected boolean getDirty() {
					return conf.subsurfaceScattering != originalValue;
				}
			});
		}
		if(conf.shaderPackInfo != null && conf.shaderPackInfo.POST_LENS_FLARES) {
			opts.add(new ShaderOption(GuiShaderConfig.loadShaderLbl("POST_LENS_FLARES"), GuiShaderConfig.loadShaderDesc("POST_LENS_FLARES")) {
				private final boolean originalValue = conf.lensFlares;
				@Override
				protected String getDisplayValue() {
					return GuiShaderConfig.getColoredOnOff(conf.lensFlares, TextFormatting.GREEN, TextFormatting.RED);
				}
				@Override
				protected void toggleOption(int dir) {
					conf.lensFlares = !conf.lensFlares;
				}
				@Override
				protected boolean getDirty() {
					return conf.lensFlares != originalValue;
				}
			});
		}
		if(conf.shaderPackInfo != null && conf.shaderPackInfo.POST_FXAA) {
			opts.add(new ShaderOption(GuiShaderConfig.loadShaderLbl("POST_FXAA"), GuiShaderConfig.loadShaderDesc("POST_FXAA")) {
				private final boolean originalValue = conf.fxaa;
				@Override
				protected String getDisplayValue() {
					return GuiShaderConfig.getColoredOnOff(conf.fxaa, TextFormatting.GREEN, TextFormatting.RED);
				}
				@Override
				protected void toggleOption(int dir) {
					conf.fxaa = !conf.fxaa;
				}
				@Override
				protected boolean getDirty() {
					return conf.fxaa != originalValue;
				}
			});
		}
		addAllOptions(opts);
		opts.clear();
		this.entries.add(new ListEntryHeader(I18n.format("shaders.gui.headerTier2")));
		if(conf.shaderPackInfo != null && conf.shaderPackInfo.SHADOWS_COLORED) {
			opts.add(new ShaderOption(GuiShaderConfig.loadShaderLbl("SHADOWS_COLORED"), GuiShaderConfig.loadShaderDesc("SHADOWS_COLORED")) {
				private final boolean originalValue = conf.shadowsColored;
				@Override
				protected String getDisplayValue() {
					return GuiShaderConfig.getColoredOnOff(conf.shadowsColored, TextFormatting.GREEN, TextFormatting.RED);
				}
				@Override
				protected void toggleOption(int dir) {
					conf.shadowsColored = !conf.shadowsColored;
				}
				@Override
				protected boolean getDirty() {
					return conf.shadowsColored != originalValue;
				}
			});
		}
		if(conf.shaderPackInfo != null && conf.shaderPackInfo.SHADOWS_SMOOTHED) {
			opts.add(new ShaderOption(GuiShaderConfig.loadShaderLbl("SHADOWS_SMOOTHED"), GuiShaderConfig.loadShaderDesc("SHADOWS_SMOOTHED")) {
				private final boolean originalValue = conf.shadowsSmoothed;
				@Override
				protected String getDisplayValue() {
					return GuiShaderConfig.getColoredOnOff(conf.shadowsSmoothed, TextFormatting.GREEN, TextFormatting.RED);
				}
				@Override
				protected void toggleOption(int dir) {
					conf.shadowsSmoothed = !conf.shadowsSmoothed;
				}
				@Override
				protected boolean getDirty() {
					return conf.shadowsSmoothed != originalValue;
				}
			});
		}
		if(conf.shaderPackInfo != null && conf.shaderPackInfo.REALISTIC_WATER) {
			opts.add(new ShaderOption(GuiShaderConfig.loadShaderLbl("REALISTIC_WATER"), GuiShaderConfig.loadShaderDesc("REALISTIC_WATER")) {
				private final boolean originalValue = conf.realisticWater;
				@Override
				protected String getDisplayValue() {
					return GuiShaderConfig.getColoredOnOff(conf.realisticWater, TextFormatting.GREEN, TextFormatting.RED);
				}
				@Override
				protected void toggleOption(int dir) {
					conf.realisticWater = !conf.realisticWater;
				}
				@Override
				protected boolean getDirty() {
					return conf.realisticWater != originalValue;
				}
			});
		}
		if(conf.shaderPackInfo != null && conf.shaderPackInfo.POST_BLOOM) {
			opts.add(new ShaderOption(GuiShaderConfig.loadShaderLbl("POST_BLOOM"), GuiShaderConfig.loadShaderDesc("POST_BLOOM")) {
				private final boolean originalValue = conf.bloom;
				@Override
				protected String getDisplayValue() {
					return GuiShaderConfig.getColoredOnOff(conf.bloom, TextFormatting.GREEN, TextFormatting.RED);
				}
				@Override
				protected void toggleOption(int dir) {
					conf.bloom = !conf.bloom;
				}
				@Override
				protected boolean getDirty() {
					return conf.bloom != originalValue;
				}
			});
		}
		if(conf.shaderPackInfo != null && conf.shaderPackInfo.LIGHT_SHAFTS) {
			opts.add(new ShaderOption(GuiShaderConfig.loadShaderLbl("LIGHT_SHAFTS"), GuiShaderConfig.loadShaderDesc("LIGHT_SHAFTS")) {
				private final boolean originalValue = conf.lightShafts;
				@Override
				protected String getDisplayValue() {
					return GuiShaderConfig.getColoredOnOff(conf.lightShafts, TextFormatting.GREEN, TextFormatting.RED);
				}
				@Override
				protected void toggleOption(int dir) {
					conf.lightShafts = !conf.lightShafts;
				}
				@Override
				protected boolean getDirty() {
					return conf.lightShafts != originalValue;
				}
			});
		}
		if(conf.shaderPackInfo != null && conf.shaderPackInfo.SCREEN_SPACE_REFLECTIONS) {
			opts.add(new ShaderOption(GuiShaderConfig.loadShaderLbl("SCREEN_SPACE_REFLECTIONS"), GuiShaderConfig.loadShaderDesc("SCREEN_SPACE_REFLECTIONS")) {
				private final boolean originalValue = conf.raytracing;
				@Override
				protected String getDisplayValue() {
					return GuiShaderConfig.getColoredOnOff(conf.raytracing, TextFormatting.GREEN, TextFormatting.RED);
				}
				@Override
				protected void toggleOption(int dir) {
					conf.raytracing = !conf.raytracing;
				}
				@Override
				protected boolean getDirty() {
					return conf.raytracing != originalValue;
				}
			});
		}
		addAllOptions(opts);
		setAllDisabled(!mcIn.gameSettings.shaders);
	}

	private void addAllOptions(List<ShaderOption> opts) {
		for(int i = 0, l = opts.size(); i < l; ++i) {
			ShaderOption opt1 = opts.get(i);
			if(++i >= l) {
				entries.add(new ListEntryButtonRow(opt1, null, null));
				break;
			}
			ShaderOption opt2 = opts.get(i);
			if(++i >= l) {
				entries.add(new ListEntryButtonRow(opt1, opt2, null));
				break;
			}
			entries.add(new ListEntryButtonRow(opt1, opt2, opts.get(i)));
		}
	}

	public void setAllDisabled(boolean disable) {
		for(int i = 0, l = entries.size(); i < l; ++i) {
			GuiShaderConfigEntry etr = entries.get(i);
			if(etr instanceof ListEntryButtonRow) {
				((ListEntryButtonRow)etr).setEnabled(!disable);
			}
		}
	}

	public int getEntryCount() {
		return entries.size();
	}

	public void resize(int width, int height) {
	}

	public void render(int mx, int my, float partialTicks, int scrollOffset) {
		int y = 32;
		for(int i = 0, l = entries.size(); i < l; ++i) {
			GuiShaderConfigEntry entry = entries.get(i);
			entry.drawEntry(i, 10, y - scrollOffset, this.screen.getFontRenderer(), this.screen, mx, my, partialTicks);
			y += entry.getHeight();
		}
	}

	public boolean mouseClicked(int mouseX, int mouseY) {
		int y = 32;
		for(int i = 0, l = entries.size(); i < l; ++i) {
			GuiShaderConfigEntry entry = entries.get(i);
			if(entry.mouseClicked(mouseX, mouseY, y)) {
				return true;
			}
			y += entry.getHeight();
		}
		return false;
	}

	private interface GuiShaderConfigEntry {
		void drawEntry(int entryID, int x, int y, FontRenderer font, GuiShaderConfig screen, int mx, int my, float partialTicks);
		boolean mouseClicked(int mx, int my, int yOffset);
		int getHeight();
	}

	private class ListEntryPackInfo implements GuiShaderConfigEntry {

		@Override
		public void drawEntry(int entryID, int x, int y, FontRenderer font, GuiShaderConfig screen, int mx, int my, float partialTicks) {
			Minecraft mc = Minecraft.getInstance();
			ShaderPackInfo info = mc.gameSettings.deferredShaderConf.shaderPackInfo;
			if(info == null) {
				String text = I18n.format("shaders.gui.nopack");
				int strWidth = font.getStringWidth(text);
				screen.drawString(font, text, x + 38, y + 10, 0xFFFFFF);
				return;
			}
			String packNameString = info.name;
			int strWidth = font.getStringWidth(packNameString) + 40;
			if(strWidth < 210) {
				strWidth = 210;
			}
			int screenWidth = screen.width;
			int x2 = strWidth > screenWidth ? x : x + (screenWidth - strWidth) / 2;
			screen.drawString(font, packNameString, x2 + 38, y + 3, 0xFFFFFF);
			screen.drawString(font, "Author: " + info.author, x2 + 38, y + 14, 0xBBBBBB);
			screen.drawString(font, "Version: " + info.vers, x2 + 38, y + 25, 0x888888);
			List<String> descLines = font.listFormattedStringToWidth(info.desc, strWidth);
			for(int i = 0, l = descLines.size(); i < l; ++i) {
				screen.drawString(font, descLines.get(i), x2, y + 43 + i * 9, 0xBBBBBB);
			}
			mc.getTextureManager().bindTexture(shaderPackIcon);
			AbstractGui.blit(x2, y + 2, 0, 0, 32, 32, 32, 32);
		}

		@Override
		public boolean mouseClicked(int mx, int my, int yOffset) {
			return false;
		}

		@Override
		public int getHeight() {
			Minecraft mc = Minecraft.getInstance();
			ShaderPackInfo info = mc.gameSettings.deferredShaderConf.shaderPackInfo;
			if(info != null) {
				List<String> descLines = mc.fontRenderer.listFormattedStringToWidth(info.desc, 210);
				return 43 + descLines.size() * 9 + 10;
			}
			return 30;
		}

	}

	private class ListEntrySpacing implements GuiShaderConfigEntry {

		@Override
		public void drawEntry(int entryID, int x, int y, FontRenderer font, GuiShaderConfig screen, int mx, int my, float partialTicks) {
		}

		@Override
		public boolean mouseClicked(int mx, int my, int yOffset) {
			return false;
		}

		@Override
		public int getHeight() {
			return 8;
		}

	}

	private class ListEntryHeader implements GuiShaderConfigEntry {

		private final String text;

		private ListEntryHeader(String text) {
			this.text = text;
		}

		@Override
		public void drawEntry(int entryID, int x, int y, FontRenderer font, GuiShaderConfig screen, int mx, int my, float partialTicks) {
			screen.drawString(font, text, x, y + 10, 0xFFFFFF);
		}

		@Override
		public boolean mouseClicked(int mx, int my, int yOffset) {
			return false;
		}

		@Override
		public int getHeight() {
			return 25;
		}

	}

	private class ListEntryButtonRow implements GuiShaderConfigEntry {

		private final ShaderOption opt1;
		private final ShaderOption opt2;
		private final ShaderOption opt3;

		private Button button1;
		private Button button2;
		private Button button3;

		private int lastX = 0;
		private int lastY = 0;

		private ListEntryButtonRow(ShaderOption opt1, ShaderOption opt2, ShaderOption opt3) {
			this.opt1 = opt1;
			this.opt2 = opt2;
			this.opt3 = opt3;
		}

		private void ensureButtonsCreated() {
			if(this.opt1 != null && this.button1 == null) {
				this.button1 = new Button(0, 0, 73, 20,
						opt1.label + ": " + opt1.getDisplayValue(),
						(btn) -> { opt1.toggleOption(1); updateButtonText(); });
			}
			if(this.opt2 != null && this.button2 == null) {
				this.button2 = new Button(0, 0, 73, 20,
						opt2.label + ": " + opt2.getDisplayValue(),
						(btn) -> { opt2.toggleOption(1); updateButtonText(); });
			}
			if(this.opt3 != null && this.button3 == null) {
				this.button3 = new Button(0, 0, 73, 20,
						opt3.label + ": " + opt3.getDisplayValue(),
						(btn) -> { opt3.toggleOption(1); updateButtonText(); });
			}
		}

		private void updateButtonText() {
			if(this.button1 != null && opt1 != null) {
				this.button1.setMessage((opt1.getDirty() ? "*" : "") + opt1.label + ": " + opt1.getDisplayValue());
			}
			if(this.button2 != null && opt2 != null) {
				this.button2.setMessage((opt2.getDirty() ? "*" : "") + opt2.label + ": " + opt2.getDisplayValue());
			}
			if(this.button3 != null && opt3 != null) {
				this.button3.setMessage((opt3.getDirty() ? "*" : "") + opt3.label + ": " + opt3.getDisplayValue());
			}
		}

		public void setEnabled(boolean enabled) {
			ensureButtonsCreated();
			if(this.button1 != null) {
				this.button1.active = enabled;
			}
			if(this.button2 != null) {
				this.button2.active = enabled;
			}
			if(this.button3 != null) {
				this.button3.active = enabled;
			}
		}

		@Override
		public void drawEntry(int entryID, int x, int y, FontRenderer font, GuiShaderConfig screen, int mx, int my, float partialTicks) {
			ensureButtonsCreated();
			lastX = x;
			lastY = y;
			if(this.button1 != null) {
				this.button1.x = x;
				this.button1.y = y;
				this.button1.render(mx, my, partialTicks);
				if(this.button1.isHovered() && y + 10 < screen.height - 36 && y + 10 > 28) {
					renderTooltip(mx, my + 15, this.opt1.desc, screen);
				}
			}
			if(this.button2 != null) {
				this.button2.x = x + 75;
				this.button2.y = y;
				this.button2.render(mx, my, partialTicks);
				if(this.button2.isHovered() && y + 10 < screen.height - 36 && y + 10 > 28) {
					renderTooltip(mx, my + 15, this.opt2.desc, screen);
				}
			}
			if(this.button3 != null) {
				this.button3.x = x + 150;
				this.button3.y = y;
				this.button3.render(mx, my, partialTicks);
				if(this.button3.isHovered() && y + 10 < screen.height - 36 && y + 10 > 28) {
					renderTooltip(mx, my + 15, this.opt3.desc, screen);
				}
			}
		}

		@Override
		public boolean mouseClicked(int mx, int my, int yOffset) {
			ensureButtonsCreated();
			if(this.button1 != null) {
				this.button1.x = lastX;
				this.button1.y = lastY;
				if(this.button1.mouseClicked(mx, my, 0)) {
					return true;
				}
			}
			if(this.button2 != null) {
				this.button2.x = lastX + 75;
				this.button2.y = lastY;
				if(this.button2.mouseClicked(mx, my, 0)) {
					return true;
				}
			}
			if(this.button3 != null) {
				this.button3.x = lastX + 150;
				this.button3.y = lastY;
				if(this.button3.mouseClicked(mx, my, 0)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public int getHeight() {
			return 22;
		}

	}

	private List<String> tooltipToShow = null;
	private int tooltipToShowX = 0;
	private int tooltipToShowY = 0;

	public void postRender(int mx, int my, float partialTicks, int scrollOffset) {
		if(tooltipToShow != null) {
			screen.renderTooltip(tooltipToShow, tooltipToShowX, tooltipToShowY);
			tooltipToShow = null;
		}
	}

	private void renderTooltip(int x, int y, List<String> msg, GuiShaderConfig screen) {
		renderTooltip(x, y, 250, msg, screen);
	}

	private void renderTooltip(int x, int y, int width, List<String> msg, GuiShaderConfig screen) {
		List<String> tooltipList = new ArrayList<>(msg.size() * 2);
		FontRenderer font = screen.getFontRenderer();
		for(int i = 0, l = msg.size(); i < l; ++i) {
			String s = msg.get(i);
			if(s.length() > 0) {
				tooltipList.addAll(font.listFormattedStringToWidth(s, width));
			}else {
				tooltipList.add("");
			}
		}
		tooltipToShow = tooltipList;
		tooltipToShowX = x;
		tooltipToShowY = y;
	}

	public boolean isDirty() {
		for(int i = 0, l = entries.size(); i < l; ++i) {
			GuiShaderConfigEntry etr = entries.get(i);
			if(etr instanceof ListEntryButtonRow) {
				ListEntryButtonRow etr2 = (ListEntryButtonRow)etr;
				if(etr2.opt1 != null) {
					if(etr2.opt1.getDirty()) {
						return true;
					}
				}
				if(etr2.opt2 != null) {
					if(etr2.opt2.getDirty()) {
						return true;
					}
				}
				if(etr2.opt3 != null) {
					if(etr2.opt3.getDirty()) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
