package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.inventory.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ScreenManager {
    private static final Logger LOG = LogManager.getLogger();
    private static final Map<ContainerType<?>, ScreenManager.IScreenFactory<?, ?>> FACTORIES = Maps.newHashMap();

    public static <T extends Container> void openScreen(ContainerType<T> type, Minecraft mc, int windowId, ITextComponent title) {
        if (type == null) {
            LOG.warn("Trying to open invalid screen with name: {}", (Object) title.getString());
        } else {
            ScreenManager.IScreenFactory<T, ?> iscreenfactory = getFactory(type);
            if (iscreenfactory == null) {
                LOG.warn("Failed to create screen for menu type: {}", (Object) Registry.MENU.getKey(type));
            } else {
                iscreenfactory.createScreen(title, type, mc, windowId);
            }
        }
    }

    private static <T extends Container> ScreenManager.IScreenFactory<T, ?> getFactory(ContainerType<T> type) {
        return (ScreenManager.IScreenFactory<T, ?>) FACTORIES.get(type);
    }

    private static <M extends Container, U extends Screen & IHasContainer<M>> void registerFactory(ContainerType<? extends M> p_216911_0_, ScreenManager.IScreenFactory<M, U> p_216911_1_) {
        ScreenManager.IScreenFactory<?, ?> iscreenfactory = FACTORIES.put(p_216911_0_, p_216911_1_);
        if (iscreenfactory != null) {
            throw new IllegalStateException("Duplicate registration for " + Registry.MENU.getKey(p_216911_0_));
        }
    }

    public static boolean isMissingScreen() {
        boolean flag = false;

        for (ContainerType<?> containertype : Registry.MENU) {
            if (!FACTORIES.containsKey(containertype)) {
                LOG.debug("Menu {} has no matching screen", (Object) Registry.MENU.getKey(containertype));
                flag = true;
            }
        }

        return flag;
    }

    static {
        registerFactory(ContainerType.GENERIC_9X1, ChestScreen::new);
        registerFactory(ContainerType.GENERIC_9X2, ChestScreen::new);
        registerFactory(ContainerType.GENERIC_9X3, ChestScreen::new);
        registerFactory(ContainerType.GENERIC_9X4, ChestScreen::new);
        registerFactory(ContainerType.GENERIC_9X5, ChestScreen::new);
        registerFactory(ContainerType.GENERIC_9X6, ChestScreen::new);
        registerFactory(ContainerType.GENERIC_3X3, DispenserScreen::new);
        registerFactory(ContainerType.ANVIL, AnvilScreen::new);
        registerFactory(ContainerType.BEACON, BeaconScreen::new);
        registerFactory(ContainerType.BLAST_FURNACE, BlastFurnaceScreen::new);
        registerFactory(ContainerType.BREWING_STAND, BrewingStandScreen::new);
        registerFactory(ContainerType.CRAFTING, CraftingScreen::new);
        registerFactory(ContainerType.ENCHANTMENT, EnchantmentScreen::new);
        registerFactory(ContainerType.FURNACE, FurnaceScreen::new);
        registerFactory(ContainerType.GRINDSTONE, GrindstoneScreen::new);
        registerFactory(ContainerType.HOPPER, HopperScreen::new);
        registerFactory(ContainerType.LECTERN, LecternScreen::new);
        registerFactory(ContainerType.LOOM, LoomScreen::new);
        registerFactory(ContainerType.MERCHANT, MerchantScreen::new);
        registerFactory(ContainerType.SHULKER_BOX, ShulkerBoxScreen::new);
        registerFactory(ContainerType.SMOKER, SmokerScreen::new);
        registerFactory(ContainerType.CARTOGRAPHY, CartographyTableScreen::new);
        registerFactory(ContainerType.STONECUTTER, StonecutterScreen::new);
    }

    @OnlyIn(Dist.CLIENT)
    interface IScreenFactory<T extends Container, U extends Screen & IHasContainer<T>> {
        default void createScreen(ITextComponent title, ContainerType<T> type, Minecraft mc, int windowId) {
            U u = this.create(type.create(windowId, mc.player.inventory), mc.player.inventory, title);
            mc.player.openContainer = ((IHasContainer) u).getContainer();
            mc.displayGuiScreen(u);
        }

        U create(T p_create_1_, PlayerInventory p_create_2_, ITextComponent p_create_3_);
    }
}
