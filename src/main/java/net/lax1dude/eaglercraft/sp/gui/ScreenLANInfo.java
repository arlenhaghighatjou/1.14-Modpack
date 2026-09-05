package net.lax1dude.eaglercraft.sp.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;

public class ScreenLANInfo extends Screen {
    private Screen parent;

    public ScreenLANInfo(Screen parent) {
        super(new StringTextComponent("LAN Info"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.addButton(new Button(this.width / 2 - 100, height / 6 + 168, 200, 20, I18n.format("Continue"), (btn) -> {
            net.minecraft.client.Minecraft.getInstance().displayGuiScreen(parent);
        }));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        this.drawCenteredString(this.font, I18n.format("lanInfo.title"), this.width / 2, this.height / 4 - 60 + 20, 16777215);
        this.font.drawSplitString(I18n.format("lanInfo.desc.0") + "\n\n\n" + I18n.format("lanInfo.desc.1", I18n.format("menu.multiplayer"), "Invite"), this.width / 2 - 100, this.height / 4 - 60 + 60, 200, -6250336);
        super.render(mouseX, mouseY, partialTicks);
    }

    private static boolean hasShown = false;

    public static Screen showLANInfoScreen(Screen cont) {
        if(!hasShown) {
            hasShown = true;
            return new ScreenLANInfo(cont);
        } else {
            return cont;
        }
    }
}
