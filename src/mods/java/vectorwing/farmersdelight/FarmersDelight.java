package vectorwing.farmersdelight;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vectorwing.farmersdelight.registry.*;
import vectorwing.farmersdelight.setup.ClientEventHandler;
import vectorwing.farmersdelight.setup.CommonEventHandler;

public class FarmersDelight
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "farmersdelight";

    public static final FDItemGroup ITEM_GROUP = new FDItemGroup(FarmersDelight.MODID);

    public static void registerContent()
    {
        ModParticleTypes.registerParticleTypes();
        ModEnchantments.registerEnchantments();
        ModBlocks.registerBlocks();
        ModItems.registerItems();
        ModEffects.registerEffects();
        ModBiomeFeatures.registerFeatures();
        ModSounds.registerSounds();
        ModTileEntityTypes.registerTileEntityTypes();
        ModContainerTypes.registerContainerTypes();
        registerRecipeTypes();
    }

    public static void commonSetup()
    {
        CommonEventHandler.init();
    }

    public static void clientSetup()
    {
        ClientEventHandler.init();
    }

    private static void registerRecipeTypes()
    {
        // CookingPotRecipe.TYPE and CuttingBoardRecipe.TYPE register themselves through IRecipeType.register
        ModRecipeSerializers.registerRecipeSerializers();
    }
}
