package net.lax1dude.eaglercraft.sp.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.lax1dude.eaglercraft.sp.relay.RelayManager;
import net.lax1dude.eaglercraft.sp.relay.RelayServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class RelaySlotList extends ExtendedList<RelaySlotList.Entry> {

    private final ScreenRelay screen;
    public final RelayManager relayManager;

    public RelaySlotList(ScreenRelay screen, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.screen = screen;
        this.relayManager = RelayManager.relayManager;
        this.updateRelays();
    }

    public void updateRelays() {
        this.clearEntries();
        for (int i = 0; i < relayManager.count(); i++) {
            this.addEntry(new RelaySlotList.RelayEntry(this, this.screen, relayManager.get(i), i));
        }
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 30;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 85;
    }

    @Override
    protected boolean isFocused() {
        return this.screen.getFocused() == this;
    }

    public abstract static class Entry extends ExtendedList.AbstractListEntry<RelaySlotList.Entry> {
    }

    public static class RelayEntry extends RelaySlotList.Entry {
        private final RelaySlotList list;
        private final ScreenRelay screen;
        private final RelayServer server;
        private final int index;
        private final Minecraft mc = Minecraft.getInstance();
        private long lastClickTime;

        public RelayEntry(RelaySlotList list, ScreenRelay screen, RelayServer server, int index) {
            this.list = list;
            this.screen = screen;
            this.server = server;
            this.index = index;
        }

        @Override
        public void render(int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
            String title = this.server.comment;
            String addr = this.server.address;

            this.mc.fontRenderer.drawString(title, (float)(left + 32 + 3), (float)(top + 1), 16777215);
            this.mc.fontRenderer.drawString(addr, (float)(left + 32 + 3), (float)(top + 12), 8421504);

            String pingStatus = "";
            int color = 8421504;
            long ping = this.server.getPing();
            if (ping > 0) {
                pingStatus = ping + "ms";
                if (ping < 150) color = 0x00FF00;
                else if (ping < 300) color = 0xFFFF00;
                else color = 0xFF0000;
            } else if (ping == 0) {
                pingStatus = "...";
            } else {
                pingStatus = "Offline";
                color = 0xFF0000;
            }

            int pingWidth = this.mc.fontRenderer.getStringWidth(pingStatus);
            this.mc.fontRenderer.drawString(pingStatus, (float)(left + width - pingWidth - 5), (float)(top + 1), color);

            if (this.server.isPrimary()) {
                this.mc.fontRenderer.drawString("[*]", (float)(left + width - pingWidth - 25), (float)(top + 1), 0x00FFFF);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            this.screen.setSelected(this.index);
            this.list.setSelected(this);
            if (Util.milliTime() - this.lastClickTime < 250L) {
                // Double click? Maybe toggle primary or edit
            }
            this.lastClickTime = Util.milliTime();
            return true;
        }
    }
}
