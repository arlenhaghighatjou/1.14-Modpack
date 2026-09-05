package net.minecraft.client.gui.advancements;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.CSeenAdvancementsPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class AdvancementsScreen extends Screen implements ClientAdvancementManager.IListener {
    private static final ResourceLocation WINDOW = new ResourceLocation("textures/gui/advancements/window.png");
    private static final ResourceLocation TABS = new ResourceLocation("textures/gui/advancements/tabs.png");
    private final ClientAdvancementManager clientAdvancementManager;
    private final Map<Advancement, AdvancementTabGui> tabs = Maps.newLinkedHashMap();
    private AdvancementTabGui selectedTab;
    private boolean isScrolling;

    public AdvancementsScreen(ClientAdvancementManager p_i47383_1_) {
        super(NarratorChatListener.field_216868_a);
        this.clientAdvancementManager = p_i47383_1_;
    }

    protected void init() {
        this.tabs.clear();
        this.selectedTab = null;
        this.clientAdvancementManager.setListener(this);
        if (this.selectedTab == null && !this.tabs.isEmpty()) {
            this.clientAdvancementManager.setSelectedTab(this.tabs.values().iterator().next().getAdvancement(), true);
        } else {
            this.clientAdvancementManager.setSelectedTab(this.selectedTab == null ? null : this.selectedTab.getAdvancement(), true);
        }

    }

    public void removed() {
        this.clientAdvancementManager.setListener((ClientAdvancementManager.IListener) null);
        ClientPlayNetHandler clientplaynethandler = this.mc.getConnection();
        if (clientplaynethandler != null) {
            clientplaynethandler.sendPacket(CSeenAdvancementsPacket.closedScreen());
        }

    }

    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        if (p_mouseClicked_5_ == 0) {
            int i = (this.width - 252) / 2;
            int j = (this.height - 140) / 2;

            for (AdvancementTabGui advancementtabgui : this.tabs.values()) {
                if (advancementtabgui.func_195627_a(i, j, p_mouseClicked_1_, p_mouseClicked_3_)) {
                    this.clientAdvancementManager.setSelectedTab(advancementtabgui.getAdvancement(), true);
                    break;
                }
            }
        }

        return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
    }

    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (this.mc.gameSettings.keyBindAdvancements.matchesKey(p_keyPressed_1_, p_keyPressed_2_)) {
            this.mc.displayGuiScreen((Screen) null);
            this.mc.mouseHelper.grabMouse();
            return true;
        } else {
            return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        }
    }

    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        int i = (this.width - 252) / 2;
        int j = (this.height - 140) / 2;
        this.renderBackground();
        this.renderInside(p_render_1_, p_render_2_, i, j);
        this.renderWindow(i, j);
        this.renderToolTips(p_render_1_, p_render_2_, i, j);
    }

    public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        if (p_mouseDragged_5_ != 0) {
            this.isScrolling = false;
            return false;
        } else {
            if (!this.isScrolling) {
                this.isScrolling = true;
            } else if (this.selectedTab != null) {
                this.selectedTab.func_195626_a(p_mouseDragged_6_, p_mouseDragged_8_);
            }

            return true;
        }
    }

    private void renderInside(int p_191936_1_, int p_191936_2_, int p_191936_3_, int p_191936_4_) {
        AdvancementTabGui advancementtabgui = this.selectedTab;
        if (advancementtabgui == null) {
            fill(p_191936_3_ + 9, p_191936_4_ + 18, p_191936_3_ + 9 + 234, p_191936_4_ + 18 + 113, -16777216);
            String s = I18n.format("advancements.empty");
            int i = this.font.getStringWidth(s);
            this.font.drawString(s, (float) (p_191936_3_ + 9 + 117 - i / 2), (float) (p_191936_4_ + 18 + 56 - 9 / 2), -1);
            this.font.drawString(":(", (float) (p_191936_3_ + 9 + 117 - this.font.getStringWidth(":(") / 2), (float) (p_191936_4_ + 18 + 113 - 9), -1);
        } else {
            int scX = p_191936_3_ + 9;
            int scY = p_191936_4_ + 18;
            int scW = 234;
            int scH = 113;
            double scale = this.mc.mainWindow.getGuiScaleFactor();
            int fbH = this.mc.mainWindow.getFramebufferHeight();
            GlStateManager.enableScissorTest();
            GlStateManager.scissor((int) (scX * scale), (int) (fbH - (scY + scH) * scale), (int) (scW * scale), (int) (scH * scale));
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float) scX, (float) scY, -400.0F);
            GlStateManager.enableDepthTest();
            advancementtabgui.drawContents();
            GlStateManager.popMatrix();
            GlStateManager.depthFunc(515);
            GlStateManager.disableDepthTest();
            GlStateManager.disableScissorTest();
        }
    }

    public void renderWindow(int p_191934_1_, int p_191934_2_) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        RenderHelper.disableStandardItemLighting();
        this.mc.getTextureManager().bindTexture(WINDOW);
        this.blit(p_191934_1_, p_191934_2_, 0, 0, 252, 140);
        if (this.tabs.size() > 1) {
            this.mc.getTextureManager().bindTexture(TABS);

            for (AdvancementTabGui advancementtabgui : this.tabs.values()) {
                advancementtabgui.drawTab(p_191934_1_, p_191934_2_, advancementtabgui == this.selectedTab);
            }

            GlStateManager.enableRescaleNormal();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderHelper.enableGUIStandardItemLighting();

            for (AdvancementTabGui advancementtabgui1 : this.tabs.values()) {
                advancementtabgui1.drawIcon(p_191934_1_, p_191934_2_, this.itemRenderer);
            }

            GlStateManager.disableBlend();
        }

        this.font.drawString(I18n.format("gui.advancements"), (float) (p_191934_1_ + 8), (float) (p_191934_2_ + 6), 4210752);
    }

    private void renderToolTips(int p_191937_1_, int p_191937_2_, int p_191937_3_, int p_191937_4_) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.selectedTab != null) {
            GlStateManager.pushMatrix();
            GlStateManager.enableDepthTest();
            GlStateManager.translatef((float) (p_191937_3_ + 9), (float) (p_191937_4_ + 18), 400.0F);
            this.selectedTab.drawToolTips(p_191937_1_ - p_191937_3_ - 9, p_191937_2_ - p_191937_4_ - 18, p_191937_3_, p_191937_4_);
            GlStateManager.disableDepthTest();
            GlStateManager.popMatrix();
        }

        if (this.tabs.size() > 1) {
            for (AdvancementTabGui advancementtabgui : this.tabs.values()) {
                if (advancementtabgui.func_195627_a(p_191937_3_, p_191937_4_, (double) p_191937_1_, (double) p_191937_2_)) {
                    this.renderTooltip(advancementtabgui.getTitle(), p_191937_1_, p_191937_2_);
                }
            }
        }

    }

    public void rootAdvancementAdded(Advancement advancementIn) {
        AdvancementTabGui advancementtabgui = AdvancementTabGui.create(this.mc, this, this.tabs.size(), advancementIn);
        if (advancementtabgui != null) {
            this.tabs.put(advancementIn, advancementtabgui);
        }
    }

    public void rootAdvancementRemoved(Advancement advancementIn) {
    }

    public void nonRootAdvancementAdded(Advancement advancementIn) {
        AdvancementTabGui advancementtabgui = this.getTab(advancementIn);
        if (advancementtabgui != null) {
            advancementtabgui.addAdvancement(advancementIn);
        }

    }

    public void nonRootAdvancementRemoved(Advancement advancementIn) {
    }

    public void onUpdateAdvancementProgress(Advancement advancementIn, AdvancementProgress progress) {
        AdvancementEntryGui advancemententrygui = this.getAdvancementGui(advancementIn);
        if (advancemententrygui != null) {
            advancemententrygui.setAdvancementProgress(progress);
        }

    }

    public void setSelectedTab(Advancement advancementIn) {
        this.selectedTab = this.tabs.get(advancementIn);
    }

    public void advancementsCleared() {
        this.tabs.clear();
        this.selectedTab = null;
    }

    public AdvancementEntryGui getAdvancementGui(Advancement p_191938_1_) {
        AdvancementTabGui advancementtabgui = this.getTab(p_191938_1_);
        return advancementtabgui == null ? null : advancementtabgui.getAdvancementGui(p_191938_1_);
    }

    private AdvancementTabGui getTab(Advancement p_191935_1_) {
        while (p_191935_1_.getParent() != null) {
            p_191935_1_ = p_191935_1_.getParent();
        }

        return this.tabs.get(p_191935_1_);
    }
}
