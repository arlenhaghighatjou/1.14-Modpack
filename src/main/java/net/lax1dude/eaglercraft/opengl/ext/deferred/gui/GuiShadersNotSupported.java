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

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiShadersNotSupported extends Screen {

	private Screen parent;
	private String reason;

	public GuiShadersNotSupported(Screen parent, String reason) {
		super(new TranslationTextComponent("shaders.gui.unsupported.title"));
		this.parent = parent;
		this.reason = reason;
	}

	protected void init() {
		this.addButton(new Button(this.width / 2 - 100, this.height / 2 + 10, 200, 20, I18n.format("gui.back"), (btn) -> {
			this.mc.displayGuiScreen(parent);
		}));
	}

	public void render(int i, int j, float var3) {
		this.renderBackground();
		drawCenteredString(this.font, I18n.format("shaders.gui.unsupported.title"), this.width / 2, this.height / 2 - 30, 0xFFFFFF);
		drawCenteredString(this.font, reason, this.width / 2, this.height / 2 - 10, 11184810);
		super.render(i, j, var3);
	}

}
