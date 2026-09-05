package net.lax1dude.eaglercraft.recording;

import net.lax1dude.eaglercraft.HString;
import net.lax1dude.eaglercraft.internal.ScreenRecordParameters;
import net.lax1dude.eaglercraft.sp.gui.GuiSlider2;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiScreenRecordingSettings extends Screen {

    private static final Logger logger = LogManager.getLogger("GuiScreenRecordingSettings");

    protected final Screen parent;

    protected Button recordButton;
    protected Button codecButton;
    protected GuiSlider2 videoResolutionSlider;
    protected GuiSlider2 videoFrameRateSlider;
    protected GuiSlider2 audioBitrateSlider;
    protected GuiSlider2 videoBitrateSlider;
    protected GuiSlider2 microphoneVolumeSlider;
    protected GuiSlider2 gameVolumeSlider;
    protected boolean dirty = false;

    public GuiScreenRecordingSettings(Screen parent) {
        super(new TranslationTextComponent("options.screenRecording.title"));
        this.parent = parent;
    }

    protected void init() {
        buttons.clear();
        addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, I18n.format("gui.done"), (p_213056_1_) -> {
            if (dirty) {
                mc.gameSettings.saveOptions();
                dirty = false;
            }
            mc.displayGuiScreen(parent);
        }));
        boolean isRecording = ScreenRecordingController.isRecording();
        addButton(codecButton = new Button(this.width / 2 + 65, this.height / 6 - 2, 75, 20, I18n.format("options.screenRecording.codecButton"), (p_213056_1_) -> {
            mc.displayGuiScreen(new GuiScreenSelectCodec(this, mc.gameSettings.screenRecordCodec));
        }));
        addButton(recordButton = new Button(this.width / 2 + 15, this.height / 6 + 28, 125, 20,
                I18n.format(isRecording ? "options.screenRecording.stop" : "options.screenRecording.start"), (p_213056_1_) -> {
            if (!ScreenRecordingController.isRecording()) {
                try {
                    ScreenRecordingController.startRecording(new ScreenRecordParameters(mc.gameSettings.screenRecordCodec,
                            mc.gameSettings.screenRecordResolution, mc.gameSettings.screenRecordVideoBitrate,
                            mc.gameSettings.screenRecordAudioBitrate, mc.gameSettings.screenRecordFPS));
                } catch (Throwable t) {
                    logger.error("Failed to begin screen recording!");
                    logger.error(t);
                    mc.displayGuiScreen(new ConfirmScreen((result) -> {
                        mc.displayGuiScreen(this);
                    }, new TranslationTextComponent("options.screenRecording.failed"), new TranslationTextComponent(t.toString())));
                }
            } else {
                ScreenRecordingController.endRecording();
            }
        }));
        addButton(videoResolutionSlider = new GuiSlider2(3, this.width / 2 - 155, this.height / 6 + 64, 150, 20, (mc.gameSettings.screenRecordResolution - 1) / 3.999f, 1.0f) {
            protected String updateDisplayString() {
                int i = (int) (sliderValue * 3.999f);
                return I18n.format("options.screenRecording.videoResolution") + ": x" + HString.format("%.2f", 1.0f / (int) Math.pow(2.0, i));
            }

            protected void onChange() {
                mc.gameSettings.screenRecordResolution = 1 + (int) (sliderValue * 3.999f);
                dirty = true;
            }
        });
        addButton(videoFrameRateSlider = new GuiSlider2(4, this.width / 2 + 5, this.height / 6 + 64, 150, 20, (Math.max(mc.gameSettings.screenRecordFPS, 9) - 9) / 51.999f, 1.0f) {
            protected String updateDisplayString() {
                int i = (int) (sliderValue * 51.999f);
                return I18n.format("options.screenRecording.videoFPS") + ": " + (i <= 0 ? I18n.format("options.screenRecording.onVSync") : 9 + i);
            }

            protected void onChange() {
                int i = (int) (sliderValue * 51.999f);
                mc.gameSettings.screenRecordFPS = i <= 0 ? -1 : 9 + i;
                dirty = true;
            }
        });
        addButton(videoBitrateSlider = new GuiSlider2(5, this.width / 2 - 155, this.height / 6 + 98, 150, 20, MathHelper.sqrt(MathHelper.clamp((mc.gameSettings.screenRecordVideoBitrate - 250) / 19750.999f, 0.0f, 1.0f)), 1.0f) {
            protected String updateDisplayString() {
                return I18n.format("options.screenRecording.videoBitrate") + ": " + (250 + (int) (sliderValue * sliderValue * 19750.999f)) + "kbps";
            }

            protected void onChange() {
                mc.gameSettings.screenRecordVideoBitrate = 250 + (int) (sliderValue * sliderValue * 19750.999f);
                dirty = true;
            }
        });
        addButton(audioBitrateSlider = new GuiSlider2(6, this.width / 2 + 5, this.height / 6 + 98, 150, 20, MathHelper.sqrt(MathHelper.clamp((mc.gameSettings.screenRecordAudioBitrate - 24) / 232.999f, 0.0f, 1.0f)), 1.0f) {
            protected String updateDisplayString() {
                return I18n.format("options.screenRecording.audioBitrate") + ": " + (24 + (int) (sliderValue * sliderValue * 232.999f)) + "kbps";
            }

            protected void onChange() {
                mc.gameSettings.screenRecordAudioBitrate = 24 + (int) (sliderValue * sliderValue * 232.999f);
                dirty = true;
            }
        });
        addButton(gameVolumeSlider = new GuiSlider2(7, this.width / 2 - 155, this.height / 6 + 130, 150, 20, mc.gameSettings.screenRecordGameVolume, 1.0f) {
            protected String updateDisplayString() {
                return I18n.format("options.screenRecording.gameVolume") + ": " + (int) (sliderValue * 100.999f) + "%";
            }

            protected void onChange() {
                mc.gameSettings.screenRecordGameVolume = sliderValue;
                ScreenRecordingController.setGameVolume(sliderValue);
                dirty = true;
            }
        });
        addButton(microphoneVolumeSlider = new GuiSlider2(8, this.width / 2 + 5, this.height / 6 + 130, 150, 20, mc.gameSettings.screenRecordMicVolume, 1.0f) {
            protected String updateDisplayString() {
                return I18n.format("options.screenRecording.microphoneVolume") + ": " + (int) (sliderValue * 100.999f) + "%";
            }

            protected void onChange() {
                mc.gameSettings.screenRecordMicVolume = sliderValue;
                ScreenRecordingController.setMicrophoneVolume(sliderValue);
                dirty = true;
            }
        });
        codecButton.active = !isRecording;
        videoResolutionSlider.active = !isRecording;
        videoFrameRateSlider.active = !isRecording;
        audioBitrateSlider.active = !isRecording;
        videoBitrateSlider.active = !isRecording;
        microphoneVolumeSlider.active = !ScreenRecordingController.isMicVolumeLocked();
    }

    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground();
        this.drawCenteredString(this.font, I18n.format("options.screenRecording.title"), this.width / 2, 15, 16777215);
        if (mc.gameSettings.screenRecordCodec == null) {
            mc.gameSettings.screenRecordCodec = ScreenRecordingController.getDefaultCodec();
        }

        String codecString = mc.gameSettings.screenRecordCodec.name;
        int codecStringWidth = this.font.getStringWidth(codecString);
        this.drawString(this.font, codecString, this.width / 2 + 60 - codecStringWidth, this.height / 6 + 4, 0xFFFFFF);

        boolean isRecording = ScreenRecordingController.isRecording();
        codecButton.active = !isRecording;
        videoResolutionSlider.active = !isRecording;
        videoFrameRateSlider.active = !isRecording;
        audioBitrateSlider.active = !isRecording;
        videoBitrateSlider.active = !isRecording;
        microphoneVolumeSlider.active = !ScreenRecordingController.isMicVolumeLocked();
        recordButton.setMessage(I18n.format(isRecording ? "options.screenRecording.stop" : "options.screenRecording.start"));
        String statusString = I18n.format("options.screenRecording.status",
                (isRecording ? TextFormatting.GREEN : TextFormatting.RED) + I18n.format(isRecording ? "options.screenRecording.status.1" : "options.screenRecording.status.0"));
        int statusStringWidth = this.font.getStringWidth(statusString);
        this.drawString(this.font, statusString, this.width / 2 + 10 - statusStringWidth, this.height / 6 + 34, 0xFFFFFF);

        super.render(p_render_1_, p_render_2_, p_render_3_);
    }

    protected void handleCodecCallback(EnumScreenRecordingCodec codec) {
        EnumScreenRecordingCodec oldCodec = mc.gameSettings.screenRecordCodec;
        if (ScreenRecordingController.codecs.contains(codec)) {
            mc.gameSettings.screenRecordCodec = codec;
        } else {
            mc.gameSettings.screenRecordCodec = ScreenRecordingController.getDefaultCodec();
        }
        if (oldCodec != mc.gameSettings.screenRecordCodec) {
            dirty = true;
        }
    }

}
