package net.lax1dude.eaglercraft.sp.gui;

import net.lax1dude.eaglercraft.sp.relay.RelayManager;
import net.lax1dude.eaglercraft.sp.relay.RelayServer;
import net.lax1dude.eaglercraft.sp.relay.RelayEntry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;

public class ScreenAddRelay extends Screen {

    private final ScreenRelay parentGui;
    private TextFieldWidget serverAddress;
    private TextFieldWidget serverName;
    
    private Button btnAdd;
    private Button btnPrimary;
    
    private boolean isPrimary;

    public ScreenAddRelay(ScreenRelay parent) {
        super(new StringTextComponent("Add Relay"));
        this.parentGui = parent;
    }

    @Override
    public void tick() {
        this.serverName.tick();
        this.serverAddress.tick();
    }

    @Override
    protected void init() {
        net.minecraft.client.Minecraft.getInstance().keyboardListener.enableRepeatEvents(true);
        this.isPrimary = RelayManager.relayManager.count() == 0;
        
        this.serverName = new TextFieldWidget(this.font, this.width / 2 - 100, 106, 200, 20, "Name");
        this.serverName.setText(RelayManager.relayManager.makeNewRelayName());
        this.children.add(this.serverName);
        
        this.serverAddress = new TextFieldWidget(this.font, this.width / 2 - 100, 66, 200, 20, "Address");
        this.serverAddress.setMaxStringLength(128);
        this.children.add(this.serverAddress);
        
        this.btnAdd = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20, I18n.format("addRelay.add"), (btn) -> {
            RelayManager.relayManager.addNew(this.serverAddress.getText(), this.serverName.getText(), this.isPrimary);
            RelayManager.relayManager.save();
            net.minecraft.client.Minecraft.getInstance().displayGuiScreen(this.parentGui);
        }));
        
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, I18n.format("gui.cancel"), (btn) -> {
            net.minecraft.client.Minecraft.getInstance().displayGuiScreen(this.parentGui);
        }));
        
        this.btnPrimary = this.addButton(new Button(this.width / 2 - 100, 142, 200, 20, I18n.format("addRelay.primary") + ": " + (this.isPrimary ? I18n.format("gui.yes") : I18n.format("gui.no")), (btn) -> {
            this.isPrimary = !this.isPrimary;
            btn.setMessage(I18n.format("addRelay.primary") + ": " + (this.isPrimary ? I18n.format("gui.yes") : I18n.format("gui.no")));
        }));
        
        //this.setFocusedDefault(this.serverAddress);
        this.updateButtons();
    }

    @Override
    public void removed() {
        net.minecraft.client.Minecraft.getInstance().keyboardListener.enableRepeatEvents(false);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 258) { // Tab
            if (this.serverName.isFocused()) {
                this.serverName.changeFocus(false);
                this.serverAddress.changeFocus(true);
            } else {
                this.serverName.changeFocus(true);
                this.serverAddress.changeFocus(false);
            }
            return true;
        } else if (keyCode == 257 || keyCode == 335) { // Enter
            if (this.btnAdd.active) {
                this.btnAdd.onPress();
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
        this.btnAdd.active = this.serverAddress.getText().length() > 0 && this.serverName.getText().length() > 0;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        this.drawCenteredString(this.font, I18n.format("addRelay.title"), this.width / 2, 17, 16777215);
        this.drawString(this.font, I18n.format("addRelay.address"), this.width / 2 - 100, 53, 10526880);
        this.drawString(this.font, I18n.format("addRelay.name"), this.width / 2 - 100, 94, 10526880);
        this.serverName.render(mouseX, mouseY, partialTicks);
        this.serverAddress.render(mouseX, mouseY, partialTicks);
        super.render(mouseX, mouseY, partialTicks);
    }
}
