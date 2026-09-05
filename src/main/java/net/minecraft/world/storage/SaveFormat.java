package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SaveFormat {
   private static final Logger field_215785_a = LogManager.getLogger();
   private static final DateTimeFormatter BACKUP_DATE_FORMAT = (new DateTimeFormatterBuilder()).appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('_').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral('-').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-').appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();
   private final VFile2 savesDir;
   private final VFile2 backupsDir;
   private final DataFixer field_215788_e;

   public SaveFormat(VFile2 p_i51277_1_, VFile2 p_i51277_2_, DataFixer p_i51277_3_) {
      this.field_215788_e = p_i51277_3_;
      this.savesDir = p_i51277_1_;
      this.backupsDir = p_i51277_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public String getName() {
      return "Anvil";
   }

   @OnlyIn(Dist.CLIENT)
   public List<WorldSummary> getSaveList() throws AnvilConverterException {
      List<WorldSummary> list = Lists.newArrayList();
      String[] lines = net.peyton.eagler.fs.FileUtils.worldsList.getAllLines();
      if (lines == null) {
         return list;
      }

      for(String s : lines) {
         s = s.trim();
         if (s.isEmpty()) continue;
         WorldInfo worldinfo = this.getWorldInfo(s);
         if (worldinfo != null && (worldinfo.getSaveVersion() == 19132 || worldinfo.getSaveVersion() == 19133)) {
            boolean flag = worldinfo.getSaveVersion() != this.func_215782_e();
            String s1 = worldinfo.getWorldName();
            if (StringUtils.isEmpty(s1)) {
               s1 = s;
            }

            list.add(new WorldSummary(worldinfo, s, s1, 0L, flag));
         }
      }

      return list;
   }

   private int func_215782_e() {
      return 19133;
   }

   public SaveHandler getSaveLoader(String saveName, MinecraftServer server) {
      return func_215783_a(this.savesDir, this.field_215788_e, saveName, server);
   }

   protected static SaveHandler func_215783_a(VFile2 p_215783_0_, DataFixer p_215783_1_, String p_215783_2_, MinecraftServer p_215783_3_) {
      return new SaveHandler(p_215783_0_, p_215783_2_, p_215783_3_, p_215783_1_);
   }

   public boolean isOldMapFormat(String saveName) {
      WorldInfo worldinfo = this.getWorldInfo(saveName);
      return worldinfo != null && worldinfo.getSaveVersion() != this.func_215782_e();
   }

   public boolean convertMapFormat(String filename, IProgressUpdate progressCallback) {
      return false;
   }

   public WorldInfo getWorldInfo(String saveName) {
      return func_215779_a(this.savesDir, this.field_215788_e, saveName);
   }

   protected static WorldInfo func_215779_a(VFile2 p_215779_0_, DataFixer p_215779_1_, String p_215779_2_) {
      VFile2 file1 = new VFile2(p_215779_0_, p_215779_2_);
      VFile2 file2 = new VFile2(file1, "level.dat");
      if (file2.exists()) {
         WorldInfo worldinfo = func_215780_a(file2, p_215779_1_);
         if (worldinfo != null) {
            return worldinfo;
         }
      }

      file2 = new VFile2(file1, "level.dat_old");
      return file2.exists() ? func_215780_a(file2, p_215779_1_) : null;
   }

   public static WorldInfo func_215780_a(VFile2 p_215780_0_, DataFixer p_215780_1_) {
      try {
         CompoundNBT compoundnbt = CompressedStreamTools.readCompressed(p_215780_0_.getInputStream());
         CompoundNBT compoundnbt1 = compoundnbt.getCompound("Data");
         CompoundNBT compoundnbt2 = compoundnbt1.contains("Player", 10) ? compoundnbt1.getCompound("Player") : null;
         compoundnbt1.remove("Player");
         int i = compoundnbt1.contains("DataVersion", 99) ? compoundnbt1.getInt("DataVersion") : -1;
         return new WorldInfo(NBTUtil.update(p_215780_1_, DefaultTypeReferences.LEVEL, compoundnbt1, i), p_215780_1_, i, compoundnbt2);
      } catch (Exception exception) {
         field_215785_a.error("Exception reading {}", p_215780_0_, exception);
         return null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void renameWorld(String dirName, String newName) {
      VFile2 file1 = new VFile2(this.savesDir, dirName);
      if (file1.exists()) {
         VFile2 file2 = new VFile2(file1, "level.dat");
         if (file2.exists()) {
            try {
               CompoundNBT compoundnbt = CompressedStreamTools.readCompressed(file2.getInputStream());
               CompoundNBT compoundnbt1 = compoundnbt.getCompound("Data");
               compoundnbt1.putString("LevelName", newName);
               CompressedStreamTools.writeCompressed(compoundnbt, file2.getOutputStream());
            } catch (Exception exception) {
               exception.printStackTrace();
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isNewLevelIdAcceptable(String saveName) {
      VFile2 vfile = new VFile2(this.savesDir, saveName);
      return !vfile.exists();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean deleteWorldDirectory(String saveName) {
      VFile2 file1 = new VFile2(this.savesDir, saveName);
      java.util.List<VFile2> files = file1.listFiles(true);
      field_215785_a.info("Deleting level {}", (Object)saveName);
      boolean ok = true;
      for (VFile2 file2 : files) {
         if (!file2.delete()) {
            ok = false;
         }
      }
      net.peyton.eagler.fs.FileUtils.removeWorldIfExists(saveName);
      return ok;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canLoadWorld(String saveName) {
      return (new VFile2(this.savesDir, saveName)).dirExists();
   }

   @OnlyIn(Dist.CLIENT)
   public VFile2 func_215781_c() {
      return this.savesDir;
   }

   public VFile2 getFile(String saveName, String filePath) {
      return new VFile2(this.savesDir, saveName + "/" + filePath);
   }

   @OnlyIn(Dist.CLIENT)
   private VFile2 getWorldFolder(String saveName) {
      return new VFile2(this.savesDir, saveName);
   }

   @OnlyIn(Dist.CLIENT)
   public VFile2 getBackupsFolder() {
      return this.backupsDir;
   }

   @OnlyIn(Dist.CLIENT)
   public long createBackup(String worldName) throws IOException {
      VFile2 worldFolder = this.getWorldFolder(worldName);
      if (!worldFolder.dirExists()) {
         throw new IOException("World '" + worldName + "' does not exist!");
      }

      String timestamp = LocalDateTime.now().format(BACKUP_DATE_FORMAT);
      String backupName = worldName + "-" + timestamp + ".zip";
      VFile2 backupFile = new VFile2(this.backupsDir, backupName);

      long totalSize = 0L;
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try (ZipOutputStream zos = new ZipOutputStream(baos)) {
         List<VFile2> files = worldFolder.listFiles(true);
         String prefix = worldFolder.getPath();
         if (!prefix.endsWith("/")) {
            prefix += "/";
         }

         for (VFile2 file : files) {
            if (!file.exists()) continue;
            if (file.dirExists()) continue;
            String entryName = file.getPath().substring(prefix.length());
            zos.putNextEntry(new ZipEntry(entryName));
            byte[] content = file.getAllBytes();
            if (content != null) {
               zos.write(content);
               totalSize += content.length;
            }
            zos.closeEntry();
         }
      }

      backupFile.setAllBytes(baos.toByteArray());
      return totalSize;
   }
}
