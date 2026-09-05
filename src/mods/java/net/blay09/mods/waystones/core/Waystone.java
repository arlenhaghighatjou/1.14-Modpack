package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.EaglercraftUUID;

public class Waystone implements IWaystone {

    private final EaglercraftUUID waystoneUid;
    private final DimensionType dimensionType;
    private final BlockPos pos;
    private final boolean wasGenerated;

    private String name;
    private boolean isGlobal;
    private EaglercraftUUID ownerUid;

    public Waystone(EaglercraftUUID waystoneUid, DimensionType dimensionType, BlockPos pos, boolean wasGenerated, @Nullable EaglercraftUUID ownerUid) {
        this.waystoneUid = waystoneUid;
        this.dimensionType = dimensionType;
        this.pos = pos;
        this.wasGenerated = wasGenerated;
        this.ownerUid = ownerUid;
    }

    @Override
    public EaglercraftUUID getWaystoneUid() {
        return waystoneUid;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public DimensionType getDimensionType() {
        return dimensionType;
    }

    @Override
    public boolean wasGenerated() {
        return wasGenerated;
    }

    @Override
    public boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    @Override
    public boolean isOwner(PlayerEntity player) {
        return ownerUid == null || player.getGameProfile().getId().equals(ownerUid) || player.abilities.isCreativeMode;
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public EaglercraftUUID getOwnerUid() {
        return ownerUid;
    }

}
