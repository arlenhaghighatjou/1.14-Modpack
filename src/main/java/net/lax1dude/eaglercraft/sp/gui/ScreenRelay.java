package net.lax1dude.eaglercraft.sp.gui;

import net.lax1dude.eaglercraft.sp.relay.RelayManager;
import net.lax1dude.eaglercraft.sp.relay.RelayServer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ScreenRelay extends Screen {

    private final Screen parent;
    private RelaySlotList slots;
    private int selected = -1;

    private Button deleteRelay;
    private Button setPrimary;

    public ScreenRelay(Screen parent) {
        super(new StringTextComponent("Relay Network Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.selected = -1;
        this.slots = new RelaySlotList(this, net.minecraft.client.Minecraft.getInstance(), this.width, this.height, 32, this.height - 64, 36);
        this.children.add(this.slots);

        this.addButton(new Button(this.width / 2 + 54, this.height - 28, 100, 20, I18n.format("gui.done"), (btn) -> {
            RelayManager.relayManager.save();
            net.minecraft.client.Minecraft.getInstance().displayGuiScreen(parent);
        }));

        this.addButton(new Button(this.width / 2 - 154, this.height - 52, 100, 20, I18n.format("networkSettings.add"), (btn) -> {
            net.minecraft.client.Minecraft.getInstance().displayGuiScreen(new ScreenAddRelay(this));
        }));

        this.deleteRelay = this.addButton(new Button(this.width / 2 - 50, this.height - 52, 100, 20, I18n.format("networkSettings.delete"), (btn) -> {
            if (this.selected >= 0) {
                RelayServer srv = RelayManager.relayManager.get(this.selected);
                net.minecraft.client.Minecraft.getInstance().displayGuiScreen(new ConfirmScreen((confirm) -> {
                    if (confirm) {
                        RelayManager.relayManager.remove(this.selected);
                        RelayManager.relayManager.save();
                    }
                    net.minecraft.client.Minecraft.getInstance().displayGuiScreen(this);
                }, new TranslationTextComponent("networkSettings.delete"), new StringTextComponent(I18n.format("addRelay.removeText1") + " '" + srv.comment + "' (" + srv.address + ")")));
            }
        }));

        this.setPrimary = this.addButton(new Button(this.width / 2 + 54, this.height - 52, 100, 20, I18n.format("networkSettings.default"), (btn) -> {
            if (this.selected >= 0) {
                RelayManager.relayManager.setPrimary(this.selected);
                this.selected = 0;
                this.slots.updateRelays();
            }
        }));

        this.addButton(new Button(this.width / 2 - 50, this.height - 28, 100, 20, I18n.format("networkSettings.refresh"), (btn) -> {
            RelayManager.relayManager.ping();
        }));

        this.addButton(new Button(this.width / 2 - 154, this.height - 28, 100, 20, I18n.format("networkSettings.loadDefaults"), (btn) -> {
            RelayManager.relayManager.loadDefaults();
            RelayManager.relayManager.ping();
            this.slots.updateRelays();
        }));

        this.addButton(new Button(this.width - 100, 0, 100, 20, I18n.format("networkSettings.downloadRelay"), (btn) -> {
            // Unimplemented for now
        }));

        RelayManager.relayManager.ping();
        updateButtons();
    }

    public void setSelected(int sel) {
        this.selected = sel;
        updateButtons();
    }

    private void updateButtons() {
        if (this.selected < 0) {
            this.deleteRelay.active = false;
            this.setPrimary.active = false;
        } else {
            this.deleteRelay.active = true;
            this.setPrimary.active = true;
        }
    }

    public void tick() {
        super.tick();
        RelayManager.relayManager.update();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        this.slots.render(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.font, I18n.format("networkSettings.title"), this.width / 2, 16, 16777215);
        super.render(mouseX, mouseY, partialTicks);
    }
}
