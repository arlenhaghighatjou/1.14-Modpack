package net.minecraft.data;

import com.google.common.collect.Lists;
import net.minecraft.util.registry.Bootstrap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class DataGenerator {
    private static final Logger LOGGER = LogManager.getLogger();

    static {
        Bootstrap.register();
    }

    private final Collection<Path> inputFolders;
    private final Path outputFolder;
    private final List<IDataProvider> providers = Lists.newArrayList();

    public DataGenerator(Path output, Collection<Path> input) {
        this.outputFolder = output;
        this.inputFolders = input;
    }

    public Collection<Path> getInputFolders() {
        return this.inputFolders;
    }

    public Path getOutputFolder() {
        return this.outputFolder;
    }

    public void run() throws IOException {
        DirectoryCache directorycache = new DirectoryCache(this.outputFolder, "cache");
        directorycache.func_218456_c(this.getOutputFolder().resolve("version.json"));

        directorycache.writeCache();
    }

    public void addProvider(IDataProvider provider) {
        this.providers.add(provider);
    }
}
