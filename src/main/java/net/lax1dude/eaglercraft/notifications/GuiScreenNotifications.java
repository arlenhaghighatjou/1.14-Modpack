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

import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketNotifBadgeShowV4EAG.EnumBadgePriority;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiScreenNotifications extends Screen {

    private static final String[] priorityLangKeys = new String[]{
            "notifications.priority.low",
            "notifications.priority.normal",
            "notifications.priority.higher",
            "notifications.priority.highest"
    };

    private static final int[] priorityOrder = new int[]{
            0, 3, 2, 1
    };

    final Screen parent;
    int selected;
    GuiSlotNotifications slots;
    Button clearAllButton;
    Button priorityButton;
    int showPriority = 0;
    EnumBadgePriority selectedMaxPriority = EnumBadgePriority.LOW;
    int lastUpdate = -1;

    public GuiScreenNotifications(Screen parent) {
        super(new TranslationTextComponent("notifications.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        selected = -1;
        this.addButton(new Button(this.width / 2 + 54, this.height - 32, 100, 20, I18n.format("gui.done"), (btn) -> {
            this.mc.displayGuiScreen(parent);
        }));
        this.addButton(clearAllButton = new Button(this.width / 2 - 154, this.height - 32, 100, 20, I18n.format("notifications.clearAll"), (btn) -> {
            if (this.mc.player != null) {
                ServerNotificationManager mgr = this.mc.player.connection.getNotifManager();
                mgr.removeAllNotifFromActiveList(mgr.getNotifLongHistory());
                clearAllButton.active = false;
            }
        }));
        int i = priorityOrder[showPriority];
        this.addButton(priorityButton = new Button(this.width / 2 - 50, this.height - 32, 100, 20,
                I18n.format("notifications.priority", I18n.format(priorityLangKeys[i])), (btn) -> {
            showPriority = (showPriority + 1) & 3;
            int i1 = priorityOrder[showPriority];
            priorityButton.setMessage(I18n.format("notifications.priority", I18n.format(priorityLangKeys[i1])));
            selectedMaxPriority = EnumBadgePriority.getByID(i1);
            updateList();
        }));
        selectedMaxPriority = EnumBadgePriority.getByID(i);
        slots = new GuiSlotNotifications(this);
        this.children.add(slots);
        lastUpdate = -69420;
        updateList();
        updateButtons();
    }

    void updateButtons() {
        clearAllButton.active = !slots.children().isEmpty();
    }

    void updateList() {
        if (this.mc.player == null) return;
        ServerNotificationManager mgr = this.mc.player.connection.getNotifManager();
        int verHash = showPriority | (mgr.getNotifListUpdateCount() << 2);
        if (verHash != lastUpdate) {
            lastUpdate = verHash;
            EaglercraftUUID selectedUUID = null;
            int oldSelectedId = selected;
            if (oldSelectedId >= 0 && oldSelectedId < slots.children().size()) {
                selectedUUID = slots.children().get(oldSelectedId).badge.badgeUUID;
            }
            slots.clearSlots();
            mgr.getNotifLongHistory().stream().filter((input) -> input.priority.priority >= priorityOrder[showPriority])
                    .map((badge) -> new GuiSlotNotifications.NotifBadgeSlot(this, badge)).forEach(slots::addSlot);
            selected = -1;
            if (selectedUUID != null) {
                for (int i = 0, l = slots.children().size(); i < l; ++i) {
                    if (selectedUUID.equals(slots.children().get(i).badge.badgeUUID)) {
                        selected = i;
                        break;
                    }
                }
            }
            if (selected != -1) {
                if (oldSelectedId != selected) {
                    slots.ensureVisibleSlot(slots.children().get(selected));
                }
            }
            updateButtons();
        }
    }

    @Override
    public void tick() {
        if (this.mc.player == null) {
            this.mc.displayGuiScreen(parent);
            return;
        }
        updateList();
    }

    @Override
    public void render(int mx, int my, float partialTicks) {
        if (this.mc.player == null) return;
        slots.render(mx, my, partialTicks);
        this.drawCenteredString(font, I18n.format("notifications.title"), this.width / 2, 16, 16777215);
        super.render(mx, my, partialTicks);
    }

    @Override
    public void removed() {
        if (this.mc.player != null) {
            this.mc.player.connection.getNotifManager().commitUnreadFlag();
        }
    }

    public void handleComponentHover(ITextComponent component, int x, int y) {
        this.renderComponentHoverEffect(component, x, y);
    }
}
