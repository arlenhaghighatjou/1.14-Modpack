package net.lax1dude.eaglercraft.sp.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;

public class ScreenLANNotSupported extends Screen {

    private Screen cont;

    public ScreenLANNotSupported(Screen cont) {
        super(new StringTextComponent("LAN Not Supported"));
        this.cont = cont;
    }

    @Override
    protected void init() {
        this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 96, 200, 20, I18n.format("singleplayer.crashed.continue"), (btn) -> {
            net.minecraft.client.Minecraft.getInstance().displayGuiScreen(cont);
        }));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        this.drawCenteredString(this.font, I18n.format("singleplayer.notSupported.title"), this.width / 2, 70, 11184810);
        this.drawCenteredString(this.font, I18n.format("singleplayer.notSupported.desc"), this.width / 2, 90, 16777215);
        super.render(mouseX, mouseY, partialTicks);
    }
}
