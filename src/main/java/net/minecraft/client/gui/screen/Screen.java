package net.minecraft.client.gui.screen;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.lax1dude.eaglercraft.EagRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public abstract class Screen extends FocusableGui implements IRenderable {
    private static final Logger LOGGER = LogManager.getLogger();
    protected final ITextComponent title;
    protected final List<IGuiEventListener> children = Lists.newArrayList();

    public Minecraft mc;
    protected ItemRenderer itemRenderer;
    public int width;
    public int height;
    protected final List<Widget> buttons = Lists.newArrayList();
    public boolean passEvents;
    protected FontRenderer font;
    private String clickedLink;
    private long showingCloseKey = 0L;

    private int touchValue;
    protected int touchModeCursorPosX = -1;
    protected int touchModeCursorPosY = -1;
    private long lastTouchEvent;

    public final IntObjectMap<int[]> touchStarts = new IntObjectHashMap<>();

    protected Screen(ITextComponent titleIn) {
        this.title = titleIn;
    }

    public ITextComponent getTitle() {
        return this.title;
    }

    public String getNarrationMessage() {
        return this.getTitle().getString();
    }

    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        for (int i = 0; i < this.buttons.size(); ++i) {
            this.buttons.get(i).render(p_render_1_, p_render_2_, p_render_3_);
        }

        long millis = EagRuntime.steadyTimeMillis();
        long closeKeyTimeout = millis - this.showingCloseKey;
        if (closeKeyTimeout < 3000L && this.showingCloseKey != 0L) {
            int alpha = 255;
            long fadeTime = 2500L;
            if (closeKeyTimeout > fadeTime) {
                alpha = (int) ((3000L - closeKeyTimeout) * 255L / (3000L - fadeTime));
            }
            String str;
            str = I18n.format("gui.exitKey", this.mc.gameSettings.keyBindClose.getLocalizedName());
            int textWidth = this.font.getStringWidth(str);
            this.fill(this.width / 2 - textWidth / 2 - 2, 2, this.width / 2 + textWidth / 2 + 2, 14, (0xAA000000) | ((alpha & 0xFF) << 24));
            this.drawCenteredString(this.font, str, this.width / 2, 4, (0xFF5555) | ((alpha & 0xFF) << 24));
        }

    }

    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (this.shouldCloseOnEsc()) {
            if (this.mc.gameSettings.keyBindClose.matchesKey(p_keyPressed_1_, p_keyPressed_2_)) {
                this.onClose();
                return true;
            }
            if (p_keyPressed_1_ == 256) {
                if (this.mc.gameSettings.keyBindClose.isInvalid()) {
                    this.onClose();
                } else {
                    this.showingCloseKey = EagRuntime.steadyTimeMillis();
                }
                return true;
            }
        }
        if (p_keyPressed_1_ == 258) {
            boolean flag = !hasShiftDown();
            if (!this.changeFocus(flag)) {
                this.changeFocus(flag);
            }

            return true;
        } else {
            return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        }
    }

    public static boolean isCloseKey(int keyCode, int scanCode) {
        return keyCode == 256 || Minecraft.getInstance().gameSettings.keyBindClose.matchesKey(keyCode, scanCode);
    }

    public boolean shouldCloseOnEsc() {
        return true;
    }

    public void onClose() {
        this.mc.displayGuiScreen((Screen) null);
        if (this.mc.currentScreen == null) {
            this.mc.setIngameFocus();
        }
    }

    protected <T extends IGuiEventListener> T addWidget(T widget) {
        if (widget instanceof Widget) {
            this.buttons.add((Widget) widget);
        }
        this.children.add(widget);
        return widget;
    }

    protected <T extends Widget> T addButton(T p_addButton_1_) {
        this.buttons.add(p_addButton_1_);
        this.children.add(p_addButton_1_);
        return p_addButton_1_;
    }

    protected void renderTooltip(ItemStack p_renderTooltip_1_, int p_renderTooltip_2_, int p_renderTooltip_3_) {
        this.renderTooltip(this.getTooltipFromItem(p_renderTooltip_1_), p_renderTooltip_2_, p_renderTooltip_3_, p_renderTooltip_1_);
    }

    public List<String> getTooltipFromItem(ItemStack p_getTooltipFromItem_1_) {
        List<ITextComponent> list = p_getTooltipFromItem_1_.getTooltip(this.mc.player, this.mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
        List<String> list1 = Lists.newArrayList();

        for (ITextComponent itextcomponent : list) {
            list1.add(itextcomponent.getFormattedText());
        }

        return list1;
    }

    public void renderTooltip(String p_renderTooltip_1_, int p_renderTooltip_2_, int p_renderTooltip_3_) {
        this.renderTooltip(Arrays.asList(p_renderTooltip_1_), p_renderTooltip_2_, p_renderTooltip_3_);
    }

    public void renderTooltip(List<String> p_renderTooltip_1_, int p_renderTooltip_2_, int p_renderTooltip_3_) {
		this.renderTooltip(p_renderTooltip_1_, p_renderTooltip_2_, p_renderTooltip_3_, ItemStack.EMPTY);
	}

	private void renderTooltip(List<String> p_renderTooltip_1_, int p_renderTooltip_2_, int p_renderTooltip_3_, ItemStack hoveredStack) {
        if (!p_renderTooltip_1_.isEmpty()) {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            int i = 0;

            for (String s : p_renderTooltip_1_) {
                int j = this.font.getStringWidth(s);
                if (j > i) {
                    i = j;
                }
            }

            int l1 = p_renderTooltip_2_ + 12;
            int i2 = p_renderTooltip_3_ - 12;
            int k = 8;
            if (p_renderTooltip_1_.size() > 1) {
                k += 2 + (p_renderTooltip_1_.size() - 1) * 10;
            }

            if (l1 + i > this.width) {
                l1 -= 28 + i;
            }

            if (i2 + k + 6 > this.height) {
                i2 = this.height - k - 6;
            }

			int tooltipTop = i2;
            this.blitOffset = 300;
            this.itemRenderer.zLevel = 300.0F;
            int l = -267386864;
            this.fillGradient(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, -267386864, -267386864);
            this.fillGradient(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, -267386864, -267386864);
            this.fillGradient(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, -267386864, -267386864);
            this.fillGradient(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, -267386864, -267386864);
            this.fillGradient(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, -267386864, -267386864);
            int i1 = 1347420415;
            int j1 = 1344798847;
            this.fillGradient(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, 1347420415, 1344798847);
            this.fillGradient(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, 1347420415, 1344798847);
            this.fillGradient(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, 1347420415, 1347420415);
            this.fillGradient(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, 1344798847, 1344798847);

            for (int k1 = 0; k1 < p_renderTooltip_1_.size(); ++k1) {
                String s1 = p_renderTooltip_1_.get(k1);
                this.font.drawStringWithShadow(s1, (float) l1, (float) i2, -1);
                if (k1 == 0) {
                    i2 += 2;
                }

                i2 += 10;
            }

			squeek.appleskin.client.TooltipOverlayHandler.onRenderTooltip(hoveredStack, l1, tooltipTop, i, k);
            this.blitOffset = 0;
            this.itemRenderer.zLevel = 0.0F;
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    protected void renderComponentHoverEffect(ITextComponent p_renderComponentHoverEffect_1_, int p_renderComponentHoverEffect_2_, int p_renderComponentHoverEffect_3_) {
        if (p_renderComponentHoverEffect_1_ != null && p_renderComponentHoverEffect_1_.getStyle().getHoverEvent() != null) {
            HoverEvent hoverevent = p_renderComponentHoverEffect_1_.getStyle().getHoverEvent();
            if (hoverevent.getAction() == HoverEvent.Action.SHOW_ITEM) {
                ItemStack itemstack = ItemStack.EMPTY;

                try {
                    INBT inbt = JsonToNBT.getTagFromJson(hoverevent.getValue().getString());
                    if (inbt instanceof CompoundNBT) {
                        itemstack = ItemStack.read((CompoundNBT) inbt);
                    }
                } catch (CommandSyntaxException var10) {
                    ;
                }

                if (itemstack.isEmpty()) {
                    this.renderTooltip(TextFormatting.RED + "Invalid Item!", p_renderComponentHoverEffect_2_, p_renderComponentHoverEffect_3_);
                } else {
                    this.renderTooltip(itemstack, p_renderComponentHoverEffect_2_, p_renderComponentHoverEffect_3_);
                }
            } else if (hoverevent.getAction() == HoverEvent.Action.SHOW_ENTITY) {
                if (this.mc.gameSettings.advancedItemTooltips) {
                    try {
                        CompoundNBT compoundnbt = JsonToNBT.getTagFromJson(hoverevent.getValue().getString());
                        List<String> list = Lists.newArrayList();
                        ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(compoundnbt.getString("name"));
                        if (itextcomponent != null) {
                            list.add(itextcomponent.getFormattedText());
                        }

                        if (compoundnbt.contains("type", 8)) {
                            String s = compoundnbt.getString("type");
                            list.add("Type: " + s);
                        }

                        list.add(compoundnbt.getString("id"));
                        this.renderTooltip(list, p_renderComponentHoverEffect_2_, p_renderComponentHoverEffect_3_);
                    } catch (CommandSyntaxException | JsonSyntaxException var9) {
                        this.renderTooltip(TextFormatting.RED + "Invalid Entity!", p_renderComponentHoverEffect_2_, p_renderComponentHoverEffect_3_);
                    }
                }
            } else if (hoverevent.getAction() == HoverEvent.Action.SHOW_TEXT) {
                this.renderTooltip(this.mc.fontRenderer.listFormattedStringToWidth(hoverevent.getValue().getFormattedText(), Math.max(this.width / 2, 200)), p_renderComponentHoverEffect_2_, p_renderComponentHoverEffect_3_);
            }

            GlStateManager.disableLighting();
        }
    }

    protected void insertText(String p_insertText_1_, boolean p_insertText_2_) {
    }

    public boolean handleComponentClicked(ITextComponent p_handleComponentClicked_1_) {
        if (p_handleComponentClicked_1_ == null) {
            return false;
        } else {
            ClickEvent clickevent = p_handleComponentClicked_1_.getStyle().getClickEvent();
            if (hasShiftDown()) {
                if (p_handleComponentClicked_1_.getStyle().getInsertion() != null) {
                    this.insertText(p_handleComponentClicked_1_.getStyle().getInsertion(), false);
                }
            } else if (clickevent != null) {
                if (clickevent.getAction() == ClickEvent.Action.OPEN_URL) {
                    if (!this.mc.gameSettings.chatLinks) {
                        return false;
                    }
                    String uri;
                    uri = clickevent.getValue();

                    if (this.mc.gameSettings.chatLinksPrompt) {
                        this.clickedLink = uri;
                        this.mc.displayGuiScreen(new ConfirmOpenLinkScreen(this::confirmLink, clickevent.getValue(), false));
                    } else {
                        this.openLink(uri);
                    }
                } else if (clickevent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
                    this.insertText(clickevent.getValue(), true);
                } else if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                    this.sendMessage(clickevent.getValue(), false);
                } else {
                    LOGGER.error("Don't know how to handle {}", (Object) clickevent);
                }

                return true;
            }

            return false;
        }
    }

    public void sendMessage(String p_sendMessage_1_) {
        this.sendMessage(p_sendMessage_1_, true);
    }

    public void sendMessage(String p_sendMessage_1_, boolean p_sendMessage_2_) {
        if (p_sendMessage_2_) {
            this.mc.ingameGUI.getChatGUI().addToSentMessages(p_sendMessage_1_);
        }

        this.mc.player.sendChatMessage(p_sendMessage_1_);
    }

    public void init(Minecraft p_init_1_, int p_init_2_, int p_init_3_) {
        this.mc = p_init_1_;
        this.itemRenderer = p_init_1_.getItemRenderer();
        this.font = p_init_1_.fontRenderer;
        this.width = p_init_2_;
        this.height = p_init_3_;
        this.buttons.clear();
        this.children.clear();
        this.setFocused((IGuiEventListener) null);
        this.init();
    }

    public void setSize(int p_setSize_1_, int p_setSize_2_) {
        this.width = p_setSize_1_;
        this.height = p_setSize_2_;
    }

    public List<? extends IGuiEventListener> children() {
        return this.children;
    }

    protected void init() {
    }

    public void tick() {
    }

    public void removed() {
    }

    public void handleTouchInput() {
        net.lax1dude.eaglercraft.internal.EnumTouchEvent et = net.lax1dude.eaglercraft.Touch.getEventType();
        if (et == net.lax1dude.eaglercraft.internal.EnumTouchEvent.TOUCHSTART) {
            net.lax1dude.eaglercraft.PointerInputAbstraction.enterTouchModeHook();
        }
        float scaleFac = getEaglerScale();
        int fw = this.mc.mainWindow.getFramebufferWidth();
        int fh = this.mc.mainWindow.getFramebufferHeight();
        for (int t = 0, c = net.lax1dude.eaglercraft.Touch.getEventTouchPointCount(); t < c; ++t) {
            int u = net.lax1dude.eaglercraft.Touch.getEventTouchPointUID(t);
            int i = net.lax1dude.eaglercraft.Touch.getEventTouchX(t);
            int j = net.lax1dude.eaglercraft.Touch.getEventTouchY(t);
            i = applyEaglerScale(scaleFac, i * this.width / fw, this.width);
            j = applyEaglerScale(scaleFac, this.height - j * this.height / fh - 1, this.height);
            float rad = net.lax1dude.eaglercraft.Touch.getEventTouchRadiusMixed(t);
            float si = rad * this.width / fw / scaleFac;
            if (si < 1.0f) si = 1.0f;
            float sj = rad * this.height / fh / scaleFac;
            if (sj < 1.0f) sj = 1.0f;
            int[] ck = touchStarts.remove(u);
            switch (et) {
            case TOUCHSTART:
                if (t == 0) {
                    touchModeCursorPosX = i;
                    touchModeCursorPosY = j;
                }
                lastTouchEvent = EagRuntime.steadyTimeMillis();
                touchStarts.put(u, new int[] { i, j, 0 });
                this.touchStarted(i, j, u);
                if (t == 0 && shouldTouchGenerateMouseEvents()) {
                    boolean handled = this.mouseClicked(i, j, 0);
                    if (handled) {
                        this.setDragging(true);
                    }
                }
                break;
            case TOUCHMOVE:
                if (t == 0) {
                    touchModeCursorPosX = i;
                    touchModeCursorPosY = j;
                }
                if (ck != null && Math.abs(ck[0] - i) < si && Math.abs(ck[1] - j) < sj) {
                    touchStarts.put(u, ck);
                    break;
                }
                int newState = (ck != null && isTouchDraggingStateLocked(u)) ? ck[2] : 1;
                touchStarts.put(u, new int[] { i, j, newState });
                this.touchMoved(i, j, u);
                if (t == 0 && shouldTouchGenerateMouseEvents()) {
                    long timeSinceLast = EagRuntime.steadyTimeMillis() - lastTouchEvent;
                    double dx = i - (ck != null ? ck[0] : i);
                    double dy = j - (ck != null ? ck[1] : j);
                    if (this.isDragging()) {
                        this.mouseDragged(i, j, 0, dx, dy);
                    }
                }
                break;
            case TOUCHEND:
                if (ck == null) break;
                if (t == 0) {
                    touchModeCursorPosX = -1;
                    touchModeCursorPosY = -1;
                }
                if (ck[2] == 1) {
                    this.touchEndMove(i, j, u);
                } else {
                    if (ck != null) {
                        i = ck[0];
                        j = ck[1];
                    }
                    this.touchTapped(i, j, u);
                }
                if (t == 0 && shouldTouchGenerateMouseEvents()) {
                    this.mouseReleased(i, j, 0);
                    this.setDragging(false);
                }
                break;
            }
        }
    }

    public void touchStarted(int x, int y, int evt) {
    }

    public void touchTapped(int x, int y, int evt) {
    }

    public void touchMoved(int x, int y, int evt) {
    }

    public void touchEndMove(int x, int y, int evt) {
    }

    public boolean isTouchPointDragging(int uid) {
        int[] ret = touchStarts.get(uid);
        return ret != null && ret[2] == 1;
    }

    public boolean isTouchDraggingStateLocked(int uid) {
        return false;
    }

    public boolean shouldTouchGenerateMouseEvents() {
        return true;
    }

    public float getEaglerScale() {
        return 1.0f;
    }

    private int applyEaglerScale(float scaleFac, int val, int max) {
        if (scaleFac == 0.0f) scaleFac = 1.0f;
        int ret = (int) (val / scaleFac);
        if (ret < 0) ret = 0;
        else if (ret > max) ret = max;
        return ret;
    }

    public void renderBackground() {
        this.renderBackground(0);
    }

    public void renderBackground(int p_renderBackground_1_) {
        if (this.mc.world != null) {
            boolean ingame = this.isPauseScreen();
            net.minecraft.util.ResourceLocation loc = (ingame && net.lax1dude.eaglercraft.PauseMenuCustomizeState.icon_background_pause != null)
                    ? net.lax1dude.eaglercraft.PauseMenuCustomizeState.icon_background_pause
                    : net.lax1dude.eaglercraft.PauseMenuCustomizeState.icon_background_all;
            float aspect = (ingame && net.lax1dude.eaglercraft.PauseMenuCustomizeState.icon_background_pause != null)
                    ? 1.0f / net.lax1dude.eaglercraft.PauseMenuCustomizeState.icon_background_pause_aspect
                    : 1.0f / net.lax1dude.eaglercraft.PauseMenuCustomizeState.icon_background_all_aspect;
            if (loc != null) {
                GlStateManager.disableLighting();
                GlStateManager.disableFog();
                GlStateManager.enableBlend();
                GlStateManager.disableAlphaTest();
                GlStateManager.enableTexture();
                GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                this.mc.getTextureManager().bindTexture(loc);
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                float f = 64.0F;
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                bufferbuilder.pos(0.0D, (double) this.height, 0.0D).tex(0.0D, (double) ((float) this.height / f))
                        .color(64, 64, 64, 192).endVertex();
                bufferbuilder.pos((double) this.width, (double) this.height, 0.0D)
                        .tex((double) ((float) this.width / f * aspect), (double) ((float) this.height / f))
                        .color(64, 64, 64, 192).endVertex();
                bufferbuilder.pos((double) this.width, 0.0D, 0.0D)
                        .tex((double) ((float) this.width / f * aspect), (double) 0).color(64, 64, 64, 192).endVertex();
                bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0D, (double) 0).color(64, 64, 64, 192).endVertex();
                tessellator.draw();
                GlStateManager.enableAlphaTest();
            } else {
                this.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
            }

            loc = (ingame && net.lax1dude.eaglercraft.PauseMenuCustomizeState.icon_watermark_pause != null)
                    ? net.lax1dude.eaglercraft.PauseMenuCustomizeState.icon_watermark_pause
                    : net.lax1dude.eaglercraft.PauseMenuCustomizeState.icon_watermark_all;
            aspect = (ingame && net.lax1dude.eaglercraft.PauseMenuCustomizeState.icon_watermark_pause != null)
                    ? net.lax1dude.eaglercraft.PauseMenuCustomizeState.icon_watermark_pause_aspect
                    : net.lax1dude.eaglercraft.PauseMenuCustomizeState.icon_watermark_all_aspect;
            if (loc != null) {
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                this.mc.getTextureManager().bindTexture(loc);
                GlStateManager.pushMatrix();
                GlStateManager.translatef(8.0f, this.height - 72.0f, 0.0f);
                float f2 = 64.0f / 256.0f;
                GlStateManager.scalef(f2 * aspect, f2, f2);
                net.minecraft.client.gui.AbstractGui.blit(0, 0, 0, 0, 256, 256, 256, 256);
                GlStateManager.popMatrix();
            }
        } else {
            this.renderDirtBackground(p_renderBackground_1_);
        }

    }

    public void renderDirtBackground(int p_renderDirtBackground_1_) {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.enableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        this.mc.getTextureManager().bindTexture(BACKGROUND_LOCATION);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(0.0D, (double) this.height, 0.0D).tex(0.0D, (double) ((float) this.height / 32.0F + (float) p_renderDirtBackground_1_)).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos((double) this.width, (double) this.height, 0.0D).tex((double) ((float) this.width / 32.0F), (double) ((float) this.height / 32.0F + (float) p_renderDirtBackground_1_)).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos((double) this.width, 0.0D, 0.0D).tex((double) ((float) this.width / 32.0F), (double) p_renderDirtBackground_1_).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0D, (double) p_renderDirtBackground_1_).color(64, 64, 64, 255).endVertex();
        tessellator.draw();
    }

    public boolean isPauseScreen() {
        return true;
    }

    private void confirmLink(boolean p_confirmLink_1_) {
        if (p_confirmLink_1_) {
            this.openLink(this.clickedLink);
        }

        this.clickedLink = null;
        this.mc.displayGuiScreen(this);
    }

    private void openLink(String p_openLink_1_) {
        EagRuntime.openLink(p_openLink_1_);
    }

    public static boolean hasControlDown() {
        if (Minecraft.IS_RUNNING_ON_MAC) {
            return InputMappings.isKeyDown(343) || InputMappings.isKeyDown(347);
        } else {
            return InputMappings.isKeyDown(341) || InputMappings.isKeyDown(345);
        }
    }

    public static boolean hasShiftDown() {
        return InputMappings.isKeyDown(340) || InputMappings.isKeyDown(344);
    }

    public static boolean hasAltDown() {
        return InputMappings.isKeyDown(342) || InputMappings.isKeyDown(346);
    }

    public static boolean isCut(int p_isCut_0_) {
        return p_isCut_0_ == 88 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }

    public static boolean isPaste(int p_isPaste_0_) {
        return p_isPaste_0_ == 86 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }

    public static boolean isCopy(int p_isCopy_0_) {
        return p_isCopy_0_ == 67 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }

    public static boolean isSelectAll(int p_isSelectAll_0_) {
        return p_isSelectAll_0_ == 65 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }

    public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
        this.init(p_resize_1_, p_resize_2_, p_resize_3_);
    }

    public static void wrapScreenError(Runnable p_wrapScreenError_0_, String p_wrapScreenError_1_, String p_wrapScreenError_2_) {
        try {
            p_wrapScreenError_0_.run();
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, p_wrapScreenError_1_);
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Affected screen");
            crashreportcategory.addDetail("Screen name", () -> {
                return p_wrapScreenError_2_;
            });
            throw new ReportedException(crashreport);
        }
    }

    protected boolean isValidCharacterForName(String p_isValidCharacterForName_1_, char p_isValidCharacterForName_2_, int p_isValidCharacterForName_3_) {
        int i = p_isValidCharacterForName_1_.indexOf(58);
        int j = p_isValidCharacterForName_1_.indexOf(47);
        if (p_isValidCharacterForName_2_ == ':') {
            return (j == -1 || p_isValidCharacterForName_3_ <= j) && i == -1;
        } else if (p_isValidCharacterForName_2_ == '/') {
            return p_isValidCharacterForName_3_ > i;
        } else {
            return p_isValidCharacterForName_2_ == '_' || p_isValidCharacterForName_2_ == '-' || p_isValidCharacterForName_2_ >= 'a' && p_isValidCharacterForName_2_ <= 'z' || p_isValidCharacterForName_2_ >= '0' && p_isValidCharacterForName_2_ <= '9' || p_isValidCharacterForName_2_ == '.';
        }
    }

    public boolean isMouseOver(double p_isMouseOver_1_, double p_isMouseOver_3_) {
        return true;
    }

    public boolean showCopyPasteButtons() {
        return false;
    }

    public boolean canCloseGui() {
        return true;
    }
}
