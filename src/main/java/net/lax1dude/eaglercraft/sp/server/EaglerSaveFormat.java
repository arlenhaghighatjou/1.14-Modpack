package net.lax1dude.eaglercraft.sp.server;

import com.mojang.datafixers.DataFixer;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldSummary;
import net.peyton.eagler.fs.FileUtils;

import java.util.List;

public class EaglerSaveFormat extends SaveFormat {

    private final VFile2 eaglerSavesDir;
    private final DataFixer eaglerDataFixer;

    public EaglerSaveFormat(VFile2 savesDir, DataFixer dataFixerIn) {
        super(savesDir, new VFile2(savesDir, "../backups"), dataFixerIn);
        this.eaglerSavesDir = savesDir;
        this.eaglerDataFixer = dataFixerIn;
    }

    @Override
    public String getName() {
        return "eagler";
    }

    @Override
    public SaveHandler getSaveLoader(String s, MinecraftServer server) {
        return FileUtils.getSaveLoader(this.eaglerSavesDir, s, server, this.eaglerDataFixer);
    }

    protected int getSaveVersion() {
        return 19133;
    }

    @Override
    public List<WorldSummary> getSaveList() throws AnvilConverterException {
        return FileUtils.getSaveList(new net.lax1dude.eaglercraft.internal.vfs2.VFile2(this.eaglerSavesDir.toString()), this);
    }

}
