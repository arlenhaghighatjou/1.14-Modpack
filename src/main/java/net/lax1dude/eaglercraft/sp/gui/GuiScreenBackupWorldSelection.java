package net.lax1dude.eaglercraft.sp.gui;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.storage.SaveFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.lax1dude.eaglercraft.sp.server.export.WorldConverterEPK;
import net.lax1dude.eaglercraft.sp.server.export.WorldConverterMCA;

public class GuiScreenBackupWorldSelection extends Screen {

    private static final Logger logger = LogManager.getLogger("GuiScreenBackupWorldSelection");

    private final Screen parentScreen;
    private final String worldName;
    private final String worldFolderName;

    private String statusMessage = null;
    private long statusMessageTime = 0L;

    public GuiScreenBackupWorldSelection(Screen parentScreen, String worldName, String worldFolderName) {
        super(new StringTextComponent(""));
        this.parentScreen = parentScreen;
        this.worldName = worldName;
        this.worldFolderName = worldFolderName;
    }

    protected void init() {
        int centerX = this.width / 2;

        this.addButton(new Button(centerX - 100, this.height / 4 + 20, 200, 20,
                I18n.format("selectWorld.edit.backup"), b -> {
            this.statusMessage = null;
            SaveFormat saveFormat = this.mc.getSaveLoader();
            try {
                long size = saveFormat.createBackup(worldFolderName);
                String sizeStr;
                if (size > 1048576L) {
                    sizeStr = String.format("%.1f MB", size / 1048576.0);
                } else if (size > 1024L) {
                    sizeStr = String.format("%.1f KB", size / 1024.0);
                } else {
                    sizeStr = size + " B";
                }
                logger.info("Created backup of world '{}' ({} bytes)", worldFolderName, size);
                this.statusMessage = I18n.format("selectWorld.edit.backupCreated", sizeStr);
                this.statusMessageTime = System.currentTimeMillis();
            } catch (Exception ex) {
                logger.error("Failed to create backup for world '{}'", worldFolderName, ex);
                this.statusMessage = I18n.format("selectWorld.edit.backupFailed");
                this.statusMessageTime = System.currentTimeMillis();
            }
        }));

        this.addButton(new Button(centerX - 100, this.height / 4 + 45, 200, 20,
                I18n.format("singleplayer.backup.exportEPK"), b -> {
            this.statusMessage = null;
            try {
                byte[] worldData = WorldConverterEPK.exportWorld(worldFolderName);
                if (worldData != null) {
                    EagRuntime.downloadFileWithName(worldName + ".epk", worldData);
                    this.statusMessage = "Exported " + worldName + ".epk";
                    this.statusMessageTime = System.currentTimeMillis();
                }
            } catch (Exception ex) {
                logger.error("Failed to export world '{}' as EPK", worldFolderName, ex);
                this.statusMessage = "EPK export failed!";
                this.statusMessageTime = System.currentTimeMillis();
            }
        }));

        this.addButton(new Button(centerX - 100, this.height / 4 + 70, 200, 20,
                I18n.format("singleplayer.backup.exportMCA"), b -> {
            this.statusMessage = null;
            try {
                byte[] worldData = WorldConverterMCA.exportWorld(worldFolderName);
                if (worldData != null) {
                    EagRuntime.downloadFileWithName(worldName + ".zip", worldData);
                    this.statusMessage = "Exported " + worldName + ".zip";
                    this.statusMessageTime = System.currentTimeMillis();
                }
            } catch (Exception ex) {
                logger.error("Failed to export world '{}' as MCA", worldFolderName, ex);
                this.statusMessage = "MCA export failed!";
                this.statusMessageTime = System.currentTimeMillis();
            }
        }));

        this.addButton(new Button(centerX - 100, this.height / 4 + 95, 200, 20,
                I18n.format("selectWorld.edit.backupFolder"), b -> {
            SaveFormat saveFormat = this.mc.getSaveLoader();
            VFile2 path = saveFormat.getBackupsFolder();
            Util.getOSType().openFile(path);
        }));

        this.addButton(new Button(centerX - 100, this.height / 4 + 135, 200, 20,
                I18n.format("gui.cancel"), b -> {
            this.mc.displayGuiScreen(parentScreen);
        }));
    }

    public void render(int par1, int par2, float par3) {
        this.renderBackground();

        this.drawCenteredString(this.font, I18n.format("singleplayer.backup.title"), this.width / 2, this.height / 4 - 20, 16777215);
        this.drawCenteredString(this.font, worldName, this.width / 2, this.height / 4, 16777215);

        if (statusMessage != null) {
            long elapsed = System.currentTimeMillis() - statusMessageTime;
            int alpha = elapsed > 4000 ? (int) Math.max(0, 255 - (elapsed - 4000) * 255 / 1000) : 255;
            if (alpha > 0) {
                int color = (alpha << 24) | 0x00FFAA;
                if (statusMessage.contains("Failed") || statusMessage.contains("failed")) {
                    color = (alpha << 24) | 0x00FF5555;
                }
                this.drawCenteredString(this.font, statusMessage, this.width / 2, this.height / 4 + 115, color);
            } else {
                statusMessage = null;
            }
        }

        super.render(par1, par2, par3);
    }
}
