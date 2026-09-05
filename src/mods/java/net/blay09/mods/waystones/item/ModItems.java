package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.Waystones;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class ModItems {
    public static Item returnScroll;
    public static Item boundScroll;
    public static Item warpScroll;
    public static Item warpStone;

    public static void register() {
        returnScroll = registerItem(ReturnScrollItem.registryName, new ReturnScrollItem());
        boundScroll = registerItem(BoundScrollItem.registryName, new BoundScrollItem());
        warpScroll = registerItem(WarpScrollItem.registryName, new WarpScrollItem());
        warpStone = registerItem(WarpStoneItem.registryName, new WarpStoneItem());
    }

    private static Item registerItem(ResourceLocation registryName, Item item) {
        return Registry.register(Registry.ITEM, registryName, item);
    }
}
