package net.minecraft.resources;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.lax1dude.eaglercraft.EagRuntime;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

public class VanillaPack implements IResourcePack {
    private static final Logger LOGGER = LogManager.getLogger();
    public static Class<?> baseClass;

    public final Set<String> resourceNamespaces;

    public VanillaPack(String... resourceNamespacesIn) {
        this.resourceNamespaces = ImmutableSet.copyOf(resourceNamespacesIn);
    }

    public InputStream getRootResourceStream(String fileName) throws IOException {
        if (!fileName.contains("/") && !fileName.contains("\\")) {
            return this.getInputStreamVanilla(fileName);
        } else {
            throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
        }
    }

    public InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException {
        InputStream inputstream = this.getInputStreamVanilla(type, location);
        if (inputstream != null) {
            return inputstream;
        } else {
            throw new FileNotFoundException(location.getPath());
        }
    }

    public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String pathIn, int maxDepth, Predicate<String> filter) {
        Set<ResourceLocation> set = Sets.newHashSet();

        for (String namespace : this.resourceNamespaces) {
            String prefix = type.getDirectoryName() + "/" + namespace + "/";
            String base = pathIn.isEmpty() ? prefix : prefix + pathIn + "/";

            for (String fullPath : EagRuntime.getAllResourcePaths("/" + base)) {
                String relative = fullPath.substring(prefix.length());
                String fileName = relative.substring(relative.lastIndexOf('/') + 1);
                if (fileName.isEmpty() || !filter.test(fileName)) {
                    continue;
                }

                String below = relative.substring(base.length() - prefix.length());
                int depth = 0;
                for (int i = 0; i < below.length(); ++i) {
                    if (below.charAt(i) == '/') {
                        ++depth;
                    }
                }

                if (depth <= maxDepth) {
                    set.add(new ResourceLocation(namespace, relative));
                }
            }
        }

        return set;
    }

    protected InputStream getInputStreamVanilla(ResourcePackType type, ResourceLocation location) {
        String s = func_223458_d(type, location);

        try {
            return EagRuntime.getRequiredResourceStream(s);
        } catch (Exception e) {
            InputStream fallback = EagRuntime.getResourceStream(s);
            if (fallback != null) {
                return fallback;
            }
            throw new RuntimeException("Could not load required resource from EPK: " + s, e);
        }
    }

    private static String func_223458_d(ResourcePackType p_223458_0_, ResourceLocation p_223458_1_) {
        return "/" + p_223458_0_.getDirectoryName() + "/" + p_223458_1_.getNamespace() + "/" + p_223458_1_.getPath();
    }

    protected InputStream getInputStreamVanilla(String pathIn) {
        String fullPath = "/" + pathIn;

        try {
            return EagRuntime.getRequiredResourceStream(fullPath);
        } catch (Exception e) {
            InputStream fallback = EagRuntime.getResourceStream(fullPath);
            if (fallback != null) {
                return fallback;
            }
            throw new RuntimeException("Could not load required resource from EPK: " + fullPath, e);
        }
    }

    public boolean resourceExists(ResourcePackType type, ResourceLocation location) {
        String s = func_223458_d(type, location);
        return EagRuntime.getResourceExists(s);
    }

    public Set<String> getResourceNamespaces(ResourcePackType type) {
        return this.resourceNamespaces;
    }

    public <T> T getMetadata(IMetadataSectionSerializer<T> deserializer) throws IOException {
        try (InputStream inputstream = this.getRootResourceStream("pack.mcmeta")) {
            Object object = ResourcePack.<T>getResourceMetadata(deserializer, inputstream);
            return (T) object;
        } catch (FileNotFoundException | RuntimeException var16) {
            return (T) null;
        }
    }

    public String getName() {
        return "Default";
    }

    public void close() {
    }
}
