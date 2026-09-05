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

package net.lax1dude.eaglercraft.profile;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiScreenGenericErrorMessage extends Screen {

    private String str1;
    private String str2;
    private Screen cont;

    public GuiScreenGenericErrorMessage(String str1, String str2, Screen cont) {
        super(new TranslationTextComponent("error"));
        this.str1 = (str1 == null || str1.isEmpty()) ? "" : I18n.format(str1);
        this.str2 = (str2 == null || str2.isEmpty()) ? "" : I18n.format(str2);
        this.cont = cont;
    }

    @Override
    protected void init() {
        this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 96, 200, 20, I18n.format("gui.done"), (btn) -> {
            this.mc.displayGuiScreen(cont);
        }));
    }

    @Override
    public void render(int mx, int my, float partialTicks) {
        this.renderBackground();
        this.drawCenteredString(font, str1, this.width / 2, 70, 11184810);
        this.drawCenteredString(font, str2, this.width / 2, 90, 16777215);
        super.render(mx, my, partialTicks);
    }
}
