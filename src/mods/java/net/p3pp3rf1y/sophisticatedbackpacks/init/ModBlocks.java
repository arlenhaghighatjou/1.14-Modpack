package net.p3pp3rf1y.sophisticatedbackpacks.init;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.registry.Registry;
import net.p3pp3rf1y.sophisticatedbackpacks.util.registry.ModRegistry;
import net.p3pp3rf1y.sophisticatedbackpacks.util.registry.RegistryObject;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlock;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackTileEntity;

public class ModBlocks {
	private static final ModRegistry<Block> BLOCKS = new ModRegistry<>(Registry.BLOCK, SophisticatedBackpacks.MOD_ID);
	private static final ModRegistry<TileEntityType<?>> TILE_ENTITIES = new ModRegistry<>(Registry.BLOCK_ENTITY_TYPE, SophisticatedBackpacks.MOD_ID);

	private ModBlocks() {}

	public static final RegistryObject<BackpackBlock> BACKPACK = BLOCKS.register("backpack", BackpackBlock::new);
	public static final RegistryObject<BackpackBlock> IRON_BACKPACK = BLOCKS.register("iron_backpack", BackpackBlock::new);
	public static final RegistryObject<BackpackBlock> GOLD_BACKPACK = BLOCKS.register("gold_backpack", BackpackBlock::new);
	public static final RegistryObject<BackpackBlock> DIAMOND_BACKPACK = BLOCKS.register("diamond_backpack", BackpackBlock::new);
	public static final RegistryObject<BackpackBlock> NETHERITE_BACKPACK = BLOCKS.register("netherite_backpack", BackpackBlock::new);

	@SuppressWarnings("ConstantConditions") //no datafixer type needed
	public static final RegistryObject<TileEntityType<BackpackTileEntity>> BACKPACK_TILE_TYPE = TILE_ENTITIES.register("backpack", () ->
			TileEntityType.Builder.create(BackpackTileEntity::new, BACKPACK, IRON_BACKPACK, GOLD_BACKPACK, DIAMOND_BACKPACK, NETHERITE_BACKPACK)
					.build(null));

	public static void registerHandlers() {
		BLOCKS.register();
		TILE_ENTITIES.register();
	}
}
