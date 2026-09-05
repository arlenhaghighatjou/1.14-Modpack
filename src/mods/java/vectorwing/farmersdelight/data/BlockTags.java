package vectorwing.farmersdelight.data;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Items;
import vectorwing.farmersdelight.registry.ModBlocks;
import vectorwing.farmersdelight.registry.ModItems;
import vectorwing.farmersdelight.utils.tags.ForgeTags;
import vectorwing.farmersdelight.utils.tags.ModTags;

public class BlockTags extends BlockTagsProvider {
	public BlockTags(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	protected void registerTags() {
		getBuilder(net.minecraft.tags.BlockTags.CARPETS).add(
				ModBlocks.FULL_TATAMI_MAT,
				ModBlocks.HALF_TATAMI_MAT
		);
		getBuilder(net.minecraft.tags.BlockTags.CROPS).add(
				ModBlocks.CABBAGE_CROP,
				ModBlocks.ONION_CROP,
				ModBlocks.RICE_CROP,
				ModBlocks.TALL_RICE_CROP,
				ModBlocks.TOMATO_CROP);
		getBuilder(net.minecraft.tags.BlockTags.SMALL_FLOWERS).add(
				ModBlocks.WILD_BEETROOTS,
				ModBlocks.WILD_CABBAGES,
				ModBlocks.WILD_CARROTS,
				ModBlocks.WILD_ONIONS,
				ModBlocks.WILD_POTATOES,
				ModBlocks.WILD_TOMATOES);

		getBuilder(ModTags.TRAY_HEAT_SOURCES).add(Blocks.CAMPFIRE, Blocks.FIRE, Blocks.LAVA);
		getBuilder(ModTags.HEAT_SOURCES).add(Blocks.MAGMA_BLOCK, ModBlocks.STOVE).add(ModTags.TRAY_HEAT_SOURCES);
		getBuilder(ModTags.COMPOST_ACTIVATORS).add(
				Blocks.BROWN_MUSHROOM,
				Blocks.RED_MUSHROOM,
				Blocks.PODZOL,
				Blocks.MYCELIUM,
				ModBlocks.ORGANIC_COMPOST,
				ModBlocks.RICH_SOIL,
				ModBlocks.RICH_SOIL_FARMLAND,
				ModBlocks.BROWN_MUSHROOM_COLONY,
				ModBlocks.RED_MUSHROOM_COLONY);
	}
}
