package net.lax1dude.eaglercraft.sp.server;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.management.PlayerList;

public class EaglerPlayerList extends PlayerList {

    private CompoundNBT hostPlayerData = null;

    public EaglerPlayerList(EaglerMinecraftServer server, int viewDistance) {
        super(server, 20);
        this.setViewDistance(Math.max(2, Math.min(viewDistance, 32)));
    }

    @Override
    protected void writePlayerData(ServerPlayerEntity playerIn) {
        
        this.hostPlayerData = playerIn.writeWithoutTypeId(new CompoundNBT());
        super.writePlayerData(playerIn);
    }

    @Override
    public CompoundNBT getHostPlayerData() {
        return this.hostPlayerData;
    }

}
