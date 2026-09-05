package net.lax1dude.eaglercraft.recording;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiScreenSelectCodec extends Screen {

    protected final GuiScreenRecordingSettings parent;
    protected EnumScreenRecordingCodec currentCodec;

    public GuiScreenSelectCodec(GuiScreenRecordingSettings parent, EnumScreenRecordingCodec currentCodec) {
        super(new TranslationTextComponent("options.screenRecording.selectCodec"));
        this.parent = parent;
        this.currentCodec = currentCodec;
    }

    protected void init() {
        this.buttons.clear();
        int idx = 0;
        for (int i = 0, l = ScreenRecordingController.advancedCodecsOrdered.size(); i < l; ++i) {
            EnumScreenRecordingCodec codec = ScreenRecordingController.advancedCodecsOrdered.get(i);
            int y = this.height / 6 + idx * 18 - 6;
            if (y + 18 > this.height - 44) break;
            this.addButton(new Button(this.width / 2 - 155, y, 310, 16, codec.name, (p_213056_1_) -> {
                parent.handleCodecCallback(codec);
                mc.displayGuiScreen(parent);
            }) {
                protected int getYImage(boolean p_getYImage_1_) {
                    return codec == currentCodec ? 1 : 0;
                }
            });
            ++idx;
        }
        this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, I18n.format("gui.done"), (p_213056_1_) -> {
            mc.displayGuiScreen(parent);
        }));
    }

    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground();
        this.drawCenteredString(this.font, I18n.format("options.screenRecording.selectCodec"), this.width / 2, 15, 16777215);
        super.render(p_render_1_, p_render_2_, p_render_3_);
    }

}
