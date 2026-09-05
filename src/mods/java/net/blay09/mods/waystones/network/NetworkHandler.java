package net.blay09.mods.waystones.network;

import io.netty.buffer.Unpooled;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.network.message.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.util.ResourceLocation;

public class NetworkHandler {

    public static final ResourceLocation CHANNEL = new ResourceLocation(Waystones.MOD_ID, "network");

    private static final int PLAYER_KNOWN_WAYSTONES = 0;
    private static final int INVENTORY_BUTTON = 1;
    private static final int EDIT_WAYSTONE = 2;
    private static final int SELECT_WAYSTONE = 3;
    private static final int TELEPORT_EFFECT = 4;
    private static final int SORT_WAYSTONE = 5;
    private static final int REMOVE_WAYSTONE = 6;
    private static final int REQUEST_EDIT_WAYSTONE = 7;
    private static final int PLAYER_WAYSTONE_COOLDOWNS = 8;
    private static final int OPEN_WAYSTONE_CONTAINER = 9;

    public static void sendTo(Object message, PlayerEntity player) {
        PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
        if (message instanceof PlayerKnownWaystonesMessage) {
            buf.writeVarInt(PLAYER_KNOWN_WAYSTONES);
            PlayerKnownWaystonesMessage.encode((PlayerKnownWaystonesMessage) message, buf);
        } else if (message instanceof TeleportEffectMessage) {
            buf.writeVarInt(TELEPORT_EFFECT);
            TeleportEffectMessage.encode((TeleportEffectMessage) message, buf);
        } else if (message instanceof PlayerWaystoneCooldownsMessage) {
            buf.writeVarInt(PLAYER_WAYSTONE_COOLDOWNS);
            PlayerWaystoneCooldownsMessage.encode((PlayerWaystoneCooldownsMessage) message, buf);
        } else if (message instanceof OpenWaystoneContainerMessage) {
            buf.writeVarInt(OPEN_WAYSTONE_CONTAINER);
            OpenWaystoneContainerMessage.encode((OpenWaystoneContainerMessage) message, buf);
        } else {
            return;
        }

        ((ServerPlayerEntity) player).connection.sendPacket(new SCustomPayloadPlayPacket(CHANNEL, buf));
    }

    public static void openContainer(ServerPlayerEntity player, net.minecraft.inventory.container.INamedContainerProvider provider, WarpMode warpMode, net.minecraft.util.math.BlockPos pos) {
        sendTo(new OpenWaystoneContainerMessage(warpMode, pos), player);
        player.openContainer(provider);
    }

    public static void sendToServer(Object message) {
        PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
        if (message instanceof InventoryButtonMessage) {
            buf.writeVarInt(INVENTORY_BUTTON);
            InventoryButtonMessage.encode((InventoryButtonMessage) message, buf);
        } else if (message instanceof EditWaystoneMessage) {
            buf.writeVarInt(EDIT_WAYSTONE);
            EditWaystoneMessage.encode((EditWaystoneMessage) message, buf);
        } else if (message instanceof SelectWaystoneMessage) {
            buf.writeVarInt(SELECT_WAYSTONE);
            SelectWaystoneMessage.encode((SelectWaystoneMessage) message, buf);
        } else if (message instanceof SortWaystoneMessage) {
            buf.writeVarInt(SORT_WAYSTONE);
            SortWaystoneMessage.encode((SortWaystoneMessage) message, buf);
        } else if (message instanceof RemoveWaystoneMessage) {
            buf.writeVarInt(REMOVE_WAYSTONE);
            RemoveWaystoneMessage.encode((RemoveWaystoneMessage) message, buf);
        } else if (message instanceof RequestEditWaystoneMessage) {
            buf.writeVarInt(REQUEST_EDIT_WAYSTONE);
            RequestEditWaystoneMessage.encode((RequestEditWaystoneMessage) message, buf);
        } else {
            return;
        }

        Minecraft.getInstance().getConnection().sendPacket(new CCustomPayloadPacket(CHANNEL, buf));
    }

    public static void handleClient(PacketBuffer buf) {
        switch (buf.readVarInt()) {
            case PLAYER_KNOWN_WAYSTONES:
                PlayerKnownWaystonesMessage.handle(PlayerKnownWaystonesMessage.decode(buf));
                break;
            case TELEPORT_EFFECT:
                TeleportEffectMessage.handle(TeleportEffectMessage.decode(buf));
                break;
            case PLAYER_WAYSTONE_COOLDOWNS:
                PlayerWaystoneCooldownsMessage.handle(PlayerWaystoneCooldownsMessage.decode(buf));
                break;
            case OPEN_WAYSTONE_CONTAINER:
                OpenWaystoneContainerMessage.handle(OpenWaystoneContainerMessage.decode(buf));
                break;
        }
    }

    public static void handleServer(PacketBuffer buf, ServerPlayerEntity player) {
        if (player == null) {
            return;
        }

        switch (buf.readVarInt()) {
            case INVENTORY_BUTTON:
                InventoryButtonMessage.handle(InventoryButtonMessage.decode(buf), player);
                break;
            case EDIT_WAYSTONE:
                EditWaystoneMessage.handle(EditWaystoneMessage.decode(buf), player);
                break;
            case SELECT_WAYSTONE:
                SelectWaystoneMessage.handle(SelectWaystoneMessage.decode(buf), player);
                break;
            case SORT_WAYSTONE:
                SortWaystoneMessage.handle(SortWaystoneMessage.decode(buf), player);
                break;
            case REMOVE_WAYSTONE:
                RemoveWaystoneMessage.handle(RemoveWaystoneMessage.decode(buf), player);
                break;
            case REQUEST_EDIT_WAYSTONE:
                RequestEditWaystoneMessage.handle(RequestEditWaystoneMessage.decode(buf), player);
                break;
        }
    }
}
