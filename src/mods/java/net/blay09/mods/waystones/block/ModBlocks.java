package net.blay09.mods.waystones.block;

import net.blay09.mods.waystones.Waystones;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class ModBlocks {
    public static Block waystone;
    public static Block mossyWaystone;
    public static Block sandyWaystone;

    public static void register() {
        waystone = registerBlock("waystone", new WaystoneBlock());
        mossyWaystone = registerBlock("mossy_waystone", new WaystoneBlock());
        sandyWaystone = registerBlock("sandy_waystone", new WaystoneBlock());
    }

    public static void registerBlockItems() {
        registerBlockItem("waystone", waystone);
        registerBlockItem("mossy_waystone", mossyWaystone);
        registerBlockItem("sandy_waystone", sandyWaystone);
    }

    private static Block registerBlock(String name, Block block) {
        Registry.register(Registry.BLOCK, new ResourceLocation(Waystones.MOD_ID, name), block);
        for (BlockState state : block.getStateContainer().getValidStates()) {
            state.func_215692_c();
            Block.BLOCK_STATE_IDS.add(state);
        }
        return block;
    }

    private static void registerBlockItem(String name, Block block) {
        BlockItem item = new BlockItem(block, new Item.Properties().group(Waystones.itemGroup));
        item.addToBlockToItemMap(Item.BLOCK_TO_ITEM, item);
        Registry.register(Registry.ITEM, new ResourceLocation(Waystones.MOD_ID, name), item);
    }

}
