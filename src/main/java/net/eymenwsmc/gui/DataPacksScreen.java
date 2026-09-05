package net.eymenwsmc.gui;

import net.lax1dude.eaglercraft.EaglerInputStream;
import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.internal.FileChooserResult;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraft.client.gui.screen.AlertScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.eymenwsmc.gui.DataPackList.DataPackEntry;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.client.resources.I18n;
import net.minecraft.resources.FolderPackFinder;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.resources.ServerPackFinder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

 /**
    A screen that Mojang has never made before.
    @author eymenwsmc
    @see net.eymenwsmc.gui.DataPackList
 */
@OnlyIn(Dist.CLIENT)
public class DataPacksScreen extends Screen {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Screen parentScreen;
    private final String worldId;
    private VFile2 datapacksFolder;
    private ResourcePackList<ClientResourcePackInfo> packList;
    private DataPackList availableList;
    private DataPackList enabledList;
    private Set<String> disabledNames;
    private boolean changed;

    public DataPacksScreen(Screen parentScreenIn, String worldIdIn) {
        super(new TranslationTextComponent("dataPack.title"));
        this.parentScreen = parentScreenIn;
        this.worldId = worldIdIn;
    }

    protected void init() {
        this.datapacksFolder = this.mc.getSaveLoader().getFile(this.worldId, "datapacks");
        if (this.disabledNames == null) {
            this.disabledNames = new HashSet<>();
            WorldInfo worldinfo = this.mc.getSaveLoader().getWorldInfo(this.worldId);
            if (worldinfo != null) {
                this.disabledNames.addAll(worldinfo.getDisabledDataPacks());
            }
        }

        this.addButton(new Button(this.width / 2 - 154, this.height - 48, 150, 20, I18n.format("dataPack.import"), (p_214296_1_) -> {
            EagRuntime.displayFileChooser("application/zip", "zip");
        }));
        this.addButton(new Button(this.width / 2 + 4, this.height - 48, 150, 20, I18n.format("gui.done"), (p_214296_1_) -> {
            this.saveChanges();
        }));

        DataPackList oldAvailable = this.availableList;
        DataPackList oldEnabled = this.enabledList;
        this.availableList = new DataPackList(this.mc, this, 200, this.height, 32, this.height - 55 + 4, true);
        this.availableList.setLeftPos(this.width / 2 - 4 - 200);
        this.children.add(this.availableList);
        this.enabledList = new DataPackList(this.mc, this, 200, this.height, 32, this.height - 55 + 4, false);
        this.enabledList.setLeftPos(this.width / 2 + 4);
        this.children.add(this.enabledList);

        if (!this.changed) {
            if (this.packList != null) {
                this.packList.close();
            }
            this.packList = new ResourcePackList<>(ClientResourcePackInfo::new);
            this.packList.addPackFinder(new ServerPackFinder());
            this.packList.addPackFinder(new FolderPackFinder(this.datapacksFolder));
            this.packList.reloadPacksFromFinders();
            this.availableList.children().clear();
            this.enabledList.children().clear();
            for (ClientResourcePackInfo info : this.packList.getAllPacks()) {
                if (info.getName().equals("vanilla")) {
                    this.enabledList.func_214365_a(new DataPackEntry(this.enabledList, this, info));
                }
            }
            for (ClientResourcePackInfo info : this.packList.getAllPacks()) {
                if (!info.getName().equals("vanilla")) {
                    if (this.disabledNames.contains(info.getName())) {
                        this.availableList.func_214365_a(new DataPackEntry(this.availableList, this, info));
                    } else {
                        this.enabledList.func_214365_a(new DataPackEntry(this.enabledList, this, info));
                    }
                }
            }
        } else {
            if (oldAvailable != null) {
                this.availableList.children().addAll(oldAvailable.children());
            }
            if (oldEnabled != null) {
                this.enabledList.children().addAll(oldEnabled.children());
            }
        }
    }

    public boolean func_214299_c(DataPackEntry entry) {
        return this.enabledList.children().contains(entry);
    }

    public void enableDPack(DataPackEntry entry) {
        this.availableList.children().remove(entry);
        this.enabledList.func_214365_a(entry);
        this.disabledNames.remove(entry.getInfo().getName());
        this.markChanged();
    }

    public void disableDPack(DataPackEntry entry) {
        this.enabledList.children().remove(entry);
        this.availableList.func_214365_a(entry);
        this.disabledNames.add(entry.getInfo().getName());
        this.markChanged();
    }

    public void moveTheShitUp(DataPackEntry entry) {
        List<DataPackEntry> list = this.enabledList.children();
        int i = list.indexOf(entry);
        if (i > 0) {
            list.remove(entry);
            list.add(i - 1, entry);
            this.markChanged();
        }
    }

    public void moveTheShitDown(DataPackEntry entry) {
        List<DataPackEntry> list = this.enabledList.children();
        int i = list.indexOf(entry);
        if (i >= 0 && i < list.size() - 1) {
            list.remove(entry);
            list.add(i + 1, entry);
            this.markChanged();
        }
    }

    public void deleteTheShit(ClientResourcePackInfo info) {
        this.mc.displayGuiScreen(new ConfirmScreen((p_214417_1_) -> {
            this.mc.displayGuiScreen(this);
            if (p_214417_1_) {
                this.doDeletePack(info);
            }
        }, new TranslationTextComponent("dataPack.delete.confirm.title"), new TranslationTextComponent("dataPack.delete.confirm", info.func_195789_b())));
    }

    private void doDeletePack(ClientResourcePackInfo info) {
        String name = info.getName();
        if (name.startsWith("file/")) {
            name = name.substring(5);
        }
        VFile2 file = new VFile2(this.datapacksFolder, name);
        for (VFile2 f : file.listFiles(true)) {
            f.delete();
        }
        file.delete();
        this.disabledNames.remove(info.getName());
        this.changed = false;
        this.init(this.mc, this.width, this.height);
    }

    private void saveChanges() {
        if (this.changed) {
            SaveFormat saveformat = this.mc.getSaveLoader();
            SaveHandler savehandler = saveformat.getSaveLoader(this.worldId, (MinecraftServer) null);
            WorldInfo worldinfo = savehandler.loadWorldInfo();
            if (worldinfo != null) {
                Set<String> enabled = worldinfo.getEnabledDataPacks();
                enabled.clear();
                enabled.add("vanilla");
                for (DataPackEntry entry : this.enabledList.children()) {
                    enabled.add(entry.getInfo().getName());
                }
                Set<String> disabled = worldinfo.getDisabledDataPacks();
                disabled.clear();
                for (DataPackEntry entry : this.availableList.children()) {
                    disabled.add(entry.getInfo().getName());
                }
                savehandler.saveWorldInfo(worldinfo);
            }
        }
        this.mc.displayGuiScreen(this.parentScreen);
    }

    public void tick() {
        if (EagRuntime.fileChooserHasResult()) {
            FileChooserResult result = EagRuntime.getFileChooserResult();
            if (result != null) {
                try {
                    this.importTheShit(result.fileName, result.fileData);
                    this.changed = false;
                    this.init(this.mc, this.width, this.height);
                } catch (Exception e) {
                    LOGGER.error("Could not import datapack: {}", result.fileName);
                    LOGGER.error(e);
                    this.mc.displayGuiScreen(new AlertScreen(() -> {
                        this.mc.displayGuiScreen(this);
                    }, new TranslationTextComponent("dataPack.title"), new TranslationTextComponent("dataPack.import.failed", result.fileName)));
                }
            }
        }
    }

    private void importTheShit(String fileName, byte[] fileData) throws IOException {
        String folderName = fileName.replace('\\', '/');
        int idx = folderName.lastIndexOf('/');
        if (idx != -1) {
            folderName = folderName.substring(idx + 1);
        }
        int dot = folderName.lastIndexOf('.');
        if (dot != -1) {
            folderName = folderName.substring(0, dot);
        }
        folderName = folderName.replaceAll("[^A-Za-z0-9\\-_ \\(\\)]", "_");

        List<String> fileNames = new ArrayList<>();
        try (ZipInputStream ziss = new ZipInputStream(new EaglerInputStream(fileData))) {
            ZipEntry zipEntry;
            while ((zipEntry = ziss.getNextEntry()) != null) {
                if (!zipEntry.isDirectory()) {
                    fileNames.add(zipEntry.getName());
                }
            }
        }

        int prefixLen = Integer.MAX_VALUE;
        for (int i = 0; i < fileNames.size(); ++i) {
            String fn = fileNames.get(i);
            if (fn.equals("pack.mcmeta") || fn.endsWith("/pack.mcmeta")) {
                int currPrefixLen = fn.length() - 11;
                if (prefixLen > currPrefixLen) {
                    prefixLen = currPrefixLen;
                }
            }
        }
        if (prefixLen == Integer.MAX_VALUE) {
            throw new IOException("No pack.mcmeta found in datapack zip!");
        }

        VFile2 dstDir = new VFile2(this.datapacksFolder, folderName);
        for (int suffix = 1; dstDir.dirExists() || dstDir.exists(); ++suffix) {
            dstDir = new VFile2(this.datapacksFolder, folderName + "-" + suffix);
        }

        try {
            try (ZipInputStream ziss = new ZipInputStream(new EaglerInputStream(fileData))) {
                ZipEntry zipEntry;
                while ((zipEntry = ziss.getNextEntry()) != null) {
                    if (!zipEntry.isDirectory()) {
                        String fn = zipEntry.getName();
                        if (fn.length() > prefixLen) {
                            byte[] buffer = EaglerInputStream.inputStreamToBytes(ziss);
                            (new VFile2(dstDir, fn.substring(prefixLen))).setAllBytes(buffer);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            for (int i = 0; i < fileNames.size(); ++i) {
                String fn = fileNames.get(i);
                if (fn.length() > prefixLen) {
                    (new VFile2(dstDir, fn.substring(prefixLen))).delete();
                }
            }
            throw ex;
        }
    }

    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderDirtBackground(0);
        this.availableList.render(p_render_1_, p_render_2_, p_render_3_);
        this.enabledList.render(p_render_1_, p_render_2_, p_render_3_);
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 16, 16777215);
        this.drawCenteredString(this.font, I18n.format("dataPack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
        super.render(p_render_1_, p_render_2_, p_render_3_);
    }

    public void markChanged() {
        this.changed = true;
    }
}
