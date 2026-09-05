/*******************************************************************************
 * Copyright 2014-2019, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/

package biomesoplenty.core;

import biomesoplenty.common.command.BOPCommand;
import biomesoplenty.init.*;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomesOPlenty
{
    public static final String MOD_ID = "biomesoplenty";

    public static BiomesOPlenty instance;
    public static CommonProxy proxy = new ClientProxy();

    public static Logger logger = LogManager.getLogger(MOD_ID);

    public BiomesOPlenty()
    {
    	instance = this;
    }

    public void commonSetup()
    {
        ModBlocks.registerBlocks();
        ModItems.registerItems();
        ModSounds.registerSounds();
        ModEntities.registerEntities();
        ModBiomes.setup();
        ModVanillaCompat.setup();
    }

    public void clientSetup()
    {
        ModEntities.registerRendering();
    }

    public void loadComplete()
    {
        proxy.init();
    }

    public void serverStarting(CommandDispatcher<CommandSource> dispatcher)
    {
        logger.info("Registering BoP commands...");
        new BOPCommand(dispatcher);
    }
}
