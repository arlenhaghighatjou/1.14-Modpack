package net.lax1dude.eaglercraft.sp.server;

import com.mojang.datafixers.DataFixer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.chunk.storage.ChunkLoader;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;

public class EaglerSaveHandler extends SaveHandler {

    public EaglerSaveHandler(net.lax1dude.eaglercraft.internal.vfs2.VFile2 worldDir, String saveDirectoryNameIn, MinecraftServer server,
                             DataFixer dataFixerIn) {
        super(worldDir, saveDirectoryNameIn, server, dataFixerIn);
    }

    public ChunkLoader getChunkLoader(DimensionType provider) {
        return new EaglerChunkLoader(
                new net.lax1dude.eaglercraft.internal.vfs2.VFile2(this.getWorldDirectory(), "level" + provider.getId()), this.getFixer());
    }

    @Override
    public void saveWorldInfoWithPlayer(WorldInfo worldInformation, CompoundNBT tagCompound) {
        worldInformation.setSaveVersion(19133);
        super.saveWorldInfoWithPlayer(worldInformation, tagCompound);
    }

    @Override
    public void saveWorldInfo(WorldInfo worldInformation) {
        worldInformation.setSaveVersion(19133);
        super.saveWorldInfo(worldInformation);
    }
}
