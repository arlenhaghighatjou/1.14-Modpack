package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ResourceIndex {
    protected static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, VFile2> resourceMap = Maps.newHashMap();

    protected ResourceIndex() {
    }

    public ResourceIndex(VFile2 assetsFolder, String indexName) {
        VFile2 file2 = new VFile2(assetsFolder, "indexes/" + indexName + ".json");
        LOGGER.error("Unable to parse resource index file: {}", (Object) file2);
    }

    public VFile2 getFile(ResourceLocation location) {
        return this.getFile(location.toString());
    }

    public VFile2 getFile(String p_200009_1_) {
        return this.resourceMap.get(p_200009_1_);
    }

    public Collection<String> getFiles(String p_211685_1_, int p_211685_2_, Predicate<String> p_211685_3_) {
        return this.resourceMap.keySet().stream().filter((p_211684_0_) -> {
            return !p_211684_0_.endsWith(".mcmeta");
        }).map(ResourceLocation::new).map(ResourceLocation::getPath).filter((p_211683_1_) -> {
            return p_211683_1_.startsWith(p_211685_1_ + "/");
        }).filter(p_211685_3_).collect(Collectors.toList());
    }
}
