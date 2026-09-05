package net.minecraft.client.resources;

import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.VanillaPack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VirtualAssetsPack extends VanillaPack {
   private final ResourceIndex field_195785_b;

   public VirtualAssetsPack(ResourceIndex p_i48115_1_) {
      super(modpack.ModLoader.resourceNamespaces("minecraft", "realms", "eagler"));
      this.field_195785_b = p_i48115_1_;
   }

   protected InputStream getInputStreamVanilla(ResourcePackType type, ResourceLocation location) {
      if (type == ResourcePackType.CLIENT_RESOURCES) {
         VFile2 file1 = this.field_195785_b.getFile(location);
         if (file1 != null && file1.exists()) {
             return file1.getInputStream();
         }
      }

      return super.getInputStreamVanilla(type, location);
   }

   public boolean resourceExists(ResourcePackType type, ResourceLocation location) {
      if (type == ResourcePackType.CLIENT_RESOURCES) {
         VFile2 file1 = this.field_195785_b.getFile(location);
         if (file1 != null && file1.exists()) {
            return true;
         }
      }

      return super.resourceExists(type, location);
   }

   protected InputStream getInputStreamVanilla(String pathIn) {
      VFile2 file1 = this.field_195785_b.getFile(pathIn);
      if (file1 != null && file1.exists()) {
          return file1.getInputStream();
      }

      return super.getInputStreamVanilla(pathIn);
   }

   public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String pathIn, int maxDepth, Predicate<String> filter) {
      Collection<ResourceLocation> collection = super.getAllResourceLocations(type, pathIn, maxDepth, filter);
      return collection;
   }
}
