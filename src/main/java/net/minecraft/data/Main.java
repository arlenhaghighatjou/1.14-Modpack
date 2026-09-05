package net.minecraft.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

public class Main {
    public static void main(String[] p_main_0_) throws IOException {

    }

    public static DataGenerator makeGenerator(Path output, Collection<Path> inputs, boolean client, boolean server, boolean dev, boolean reports, boolean validate) {
        DataGenerator datagenerator = new DataGenerator(output, inputs);
        if (client || server) {
            datagenerator.addProvider((new SNBTToNBTConverter(datagenerator)).func_225369_a(new StructureUpdater()));
        }

        if (server) {
            datagenerator.addProvider(new FluidTagsProvider(datagenerator));
            datagenerator.addProvider(new BlockTagsProvider(datagenerator));
            datagenerator.addProvider(new ItemTagsProvider(datagenerator));
            datagenerator.addProvider(new EntityTypeTagsProvider(datagenerator));
            datagenerator.addProvider(new RecipeProvider(datagenerator));
            datagenerator.addProvider(new AdvancementProvider(datagenerator));
            datagenerator.addProvider(new LootTableProvider(datagenerator));
        }

        if (dev) {
            datagenerator.addProvider(new NBTToSNBTConverter(datagenerator));
        }

        if (reports) {
            datagenerator.addProvider(new BlockListReport(datagenerator));
            datagenerator.addProvider(new RegistryDumpReport(datagenerator));
            datagenerator.addProvider(new CommandsReport(datagenerator));
        }

        return datagenerator;
    }
}
