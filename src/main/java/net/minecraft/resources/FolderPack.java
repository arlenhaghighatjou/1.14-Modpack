package net.minecraft.resources;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;

public class FolderPack extends ResourcePack {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final boolean OS_WINDOWS = Util.getOSType() == Util.OS.WINDOWS;
    private static final CharMatcher BACKSLASH_MATCHER = CharMatcher.is('\\');

    public FolderPack(VFile2 folder) {
        super(folder);
    }

    public static boolean validatePath(VFile2 p_195777_0_, String p_195777_1_) throws IOException {
        String s = p_195777_0_.getPath();
        if (OS_WINDOWS) {
            s = BACKSLASH_MATCHER.replaceFrom(s, '/');
        }

        return s.endsWith(p_195777_1_);
    }

    protected InputStream getInputStream(String resourcePath) throws IOException {
        VFile2 file1 = this.getFile(resourcePath);
        if (file1 == null) {
            throw new ResourcePackFileNotFoundException(this.file, resourcePath);
        } else {
            return file1.getInputStream();
        }
    }

    protected boolean resourceExists(String resourcePath) {
        return this.getFile(resourcePath) != null;
    }

    private VFile2 getFile(String p_195776_1_) {
        try {
            VFile2 file1 = new VFile2(this.file, p_195776_1_);
            if (file1.exists() && validatePath(file1, p_195776_1_)) {
                return file1;
            }
        } catch (IOException var3) {
            ;
        }

        return null;
    }

    public Set<String> getResourceNamespaces(ResourcePackType type) {
        Set<String> set = Sets.newHashSet();
        VFile2 file1 = new VFile2(this.file, type.getDirectoryName());
        java.util.List<VFile2> validDirs = new java.util.ArrayList<>();
        for (VFile2 f : file1.listFiles(true)) {
            String path = f.getPath();
            String folderPath = file1.getPath();
            if (path.startsWith(folderPath + "/")) {
                String relative = path.substring(folderPath.length() + 1);
                int slashIndex = relative.indexOf('/');
                if (slashIndex != -1) {
                    String dirName = relative.substring(0, slashIndex);
                    validDirs.add(new VFile2(file1, dirName));
                }
            }
        }
        for (VFile2 file2 : validDirs) {
            String s = file2.getName();
            if (s.equals(s.toLowerCase(Locale.ROOT))) {
                set.add(s);
            } else {
                LOGGER.warn("Ignored non-lowercase namespace: {} in {}", s, file1);
            }
        }

        return set;
    }

    public void close() throws IOException {
    }

    public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String pathIn, int maxDepth, Predicate<String> filter) {
        VFile2 file1 = new VFile2(this.file, type.getDirectoryName());
        List<ResourceLocation> list = Lists.newArrayList();

        for (String s : this.getResourceNamespaces(type)) {
            this.func_199546_a(new VFile2(new VFile2(file1, s), pathIn), maxDepth, s, list, pathIn + "/", filter);
        }

        return list;
    }

    private void func_199546_a(VFile2 p_199546_1_, int p_199546_2_, String p_199546_3_, List<ResourceLocation> p_199546_4_, String p_199546_5_, Predicate<String> p_199546_6_) {
        for (VFile2 f : p_199546_1_.listFiles(true)) {
            String path = f.getPath();
            String folderPath = p_199546_1_.getPath();
            if (path.startsWith(folderPath + "/")) {
                String relative = path.substring(folderPath.length() + 1);
                if (!relative.endsWith(".mcmeta") && p_199546_6_.test(f.getName())) {
                    int depth = relative.split("/").length - 1;
                    if (depth <= p_199546_2_) {
                        try {
                            p_199546_4_.add(new ResourceLocation(p_199546_3_, p_199546_5_ + relative));
                        } catch (ResourceLocationException resourcelocationexception) {
                            LOGGER.error(resourcelocationexception.getMessage());
                        }
                    }
                }
            }
        }
    }
}
