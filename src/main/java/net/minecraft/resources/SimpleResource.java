package net.minecraft.resources;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.lax1dude.eaglercraft.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleResource implements IResource {
   public static final Executor RESOURCE_IO_EXECUTOR = new Executor() {
      @Override
      public void execute(Runnable command) {
         command.run();
      }
   };
   private static final Logger LOGGER = LogManager.getLogger();
   private final String packName;
   private final ResourceLocation location;
   private final InputStream inputStream;
   private final InputStream metadataInputStream;
   @OnlyIn(Dist.CLIENT)
   private boolean wasMetadataRead;
   @OnlyIn(Dist.CLIENT)
   private JsonObject metadataJson;

   public SimpleResource(String packNameIn, ResourceLocation locationIn, InputStream inputStreamIn,  InputStream metadataInputStreamIn) {
      this.packName = packNameIn;
      this.location = locationIn;
      this.inputStream = inputStreamIn;
      this.metadataInputStream = metadataInputStreamIn;
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getLocation() {
      return this.location;
   }

   public InputStream getInputStream() {
      return this.inputStream;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasMetadata() {
      return this.metadataInputStream != null;
   }

   @OnlyIn(Dist.CLIENT)
   public <T> T getMetadata(IMetadataSectionSerializer<T> serializer) {
      if (!this.hasMetadata()) {
         return (T)null;
      } else {
         if (this.metadataJson == null && !this.wasMetadataRead) {
            this.wasMetadataRead = true;
            Reader bufferedreader = null;

            try {
               bufferedreader = new InputStreamReader(this.metadataInputStream, StandardCharsets.UTF_8);
               this.metadataJson = JSONUtils.fromJson(bufferedreader);
            } finally {
               IOUtils.closeQuietly((Reader)bufferedreader);
            }
         }

         if (this.metadataJson == null) {
            return (T)null;
         } else {
            String s = serializer.getSectionName();
            return (T)(this.metadataJson.has(s) ? serializer.deserialize(JSONUtils.getJsonObject(this.metadataJson, s)) : null);
         }
      }
   }

   public String getPackName() {
      return this.packName;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof SimpleResource)) {
         return false;
      } else {
         SimpleResource simpleresource = (SimpleResource)p_equals_1_;
         if (this.location != null) {
            if (!this.location.equals(simpleresource.location)) {
               return false;
            }
         } else if (simpleresource.location != null) {
            return false;
         }

         if (this.packName != null) {
            if (!this.packName.equals(simpleresource.packName)) {
               return false;
            }
         } else if (simpleresource.packName != null) {
            return false;
         }

         return true;
      }
   }

   public int hashCode() {
      int i = this.packName != null ? this.packName.hashCode() : 0;
      i = 31 * i + (this.location != null ? this.location.hashCode() : 0);
      return i;
   }

   public void close() throws IOException {
      this.inputStream.close();
      if (this.metadataInputStream != null) {
         this.metadataInputStream.close();
      }

   }
}
