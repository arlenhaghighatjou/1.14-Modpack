package net.blay09.mods.waystones.client;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.client.gui.screen.InventoryButtonReturnConfirmScreen;
import net.blay09.mods.waystones.client.gui.widget.WaystoneInventoryButton;
import net.blay09.mods.waystones.config.InventoryButtonMode;
import net.blay09.mods.waystones.config.WaystoneConfig;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.InventoryButtonMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class InventoryButtonGuiHandler {

    private static WaystoneInventoryButton buttonWarp;

    public static void onInitGui(InventoryScreen screen) {
        buttonWarp = null;

        InventoryButtonMode inventoryButtonMode = WaystoneConfig.getInventoryButtonMode();
        if (!inventoryButtonMode.isEnabled()) {
            return;
        }

        buttonWarp = new WaystoneInventoryButton(screen, button -> {
            Minecraft mc = Minecraft.getInstance();
            PlayerEntity player = mc.player;

            // Reset cooldown if player is in creative mode
            if (player.abilities.isCreativeMode) {
                PlayerWaystoneManager.setInventoryButtonCooldownUntil(player, 0);
            }

            if (PlayerWaystoneManager.canUseInventoryButton(player)) {
                if (inventoryButtonMode.hasNamedTarget()) {
                    mc.displayGuiScreen(new InventoryButtonReturnConfirmScreen(inventoryButtonMode.getNamedTarget()));
                } else if (inventoryButtonMode.isReturnToNearest()) {
                    if (PlayerWaystoneManager.getNearestWaystone(player) != null) {
                        mc.displayGuiScreen(new InventoryButtonReturnConfirmScreen());
                    }
                } else if (inventoryButtonMode.isReturnToAny()) {
                    NetworkHandler.sendToServer(new InventoryButtonMessage());
                }
            } else {
                mc.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 0.5f));
            }
        });
        screen.addInventoryButton(buttonWarp);
    }

    public static void onDrawScreen(InventoryScreen screen, int mouseX, int mouseY) {
        // Render the inventory button tooltip when it's hovered
        if (buttonWarp != null && buttonWarp.isHovered()) {
            InventoryButtonMode inventoryButtonMode = WaystoneConfig.getInventoryButtonMode();
            List<String> tooltip = new ArrayList<>();
            long timeLeft = PlayerWaystoneManager.getInventoryButtonCooldownLeft(Minecraft.getInstance().player);
            int secondsLeft = (int) (timeLeft / 1000);
            if (inventoryButtonMode.hasNamedTarget()) {
                tooltip.add(TextFormatting.YELLOW + I18n.format("gui.waystones.inventory.return_to_waystone"));
                tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones.bound_to", TextFormatting.DARK_AQUA + inventoryButtonMode.getNamedTarget()));
                if (secondsLeft > 0) {
                    tooltip.add("");
                }
            } else if (inventoryButtonMode.isReturnToNearest()) {
                tooltip.add(TextFormatting.YELLOW + I18n.format("gui.waystones.inventory.return_to_nearest_waystone"));
                IWaystone nearestWaystone = PlayerWaystoneManager.getNearestWaystone(Minecraft.getInstance().player);
                if (nearestWaystone != null) {
                    tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones.bound_to", TextFormatting.DARK_AQUA + nearestWaystone.getName()));
                } else {
                    tooltip.add(TextFormatting.RED + I18n.format("gui.waystones.inventory.no_waystones_activated"));
                }
                if (secondsLeft > 0) {
                    tooltip.add("");
                }
            } else if (inventoryButtonMode.isReturnToAny()) {
                tooltip.add(TextFormatting.YELLOW + I18n.format("gui.waystones.inventory.return_to_waystone"));
                if (PlayerWaystoneManager.getWaystones(Minecraft.getInstance().player).isEmpty()) {
                    tooltip.add(TextFormatting.RED + I18n.format("gui.waystones.inventory.no_waystones_activated"));
                }
            }

            if (secondsLeft > 0) {
                tooltip.add(TextFormatting.GOLD + I18n.format("tooltip.waystones.cooldown_left", secondsLeft));
            }

            screen.renderTooltip(tooltip, mouseX, mouseY);
        }
    }

}
