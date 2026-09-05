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

package net.lax1dude.eaglercraft.profanity_filter;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiScreenContentWarning extends Screen {

    private final Screen cont;
    private boolean enableState;
    private Button optButton;

    public GuiScreenContentWarning(Screen cont) {
        super(new TranslationTextComponent("profanityFilterWarning.title"));
        this.cont = cont;
    }

    @Override
    protected void init() {
        enableState = mc.gameSettings.enableProfanityFilter;
        this.addButton(optButton = new Button(this.width / 2 - 100, this.height / 6 + 108, 200, 20,
                I18n.format("options.profanityFilterButton") + ": " + I18n.format(enableState ? "gui.yes" : "gui.no"), (btn) -> {
            enableState = !enableState;
            optButton.setMessage(I18n.format("options.profanityFilterButton") + ": " + I18n.format(enableState ? "gui.yes" : "gui.no"));
        }));
        this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 138, 200, 20, I18n.format("gui.done"), (btn) -> {
            mc.gameSettings.enableProfanityFilter = enableState;
            mc.gameSettings.hasShownProfanityFilter = true;
            mc.gameSettings.saveOptions();
            mc.displayGuiScreen(cont);
        }));
    }

    @Override
    public void render(int mx, int my, float partialTicks) {
        this.renderBackground();
        this.drawCenteredString(font, TextFormatting.BOLD + I18n.format("profanityFilterWarning.title"), this.width / 2, 50, 0xFF4444);
        this.drawCenteredString(font, I18n.format("profanityFilterWarning.text0"), this.width / 2, 70, 16777215);
        this.drawCenteredString(font, I18n.format("profanityFilterWarning.text1"), this.width / 2, 82, 16777215);
        this.drawCenteredString(font, I18n.format("profanityFilterWarning.text2"), this.width / 2, 94, 16777215);
        this.drawCenteredString(font, I18n.format("profanityFilterWarning.text4"), this.width / 2, 116, 0xCCCCCC);
        super.render(mx, my, partialTicks);
    }
}
