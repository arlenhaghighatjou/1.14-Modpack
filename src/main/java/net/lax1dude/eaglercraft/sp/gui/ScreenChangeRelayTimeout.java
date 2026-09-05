package net.lax1dude.eaglercraft.sp.gui;

import net.lax1dude.eaglercraft.sp.relay.RelayManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;

public class ScreenChangeRelayTimeout extends Screen {

    private final Screen parent;
    private TextFieldWidget timeoutField;
    private Button btnDone;

    public ScreenChangeRelayTimeout(Screen parent) {
        super(new StringTextComponent("Change Relay Timeout"));
        this.parent = parent;
    }

    @Override
    public void tick() {
        this.timeoutField.tick();
    }

    @Override
    protected void init() {
        net.minecraft.client.Minecraft.getInstance().keyboardListener.enableRepeatEvents(true);
        
        this.timeoutField = new TextFieldWidget(this.font, this.width / 2 - 100, 106, 200, 20, "Timeout");
        this.timeoutField.setMaxStringLength(5);
        this.timeoutField.setText(Integer.toString(4));
        this.children.add(this.timeoutField);
        
        this.btnDone = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20, I18n.format("gui.done"), (btn) -> {
            try {
                int timeout = Integer.parseInt(this.timeoutField.getText());
                if (timeout > 0) {
                    // TODO timeout
                    RelayManager.relayManager.save();
                }
            } catch (NumberFormatException ignored) {}
            net.minecraft.client.Minecraft.getInstance().displayGuiScreen(this.parent);
        }));
        
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, I18n.format("gui.cancel"), (btn) -> {
            net.minecraft.client.Minecraft.getInstance().displayGuiScreen(this.parent);
        }));
        
        //this.setFocusedDefault(this.timeoutField);
        this.updateButtons();
    }

    @Override
    public void removed() {
        net.minecraft.client.Minecraft.getInstance().keyboardListener.enableRepeatEvents(false);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 || keyCode == 335) { // Enter
            if (this.btnDone.active) {
                this.btnDone.onPress();
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
        boolean valid = false;
        try {
            int timeout = Integer.parseInt(this.timeoutField.getText());
            valid = timeout > 0;
        } catch (NumberFormatException ignored) {}
        this.btnDone.active = valid;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        this.drawCenteredString(this.font, I18n.format("networkSettings.timeout"), this.width / 2, 17, 16777215);
        this.drawString(this.font, I18n.format("networkSettings.timeoutMs"), this.width / 2 - 100, 94, 10526880);
        this.timeoutField.render(mouseX, mouseY, partialTicks);
        super.render(mouseX, mouseY, partialTicks);
    }
}
