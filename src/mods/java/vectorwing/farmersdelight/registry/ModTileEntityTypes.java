package vectorwing.farmersdelight.registry;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.tile.*;

public class ModTileEntityTypes
{
	public static TileEntityType<StoveTileEntity> STOVE_TILE;
	public static TileEntityType<CookingPotTileEntity> COOKING_POT_TILE;
	public static TileEntityType<BasketTileEntity> BASKET_TILE;
	public static TileEntityType<CuttingBoardTileEntity> CUTTING_BOARD_TILE;
	public static TileEntityType<PantryTileEntity> PANTRY_TILE;

	public static void registerTileEntityTypes()
	{
		STOVE_TILE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(FarmersDelight.MODID, "stove"), TileEntityType.Builder.create(StoveTileEntity::new, ModBlocks.STOVE).build(null));
		COOKING_POT_TILE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(FarmersDelight.MODID, "cooking_pot"), TileEntityType.Builder.create(CookingPotTileEntity::new, ModBlocks.COOKING_POT).build(null));
		BASKET_TILE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(FarmersDelight.MODID, "basket"), TileEntityType.Builder.create(BasketTileEntity::new, ModBlocks.BASKET).build(null));
		CUTTING_BOARD_TILE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(FarmersDelight.MODID, "cutting_board"), TileEntityType.Builder.create(CuttingBoardTileEntity::new, ModBlocks.CUTTING_BOARD).build(null));
		PANTRY_TILE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(FarmersDelight.MODID, "pantry"), TileEntityType.Builder.create(PantryTileEntity::new, ModBlocks.OAK_PANTRY, ModBlocks.BIRCH_PANTRY, ModBlocks.SPRUCE_PANTRY, ModBlocks.JUNGLE_PANTRY, ModBlocks.ACACIA_PANTRY, ModBlocks.DARK_OAK_PANTRY) .build(null));
	}
}
