package net.lax1dude.eaglercraft.recording;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiScreenRecordingNote extends Screen {

    public static boolean hasShown = false;
    private Screen cont;

    public GuiScreenRecordingNote(Screen cont) {
        super(new TranslationTextComponent("options.recordingNote.title"));
        this.cont = cont;
    }

    protected void init() {
        this.buttons.clear();
        this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 108, 200, 20, I18n.format("gui.done"), (p_213056_1_) -> {
            hasShown = true;
            mc.displayGuiScreen(cont);
        }));
    }

    public void render(int par1, int par2, float par3) {
        this.renderBackground();
        this.drawCenteredString(this.font, I18n.format("options.recordingNote.title"), this.width / 2, 70, 11184810);
        this.drawCenteredString(this.font, I18n.format("options.recordingNote.text0"), this.width / 2, 90, 16777215);
        this.drawCenteredString(this.font, I18n.format("options.recordingNote.text1"), this.width / 2, 102, 16777215);
        super.render(par1, par2, par3);
    }

}
