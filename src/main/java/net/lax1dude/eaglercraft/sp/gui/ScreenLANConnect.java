package net.lax1dude.eaglercraft.sp.gui;

import net.minecraft.client.gui.screen.LANConnectingScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ScreenLANConnect extends Screen {

    private final Screen parent;
    private TextFieldWidget codeTextField;
    private Button btnJoin;
    
    private static String lastCode = "";

    public ScreenLANConnect(Screen parent) {
        super(new StringTextComponent("Join Shared World"));
        this.parent = parent;
    }

    @Override
    public void tick() {
        this.codeTextField.tick();
    }

    @Override
    protected void init() {
        net.minecraft.client.Minecraft.getInstance().keyboardListener.enableRepeatEvents(true);
        
        this.codeTextField = new TextFieldWidget(this.font, this.width / 2 - 100, this.height / 4 + 27, 200, 20, "Code");
        this.codeTextField.setMaxStringLength(48);
        this.codeTextField.setText(lastCode);
        this.children.add(this.codeTextField);
        
        this.btnJoin = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20, I18n.format("directConnect.lanWorldJoin"), (btn) -> {
            net.minecraft.client.Minecraft.getInstance().displayGuiScreen(new LANConnectingScreen(this.parent, this.codeTextField.getText().trim()));
        }));
        
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, I18n.format("gui.cancel"), (btn) -> {
            net.minecraft.client.Minecraft.getInstance().displayGuiScreen(this.parent);
        }));
        
        this.addButton(new Button(this.width - 120, 10, 110, 20, I18n.format("directConnect.lanWorldRelay"), (btn) -> {
            net.minecraft.client.Minecraft.getInstance().displayGuiScreen(new ScreenRelay(this));
        }));

        //this.setFocusedDefault(this.codeTextField);
        this.updateButtons();
    }

    @Override
    public void removed() {
        net.minecraft.client.Minecraft.getInstance().keyboardListener.enableRepeatEvents(false);
        lastCode = this.codeTextField.getText().trim();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 || keyCode == 335) { // Enter
            if (this.btnJoin.active) {
                this.btnJoin.onPress();
            }
            return true;
        }
        boolean res = super.keyPressed(keyCode, scanCode, modifiers);
        this.updateButtons();
        return res;
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        boolean res = super.charTyped(codePoint, modifiers);
        this.updateButtons();
        return res;
    }

    private void updateButtons() {
        this.btnJoin.active = this.codeTextField.getText().trim().length() > 0;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        this.drawCenteredString(this.font, I18n.format("selectServer.direct"), this.width / 2, this.height / 4 - 60 + 20, 16777215);
        this.drawString(this.font, I18n.format("directConnect.lanWorldCode"), this.width / 2 - 100, this.height / 4 + 12, 10526880);
        this.drawCenteredString(this.font, I18n.format("directConnect.networkSettingsNote"), this.width / 2, this.height / 4 + 63, 10526880);
        this.drawCenteredString(this.font, I18n.format("directConnect.ipGrabNote"), this.width / 2, this.height / 4 + 77, 10526880);
        this.codeTextField.render(mouseX, mouseY, partialTicks);
        super.render(mouseX, mouseY, partialTicks);
    }
}
