package com.polyvalord.extcaves.config;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigBuild {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final ForgeConfigSpec.Builder server_builder = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec server_config;

    static {
        Config.init(server_builder);
        server_config = server_builder.build();
    }

    public static void loadConfig(ForgeConfigSpec config, String path) {
        LOGGER.info("Loading config : " + path);
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        LOGGER.info("Built config : " + path);
        file.load();
        LOGGER.info("Loaded config : " + path);
        config.setConfig(file);
    }
}
