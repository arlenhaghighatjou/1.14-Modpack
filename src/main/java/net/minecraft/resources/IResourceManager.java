package net.minecraft.resources;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public interface IResourceManager {
    @OnlyIn(Dist.CLIENT)
    Set<String> getResourceNamespaces();

    IResource getResource(ResourceLocation resourceLocationIn) throws IOException;

    @OnlyIn(Dist.CLIENT)
    boolean hasResource(ResourceLocation p_219533_1_);

    List<IResource> getAllResources(ResourceLocation resourceLocationIn) throws IOException;

    Collection<ResourceLocation> getAllResourceLocations(String pathIn, Predicate<String> filter);

    @OnlyIn(Dist.CLIENT)
    void addResourcePack(IResourcePack resourcePack);
}
