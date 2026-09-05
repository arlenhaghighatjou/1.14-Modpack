package net.minecraft.client.particle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TexturesParticle {

   private final List<ResourceLocation> textures;

   private TexturesParticle( List<ResourceLocation> p_i51017_1_) {
      this.textures = p_i51017_1_;
   }

   public List<ResourceLocation> getTextures() {
      return this.textures;
   }

   public static TexturesParticle deserialize(JsonObject p_217595_0_) {
      JsonArray jsonarray = JSONUtils.getJsonArray(p_217595_0_, "textures", (JsonArray)null);
      List<ResourceLocation> list;
      if (jsonarray != null) {
         list = Streams.stream(jsonarray).map((p_217597_0_) -> {
            return JSONUtils.getString(p_217597_0_, "texture");
         }).map(ResourceLocation::new).collect(Collectors.toList());
      } else {
         list = null;
      }

      return new TexturesParticle(list);
   }
}
