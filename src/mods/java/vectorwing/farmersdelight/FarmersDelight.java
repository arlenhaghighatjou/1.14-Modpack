package vectorwing.farmersdelight;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vectorwing.farmersdelight.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.crafting.CuttingBoardRecipe;
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
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(FarmersDelight.MODID, "cooking"), CookingPotRecipe.TYPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(FarmersDelight.MODID, "cutting"), CuttingBoardRecipe.TYPE);
        ModRecipeSerializers.registerRecipeSerializers();
    }
}
