package net.lax1dude.eaglercraft.profile;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class GuiPasswordTextField extends TextFieldWidget {

    private static final char[] STARS = new char[]{'*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*',
            '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*',
            '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*',
            '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*',
            '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*',
            '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*',
            '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*',
            '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*',
            '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*',
            '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*',
            '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*'};

    public GuiPasswordTextField(FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height) {
        super(fontrendererObj, x, y, par5Width, par6Height, "");
    }

    public static String stars(int len) {
        return new String(STARS, 0, len > STARS.length ? STARS.length : len);
    }

    public void drawTextBox() {
        String oldText = getText();
        setText(stars(oldText.length()));
        super.renderButton(0, 0, 0);
        setText(oldText);
    }

    public boolean mouseClicked(double parInt1, double parInt2, int parInt3) {
        String oldText = getText();
        setText(stars(oldText.length()));
        boolean result = super.mouseClicked(parInt1, parInt2, parInt3);
        setText(oldText);
        return result;
    }

}
