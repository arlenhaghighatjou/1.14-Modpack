package net.minecraft.client.gui.screen;

import net.lax1dude.eaglercraft.opengl.ext.deferred.EaglerDeferredPipeline;
import net.lax1dude.eaglercraft.opengl.ext.deferred.gui.GuiShaderConfig;
import net.lax1dude.eaglercraft.opengl.ext.deferred.gui.GuiShadersNotSupported;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VideoSettingsScreen extends Screen {
    private final Screen parentGuiScreen;
    private final GameSettings guiGameSettings;
    private OptionsRowList optionsRowList;
    private static final AbstractOption[] OPTIONS = new AbstractOption[]{AbstractOption.GRAPHICS, AbstractOption.RENDER_DISTANCE, AbstractOption.AO, AbstractOption.FRAMERATE_LIMIT, AbstractOption.VSYNC, AbstractOption.VIEW_BOBBING, AbstractOption.GUI_SCALE, AbstractOption.ATTACK_INDICATOR, AbstractOption.GAMMA, AbstractOption.RENDER_CLOUDS, AbstractOption.FULLSCREEN, AbstractOption.PARTICLES, AbstractOption.MIPMAP_LEVELS, AbstractOption.ENTITY_SHADOWS, AbstractOption.BIOME_BLEND_RADIUS, AbstractOption.FOG, AbstractOption.SHOW_FPS, AbstractOption.SHOW_XYZ, AbstractOption.SHOW_WORLD_HUD, AbstractOption.SHOW_STATS_HUD, AbstractOption.CHUNK_FIX, AbstractOption.UPDATES_PER_FRAME, AbstractOption.FAST_ENTITY_RENDER, AbstractOption.FAST_TILEENTITY_RENDER, AbstractOption.DISABLE_WEATHER};
    private int field_213108_e;

    public VideoSettingsScreen(Screen parentScreenIn, GameSettings gameSettingsIn) {
        super(new TranslationTextComponent("options.videoTitle"));
        this.parentGuiScreen = parentScreenIn;
        this.guiGameSettings = gameSettingsIn;
    }

    protected void init() {
        this.field_213108_e = this.guiGameSettings.mipmapLevels;
        this.optionsRowList = new OptionsRowList(this.mc, this.width, this.height, 32, this.height - 32, 25);
        this.optionsRowList.func_214335_a(OPTIONS);
        this.children.add(this.optionsRowList);
        this.addButton(new Button(this.width / 2 - 155, this.height - 27, 150, 20, I18n.format("gui.shaders"), (p_213106_2_) -> {
            if (EaglerDeferredPipeline.isSupported()) {
                this.mc.displayGuiScreen(new GuiShaderConfig(this));
            } else {
                String unsupportedReason = EaglerDeferredPipeline.getReasonUnsupported();
                this.mc.displayGuiScreen(new GuiShadersNotSupported(this, unsupportedReason != null ? unsupportedReason : "Shaders are not supported on this device"));
            }
        })).active = false;
        this.addButton(new Button(this.width / 2 + 5, this.height - 27, 150, 20, I18n.format("gui.done"), (p_213106_1_) -> {
            this.mc.gameSettings.saveOptions();
            this.mc.mainWindow.update();
            Screen contScreen = this.parentGuiScreen;
            int vidIssues = this.mc.gameSettings.checkBadVideoSettings();
            if (vidIssues != 0) {
                contScreen = new GuiScreenVideoSettingsWarning(contScreen, vidIssues);
            }
            this.mc.displayGuiScreen(contScreen);
        }));
    }

    public void removed() {
        if (this.guiGameSettings.mipmapLevels != this.field_213108_e) {
            this.mc.getTextureMap().setMipmapLevels(this.guiGameSettings.mipmapLevels);
            this.mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            this.mc.getTextureMap().setBlurMipmapDirect(false, this.guiGameSettings.mipmapLevels > 0);
            this.mc.func_213245_w();
        }

        this.mc.gameSettings.saveOptions();
    }

    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        int i = this.guiGameSettings.guiScale;
        if (super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
            if (this.guiGameSettings.guiScale != i) {
                this.mc.updateWindowSize();
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
        int i = this.guiGameSettings.guiScale;
        if (super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_)) {
            return true;
        } else if (this.optionsRowList.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_)) {
            if (this.guiGameSettings.guiScale != i) {
                this.mc.updateWindowSize();
            }

            return true;
        } else {
            return false;
        }
    }

    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground();
        this.optionsRowList.render(p_render_1_, p_render_2_, p_render_3_);
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 5, 16777215);
        super.render(p_render_1_, p_render_2_, p_render_3_);
    }
}
