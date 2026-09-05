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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

import java.io.IOException;

public class GuiScreenImportProfile extends Screen {

    private final Screen back;
    private final ProfileImporter importer;

    private Button importProfile;
    private boolean doImportProfile;
    private Button importSettings;
    private boolean doImportSettings;
    private Button importServers;
    private boolean doImportServers;
    private Button importResourcePacks;
    private boolean doImportResourcePacks;

    public GuiScreenImportProfile(ProfileImporter importer, Screen back) {
        super(new TranslationTextComponent("settingsBackup.import.title"));
        this.back = back;
        this.importer = importer;
        this.doImportProfile = importer.hasProfile();
        this.doImportSettings = importer.hasSettings();
        this.doImportServers = importer.hasServers();
        this.doImportResourcePacks = importer.hasResourcePacks();
    }

    @Override
    protected void init() {
        this.addButton(importProfile = new Button(this.width / 2 - 100, this.height / 4, 200, 20,
                I18n.format("settingsBackup.import.option.profile") + " " + I18n.format(doImportProfile ? "gui.yes" : "gui.no"), (btn) -> {
            doImportProfile = !doImportProfile;
            importProfile.setMessage(I18n.format("settingsBackup.import.option.profile") + " " + I18n.format(doImportProfile ? "gui.yes" : "gui.no"));
        }));
        importProfile.active = importer.hasProfile();
        this.addButton(importSettings = new Button(this.width / 2 - 100, this.height / 4 + 25, 200, 20,
                I18n.format("settingsBackup.import.option.settings") + " " + I18n.format(doImportSettings ? "gui.yes" : "gui.no"), (btn) -> {
            doImportSettings = !doImportSettings;
            importSettings.setMessage(I18n.format("settingsBackup.import.option.settings") + " " + I18n.format(doImportSettings ? "gui.yes" : "gui.no"));
        }));
        importSettings.active = importer.hasSettings();
        this.addButton(importServers = new Button(this.width / 2 - 100, this.height / 4 + 50, 200, 20,
                I18n.format("settingsBackup.import.option.servers") + " " + I18n.format(doImportServers ? "gui.yes" : "gui.no"), (btn) -> {
            doImportServers = !doImportServers;
            importServers.setMessage(I18n.format("settingsBackup.import.option.servers") + " " + I18n.format(doImportServers ? "gui.yes" : "gui.no"));
        }));
        importServers.active = importer.hasServers();
        this.addButton(importResourcePacks = new Button(this.width / 2 - 100, this.height / 4 + 75, 200, 20,
                I18n.format("settingsBackup.import.option.resourcePacks") + " " + I18n.format(doImportResourcePacks ? "gui.yes" : "gui.no"), (btn) -> {
            doImportResourcePacks = !doImportResourcePacks;
            importResourcePacks.setMessage(I18n.format("settingsBackup.import.option.resourcePacks") + " " + I18n.format(doImportResourcePacks ? "gui.yes" : "gui.no"));
        }));
        importResourcePacks.active = importer.hasResourcePacks();
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 115, 200, 20, I18n.format("settingsBackup.import.option.import"), (btn) -> {
            if (!doImportProfile && !doImportSettings && !doImportServers && !doImportResourcePacks) {
                this.mc.displayGuiScreen(back);
            } else {
                try {
                    importer.importProfileAndSettings(doImportProfile, doImportSettings, doImportServers, doImportResourcePacks);
                    if (doImportResourcePacks) {
                        this.mc.getResourcePackList().reloadPacksFromFinders();
                        this.mc.reloadResources();
                    }
                    this.mc.displayGuiScreen(back);
                } catch (IOException e) {
                    EagRuntime.debugPrintStackTrace(e);
                    this.mc.displayGuiScreen(new GuiScreenGenericErrorMessage("settingsBackup.importing.failed.1", "settingsBackup.importing.failed.2", back));
                }
            }
        }));
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 140, 200, 20, I18n.format("gui.cancel"), (btn) -> {
            this.mc.displayGuiScreen(back);
        }));
    }

    @Override
    public void removed() {
        try {
            importer.close();
        } catch (IOException e) {
        }
    }

    @Override
    public void render(int mx, int my, float partialTicks) {
        this.renderBackground();
        this.drawCenteredString(font, I18n.format("settingsBackup.import.title"), this.width / 2, this.height / 4 - 25, 16777215);
        super.render(mx, my, partialTicks);
    }
}
