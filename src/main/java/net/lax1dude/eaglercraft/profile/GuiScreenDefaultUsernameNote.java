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
import net.minecraft.util.text.StringTextComponent;

public class GuiScreenDefaultUsernameNote extends Screen {

    private final Screen back;
    private final Screen cont;

    public GuiScreenDefaultUsernameNote(Screen back, Screen cont) {
        super(new StringTextComponent(""));
        this.back = back;
        this.cont = cont;
    }

    @Override
    protected void init() {
        this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 112, 200, 20, I18n.format("defaultUsernameDetected.changeUsername"), (btn) -> {
            this.mc.displayGuiScreen(back);
        }));
        this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 142, 200, 20, I18n.format("defaultUsernameDetected.continueAnyway"), (btn) -> {
            this.mc.displayGuiScreen(cont);
        }));
        this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 172, 200, 20, I18n.format("defaultUsernameDetected.doNotShow"), (btn) -> {
            this.mc.gameSettings.hideDefaultUsernameWarning = true;
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(cont);
        }));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        this.drawCenteredString(this.font, I18n.format("defaultUsernameDetected.title"), this.width / 2, 70, 11184810);
        this.drawCenteredString(this.font, I18n.format("defaultUsernameDetected.text0", EaglerProfile.getName()), this.width / 2, 90, 16777215);
        this.drawCenteredString(this.font, I18n.format("defaultUsernameDetected.text1"), this.width / 2, 105, 16777215);
        this.drawCenteredString(this.font, I18n.format("defaultUsernameDetected.text2"), this.width / 2, 120, 16777215);
        super.render(mouseX, mouseY, partialTicks);
    }

}
