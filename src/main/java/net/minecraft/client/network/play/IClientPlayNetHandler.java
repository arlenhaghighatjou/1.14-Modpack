package net.minecraft.client.network.play;

import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.*;

public interface IClientPlayNetHandler extends INetHandler {
    void handleSpawnObject(SSpawnObjectPacket packetIn);

    void handleSpawnExperienceOrb(SSpawnExperienceOrbPacket packetIn);

    void handleSpawnGlobalEntity(SSpawnGlobalEntityPacket packetIn);

    void handleSpawnMob(SSpawnMobPacket packetIn);

    void handleScoreboardObjective(SScoreboardObjectivePacket packetIn);

    void handleSpawnPainting(SSpawnPaintingPacket packetIn);

    void handleSpawnPlayer(SSpawnPlayerPacket packetIn);

    void handleAnimation(SAnimateHandPacket packetIn);

    void handleStatistics(SStatisticsPacket packetIn);

    void handleRecipeBook(SRecipeBookPacket packetIn);

    void handleBlockBreakAnim(SAnimateBlockBreakPacket packetIn);

    void handleSignEditorOpen(SOpenSignMenuPacket packetIn);

    void handleUpdateTileEntity(SUpdateTileEntityPacket packetIn);

    void handleBlockAction(SBlockActionPacket packetIn);

    void handleBlockChange(SChangeBlockPacket packetIn);

    void handleChat(SChatPacket packetIn);

    void handleMultiBlockChange(SMultiBlockChangePacket packetIn);

    void handleMaps(SMapDataPacket packetIn);

    void handleConfirmTransaction(SConfirmTransactionPacket packetIn);

    void handleCloseWindow(SCloseWindowPacket packetIn);

    void handleWindowItems(SWindowItemsPacket packetIn);

    void func_217271_a(SOpenHorseWindowPacket p_217271_1_);

    void handleWindowProperty(SWindowPropertyPacket packetIn);

    void handleSetSlot(SSetSlotPacket packetIn);

    void handleCustomPayload(SCustomPayloadPlayPacket packetIn);

    void handleDisconnect(SDisconnectPacket packetIn);

    void handleEntityStatus(SEntityStatusPacket packetIn);

    void handleEntityAttach(SMountEntityPacket packetIn);

    void handleSetPassengers(SSetPassengersPacket packetIn);

    void handleExplosion(SExplosionPacket packetIn);

    void handleChangeGameState(SChangeGameStatePacket packetIn);

    void handleKeepAlive(SKeepAlivePacket packetIn);

    void handleChunkData(SChunkDataPacket packetIn);

    void processChunkUnload(SUnloadChunkPacket packetIn);

    void handleEffect(SPlaySoundEventPacket packetIn);

    void handleJoinGame(SJoinGamePacket packetIn);

    void handleEntityMovement(SEntityPacket packetIn);

    void handlePlayerPosLook(SPlayerPositionLookPacket packetIn);

    void handleParticles(SSpawnParticlePacket packetIn);

    void handlePlayerAbilities(SPlayerAbilitiesPacket packetIn);

    void handlePlayerListItem(SPlayerListItemPacket packetIn);

    void handleDestroyEntities(SDestroyEntitiesPacket packetIn);

    void handleRemoveEntityEffect(SRemoveEntityEffectPacket packetIn);

    void handleRespawn(SRespawnPacket packetIn);

    void handleEntityHeadLook(SEntityHeadLookPacket packetIn);

    void handleHeldItemChange(SHeldItemChangePacket packetIn);

    void handleDisplayObjective(SDisplayObjectivePacket packetIn);

    void handleEntityMetadata(SEntityMetadataPacket packetIn);

    void handleEntityVelocity(SEntityVelocityPacket packetIn);

    void handleEntityEquipment(SEntityEquipmentPacket packetIn);

    void handleSetExperience(SSetExperiencePacket packetIn);

    void handleUpdateHealth(SUpdateHealthPacket packetIn);

    void handleTeams(STeamsPacket packetIn);

    void handleUpdateScore(SUpdateScorePacket packetIn);

    void handleSpawnPosition(SSpawnPositionPacket packetIn);

    void handleTimeUpdate(SUpdateTimePacket packetIn);

    void handleSoundEffect(SPlaySoundEffectPacket packetIn);

    void func_217266_a(SSpawnMovingSoundEffectPacket p_217266_1_);

    void handleCustomSound(SPlaySoundPacket packetIn);

    void handleCollectItem(SCollectItemPacket packetIn);

    void handleEntityTeleport(SEntityTeleportPacket packetIn);

    void handleEntityProperties(SEntityPropertiesPacket packetIn);

    void handleEntityEffect(SPlayEntityEffectPacket packetIn);

    void handleTags(STagsListPacket packetIn);

    void handleCombatEvent(SCombatPacket packetIn);

    void handleServerDifficulty(SServerDifficultyPacket packetIn);

    void handleCamera(SCameraPacket packetIn);

    void handleWorldBorder(SWorldBorderPacket packetIn);

    void handleTitle(STitlePacket packetIn);

    void handlePlayerListHeaderFooter(SPlayerListHeaderFooterPacket packetIn);

    void handleResourcePack(SSendResourcePackPacket packetIn);

    void handleUpdateBossInfo(SUpdateBossInfoPacket packetIn);

    void handleCooldown(SCooldownPacket packetIn);

    void handleMoveVehicle(SMoveVehiclePacket packetIn);

    void handleAdvancementInfo(SAdvancementInfoPacket packetIn);

    void handleSelectAdvancementsTab(SSelectAdvancementsTabPacket packetIn);

    void handlePlaceGhostRecipe(SPlaceGhostRecipePacket packetIn);

    void handleCommandList(SCommandListPacket packetIn);

    void handleStopSound(SStopSoundPacket packetIn);

    void handleTabComplete(STabCompletePacket packetIn);

    void handleUpdateRecipes(SUpdateRecipesPacket packetIn);

    void handlePlayerLook(SPlayerLookPacket packetIn);

    void handleNBTQueryResponse(SQueryNBTResponsePacket packetIn);

    void handleUpdateLight(SUpdateLightPacket packetIn);

    void func_217268_a(SOpenBookWindowPacket p_217268_1_);

    void func_217272_a(SOpenWindowPacket p_217272_1_);

    void func_217273_a(SMerchantOffersPacket p_217273_1_);

    void func_217270_a(SUpdateViewDistancePacket p_217270_1_);

    void func_217267_a(SUpdateChunkPositionPacket p_217267_1_);

    void func_225312_a(SPlayerDiggingPacket p_225312_1_);
}
