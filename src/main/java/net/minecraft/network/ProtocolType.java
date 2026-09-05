package net.minecraft.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.login.client.CCustomPayloadLoginPacket;
import net.minecraft.network.login.client.CEncryptionResponsePacket;
import net.minecraft.network.login.client.CLoginStartPacket;
import net.minecraft.network.login.server.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.network.status.client.CPingPacket;
import net.minecraft.network.status.client.CServerQueryPacket;
import net.minecraft.network.status.server.SPongPacket;
import net.minecraft.network.status.server.SServerInfoPacket;
import org.apache.logging.log4j.LogManager;

import java.util.Map;
import java.util.function.Supplier;

public enum ProtocolType {
    HANDSHAKING(-1) {
        {
            this.registerPacket(PacketDirection.SERVERBOUND, CHandshakePacket.class, CHandshakePacket::new);
        }
    },
    PLAY(0) {
        {
            this.registerPacket(PacketDirection.CLIENTBOUND, SSpawnObjectPacket.class, SSpawnObjectPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SSpawnExperienceOrbPacket.class, SSpawnExperienceOrbPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SSpawnGlobalEntityPacket.class, SSpawnGlobalEntityPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SSpawnMobPacket.class, SSpawnMobPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SSpawnPaintingPacket.class, SSpawnPaintingPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SSpawnPlayerPacket.class, SSpawnPlayerPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SAnimateHandPacket.class, SAnimateHandPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SStatisticsPacket.class, SStatisticsPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SAnimateBlockBreakPacket.class, SAnimateBlockBreakPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SUpdateTileEntityPacket.class, SUpdateTileEntityPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SBlockActionPacket.class, SBlockActionPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SChangeBlockPacket.class, SChangeBlockPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SUpdateBossInfoPacket.class, SUpdateBossInfoPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SServerDifficultyPacket.class, SServerDifficultyPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SChatPacket.class, SChatPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SMultiBlockChangePacket.class, SMultiBlockChangePacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, STabCompletePacket.class, STabCompletePacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SCommandListPacket.class, SCommandListPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SConfirmTransactionPacket.class, SConfirmTransactionPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SCloseWindowPacket.class, SCloseWindowPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SWindowItemsPacket.class, SWindowItemsPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SWindowPropertyPacket.class, SWindowPropertyPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SSetSlotPacket.class, SSetSlotPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SCooldownPacket.class, SCooldownPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SCustomPayloadPlayPacket.class, SCustomPayloadPlayPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SPlaySoundPacket.class, SPlaySoundPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SDisconnectPacket.class, SDisconnectPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SEntityStatusPacket.class, SEntityStatusPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SExplosionPacket.class, SExplosionPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SUnloadChunkPacket.class, SUnloadChunkPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SChangeGameStatePacket.class, SChangeGameStatePacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SOpenHorseWindowPacket.class, SOpenHorseWindowPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SKeepAlivePacket.class, SKeepAlivePacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SChunkDataPacket.class, SChunkDataPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SPlaySoundEventPacket.class, SPlaySoundEventPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SSpawnParticlePacket.class, SSpawnParticlePacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SUpdateLightPacket.class, SUpdateLightPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SJoinGamePacket.class, SJoinGamePacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SMapDataPacket.class, SMapDataPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SMerchantOffersPacket.class, SMerchantOffersPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SEntityPacket.RelativeMovePacket.class, SEntityPacket.RelativeMovePacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SEntityPacket.MovePacket.class, SEntityPacket.MovePacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SEntityPacket.LookPacket.class, SEntityPacket.LookPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SEntityPacket.class, SEntityPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SMoveVehiclePacket.class, SMoveVehiclePacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SOpenBookWindowPacket.class, SOpenBookWindowPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SOpenWindowPacket.class, SOpenWindowPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SOpenSignMenuPacket.class, SOpenSignMenuPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SPlaceGhostRecipePacket.class, SPlaceGhostRecipePacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SPlayerAbilitiesPacket.class, SPlayerAbilitiesPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SCombatPacket.class, SCombatPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SPlayerListItemPacket.class, SPlayerListItemPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SPlayerLookPacket.class, SPlayerLookPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SPlayerPositionLookPacket.class, SPlayerPositionLookPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SRecipeBookPacket.class, SRecipeBookPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SDestroyEntitiesPacket.class, SDestroyEntitiesPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SRemoveEntityEffectPacket.class, SRemoveEntityEffectPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SSendResourcePackPacket.class, SSendResourcePackPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SRespawnPacket.class, SRespawnPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SEntityHeadLookPacket.class, SEntityHeadLookPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SSelectAdvancementsTabPacket.class, SSelectAdvancementsTabPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SWorldBorderPacket.class, SWorldBorderPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SCameraPacket.class, SCameraPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SHeldItemChangePacket.class, SHeldItemChangePacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SUpdateChunkPositionPacket.class, SUpdateChunkPositionPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SUpdateViewDistancePacket.class, SUpdateViewDistancePacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SDisplayObjectivePacket.class, SDisplayObjectivePacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SEntityMetadataPacket.class, SEntityMetadataPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SMountEntityPacket.class, SMountEntityPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SEntityVelocityPacket.class, SEntityVelocityPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SEntityEquipmentPacket.class, SEntityEquipmentPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SSetExperiencePacket.class, SSetExperiencePacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SUpdateHealthPacket.class, SUpdateHealthPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SScoreboardObjectivePacket.class, SScoreboardObjectivePacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SSetPassengersPacket.class, SSetPassengersPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, STeamsPacket.class, STeamsPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SUpdateScorePacket.class, SUpdateScorePacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SSpawnPositionPacket.class, SSpawnPositionPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SUpdateTimePacket.class, SUpdateTimePacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, STitlePacket.class, STitlePacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SSpawnMovingSoundEffectPacket.class, SSpawnMovingSoundEffectPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SPlaySoundEffectPacket.class, SPlaySoundEffectPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SStopSoundPacket.class, SStopSoundPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SPlayerListHeaderFooterPacket.class, SPlayerListHeaderFooterPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SQueryNBTResponsePacket.class, SQueryNBTResponsePacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SCollectItemPacket.class, SCollectItemPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SEntityTeleportPacket.class, SEntityTeleportPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SAdvancementInfoPacket.class, SAdvancementInfoPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SEntityPropertiesPacket.class, SEntityPropertiesPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SPlayEntityEffectPacket.class, SPlayEntityEffectPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SUpdateRecipesPacket.class, SUpdateRecipesPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, STagsListPacket.class, STagsListPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SPlayerDiggingPacket.class, SPlayerDiggingPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CConfirmTeleportPacket.class, CConfirmTeleportPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CQueryTileEntityNBTPacket.class, CQueryTileEntityNBTPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CSetDifficultyPacket.class, CSetDifficultyPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CChatMessagePacket.class, CChatMessagePacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CClientStatusPacket.class, CClientStatusPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CClientSettingsPacket.class, CClientSettingsPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CTabCompletePacket.class, CTabCompletePacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CConfirmTransactionPacket.class, CConfirmTransactionPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CEnchantItemPacket.class, CEnchantItemPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CClickWindowPacket.class, CClickWindowPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CCloseWindowPacket.class, CCloseWindowPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CCustomPayloadPacket.class, CCustomPayloadPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CEditBookPacket.class, CEditBookPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CQueryEntityNBTPacket.class, CQueryEntityNBTPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CUseEntityPacket.class, CUseEntityPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CKeepAlivePacket.class, CKeepAlivePacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CLockDifficultyPacket.class, CLockDifficultyPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CPlayerPacket.PositionPacket.class, CPlayerPacket.PositionPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CPlayerPacket.PositionRotationPacket.class, CPlayerPacket.PositionRotationPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CPlayerPacket.RotationPacket.class, CPlayerPacket.RotationPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CPlayerPacket.class, CPlayerPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CMoveVehiclePacket.class, CMoveVehiclePacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CSteerBoatPacket.class, CSteerBoatPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CPickItemPacket.class, CPickItemPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CPlaceRecipePacket.class, CPlaceRecipePacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CPlayerAbilitiesPacket.class, CPlayerAbilitiesPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CPlayerDiggingPacket.class, CPlayerDiggingPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CEntityActionPacket.class, CEntityActionPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CInputPacket.class, CInputPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CRecipeInfoPacket.class, CRecipeInfoPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CRenameItemPacket.class, CRenameItemPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CResourcePackStatusPacket.class, CResourcePackStatusPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CSeenAdvancementsPacket.class, CSeenAdvancementsPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CSelectTradePacket.class, CSelectTradePacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CUpdateBeaconPacket.class, CUpdateBeaconPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CHeldItemChangePacket.class, CHeldItemChangePacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CUpdateCommandBlockPacket.class, CUpdateCommandBlockPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CUpdateMinecartCommandBlockPacket.class, CUpdateMinecartCommandBlockPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CCreativeInventoryActionPacket.class, CCreativeInventoryActionPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CUpdateJigsawBlockPacket.class, CUpdateJigsawBlockPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CUpdateStructureBlockPacket.class, CUpdateStructureBlockPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CUpdateSignPacket.class, CUpdateSignPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CAnimateHandPacket.class, CAnimateHandPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CSpectatePacket.class, CSpectatePacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CPlayerTryUseItemOnBlockPacket.class, CPlayerTryUseItemOnBlockPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CPlayerTryUseItemPacket.class, CPlayerTryUseItemPacket::new);
        }
    },
    STATUS(1) {
        {
            this.registerPacket(PacketDirection.SERVERBOUND, CServerQueryPacket.class, CServerQueryPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SServerInfoPacket.class, SServerInfoPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CPingPacket.class, CPingPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SPongPacket.class, SPongPacket::new);
        }
    },
    LOGIN(2) {
        {
            this.registerPacket(PacketDirection.CLIENTBOUND, SDisconnectLoginPacket.class, SDisconnectLoginPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SEncryptionRequestPacket.class, SEncryptionRequestPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SLoginSuccessPacket.class, SLoginSuccessPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SEnableCompressionPacket.class, SEnableCompressionPacket::new);
            this.registerPacket(PacketDirection.CLIENTBOUND, SCustomPayloadLoginPacket.class, SCustomPayloadLoginPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CLoginStartPacket.class, CLoginStartPacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CEncryptionResponsePacket.class, CEncryptionResponsePacket::new);
            this.registerPacket(PacketDirection.SERVERBOUND, CCustomPayloadLoginPacket.class, CCustomPayloadLoginPacket::new);
        }
    };

    private static final ProtocolType[] STATES_BY_ID = new ProtocolType[4];
    private static final Map<Class<? extends IPacket<?>>, ProtocolType> STATES_BY_CLASS = Maps.newHashMap();
    private final int id;
    private final Map<PacketDirection, BiMap<Integer, Class<? extends IPacket<?>>>> directionMaps = Maps.newEnumMap(PacketDirection.class);
    private final Map<PacketDirection, Map<Integer, Supplier<IPacket<?>>>> supplierMaps = Maps.newEnumMap(PacketDirection.class);

    private ProtocolType(int protocolId) {
        this.id = protocolId;
    }

    protected ProtocolType registerPacket(PacketDirection direction, Class<? extends IPacket<?>> packetClass, Supplier<IPacket<?>> factory) {
        BiMap<Integer, Class<? extends IPacket<?>>> bimap = this.directionMaps.get(direction);
        if (bimap == null) {
            bimap = HashBiMap.create();
            this.directionMaps.put(direction, bimap);
        }

        if (bimap.containsValue(packetClass)) {
            String s = direction + " packet " + packetClass + " is already known to ID " + bimap.inverse().get(packetClass);
            LogManager.getLogger().fatal(s);
            throw new IllegalArgumentException(s);
        } else {
            int id = bimap.size();
            bimap.put(id, packetClass);
            Map<Integer, Supplier<IPacket<?>>> suppliers = this.supplierMaps.get(direction);
            if (suppliers == null) {
                suppliers = Maps.newHashMap();
                this.supplierMaps.put(direction, suppliers);
            }
            suppliers.put(id, factory);
            return this;
        }
    }

    public Integer getPacketId(PacketDirection direction, IPacket<?> packetIn) throws Exception {
        return this.directionMaps.get(direction).inverse().get(packetIn.getClass());
    }

    public IPacket<?> getPacket(PacketDirection direction, int packetId) {
        Map<Integer, Supplier<IPacket<?>>> suppliers = this.supplierMaps.get(direction);
        if (suppliers == null) {
            return null;
        }
        Supplier<IPacket<?>> supplier = suppliers.get(packetId);
        return supplier == null ? null : supplier.get();
    }

    public int getId() {
        return this.id;
    }

    public static ProtocolType getById(int stateId) {
        return stateId >= -1 && stateId <= 2 ? STATES_BY_ID[stateId - -1] : null;
    }

    public static ProtocolType getFromPacket(IPacket<?> packetIn) {
        return STATES_BY_CLASS.get(packetIn.getClass());
    }

    static {
        for (ProtocolType protocoltype : values()) {
            int i = protocoltype.getId();
            if (i < -1 || i > 2) {
                throw new Error("Invalid protocol ID " + Integer.toString(i));
            }

            STATES_BY_ID[i - -1] = protocoltype;

            for (PacketDirection packetdirection : protocoltype.directionMaps.keySet()) {
                for (Class<? extends IPacket<?>> oclass : protocoltype.directionMaps.get(packetdirection).values()) {
                    if (STATES_BY_CLASS.containsKey(oclass) && STATES_BY_CLASS.get(oclass) != protocoltype) {
                        throw new Error("Packet " + oclass + " is already assigned to protocol " + STATES_BY_CLASS.get(oclass) + " - can't reassign to " + protocoltype);
                    }

                    STATES_BY_CLASS.put(oclass, protocoltype);
                }
            }
        }

    }
}
