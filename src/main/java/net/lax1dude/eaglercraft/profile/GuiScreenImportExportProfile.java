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

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.internal.FileChooserResult;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

import java.io.IOException;

public class GuiScreenImportExportProfile extends Screen {

    private Screen back;
    private boolean waitingForFile = false;

    public GuiScreenImportExportProfile(Screen back) {
        super(new TranslationTextComponent("settingsBackup.importExport.title"));
        this.back = back;
    }

    @Override
    protected void init() {
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 40, 200, 20, I18n.format("settingsBackup.importExport.import"), (btn) -> {
            waitingForFile = true;
            EagRuntime.displayFileChooser(null, "epk");
        }));
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 65, 200, 20, I18n.format("settingsBackup.importExport.export"), (btn) -> {
            this.mc.displayGuiScreen(new GuiScreenExportProfile(back));
        }));
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 130, 200, 20, I18n.format("gui.cancel"), (btn) -> {
            this.mc.displayGuiScreen(back);
        }));
    }

    @Override
    public void tick() {
        if (waitingForFile && EagRuntime.fileChooserHasResult()) {
            waitingForFile = false;
            FileChooserResult result = EagRuntime.getFileChooserResult();
            if (result != null) {
                ProfileImporter importer = new ProfileImporter(result.fileData);
                try {
                    importer.readHeader();
                    this.mc.displayGuiScreen(new GuiScreenImportProfile(importer, back));
                } catch (IOException ex) {
                    try {
                        importer.close();
                    } catch (IOException e) {
                    }
                    EagRuntime.debugPrintStackTrace(ex);
                    this.mc.displayGuiScreen(new GuiScreenGenericErrorMessage("settingsBackup.importing.failed.1", "settingsBackup.importing.failed.2", back));
                }
            }
        }
    }

    @Override
    public void render(int mx, int my, float partialTicks) {
        this.renderBackground();
        this.drawCenteredString(font, I18n.format("settingsBackup.importExport.title"), this.width / 2, this.height / 4, 16777215);
        super.render(mx, my, partialTicks);
    }
}
