package net.lax1dude.eaglercraft.profile;

import net.lax1dude.eaglercraft.Keyboard;
import net.lax1dude.eaglercraft.KeyboardConstants;
import net.lax1dude.eaglercraft.socket.ConnectionHandshake;
import net.lax1dude.eaglercraft.socket.HandshakePacketTypes;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;

public class GuiAuthenticationScreen extends Screen {

    private final Screen retAfterAuthScreen;
    private final Screen parent;
    private final String message;
    private Button continueButton;
    private GuiPasswordTextField password;
    private int authTypeForWarning = Integer.MAX_VALUE;
    private boolean allowPlaintext = false;

    public GuiAuthenticationScreen(Screen retAfterAuthScreen, Screen parent, String message) {
        super(new StringTextComponent(""));
        this.retAfterAuthScreen = retAfterAuthScreen;
        this.parent = parent;
        String authRequired = HandshakePacketTypes.AUTHENTICATION_REQUIRED;
        if (message.startsWith(authRequired)) {
            message = message.substring(authRequired.length()).trim();
        }
        if (message.length() > 0 && message.charAt(0) == '[') {
            int idx = message.indexOf(']', 1);
            if (idx != -1) {
                String authType = message.substring(1, idx);
                int type = Integer.MAX_VALUE;
                try {
                    type = Integer.parseInt(authType);
                } catch (NumberFormatException ex) {
                }
                if (type != Integer.MAX_VALUE) {
                    authTypeForWarning = type;
                    message = message.substring(idx + 1).trim();
                }
            }
        }
        this.message = message;
    }

    protected void init() {
        if (authTypeForWarning != Integer.MAX_VALUE) {
            Screen scr = ConnectionHandshake.displayAuthProtocolConfirm(authTypeForWarning, parent, this);
            authTypeForWarning = Integer.MAX_VALUE;
            if (scr != null) {
                mc.displayGuiScreen(scr);
                allowPlaintext = true;
                return;
            }
        }
        Keyboard.enableRepeatEvents(true);
        this.addButton(continueButton = new Button(this.width / 2 - 100, this.height / 4 + 80 + 12, 200, 20,
                I18n.format("auth.continue"), b -> {
            String pass = password.getText();
            mc.displayGuiScreen(retAfterAuthScreen);
        }));
        continueButton.active = false;
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 80 + 37, 200, 20,
                I18n.format("gui.cancel"), b -> {
            mc.displayGuiScreen(parent);
        }));
        this.password = new GuiPasswordTextField(this.font, this.width / 2 - 100, this.height / 4 + 40, 200, 20);
        this.password.setFocused2(true);
        this.password.setCanLoseFocus(false);
    }

    public void removed() {
        Keyboard.enableRepeatEvents(false);
    }

    public void render(int i, int j, float var3) {
        renderDirtBackground(0);
        this.password.drawTextBox();
        this.drawCenteredString(this.font, I18n.format("auth.required"), this.width / 2,
                this.height / 4 - 5, 16777215);
        this.drawCenteredString(this.font, message, this.width / 2, this.height / 4 + 15, 0xAAAAAA);
        super.render(i, j, var3);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        String pass = password.getText();
        if (keyCode == KeyboardConstants.KEY_RETURN && pass.length() > 0) {
            mc.displayGuiScreen(retAfterAuthScreen);
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        return true;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        this.password.mouseClicked(mouseX, mouseY, button);
        this.continueButton.active = password.getText().length() > 0;
        return true;
    }
}
