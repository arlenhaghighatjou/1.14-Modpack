package net.lax1dude.eaglercraft.recording;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiSlotSelectCodec extends Screen {

    protected final GuiScreenSelectCodec screen;

    public GuiSlotSelectCodec(GuiScreenSelectCodec screen) {
        super(new TranslationTextComponent("options.screenRecording.selectCodec"));
        this.screen = screen;
    }

    protected void init() {
        this.buttons.clear();
        this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, I18n.format("gui.done"), (p_213056_1_) -> {
            mc.displayGuiScreen(screen);
        }));
    }

    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground();
        this.drawCenteredString(this.font, I18n.format("options.screenRecording.selectCodec"), this.width / 2, 15, 16777215);
        super.render(p_render_1_, p_render_2_, p_render_3_);
    }

}
