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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.opengl.ext.deferred.EaglerDeferredConfig;
import net.lax1dude.eaglercraft.opengl.ext.deferred.ShaderPackInfo;
import net.lax1dude.eaglercraft.opengl.ext.deferred.program.ShaderSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiShaderConfig extends Screen {

	private static final Logger logger = LogManager.getLogger();

	boolean shaderStartState = false;

	private final Screen parent;
	private GuiShaderConfigList configList;

	private Button enableDisableButton;
	private Button doneButton;

	private int scrollOffset = 0;
	private int contentHeight = 0;

	public GuiShaderConfig(Screen parent) {
		super(new TranslationTextComponent("shaders.gui.title"));
		this.parent = parent;
		this.shaderStartState = Minecraft.getInstance().gameSettings.shaders;
	}

	protected void init() {
		this.enableDisableButton = this.addButton(new Button(this.width / 2 - 155, this.height - 30, 150, 20,
				I18n.format("shaders.gui.enable") + ": " + (mc.gameSettings.shaders ? I18n.format("gui.yes") : I18n.format("gui.no")),
				(btn) -> {
			mc.gameSettings.shaders = !mc.gameSettings.shaders;
			if(configList != null) {
				configList.setAllDisabled(!mc.gameSettings.shaders);
			}
			enableDisableButton.setMessage(I18n.format("shaders.gui.enable") + ": "
					+ (mc.gameSettings.shaders ? I18n.format("gui.yes") : I18n.format("gui.no")));
		}));
		this.doneButton = this.addButton(new Button(this.width / 2 + 5, this.height - 30, 150, 20,
				I18n.format("gui.done"), (btn) -> {
			this.mc.displayGuiScreen(parent);
		}));
		if(configList == null) {
			this.configList = new GuiShaderConfigList(this, mc);
		}else {
			this.configList.resize(this.width, this.height);
		}
		scrollOffset = 0;
		recalculateContentHeight();
	}

	private void recalculateContentHeight() {
		contentHeight = 0;
		if(configList != null) {
			contentHeight = Math.max(32 + configList.getEntryCount() * 30 + 20, this.height - 40);
		}
	}

	public void onClose() {
		if(shaderStartState != mc.gameSettings.shaders || configList.isDirty()) {
			mc.gameSettings.saveOptions();
			if(shaderStartState != mc.gameSettings.shaders) {
				mc.reloadResources();
			}else {
				logger.info("Reloading shaders...");
				try {
					mc.gameSettings.deferredShaderConf.reloadShaderPackInfo(mc.getResourceManager());
				}catch(IOException ex) {
					logger.info("Could not reload shader pack info!");
					logger.info(ex);
					logger.info("Shaders have been disabled");
					mc.gameSettings.shaders = false;
					mc.reloadResources();
					return;
				}

				if(mc.gameSettings.shaders) {
					ShaderSource.clearCache();
				}

				if (mc.worldRenderer != null) {
					mc.worldRenderer.loadRenderers();
				}
			}
		}
	}

	public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
		scrollOffset = (int) Math.max(0, Math.min(scrollOffset - scrollDelta * 10, Math.max(0, contentHeight - (this.height - 72))));
		return true;
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(super.mouseClicked(mouseX, mouseY, button)) {
			return true;
		}
		if(configList != null && button == 0) {
			return configList.mouseClicked((int)mouseX, (int)mouseY);
		}
		return false;
	}

	public void render(int i, int j, float f) {
		this.renderBackground();
		if(configList != null) {
			configList.render(i, j, f, scrollOffset);
		}
		drawCenteredString(this.font, I18n.format("shaders.gui.title"), this.width / 2, 15, 16777215);
		super.render(i, j, f);
		if(configList != null) {
			configList.postRender(i, j, f, scrollOffset);
		}
	}

	public void renderTooltip(List<String> txt, int x, int y) {
		super.renderTooltip(txt, x, y);
	}

	FontRenderer getFontRenderer() {
		return this.font;
	}

	static List<String> loadDescription(String key) {
		List<String> ret = new ArrayList<>();
		String msg;
		int i = 0;
		while(true) {
			if((msg = I18n.format(key + '.' + i)).equals(key + '.' + i)) {
				if(!I18n.format(key + '.' + (i + 1)).equals(key + '.' + (i + 1))) {
					msg = "";
				}else {
					break;
				}
			}
			ret.add(msg);
			++i;
		}
		if(ret.size() == 0) {
			ret.add("" + TextFormatting.GRAY + TextFormatting.ITALIC + "(no description found)");
		}
		return ret;
	}

	static String loadShaderLbl(String key) {
		return I18n.format("shaders.gui.option." + key + ".label");
	}

	static List<String> loadShaderDesc(String key) {
		return loadDescription("shaders.gui.option." + key + ".desc");
	}

	static String getColoredOnOff(boolean state, TextFormatting onColor, TextFormatting offColor) {
		return state ? "" + onColor + I18n.format("options.on") : "" + offColor + I18n.format("options.off");
	}

}
