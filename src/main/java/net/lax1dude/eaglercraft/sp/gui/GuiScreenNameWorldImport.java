/*
 * Copyright (c) 2022-2024 lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.sp.gui;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.internal.FileChooserResult;
import net.lax1dude.eaglercraft.sp.SingleplayerServerController;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;

public class GuiScreenNameWorldImport extends Screen {
    private Screen parentGuiScreen;
    private TextFieldWidget theGuiTextField;
    private Button loadSpawnChunksBtn;
    private Button enhancedGameRulesBtn;
    private int importFormat;
    private FileChooserResult world;
    private String name;
    private boolean timeToImport = false;
    private boolean definetlyTimeToImport = false;
    private boolean isImporting = false;
    private boolean loadSpawnChunks = false;
    private boolean enhancedGameRules = true;

    public GuiScreenNameWorldImport(Screen menu, FileChooserResult world, int format) {
        super(new StringTextComponent(""));
        this.parentGuiScreen = menu;
        this.importFormat = format;
        this.world = world;
        this.name = world.fileName;
        if(name.length() > 4 && (name.endsWith(".epk") || name.endsWith(".zip"))) {
            name = name.substring(0, name.length() - 4);
        }
    }

    public void tick() {
        if(!timeToImport) {
            this.theGuiTextField.tick();
        }
        if(definetlyTimeToImport && !isImporting) {
            if (!SingleplayerServerController.isIntegratedServerWorkerAlive()) {
                SingleplayerServerController.startIntegratedServerWorker(false);
                return;
            }
            if (!SingleplayerServerController.isReady()) {
                return;
            }
            isImporting = true;
            String worldName = CreateWorldScreen.getUncollidingSaveDirName(mc.getSaveLoader(), this.theGuiTextField.getText().trim());
            String saveName = worldName.replaceAll("[\\./\"]", "_");
            SingleplayerServerController.importWorld(saveName, world.fileData, importFormat, (byte) ((loadSpawnChunks ? 2 : 0) | (enhancedGameRules ? 1 : 0)));
            mc.displayGuiScreen(new GuiScreenIntegratedServerBusy(parentGuiScreen, "singleplayer.busy.importing." + (importFormat + 1),
                    "singleplayer.failed.importing." + (importFormat + 1), SingleplayerServerController::isReady));
        }
    }

    protected void init() {
        if(!timeToImport) {
            this.mc.keyboardListener.enableRepeatEvents(true);
            this.children.clear();
            this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20,
                    I18n.format("singleplayer.import.continue"), (btn) -> {
                this.children.clear();
                timeToImport = true;
            }));
            this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20,
                    I18n.format("gui.cancel"), (btn) -> {
                EagRuntime.clearFileChooserResult();
                this.mc.displayGuiScreen(this.parentGuiScreen);
            }));
            this.theGuiTextField = new TextFieldWidget(this.font, this.width / 2 - 100, this.height / 4 + 3, 200, 20, I18n.format("singleplayer.import.enterName"));
            this.theGuiTextField.setFocused2(true);
            this.theGuiTextField.setText(name);
            this.children.add(this.theGuiTextField);
            this.addButton(loadSpawnChunksBtn = new Button(this.width / 2 - 100, this.height / 4 + 24 + 12, 200, 20,
                    I18n.format("singleplayer.import.loadSpawnChunks", loadSpawnChunks ? I18n.format("gui.yes") : I18n.format("gui.no")), (btn) -> {
                loadSpawnChunks = !loadSpawnChunks;
                loadSpawnChunksBtn.setMessage(I18n.format("singleplayer.import.loadSpawnChunks", loadSpawnChunks ? I18n.format("gui.yes") : I18n.format("gui.no")));
            }));
            this.addButton(enhancedGameRulesBtn = new Button(this.width / 2 - 100, this.height / 4 + 48 + 12, 200, 20,
                    I18n.format("singleplayer.import.enhancedGameRules", enhancedGameRules ? I18n.format("gui.yes") : I18n.format("gui.no")), (btn) -> {
                enhancedGameRules = !enhancedGameRules;
                enhancedGameRulesBtn.setMessage(I18n.format("singleplayer.import.enhancedGameRules", enhancedGameRules ? I18n.format("gui.yes") : I18n.format("gui.no")));
            }));
        }
    }

    public void removed() {
        this.mc.keyboardListener.enableRepeatEvents(false);
    }

    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (p_keyPressed_1_ == 256) {
            EagRuntime.clearFileChooserResult();
            this.mc.displayGuiScreen(this.parentGuiScreen);
            return true;
        }
        if (!timeToImport) {
            if (p_keyPressed_1_ == 257 || p_keyPressed_1_ == 335) {
                this.children.clear();
                timeToImport = true;
                return true;
            }
            return this.theGuiTextField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        }
        return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }

    public boolean charTyped(char par1, int par2) {
        if (!timeToImport) {
            return this.theGuiTextField.charTyped(par1, par2);
        }
        return super.charTyped(par1, par2);
    }

    public boolean mouseClicked(double par1, double par2, int par3) {
        if(!timeToImport) {
            this.theGuiTextField.mouseClicked(par1, par2, par3);
        }
        return super.mouseClicked(par1, par2, par3);
    }

    public void render(int par1, int par2, float par3) {
        this.renderBackground();
        if(!timeToImport) {
            this.drawCenteredString(this.font, I18n.format("singleplayer.import.title"), this.width / 2, this.height / 4 - 60 + 20, 16777215);
            this.drawString(this.font, I18n.format("singleplayer.import.enterName"), this.width / 2 - 100, this.height / 4 - 60 + 50, 10526880);
            this.drawCenteredString(this.font, I18n.format("createWorld.seedNote"), this.width / 2, this.height / 4 + 90, -6250336);
            this.theGuiTextField.render(par1, par2, par3);
        }else {
            definetlyTimeToImport = true;
            long dots = (EagRuntime.steadyTimeMillis() / 500l) % 4l;
            String str = I18n.format("singleplayer.import.reading", world.fileName);
            this.drawString(font, str + (dots > 0 ? "." : "") + (dots > 1 ? "." : "") + (dots > 2 ? "." : ""), (this.width - this.font.getStringWidth(str)) / 2, this.height / 3 + 10, 0xFFFFFF);
        }
        super.render(par1, par2, par3);
    }
}
