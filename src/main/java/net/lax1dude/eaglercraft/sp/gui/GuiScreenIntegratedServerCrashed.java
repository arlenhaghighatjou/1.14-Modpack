package net.lax1dude.eaglercraft.sp.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;

public class GuiScreenIntegratedServerCrashed extends Screen {

    private Screen mainmenu;
    private String crashReport;

    public GuiScreenIntegratedServerCrashed(Screen mainmenu, String crashReport) {
        super(new StringTextComponent(""));
        this.mainmenu = mainmenu;
        this.crashReport = crashReport;
    }

    protected void init() {
        this.addButton(new Button(this.width / 2 - 100, this.height - 50, 200, 20,
                I18n.format("singleplayer.crashed.continue"), b -> {
            this.mc.displayGuiScreen(mainmenu);
        }));
        int i = (int) this.mc.mainWindow.getGuiScaleFactor();
        CrashScreen.showCrashReportOverlay(crashReport, 90 * i, 60 * i, (width - 180) * i, (height - 130) * i);
    }

    public void removed() {
        CrashScreen.hideCrashReportOverlay();
    }

    public void render(int par1, int par2, float par3) {
        this.renderBackground();

        this.drawCenteredString(font, I18n.format("singleplayer.crashed.title"), this.width / 2, 25, 0xFFAAAA);
        this.drawCenteredString(font, I18n.format("singleplayer.crashed.checkConsole"), this.width / 2, 40, 0xBBBBBB);

        super.render(par1, par2, par3);
    }

}
