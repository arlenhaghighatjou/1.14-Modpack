package vectorwing.farmersdelight.loot.modifiers;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import vectorwing.farmersdelight.blocks.RiceUpperCropBlock;
import vectorwing.farmersdelight.registry.ModBlocks;
import vectorwing.farmersdelight.registry.ModItems;
import vectorwing.farmersdelight.utils.tags.ModTags;

import java.util.List;

/**
 * Extra drops Farmer's Delight grants on top of a block's own loot table.
 */
public class LootModifiers
{
	private static final int CAKE_SLICES = 7;
	private static final float GRASS_STRAW_CHANCE = 0.2F;

	public static List<ItemStack> apply(BlockState state, LootContext context, List<ItemStack> generatedLoot)
	{
		ItemStack tool = context.get(LootParameters.TOOL);
		if (tool == null || tool.isEmpty()) {
			return generatedLoot;
		}

		if (ModTags.KNIVES.contains(tool.getItem()) && state.getBlock() == Blocks.CAKE) {
			generatedLoot.add(new ItemStack(ModItems.CAKE_SLICE, CAKE_SLICES - state.get(BlockStateProperties.BITES_0_6)));
		}

		if (ModTags.STRAW_HARVESTERS.contains(tool.getItem()) && dropsStraw(state, context)) {
			generatedLoot.add(new ItemStack(ModItems.STRAW));
		}

		return generatedLoot;
	}

	private static boolean dropsStraw(BlockState state, LootContext context)
	{
		if (state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.TALL_GRASS) {
			return context.getRandom().nextFloat() < GRASS_STRAW_CHANCE;
		}

		if (state.getBlock() == Blocks.WHEAT) {
			return state.get(BlockStateProperties.AGE_0_7) == 7;
		}

		if (state.getBlock() == ModBlocks.RICE_UPPER_CROP) {
			return state.get(RiceUpperCropBlock.RICE_AGE) == 3;
		}

		if (state.getBlock() == ModBlocks.TALL_RICE_CROP) {
			return state.get(BlockStateProperties.AGE_0_7) == 7;
		}

		return false;
	}
}
