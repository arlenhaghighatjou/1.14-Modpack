package net.lax1dude.eaglercraft.sp.gui;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.internal.FileChooserResult;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;

public class GuiScreenCreateWorldSelection extends Screen {

    private Screen mainmenu;
    private Button worldCreate = null;
    private Button worldImport = null;
    private Button worldVanilla = null;
    private boolean isImportingEPK = false;
    private boolean isImportingMCA = false;

    public GuiScreenCreateWorldSelection(Screen mainmenu) {
        super(new StringTextComponent(""));
        this.mainmenu = mainmenu;
    }

    protected void init() {
        this.worldCreate = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 40, 200, 20,
                I18n.format("singleplayer.create.create"), b -> {
            this.mc.displayGuiScreen(new CreateWorldScreen(mainmenu));
        }));
        this.worldImport = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 65, 200, 20,
                I18n.format("singleplayer.create.import"), b -> {
            isImportingEPK = true;
            EagRuntime.displayFileChooser(null, "epk");
        }));
        this.worldVanilla = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 90, 200, 20,
                I18n.format("singleplayer.create.vanilla"), b -> {
            isImportingMCA = true;
            EagRuntime.displayFileChooser(null, "zip");
        }));
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 130, 200, 20,
                I18n.format("gui.cancel"), b -> {
            this.mc.displayGuiScreen(mainmenu);
        }));
    }

    public void tick() {
        if (EagRuntime.fileChooserHasResult() && (isImportingEPK || isImportingMCA)) {
            FileChooserResult fr = EagRuntime.getFileChooserResult();
            if (fr != null) {
                this.mc.displayGuiScreen(new GuiScreenNameWorldImport(mainmenu, fr, isImportingEPK ? 0 : (isImportingMCA ? 1 : -1)));
            }
            isImportingEPK = isImportingMCA = false;
        }
    }

    public void render(int par1, int par2, float par3) {
        this.renderBackground();

        this.drawCenteredString(this.font, I18n.format("singleplayer.create.title"), this.width / 2, this.height / 4, 16777215);

        int toolTipColor = 0xDDDDAA;
        if (worldCreate.isHovered()) {
            this.drawCenteredString(this.font, I18n.format("singleplayer.create.create.tooltip"), this.width / 2, this.height / 4 + 20, toolTipColor);
        } else if (worldImport.isHovered()) {
            this.drawCenteredString(this.font, I18n.format("singleplayer.create.import.tooltip"), this.width / 2, this.height / 4 + 20, toolTipColor);
        } else if (worldVanilla.isHovered()) {
            this.drawCenteredString(this.font, I18n.format("singleplayer.create.vanilla.tooltip"), this.width / 2, this.height / 4 + 20, toolTipColor);
        }

        super.render(par1, par2, par3);
    }

}
