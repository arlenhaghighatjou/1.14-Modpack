package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.EaglercraftUUID;

public class InvalidWaystone implements IWaystone {

    public static final IWaystone INSTANCE = new InvalidWaystone();

    @Override
    public boolean isValid() {
        return false;
    }

    @Nullable
    @Override
    public EaglercraftUUID getOwnerUid() {
        return null;
    }

    @Override
    public EaglercraftUUID getWaystoneUid() {
        return EaglercraftUUID.randomUUID();
    }

    @Override
    public String getName() {
        return "invalid";
    }

    @Override
    public DimensionType getDimensionType() {
        return DimensionType.OVERWORLD;
    }

    @Override
    public boolean wasGenerated() {
        return false;
    }

    @Override
    public boolean isGlobal() {
        return false;
    }

    @Override
    public boolean isOwner(PlayerEntity player) {
        return false;
    }

    @Override
    public BlockPos getPos() {
        return BlockPos.ZERO;
    }

}
