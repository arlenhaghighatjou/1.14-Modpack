package net.minecraft.client.resources;

import java.io.File;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FolderResourceIndex extends ResourceIndex {
   private final File baseDir;

   public FolderResourceIndex(File folder) {
      this.baseDir = folder;
   }

   public net.lax1dude.eaglercraft.internal.vfs2.VFile2 getFile(ResourceLocation location) {
      return new net.lax1dude.eaglercraft.internal.vfs2.VFile2(this.baseDir, location.toString().replace(':', '/'));
   }

   public net.lax1dude.eaglercraft.internal.vfs2.VFile2 getFile(String p_200009_1_) {
      return new net.lax1dude.eaglercraft.internal.vfs2.VFile2(this.baseDir, p_200009_1_);
   }

}
