package net.lax1dude.eaglercraft.sp.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;

public class ScreenNoRelays extends Screen {

    private Screen parent;
    private String title1;
    private String title2;
    private String title3;

    public ScreenNoRelays(Screen parent, String title) {
        super(new StringTextComponent(title));
        this.parent = parent;
        this.title1 = title;
        this.title2 = null;
        this.title3 = null;
    }

    public ScreenNoRelays(Screen parent, String title1, String title2, String title3) {
        super(new StringTextComponent(title1));
        this.parent = parent;
        this.title1 = title1;
        this.title2 = title2;
        this.title3 = title3;
    }
    
    // For dummy compat
    public ScreenNoRelays(Screen parent) {
        super(new StringTextComponent("No Relays"));
        this.parent = parent;
        this.title1 = "noRelay.worldNotFound1";
        this.title2 = "noRelay.worldNotFound2";
        this.title3 = "noRelay.worldNotFound3";
    }

    @Override
    protected void init() {
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 - 60 + 145, 200, 20, I18n.format("gui.cancel"), (btn) -> {
            net.minecraft.client.Minecraft.getInstance().displayGuiScreen(parent);
        }));
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 - 60 + 115, 200, 20, I18n.format("directConnect.lanWorldRelay"), (btn) -> {
            net.minecraft.client.Minecraft.getInstance().displayGuiScreen(ScreenLANInfo.showLANInfoScreen(new ScreenRelay(parent)));
        }));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        this.drawCenteredString(this.font, I18n.format(title1), this.width / 2, this.height / 4 - 60 + 70, 16777215);
        if(title2 != null) {
            this.drawCenteredString(this.font, I18n.format(title2), this.width / 2, this.height / 4 - 60 + 80, 0xCCCCCC);
        }
        if(title3 != null) {
            this.drawCenteredString(this.font, I18n.format(title3), this.width / 2, this.height / 4 - 60 + 90, 0xCCCCCC);
        }
        super.render(mouseX, mouseY, partialTicks);
    }
}
