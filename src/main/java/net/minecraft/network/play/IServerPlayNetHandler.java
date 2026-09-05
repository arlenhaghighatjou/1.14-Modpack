package net.minecraft.network.play;

import net.minecraft.network.INetHandler;
import net.minecraft.network.play.client.*;

public interface IServerPlayNetHandler extends INetHandler {
    void handleAnimation(CAnimateHandPacket packetIn);

    void processChatMessage(CChatMessagePacket packetIn);

    void processClientStatus(CClientStatusPacket packetIn);

    void processClientSettings(CClientSettingsPacket packetIn);

    void processConfirmTransaction(CConfirmTransactionPacket packetIn);

    void processEnchantItem(CEnchantItemPacket packetIn);

    void processClickWindow(CClickWindowPacket packetIn);

    void processPlaceRecipe(CPlaceRecipePacket packetIn);

    void processCloseWindow(CCloseWindowPacket packetIn);

    void processCustomPayload(CCustomPayloadPacket packetIn);

    void processUseEntity(CUseEntityPacket packetIn);

    void processKeepAlive(CKeepAlivePacket packetIn);

    void processPlayer(CPlayerPacket packetIn);

    void processPlayerAbilities(CPlayerAbilitiesPacket packetIn);

    void processPlayerDigging(CPlayerDiggingPacket packetIn);

    void processEntityAction(CEntityActionPacket packetIn);

    void processInput(CInputPacket packetIn);

    void processHeldItemChange(CHeldItemChangePacket packetIn);

    void processCreativeInventoryAction(CCreativeInventoryActionPacket packetIn);

    void processUpdateSign(CUpdateSignPacket packetIn);

    void processTryUseItemOnBlock(CPlayerTryUseItemOnBlockPacket packetIn);

    void processTryUseItem(CPlayerTryUseItemPacket packetIn);

    void handleSpectate(CSpectatePacket packetIn);

    void handleResourcePackStatus(CResourcePackStatusPacket packetIn);

    void processSteerBoat(CSteerBoatPacket packetIn);

    void processVehicleMove(CMoveVehiclePacket packetIn);

    void processConfirmTeleport(CConfirmTeleportPacket packetIn);

    void handleRecipeBookUpdate(CRecipeInfoPacket packetIn);

    void handleSeenAdvancements(CSeenAdvancementsPacket packetIn);

    void processTabComplete(CTabCompletePacket packetIn);

    void processUpdateCommandBlock(CUpdateCommandBlockPacket packetIn);

    void processUpdateCommandMinecart(CUpdateMinecartCommandBlockPacket packetIn);

    void processPickItem(CPickItemPacket packetIn);

    void processRenameItem(CRenameItemPacket packetIn);

    void processUpdateBeacon(CUpdateBeaconPacket packetIn);

    void processUpdateStructureBlock(CUpdateStructureBlockPacket packetIn);

    void processSelectTrade(CSelectTradePacket packetIn);

    void processEditBook(CEditBookPacket packetIn);

    void processNBTQueryEntity(CQueryEntityNBTPacket packetIn);

    void processNBTQueryBlockEntity(CQueryTileEntityNBTPacket packetIn);

    void func_217262_a(CUpdateJigsawBlockPacket p_217262_1_);

    void func_217263_a(CSetDifficultyPacket p_217263_1_);

    void func_217261_a(CLockDifficultyPacket p_217261_1_);
}
