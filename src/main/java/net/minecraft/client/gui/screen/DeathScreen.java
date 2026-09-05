package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DeathScreen extends Screen {
    private int enableButtonsTimer;
    private final ITextComponent causeOfDeath;
    private final boolean field_213023_c;

    public DeathScreen(ITextComponent p_i51118_1_, boolean p_i51118_2_) {
        super(new TranslationTextComponent(p_i51118_2_ ? "deathScreen.title.hardcore" : "deathScreen.title"));
        this.causeOfDeath = p_i51118_1_;
        this.field_213023_c = p_i51118_2_;
    }

    protected void init() {
        this.enableButtonsTimer = 0;
        String s;
        String s1;
        if (this.field_213023_c) {
            s = I18n.format("deathScreen.spectate");
            s1 = I18n.format("deathScreen." + (this.mc.isIntegratedServerRunning() ? "deleteWorld" : "leaveServer"));
        } else {
            s = I18n.format("deathScreen.respawn");
            s1 = I18n.format("deathScreen.titleScreen");
        }

        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 72, 200, 20, s, (p_213021_1_) -> {
            this.mc.player.respawnPlayer();
            this.mc.displayGuiScreen((Screen) null);
        }));
        Button button = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96, 200, 20, s1, (p_213020_1_) -> {
            if (this.field_213023_c) {
                this.mc.displayGuiScreen(new MainMenuScreen());
            } else {
                ConfirmScreen confirmscreen = new ConfirmScreen(this::confirmCallback, new TranslationTextComponent("deathScreen.quit.confirm"), new StringTextComponent(""), I18n.format("deathScreen.titleScreen"), I18n.format("deathScreen.respawn"));
                this.mc.displayGuiScreen(confirmscreen);
                confirmscreen.setButtonDelay(20);
            }
        }));
        if (!this.field_213023_c && this.mc.getSession() == null) {
            button.active = false;
        }

        for (Widget widget : this.buttons) {
            widget.active = false;
        }

    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    private void confirmCallback(boolean p_213022_1_) {
        if (p_213022_1_) {
            if (this.mc.isIntegratedServerRunning()) {
                this.mc.scheduleWorldUnload(new DirtMessageScreen(new TranslationTextComponent("menu.savingLevel")), new MainMenuScreen());
            } else {
                if (this.mc.world != null) {
                    this.mc.world.sendQuittingDisconnectingPacket();
                }

                this.mc.func_213231_b(new DirtMessageScreen(new TranslationTextComponent("menu.savingLevel")));
                this.mc.displayGuiScreen(new MainMenuScreen());
            }
        } else {
            this.mc.player.respawnPlayer();
            this.mc.displayGuiScreen((Screen) null);
        }

    }

    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.fillGradient(0, 0, this.width, this.height, 1615855616, -1602211792);
        GlStateManager.pushMatrix();
        GlStateManager.scalef(2.0F, 2.0F, 2.0F);
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2 / 2, 30, 16777215);
        GlStateManager.popMatrix();
        if (this.causeOfDeath != null) {
            this.drawCenteredString(this.font, this.causeOfDeath.getFormattedText(), this.width / 2, 85, 16777215);
        }

        this.drawCenteredString(this.font, I18n.format("deathScreen.score") + ": " + TextFormatting.YELLOW + this.mc.player.getScore(), this.width / 2, 100, 16777215);
        if (this.causeOfDeath != null && p_render_2_ > 85 && p_render_2_ < 85 + 9) {
            ITextComponent itextcomponent = this.getClickedComponentAt(p_render_1_);
            if (itextcomponent != null && itextcomponent.getStyle().getHoverEvent() != null) {
                this.renderComponentHoverEffect(itextcomponent, p_render_1_, p_render_2_);
            }
        }

        super.render(p_render_1_, p_render_2_, p_render_3_);
    }

    public ITextComponent getClickedComponentAt(int p_184870_1_) {
        if (this.causeOfDeath == null) {
            return null;
        } else {
            int i = this.mc.fontRenderer.getStringWidth(this.causeOfDeath.getFormattedText());
            int j = this.width / 2 - i / 2;
            int k = this.width / 2 + i / 2;
            int l = j;
            if (p_184870_1_ >= j && p_184870_1_ <= k) {
                for (ITextComponent itextcomponent : this.causeOfDeath) {
                    l += this.mc.fontRenderer.getStringWidth(RenderComponentsUtil.removeTextColorsIfConfigured(itextcomponent.getUnformattedComponentText(), false));
                    if (l > p_184870_1_) {
                        return itextcomponent;
                    }
                }

                return null;
            } else {
                return null;
            }
        }
    }

    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        if (this.causeOfDeath != null && p_mouseClicked_3_ > 85.0D && p_mouseClicked_3_ < (double) (85 + 9)) {
            ITextComponent itextcomponent = this.getClickedComponentAt((int) p_mouseClicked_1_);
            if (itextcomponent != null && itextcomponent.getStyle().getClickEvent() != null && itextcomponent.getStyle().getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
                this.handleComponentClicked(itextcomponent);
                return false;
            }
        }

        return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
    }

    public boolean isPauseScreen() {
        return false;
    }

    public void tick() {
        super.tick();
        ++this.enableButtonsTimer;
        if (this.enableButtonsTimer == 20) {
            for (Widget widget : this.buttons) {
                widget.active = true;
            }
        }

    }
}
