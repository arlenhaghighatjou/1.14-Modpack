package net.lax1dude.eaglercraft.sp.gui;

import net.lax1dude.eaglercraft.sp.SingleplayerServerController;
import net.lax1dude.eaglercraft.sp.internal.ClientPlatformSingleplayer;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;

public class GuiScreenIntegratedServerFailed extends Screen {

    private String str1;
    private String str2;
    private Screen cont;

    public GuiScreenIntegratedServerFailed(String str1, String str2, Screen cont) {
        super(new StringTextComponent(""));
        this.str1 = I18n.format(str1);
        this.str2 = I18n.format(str2);
        this.cont = cont;
    }

    public GuiScreenIntegratedServerFailed(String str2, Screen cont) {
        super(new StringTextComponent(""));
        this.str1 = I18n.format("singleplayer.failed.title");
        this.str2 = I18n.format(str2);
        this.cont = cont;
    }

    protected void init() {
        this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 96, 200, 20,
                I18n.format("singleplayer.crashed.continue"), b -> {
            this.mc.displayGuiScreen(cont);
        }));
        if (!ClientPlatformSingleplayer.isRunningSingleThreadMode() && ClientPlatformSingleplayer.isSingleThreadModeSupported()) {
            this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 126, 200, 20,
                    I18n.format("singleplayer.crashed.singleThreadCont"), b -> {
                if (SingleplayerServerController.canKillWorker()) {
                    SingleplayerServerController.killWorker();
                }
                this.mc.displayGuiScreen(new GuiScreenIntegratedServerStartup(new MainMenuScreen(), true));
            }));
        }
    }

    public void render(int par1, int par2, float par3) {
        this.renderBackground();
        this.drawCenteredString(font, str1, this.width / 2, 70, 11184810);
        this.drawCenteredString(font, str2, this.width / 2, 90, 16777215);
        super.render(par1, par2, par3);
    }

}
