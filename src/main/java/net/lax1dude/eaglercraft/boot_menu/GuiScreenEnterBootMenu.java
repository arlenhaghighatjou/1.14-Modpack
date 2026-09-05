/*
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
 *
 * 
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

package net.lax1dude.eaglercraft.boot_menu;

import net.lax1dude.eaglercraft.EagRuntime;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiScreenEnterBootMenu extends Screen {

    private final Screen parent;

    public GuiScreenEnterBootMenu(Screen parent) {
        super(new TranslationTextComponent("enterBootMenu.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {

    }

    @Override
    public void removed() {
    }

    @Override
    public void render(int mx, int my, float partialTicks) {
        this.renderBackground();
        this.drawCenteredString(font, I18n.format("enterBootMenu.title"), this.width / 2, 70, 11184810);
        this.drawCenteredString(font, I18n.format("enterBootMenu.text0"), this.width / 2, 90, 16777215);
        super.render(mx, my, partialTicks);
    }
}
