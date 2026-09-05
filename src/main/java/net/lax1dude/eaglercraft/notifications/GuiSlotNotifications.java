/*
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
 *
 * 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.lax1dude.eaglercraft.notifications;

import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketNotifBadgeShowV4EAG.EnumBadgePriority;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GuiSlotNotifications extends AbstractList<GuiSlotNotifications.NotifBadgeSlot> {

    private static final ResourceLocation eaglerGui = new ResourceLocation("eagler:gui/eagler_gui.png");
    private static final ResourceLocation largeNotifBk = new ResourceLocation("eagler:gui/notif_bk_large.png");

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");

    final GuiScreenNotifications parent;

    int mouseX;
    int mouseY;

    public GuiSlotNotifications(GuiScreenNotifications parent) {
        super(Minecraft.getInstance(), parent.width, parent.height, 32, parent.height - 44, 68);
        this.parent = parent;
    }

    public void clearSlots() {
        clearEntries();
    }

    public void addSlot(NotifBadgeSlot slot) {
        addEntry(slot);
    }

    public void ensureVisibleSlot(NotifBadgeSlot slot) {
        ensureVisible(slot);
    }

    public int getTop() {
        return this.y0;
    }

    public int getBottom() {
        return this.y1;
    }

    @Override
    public int getRowWidth() {
        return 224;
    }

    @Override
    protected boolean isSelectedItem(int index) {
        return index == parent.selected;
    }

    @Override
    public void render(int mouseXIn, int mouseYIn, float partialTicks) {
        mouseX = mouseXIn;
        mouseY = mouseYIn;
        for (int i = 0, l = children().size(); i < l; ++i) {
            NotifBadgeSlot slot = children().get(i);
            slot.currentScreenX = -69420;
            slot.currentScreenY = -69420;
            slot.currentIndex = i;
        }
        super.render(mouseXIn, mouseYIn, partialTicks);
    }

    protected static class NotifBadgeSlot extends AbstractList.AbstractListEntry<NotifBadgeSlot> {

        protected final GuiScreenNotifications parent;
        protected final NotificationBadge badge;
        protected final List<ClickEventZone> cursorEvents = new ArrayList<>();
        protected int currentScreenX = -69420;
        protected int currentScreenY = -69420;
        protected int currentIndex = -1;

        protected NotifBadgeSlot(GuiScreenNotifications parent, NotificationBadge badge) {
            this.parent = parent;
            this.badge = badge;
        }

        @Override
        public void render(int id, int xx, int yy, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            currentScreenX = xx;
            currentScreenY = yy;
            NotificationBadge bd = badge;
            if (yy + 32 > parent.slots.getTop() && yy + 32 < parent.slots.getBottom()) {
                bd.markRead();
            }
            GlStateManager.pushMatrix();
            GlStateManager.translate(xx, yy, 0.0f);
            Minecraft mc = Minecraft.getInstance();
            mc.getTextureManager().bindTexture(largeNotifBk);
            int badgeWidth = width - 4;
            int badgeHeight = height - 4;
            float r = ((bd.backgroundColor >> 16) & 0xFF) * 0.00392156f;
            float g = ((bd.backgroundColor >> 8) & 0xFF) * 0.00392156f;
            float b = (bd.backgroundColor & 0xFF) * 0.00392156f;
            if (parent.selected != id) {
                r *= 0.85f;
                g *= 0.85f;
                b *= 0.85f;
            }
            GlStateManager.color(r, g, b, 1.0f);
            parent.blit(0, 0, 0, bd.unreadFlagRender ? 64 : 0, badgeWidth - 32, 64);
            parent.blit(badgeWidth - 32, 0, 224, bd.unreadFlagRender ? 64 : 0, 32, 64);
            mc.getTextureManager().bindTexture(eaglerGui);
            if (bd.priority == EnumBadgePriority.LOW) {
                parent.blit(badgeWidth - 21, badgeHeight - 21, 192, 176, 16, 16);
            }
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

            switch (bd.priority) {
            default:
                break;
            case NORMAL:
                parent.blit(badgeWidth - 21, badgeHeight - 21, 208, 176, 16, 16);
                break;
            case HIGHER:
                parent.blit(badgeWidth - 21, badgeHeight - 21, 224, 176, 16, 16);
                break;
            case HIGHEST:
                parent.blit(badgeWidth - 21, badgeHeight - 21, 240, 176, 16, 16);
                break;
            }

            int bodyYOffset = 16;

            int leftPadding = 6;
            int rightPadding = 26;

            int mainIconSW = 32;
            boolean mainIconEn = bd.mainIcon != null && bd.mainIcon.isValid();
            if (mainIconEn) {
                int iw = bd.mainIcon.texture.getWidth();
                int ih = bd.mainIcon.texture.getHeight();
                float iaspect = (float) iw / (float) ih;
                mainIconSW = (int) (32 * iaspect);
                leftPadding += Math.min(mainIconSW, 64) + 3;
            }

            int textZoneWidth = badgeWidth - leftPadding - rightPadding;

            if (mainIconEn) {
                mc.getTextureManager().bindTexture(bd.mainIcon.resource);
                ServerNotificationRenderer.drawTexturedRect(6, bodyYOffset, mainIconSW, 32);
            }

            boolean titleIconEn = bd.titleIcon != null && bd.titleIcon.isValid();
            if (titleIconEn) {
                mc.getTextureManager().bindTexture(bd.titleIcon.resource);
                ServerNotificationRenderer.drawTexturedRect(6, 5, 8, 8);
            }

            String titleText = "";
            ITextComponent titleComponent = bd.getTitleProfanityFilter();
            if (titleComponent != null) {
                titleText = titleComponent.getFormattedText();
            }

            titleText += TextFormatting.GRAY + (titleText.length() > 0 ? " @ " : "@ ")
                    + (bd.unreadFlagRender ? TextFormatting.YELLOW : TextFormatting.GRAY)
                    + formatAge(bd.serverTimestamp);

            GlStateManager.pushMatrix();
            GlStateManager.translate(6 + (titleIconEn ? 10 : 0), 6, 0.0f);
            GlStateManager.scale(0.75f, 0.75f, 0.75f);
            mc.fontRenderer.drawStringWithShadow(titleText, 0, 0, bd.titleTxtColor);
            GlStateManager.popMatrix();

            String sourceText = null;
            ITextComponent sourceComponent = bd.getSourceProfanityFilter();
            if (sourceComponent != null) {
                sourceText = sourceComponent.getFormattedText();
                if (sourceText.length() == 0) {
                    sourceText = null;
                }
            }

            List<ITextComponent> bodyLines = null;
            float bodyFontSize = (sourceText != null || titleIconEn) ? 0.75f : 1.0f;
            ITextComponent bodyComponent = bd.getBodyProfanityFilter();
            if (bodyComponent != null) {
                bodyLines = RenderComponentsUtil.splitText(bodyComponent, (int) (textZoneWidth / bodyFontSize),
                        mc.fontRenderer, true, true);

                int maxHeight = badgeHeight - (sourceText != null ? 32 : 22);
                int maxLines = MathHelper.floor(maxHeight / (9 * bodyFontSize));
                if (bodyLines.size() > maxLines) {
                    bodyLines = bodyLines.subList(0, maxLines);
                    ITextComponent cmp = bodyLines.get(maxLines - 1);
                    List<ITextComponent> siblings = cmp.getSiblings();
                    ITextComponent dots = new StringTextComponent("...");
                    if (siblings != null && siblings.size() > 0) {
                        dots.setStyle(siblings.get(siblings.size() - 1).getStyle());
                    }
                    cmp.appendSibling(dots);
                }
            }

            cursorEvents.clear();
            if (bodyLines != null && !bodyLines.isEmpty()) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(leftPadding, bodyYOffset, 0.0f);
                int l = bodyLines.size();
                GlStateManager.scale(bodyFontSize, bodyFontSize, bodyFontSize);
                ITextComponent toolTip = null;
                for (int i = 0; i < l; ++i) {
                    int startXLocal = 0;
                    int startXReal = leftPadding;
                    for (ITextComponent comp : bodyLines.get(i)) {
                        int w = mc.fontRenderer.drawStringWithShadow(
                                comp.getStyle().getFormattingCode() + comp.getUnformattedComponentText(), startXLocal,
                                i * 9, bd.bodyTxtColor) - startXLocal;
                        ClickEvent clickEvent = comp.getStyle().getClickEvent();
                        HoverEvent hoverEvent = toolTip == null ? comp.getStyle().getHoverEvent() : null;
                        if (clickEvent != null && !clickEvent.getAction().shouldAllowInChat()) {
                            clickEvent = null;
                        }
                        if (hoverEvent != null && !hoverEvent.getAction().shouldAllowInChat()) {
                            hoverEvent = null;
                        }
                        if (clickEvent != null) {
                            cursorEvents.add(new ClickEventZone(startXReal + (int) (startXLocal * bodyFontSize),
                                    bodyYOffset + (int) (i * 9 * bodyFontSize), (int) (w * bodyFontSize),
                                    (int) (9 * bodyFontSize), comp, clickEvent != null, hoverEvent != null));
                        }
                        if (hoverEvent != null) {
                            int px = xx + startXReal + (int) (startXLocal * bodyFontSize);
                            int py = yy + bodyYOffset + (int) (i * 9 * bodyFontSize);
                            if (mouseX >= px && mouseX < px + (int) (w * bodyFontSize) && mouseY >= py
                                    && mouseY < py + (int) (9 * bodyFontSize)) {
                                toolTip = comp;
                            }
                        }
                        startXLocal += w;
                    }
                }
                GlStateManager.popMatrix();
                if (toolTip != null) {
                    parent.handleComponentHover(toolTip, mouseX, mouseY);
                }
            }

            if (sourceText != null) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(badgeWidth - 21, badgeHeight - 5, 0.0f);
                GlStateManager.scale(0.75f, 0.75f, 0.75f);
                mc.fontRenderer.drawStringWithShadow(sourceText, -mc.fontRenderer.getStringWidth(sourceText) - 4, -10, bd.sourceTxtColor);
                GlStateManager.popMatrix();
            }

            GlStateManager.popMatrix();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button != 0) return false;
            if (currentScreenY != -69420 && currentScreenX != -69420) {
                Minecraft mc = Minecraft.getInstance();
                int w = parent.slots.getRowWidth();
                int localX = (int) mouseX - currentScreenX;
                int localY = (int) mouseY - currentScreenY;
                if (localX >= w - 22 && localX < w - 5 && localY >= 5 && localY < 21) {
                    badge.removeNotif();
                    mc.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
                ITextComponent cmp = badge.bodyComponent;
                if (cmp != null) {
                    if (parent.selected != currentIndex) {
                        parent.selected = currentIndex;
                        return true;
                    } else {
                        List<ClickEventZone> cursorEvents = this.cursorEvents;
                        if (cursorEvents != null && !cursorEvents.isEmpty()) {
                            for (int j = 0, m = cursorEvents.size(); j < m; ++j) {
                                ClickEventZone evt = cursorEvents.get(j);
                                if (evt.hasClickEvent) {
                                    int offsetPosX = currentScreenX + evt.posX;
                                    int offsetPosY = currentScreenY + evt.posY;
                                    if (mouseX >= offsetPosX && mouseY >= offsetPosY && mouseX < offsetPosX + evt.width && mouseY < offsetPosY + evt.height) {
                                        if (parent.handleComponentClicked(evt.chatComponent)) {
                                            mc.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return false;
        }
    }

    private static String formatAge(long serverTimestamp) {
        long cur = System.currentTimeMillis();
        long daysAgo = Math.round((cur - serverTimestamp) / 86400000.0);
        String ret = dateFormat.format(new Date(serverTimestamp));
        if (daysAgo > 0l) {
            ret += " (" + daysAgo + (daysAgo == 1l ? " day" : " days") + " ago)";
        } else if (daysAgo < 0l) {
            ret += " (in " + -daysAgo + (daysAgo == -1l ? " day" : " days") + ")";
        }
        return ret;
    }
}
