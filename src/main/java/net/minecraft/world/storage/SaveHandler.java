package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveHandler implements IPlayerFileData {
   private static final Logger field_215773_b = LogManager.getLogger();
   private final VFile2 field_215774_c;
   private final VFile2 playersDirectory;
   private final long field_215776_e = Util.milliTime();
   private final String field_215777_f;
   private final TemplateManager field_215778_g;
   protected final DataFixer field_215772_a;

   public SaveHandler(VFile2 p_i51278_1_, String p_i51278_2_,  MinecraftServer p_i51278_3_, DataFixer p_i51278_4_) {
      this.field_215772_a = p_i51278_4_;
      this.field_215774_c = new VFile2(p_i51278_1_, p_i51278_2_);
      this.playersDirectory = new VFile2(this.field_215774_c, "playerdata");
      this.field_215777_f = p_i51278_2_;
      if (p_i51278_3_ != null) {
         this.field_215778_g = new TemplateManager(p_i51278_3_, new java.io.File(this.field_215774_c.getPath()), p_i51278_4_);
      } else {
         this.field_215778_g = null;
      }

      this.func_215770_h();
   }

   public void saveWorldInfoWithPlayer(WorldInfo worldInformation,  CompoundNBT tagCompound) {
      worldInformation.setSaveVersion(19133);
      CompoundNBT compoundnbt = worldInformation.cloneNBTCompound(tagCompound);
      CompoundNBT compoundnbt1 = new CompoundNBT();
      compoundnbt1.put("Data", compoundnbt);

      try {
         VFile2 file1 = new VFile2(this.field_215774_c, "level.dat_new");
         VFile2 file2 = new VFile2(this.field_215774_c, "level.dat_old");
         VFile2 file3 = new VFile2(this.field_215774_c, "level.dat");
         CompressedStreamTools.writeCompressed(compoundnbt1, file1.getOutputStream());
         if (file2.exists()) {
            file2.delete();
         }

         file3.renameTo(file2);
         if (file3.exists()) {
            file3.delete();
         }

         file1.renameTo(file3);
         if (file1.exists()) {
            file1.delete();
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }

   }

   private void func_215770_h() {
      try {
         VFile2 file1 = new VFile2(this.field_215774_c, "session.lock");
         DataOutputStream dataoutputstream = new DataOutputStream(file1.getOutputStream());

         try {
            dataoutputstream.writeLong(this.field_215776_e);
         } finally {
            dataoutputstream.close();
         }

      } catch (IOException ioexception) {
         ioexception.printStackTrace();
         throw new RuntimeException("Failed to check session lock, aborting");
      }
   }

   public VFile2 getWorldDirectory() {
      return this.field_215774_c;
   }

   public void checkSessionLock() throws SessionLockException {
      try {
         VFile2 file1 = new VFile2(this.field_215774_c, "session.lock");
         DataInputStream datainputstream = new DataInputStream(file1.getInputStream());

         try {
            if (datainputstream.readLong() != this.field_215776_e) {
               throw new SessionLockException("The save is being accessed from another location, aborting");
            }
         } finally {
            datainputstream.close();
         }

      } catch (IOException var7) {
         throw new SessionLockException("Failed to check session lock, aborting");
      }
   }

   public WorldInfo loadWorldInfo() {
      VFile2 file1 = new VFile2(this.field_215774_c, "level.dat");
      if (file1.exists()) {
         WorldInfo worldinfo = SaveFormat.func_215780_a(file1, this.field_215772_a);
         if (worldinfo != null) {
            return worldinfo;
         }
      }

      file1 = new VFile2(this.field_215774_c, "level.dat_old");
      return file1.exists() ? SaveFormat.func_215780_a(file1, this.field_215772_a) : null;
   }

   public void saveWorldInfo(WorldInfo worldInformation) {
      this.saveWorldInfoWithPlayer(worldInformation, (CompoundNBT)null);
   }

   public void writePlayerData(PlayerEntity player) {
      try {
         CompoundNBT compoundnbt = player.writeWithoutTypeId(new CompoundNBT());
         VFile2 file1 = new VFile2(this.playersDirectory, player.getCachedUniqueIdString() + ".dat");
         CompressedStreamTools.writeCompressed(compoundnbt, file1.getOutputStream());
      } catch (Exception var5) {
         field_215773_b.warn("Failed to save player data for {}", player.getName().getString(), var5);
      }

   }

   public CompoundNBT readPlayerData(PlayerEntity player) {
      CompoundNBT compoundnbt = null;

      try {
         VFile2 file1 = new VFile2(this.playersDirectory, player.getCachedUniqueIdString() + ".dat");
         if (file1.exists() && file1.exists()) {
            compoundnbt = CompressedStreamTools.readCompressed(file1.getInputStream());
         }
      } catch (Exception var4) {
         field_215773_b.warn("Failed to load player data for {}", (Object)player.getName().getString());
      }

      if (compoundnbt != null) {
         int i = compoundnbt.contains("DataVersion", 3) ? compoundnbt.getInt("DataVersion") : -1;
         player.read(NBTUtil.update(this.field_215772_a, DefaultTypeReferences.PLAYER, compoundnbt, i));
      }

      return compoundnbt;
   }

   public String[] func_215771_d() {
      String[] astring = this.playersDirectory.listFiles(false).stream().map(f -> f.getName()).toArray(String[]::new);
      if (astring == null) {
         astring = new String[0];
      }

      for(int i = 0; i < astring.length; ++i) {
         if (astring[i].endsWith(".dat")) {
            astring[i] = astring[i].substring(0, astring[i].length() - 4);
         }
      }

      return astring;
   }

   public TemplateManager getStructureTemplateManager() {
      return this.field_215778_g;
   }

   public DataFixer getFixer() {
      return this.field_215772_a;
   }
}
