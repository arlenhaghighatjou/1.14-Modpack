package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.config.InventoryButtonMode;
import net.blay09.mods.waystones.config.WaystoneConfig;
import net.blay09.mods.waystones.container.WaystoneSelectionContainer;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.core.WaystoneManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.blay09.mods.waystones.network.NetworkHandler;

import java.util.Optional;

public class InventoryButtonMessage {

    private static final INamedContainerProvider containerProvider = new INamedContainerProvider() {
        @Override
        public ITextComponent getDisplayName() {
            return new TranslationTextComponent("container.waystones.waystone_selection");
        }

        @Override
        public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
            return new WaystoneSelectionContainer(i, WarpMode.INVENTORY_BUTTON, null);
        }
    };

    public static void encode(InventoryButtonMessage message, PacketBuffer buf) {
    }

    public static InventoryButtonMessage decode(PacketBuffer buf) {
        return new InventoryButtonMessage();
    }

    public static void handle(InventoryButtonMessage message, ServerPlayerEntity player) {
        InventoryButtonMode inventoryButtonMode = WaystoneConfig.getInventoryButtonMode();
        if (!inventoryButtonMode.isEnabled()) {
            return;
        }

        // Reset cooldown if player is in creative mode
        if (player.abilities.isCreativeMode) {
            PlayerWaystoneManager.setInventoryButtonCooldownUntil(player, 0);
        }

        if (!PlayerWaystoneManager.canUseInventoryButton(player)) {
            return;
        }

        if (inventoryButtonMode.isReturnToNearest()) {
            IWaystone nearestWaystone = PlayerWaystoneManager.getNearestWaystone(player);
            if (nearestWaystone != null) {
                PlayerWaystoneManager.tryTeleportToWaystone(player, nearestWaystone, WarpMode.INVENTORY_BUTTON, null);
            }
        } else if (inventoryButtonMode.isReturnToAny()) {
            NetworkHandler.openContainer(player, containerProvider, WarpMode.INVENTORY_BUTTON, null);
        } else if (inventoryButtonMode.hasNamedTarget()) {
            Optional<IWaystone> waystone = WaystoneManager.get().findWaystoneByName(inventoryButtonMode.getNamedTarget());
            waystone.ifPresent(iWaystone -> PlayerWaystoneManager.tryTeleportToWaystone(player, iWaystone, WarpMode.INVENTORY_BUTTON, null));
        }
    }

}
