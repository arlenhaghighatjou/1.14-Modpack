package com.polyvalord.extcaves;

import com.polyvalord.extcaves.blocks.RegBlockItems;
import com.polyvalord.extcaves.items.RegItems;
import com.polyvalord.extcaves.world.RegFeatures;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.polyvalord.extcaves.blocks.RegBlocks;
import com.polyvalord.extcaves.config.ConfigBuild;
import com.polyvalord.extcaves.world.GenFeatures;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExtCaves.MODID)
public class ExtCaves
{
    public static final String MODID = "extcaves";

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public ExtCaves()
    {
    	// config file
    	ModLoadingContext.get().registerConfig(Type.SERVER, ConfigBuild.server_config);
    	ConfigBuild.loadConfig(ConfigBuild.server_config, FMLPaths.CONFIGDIR.get().resolve("extcaves_config.toml").toString());

        // Register the doClientStuff method for modloading
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, GenFeatures::onBiomeLoad);

        // Deferred registries
        RegBlocks.BLOCKS.register(eventBus);
        RegBlockItems.ITEMS.register(eventBus);
        RegItems.ITEMS.register(eventBus);
        RegFeatures.FEATURES.register(eventBus);
    }
    
    // custom creative tab
    public static ItemGroup extcaves_tab = new ItemGroup(MODID)
    {
    	@Override
        public ItemStack createIcon()
        {
            return new ItemStack(RegBlocks.MUSHROOM_GOLDISHROOM.get());
        }
    };

    private void doClientStuff(final FMLClientSetupEvent event) 
    {
        
        RenderTypeLookup.setRenderLayer(RegBlocks.MUSHROOM_SWEETSHROOM.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegBlocks.MUSHROOM_GOLDISHROOM.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegBlocks.MUSHROOM_SHINYSHROOM.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegBlocks.MUSHROOM_LUMISHROOM.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegBlocks.MUSHROOM_FLUOSHROOM.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegBlocks.MUSHROOM_ROCKSHROOM.get(), RenderType.getCutout());
        
        RenderTypeLookup.setRenderLayer(RegBlocks.MOSS_DRY.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegBlocks.MOSS_FIRE.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegBlocks.MOSS_FROZEN.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegBlocks.MOSS_HANGING_ROOTS.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegBlocks.MOSS_HUMID_GROUND.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegBlocks.MOSS_HUMID_CEILING.get(), RenderType.getCutout());
        
        RenderTypeLookup.setRenderLayer(RegBlocks.CAVE_VINE.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegBlocks.CAVE_VINE_END.get(), RenderType.getCutout());
        
    }
}
