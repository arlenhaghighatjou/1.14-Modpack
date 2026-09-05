package net.minecraft.data;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.lax1dude.eaglercraft.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class DirectoryCache {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Path outputFolder;
    private final Path cacheFile;
    private final Map<Path, String> staleFiles = Maps.newHashMap();
    private final Map<Path, String> createdFiles = Maps.newHashMap();
    private final Set<Path> field_218457_g = Sets.newHashSet();
    private int hits;

    public DirectoryCache(Path folder, String fileName) throws IOException {
        this.outputFolder = folder;
        Path path = folder.resolve(".cache");
        Files.createDirectories(path);
        this.cacheFile = path.resolve(fileName);
        this.getFiles().forEach((p_209395_1_) -> {
            String s = this.staleFiles.put(p_209395_1_, "");
        });
        if (Files.isReadable(this.cacheFile)) {
            IOUtils.readLines(Files.newInputStream(this.cacheFile), Charsets.UTF_8).forEach((p_208315_2_) -> {
                int i = p_208315_2_.indexOf(32);
                this.staleFiles.put(folder.resolve(p_208315_2_.substring(i + 1)), p_208315_2_.substring(0, i));
            });
        }

    }

    public void writeCache() throws IOException {
        this.func_209400_b();

        Writer writer;
        try {
            writer = Files.newBufferedWriter(this.cacheFile);
        } catch (IOException ioexception) {
            LOGGER.warn("Unable write cachefile {}: {}", this.cacheFile, ioexception.toString());
            return;
        }

        writer.close();
        LOGGER.debug("Caching: cache hits: {}, created: {} removed: {}", this.hits, this.createdFiles.size() - this.hits, this.staleFiles.size());
    }

    public String getPreviousHash(Path fileIn) {
        return this.staleFiles.get(fileIn);
    }

    public void func_208316_a(Path fileIn, String hash) {
        this.createdFiles.put(fileIn, hash);
        if (Objects.equals(this.staleFiles.remove(fileIn), hash)) {
            ++this.hits;
        }

    }

    public boolean func_208320_b(Path fileIn) {
        return this.staleFiles.containsKey(fileIn);
    }

    public void func_218456_c(Path p_218456_1_) {
        this.field_218457_g.add(p_218456_1_);
    }

    private void func_209400_b() throws IOException {
        this.getFiles().forEach((p_208322_1_) -> {
            if (this.func_208320_b(p_208322_1_) && !this.field_218457_g.contains(p_208322_1_)) {
                try {
                    Files.delete(p_208322_1_);
                } catch (IOException ioexception) {
                    LOGGER.debug("Unable to delete: {} ({})", p_208322_1_, ioexception.toString());
                }
            }

        });
    }

    private Stream<Path> getFiles() throws IOException {
        return Files.walk(this.outputFolder).filter((p_209397_1_) -> {
            return !Objects.equals(this.cacheFile, p_209397_1_) && !Files.isDirectory(p_209397_1_);
        });
    }
}
