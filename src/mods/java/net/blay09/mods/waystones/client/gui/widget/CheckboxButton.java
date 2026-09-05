package net.blay09.mods.waystones.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;

public class CheckboxButton extends Button {

    private static final int BOX_SIZE = 11;

    private boolean checked;

    public CheckboxButton(int x, int y, String text, boolean checked) {
        super(x, y, BOX_SIZE + 2 + Minecraft.getInstance().fontRenderer.getStringWidth(text), BOX_SIZE, text, button -> {
        });
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }

    @Override
    public void onPress() {
        this.checked = !this.checked;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        GlStateManager.color4f(1f, 1f, 1f, 1f);
        fill(x, y, x + BOX_SIZE, y + BOX_SIZE, 0xFF000000);
        fill(x + 1, y + 1, x + BOX_SIZE - 1, y + BOX_SIZE - 1, 0xFF8B8B8B);

        if (checked) {
            drawCenteredString(mc.fontRenderer, "x", x + BOX_SIZE / 2 + 1, y + 1, 0xFFFFFF);
        }

        mc.fontRenderer.drawStringWithShadow(getMessage(), x + BOX_SIZE + 2, y + (BOX_SIZE - 8) / 2, isHovered ? 0xFFFFA0 : 0xE0E0E0);
    }
}
