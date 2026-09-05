package net.lax1dude.eaglercraft.sp.gui;

import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;

public class GuiSlider2 extends Button {
    public float sliderValue = 1.0F;
    public float sliderMax = 1.0F;

    public boolean dragging = false;

    public GuiSlider2(int buttonId, int x, int y, int widthIn, int heightIn, float sliderValue, float sliderMax) {
        super(x, y, widthIn, heightIn, null, b -> {
        });
        this.sliderValue = sliderValue;
        this.sliderMax = sliderMax;
        this.setMessage(updateDisplayString());
    }

    protected int getYImage(boolean p_getYImage_1_) {
        return 0;
    }

    protected void renderBg(Minecraft par1Minecraft, int par2, int par3) {
        if (this.visible) {
            if (this.dragging) {
                float oldValue = sliderValue;
                this.sliderValue = (float) (par2 - (this.x + 4)) / (float) (this.width - 8);

                if (this.sliderValue < 0.0F) {
                    this.sliderValue = 0.0F;
                }

                if (this.sliderValue > 1.0F) {
                    this.sliderValue = 1.0F;
                }

                if (oldValue != sliderValue) {
                    onChange();
                }

                this.setMessage(updateDisplayString());
            }

            if (this.active) {
                par1Minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                int i = (this.isHovered() ? 2 : 1) * 20;
                this.blit(this.x + (int) (this.sliderValue * (float) (this.width - 8)), this.y, 0, 46 + i, 4, 20);
                this.blit(this.x + (int) (this.sliderValue * (float) (this.width - 8)) + 4, this.y, 196, 46 + i, 4, 20);
            }
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            float oldValue = sliderValue;
            this.sliderValue = (float) (mouseX - (this.x + 4)) / (float) (this.width - 8);

            if (this.sliderValue < 0.0F) {
                this.sliderValue = 0.0F;
            }

            if (this.sliderValue > 1.0F) {
                this.sliderValue = 1.0F;
            }

            if (oldValue != sliderValue) {
                onChange();
            }

            this.setMessage(updateDisplayString());
            this.dragging = true;
            return true;
        } else {
            return false;
        }
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    protected String updateDisplayString() {
        return (int) (this.sliderValue * this.sliderMax * 100.0F) + "%";
    }

    protected void onChange() {

    }

    public boolean isSliderTouchEvents() {
        return true;
    }

}
