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
import net.lax1dude.eaglercraft.minecraft.EaglerFolderResourcePack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

import java.io.IOException;

public class GuiScreenExportProfile extends Screen {

    private final Screen back;

    private Button exportProfile;
    private boolean doExportProfile = true;
    private Button exportSettings;
    private boolean doExportSettings = true;
    private Button exportServers;
    private boolean doExportServers = true;
    private Button exportResourcePacks;
    private boolean doExportResourcePacks = false;

    public GuiScreenExportProfile(Screen back) {
        super(new TranslationTextComponent("settingsBackup.export.title"));
        this.back = back;
    }

    @Override
    protected void init() {
        this.addButton(exportProfile = new Button(this.width / 2 - 100, this.height / 4, 200, 20,
                I18n.format("settingsBackup.export.option.profile") + " " + I18n.format(doExportProfile ? "gui.yes" : "gui.no"), (btn) -> {
            doExportProfile = !doExportProfile;
            exportProfile.setMessage(I18n.format("settingsBackup.export.option.profile") + " " + I18n.format(doExportProfile ? "gui.yes" : "gui.no"));
        }));
        this.addButton(exportSettings = new Button(this.width / 2 - 100, this.height / 4 + 25, 200, 20,
                I18n.format("settingsBackup.export.option.settings") + " " + I18n.format(doExportSettings ? "gui.yes" : "gui.no"), (btn) -> {
            doExportSettings = !doExportSettings;
            exportSettings.setMessage(I18n.format("settingsBackup.export.option.settings") + " " + I18n.format(doExportSettings ? "gui.yes" : "gui.no"));
        }));
        this.addButton(exportServers = new Button(this.width / 2 - 100, this.height / 4 + 50, 200, 20,
                I18n.format("settingsBackup.export.option.servers") + " " + I18n.format(doExportServers ? "gui.yes" : "gui.no"), (btn) -> {
            doExportServers = !doExportServers;
            exportServers.setMessage(I18n.format("settingsBackup.export.option.servers") + " " + I18n.format(doExportServers ? "gui.yes" : "gui.no"));
        }));
        this.addButton(exportResourcePacks = new Button(this.width / 2 - 100, this.height / 4 + 75, 200, 20,
                I18n.format("settingsBackup.export.option.resourcePacks") + " " + I18n.format(doExportResourcePacks ? "gui.yes" : "gui.no"), (btn) -> {
            doExportResourcePacks = !doExportResourcePacks;
            exportResourcePacks.setMessage(I18n.format("settingsBackup.export.option.resourcePacks") + " " + I18n.format(doExportResourcePacks ? "gui.yes" : "gui.no"));
        }));
        exportResourcePacks.active = EaglerFolderResourcePack.isSupported();
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 115, 200, 20, I18n.format("settingsBackup.export.option.export"), (btn) -> {
            if (!doExportProfile && !doExportSettings && !doExportServers && !doExportResourcePacks) {
                this.mc.displayGuiScreen(back);
            } else {
                try {
                    ProfileExporter.exportProfileAndSettings(doExportProfile, doExportSettings, doExportServers, doExportResourcePacks);
                    this.mc.displayGuiScreen(back);
                } catch (IOException e) {
                    EagRuntime.debugPrintStackTrace(e);
                    this.mc.displayGuiScreen(new GuiScreenGenericErrorMessage("settingsBackup.exporting.failed.1", "settingsBackup.exporting.failed.2", back));
                }
            }
        }));
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 140, 200, 20, I18n.format("gui.cancel"), (btn) -> {
            this.mc.displayGuiScreen(back);
        }));
    }

    @Override
    public void render(int mx, int my, float partialTicks) {
        this.renderBackground();
        this.drawCenteredString(font, I18n.format("settingsBackup.export.title"), this.width / 2, this.height / 4 - 25, 16777215);
        super.render(mx, my, partialTicks);
    }
}
