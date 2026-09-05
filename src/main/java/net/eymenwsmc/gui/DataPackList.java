package net.eymenwsmc.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.list.ExtendedList;import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/**
  A screen that Mojang has never made before.
  @author eymenwsmc
  @see net.eymenwsmc.gui.DataPacksScreen
 */
@OnlyIn(Dist.CLIENT)
public class DataPackList extends ExtendedList<DataPackList.DataPackEntry> {

    private static final ResourceLocation RESOURCE_PACK_TEXTURE = new ResourceLocation("textures/gui/resource_packs.png");

    protected final Minecraft mc;
    private final DataPacksScreen screen;
    private final boolean available;

    public DataPackList(Minecraft mc, DataPacksScreen screen, int width, int height, int top, int bottom, boolean available) {
        super(mc, width, height, top, bottom, 36);
        this.mc = mc;
        this.screen = screen;
        this.available = available;
        this.centerListVertically = false;
        this.setRenderHeader(true, (int) (9.0F * 1.5F));
    }

    public void func_214365_a(DataPackEntry entry) {
        this.addEntry(entry);
        entry.field_214430_c = this;
    }

    protected void renderHeader(int p_renderHeader_1_, int p_renderHeader_2_, Tessellator p_renderHeader_3_) {
        String s = (new TranslationTextComponent(this.available ? "dataPack.available.title" : "dataPack.selected.title")).applyTextStyle(TextFormatting.UNDERLINE).getFormattedText();
        this.mc.fontRenderer.drawString(s, (float) (p_renderHeader_1_ + this.width / 2 - this.mc.fontRenderer.getStringWidth(s) / 2), (float) Math.min(this.y0 + 3, p_renderHeader_2_), 16777215);
    }

    public int getRowWidth() {
        return this.width;
    }

    protected int getScrollbarPosition() {
        return this.x1 - 6;
    }

    @OnlyIn(Dist.CLIENT)
    public static class DataPackEntry extends ExtendedList.AbstractListEntry<DataPackEntry> {

        private DataPackList field_214430_c;
        protected final Minecraft field_214428_a;
        private final DataPacksScreen field_214429_b;
        private final ClientResourcePackInfo info;

        public DataPackEntry(DataPackList list, DataPacksScreen screen, ClientResourcePackInfo infoIn) {
            this.field_214428_a = Minecraft.getInstance();
            this.field_214429_b = screen;
            this.info = infoIn;
            this.field_214430_c = list;
        }

        public ClientResourcePackInfo getInfo() {
            return this.info;
        }

        private boolean isMovable() {
            return !this.info.getName().equals("vanilla");
        }

        private boolean isEnabled() {
            return this.field_214429_b.func_214299_c(this);
        }

        private boolean canMoveUp() {
            return this.isEnabled() && this.field_214430_c.children().indexOf(this) > 0;
        }

        private boolean canMoveDown() {
            List<DataPackEntry> list = this.field_214430_c.children();
            int i = list.indexOf(this);
            return this.isEnabled() && i >= 0 && i < list.size() - 1;
        }

        public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
            PackCompatibility packcompatibility = this.info.getCompatibility();
            if (!packcompatibility.func_198968_a()) {
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                AbstractGui.fill(p_render_3_ - 1, p_render_2_ - 1, p_render_3_ + p_render_4_ - 9, p_render_2_ + p_render_5_ + 1, -8978432);
            }

            this.info.func_195808_a(this.field_214428_a.getTextureManager());
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            AbstractGui.blit(p_render_3_, p_render_2_, 0.0F, 0.0F, 32, 32, 32, 32);

            int i = p_render_6_ - p_render_3_;
            int j = p_render_7_ - p_render_2_;

            if (this.isMovable() && (this.field_214428_a.gameSettings.touchscreen || p_render_8_)) {
                this.field_214428_a.getTextureManager().bindTexture(RESOURCE_PACK_TEXTURE);
                AbstractGui.fill(p_render_3_, p_render_2_, p_render_3_ + 32, p_render_2_ + 32, -1601138544);
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                if (!this.isEnabled()) {
                    if (i < 32) {
                        AbstractGui.blit(p_render_3_, p_render_2_, 0.0F, 32.0F, 32, 32, 256, 256);
                    } else {
                        AbstractGui.blit(p_render_3_, p_render_2_, 0.0F, 0.0F, 32, 32, 256, 256);
                    }
                } else {
                    if (i < 16) {
                        AbstractGui.blit(p_render_3_, p_render_2_, 32.0F, 32.0F, 32, 32, 256, 256);
                    } else {
                        if (this.canMoveUp()) {
                            if (i < 32 && j < 16) {
                                AbstractGui.blit(p_render_3_, p_render_2_, 96.0F, 32.0F, 32, 32, 256, 256);
                            } else {
                                AbstractGui.blit(p_render_3_, p_render_2_, 96.0F, 0.0F, 32, 32, 256, 256);
                            }
                        }
                        if (this.canMoveDown()) {
                            if (i < 32 && j > 16) {
                                AbstractGui.blit(p_render_3_, p_render_2_, 64.0F, 32.0F, 32, 32, 256, 256);
                            } else {
                                AbstractGui.blit(p_render_3_, p_render_2_, 64.0F, 0.0F, 32, 32, 256, 256);
                            }
                        }
                    }
                }
            }

            String s = this.info.func_195789_b().getFormattedText();
            String s1 = this.info.getDescription().getFormattedText();
            int l = this.field_214428_a.fontRenderer.getStringWidth(s);
            if (l > 130) {
                s = this.field_214428_a.fontRenderer.trimStringToWidth(s, 130 - this.field_214428_a.fontRenderer.getStringWidth("...")) + "...";
            }

            this.field_214428_a.fontRenderer.drawStringWithShadow(s, (float) (p_render_3_ + 34), (float) (p_render_2_ + 1), 16777215);
            List<String> list = this.field_214428_a.fontRenderer.listFormattedStringToWidth(s1, 110);
            for (int k = 0; k < 2 && k < list.size(); ++k) {
                this.field_214428_a.fontRenderer.drawStringWithShadow(list.get(k), (float) (p_render_3_ + 34), (float) (p_render_2_ + 12 + 10 * k), 8421504);
            }

            if (this.isMovable()) {
                int bx = p_render_3_ + p_render_4_ - 14;
                int by = p_render_2_ + (p_render_5_ - 9) / 2;
                boolean hovered = i >= p_render_4_ - 20 && i <= p_render_4_ && j >= 0 && j <= p_render_5_;
                this.field_214428_a.fontRenderer.drawStringWithShadow("X", (float) bx, (float) by, hovered ? 0xFF5555 : 0xFF6666);
            }
        }

        public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
            double d0 = p_mouseClicked_1_ - (double) this.field_214430_c.getRowLeft();
            double d1 = p_mouseClicked_3_ - (double) this.field_214430_c.getRowTop(this.field_214430_c.children().indexOf(this));
            if (this.isMovable() && d0 >= 0.0D && d0 <= 32.0D) {
                if (!this.isEnabled()) {
                    this.field_214429_b.enableDPack(this);
                } else if (d0 < 16.0D) {
                    this.field_214429_b.disableDPack(this);
                } else if (d1 < 16.0D && this.canMoveUp()) {
                    this.field_214429_b.moveTheShitUp(this);
                } else if (d1 > 16.0D && this.canMoveDown()) {
                    this.field_214429_b.moveTheShitDown(this);
                }
                return true;
            }
            if (this.isMovable() && d0 >= (double) (this.field_214430_c.getRowWidth() - 20) && d0 <= (double) this.field_214430_c.getRowWidth() && d1 >= 0.0D && d1 < 36.0D) {
                this.field_214429_b.deleteTheShit(this.info);
                return true;
            }
            return false;
        }
    }
}
