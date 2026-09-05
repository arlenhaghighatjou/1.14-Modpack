package net.lax1dude.eaglercraft.sp.gui;

import net.lax1dude.eaglercraft.sp.SingleplayerServerController;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;

public class GuiScreenRAMDiskModeDetected extends Screen {

    private Screen cont;

    public GuiScreenRAMDiskModeDetected(Screen cont) {
        super(new StringTextComponent(""));
        this.cont = cont;
    }

    protected void init() {
        this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 106, 200, 20,
                I18n.format("singleplayer.ramdiskdetected.continue"), b -> {
            this.mc.displayGuiScreen(cont);
        }));
        this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 136, 200, 20,
                I18n.format("singleplayer.ramdiskdetected.singleThreadCont"), b -> {
            SingleplayerServerController.killWorker();
            mc.displayGuiScreen(new GuiScreenIntegratedServerStartup(new MainMenuScreen(), true));
        }));
    }

    public void render(int par1, int par2, float par3) {
        this.renderBackground();
        this.drawCenteredString(font, I18n.format("singleplayer.ramdiskdetected.title"), this.width / 2, 70, 11184810);
        this.drawCenteredString(font, I18n.format("singleplayer.ramdiskdetected.text0"), this.width / 2, 90, 16777215);
        this.drawCenteredString(font, I18n.format("singleplayer.ramdiskdetected.text1"), this.width / 2, 105, 16777215);
        super.render(par1, par2, par3);
    }

}
