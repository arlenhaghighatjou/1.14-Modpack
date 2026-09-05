package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class GuiScreenVideoSettingsWarning extends Screen {

    private final Screen cont;
    private final int mask;
    private final List<String> messages = new ArrayList<>();
    private int top = 0;

    public static final int WARNING_VSYNC = 1;
    public static final int WARNING_RENDER_DISTANCE = 2;
    public static final int WARNING_FRAME_LIMIT = 4;

    public GuiScreenVideoSettingsWarning(Screen cont, int mask) {
        super(new StringTextComponent(""));
        this.cont = cont;
        this.mask = mask;
    }

    public void init() {
        messages.clear();
        messages.add(TextFormatting.RED + I18n.format("options.badVideoSettingsDetected.title"));
        messages.add(null);
        messages.add(TextFormatting.GRAY + I18n.format("options.badVideoSettingsDetected.0"));
        messages.add(TextFormatting.GRAY + I18n.format("options.badVideoSettingsDetected.1"));
        if ((mask & WARNING_VSYNC) != 0) {
            messages.add(null);
            messages.add(I18n.format("options.badVideoSettingsDetected.vsync.0"));
        }
        if ((mask & WARNING_RENDER_DISTANCE) != 0) {
            messages.add(null);
            messages.add(I18n.format("options.badVideoSettingsDetected.renderDistance.0", mc.gameSettings.renderDistanceChunks));
            messages.add(I18n.format("options.badVideoSettingsDetected.renderDistance.1"));
            messages.add(I18n.format("options.badVideoSettingsDetected.renderDistance.2"));
        }
        if ((mask & WARNING_FRAME_LIMIT) != 0) {
            messages.add(null);
            messages.add(I18n.format("options.badVideoSettingsDetected.frameLimit.0", mc.gameSettings.framerateLimit));
        }
        int j = 0;
        for (int i = 0, l = messages.size(); i < l; ++i) {
            if (messages.get(i) != null) {
                j += 9;
            } else {
                j += 5;
            }
        }
        top = this.height / 6 + j / -12;
        j += top;
        this.addButton(new Button(this.width / 2 - 100, j + 16, 200, 20, I18n.format("options.badVideoSettingsDetected.fixSettings"), (p_213078_1_) -> {
            mc.gameSettings.fixBadVideoSettings();
            mc.gameSettings.saveOptions();
            if ((mask & WARNING_RENDER_DISTANCE) != 0 && mc.world != null) {
                mc.worldRenderer.loadRenderers();
            }
            mc.displayGuiScreen(cont);
        }));
        this.addButton(new Button(this.width / 2 - 100, j + 40, 200, 20, I18n.format("options.badVideoSettingsDetected.continueAnyway"), (p_213078_2_) -> {
            mc.displayGuiScreen(cont);
        }));
        this.addButton(new Button(this.width / 2 - 100, j + 64, 200, 20, I18n.format("options.badVideoSettingsDetected.doNotShowAgain"), (p_213078_3_) -> {
            mc.gameSettings.hideVideoSettingsWarning = true;
            mc.gameSettings.saveOptions();
            mc.displayGuiScreen(cont);
        }));
    }

    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground();
        int j = 0;
        for (int i = 0, l = messages.size(); i < l; ++i) {
            String str = messages.get(i);
            if (str != null) {
                this.drawCenteredString(this.font, str, this.width / 2, top + j, 16777215);
                j += 9;
            } else {
                j += 5;
            }
        }
        super.render(p_render_1_, p_render_2_, p_render_3_);
    }

}
