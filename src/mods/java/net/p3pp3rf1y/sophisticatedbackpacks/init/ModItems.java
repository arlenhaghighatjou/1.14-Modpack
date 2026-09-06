package net.p3pp3rf1y.sophisticatedbackpacks.init;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.BlastingRecipe;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SmokingRecipe;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackScreen;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SettingsScreen;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.UpgradeGuiManager;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.SettingsContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.UpgradeContainerRegistry;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.UpgradeContainerType;
import net.p3pp3rf1y.sophisticatedbackpacks.crafting.BackpackDyeRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.crafting.BackpackUpgradeRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.crafting.SmithingBackpackUpgradeRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.crafting.UpgradeClearRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.crafting.UpgradeNextTierRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.ContentsFilteredUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.FilteredUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.battery.BatteryInventoryPart;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.battery.BatteryUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.battery.BatteryUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.battery.BatteryUpgradeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.battery.BatteryUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.compacting.CompactingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.compacting.CompactingUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.compacting.CompactingUpgradeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.compacting.CompactingUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.cooking.AutoBlastingUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.cooking.AutoCookingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.cooking.AutoCookingUpgradeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.cooking.AutoCookingUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.cooking.AutoSmeltingUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.cooking.AutoSmokingUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.cooking.BlastingUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.cooking.CookingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.cooking.CookingUpgradeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.cooking.CookingUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.cooking.SmeltingUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.cooking.SmokingUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.crafting.CraftingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.crafting.CraftingUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.crafting.CraftingUpgradeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.crafting.CraftingUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit.DepositUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit.DepositUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit.DepositUpgradeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit.DepositUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.everlasting.EverlastingBackpackItemEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.everlasting.EverlastingUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.feeding.FeedingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.feeding.FeedingUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.feeding.FeedingUpgradeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.feeding.FeedingUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.filter.FilterUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.filter.FilterUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.filter.FilterUpgradeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.inception.InceptionUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.inception.InceptionUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.inception.InceptionUpgradeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.inception.InceptionUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.jukebox.JukeboxUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.jukebox.JukeboxUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.jukebox.JukeboxUpgradeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.magnet.MagnetUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.magnet.MagnetUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.magnet.MagnetUpgradeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.magnet.MagnetUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.pickup.PickupUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.pickup.PickupUpgradeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.pickup.PickupUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.pump.PumpUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.pump.PumpUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.pump.PumpUpgradeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.pump.PumpUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.refill.RefillUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.refill.RefillUpgradeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.refill.RefillUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.restock.RestockUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.restock.RestockUpgradeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.restock.RestockUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.stack.StackUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.stonecutter.StonecutterUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.stonecutter.StonecutterUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.stonecutter.StonecutterUpgradeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.stonecutter.StonecutterUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.tank.TankInventoryPart;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.tank.TankUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.tank.TankUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.tank.TankUpgradeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.tank.TankUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.toolswapper.ToolSwapperUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.toolswapper.ToolSwapperUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.toolswapper.ToolSwapperUpgradeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.toolswapper.ToolSwapperUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.voiding.VoidUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.voiding.VoidUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.voiding.VoidUpgradeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.voiding.VoidUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.xppump.XpPumpUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.xppump.XpPumpUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.xppump.XpPumpUpgradeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.xppump.XpPumpUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.util.ItemBase;

public class ModItems {
	private ModItems() {}


	public static BackpackItem BACKPACK;
	public static BackpackItem IRON_BACKPACK;
	public static BackpackItem GOLD_BACKPACK;
	public static BackpackItem DIAMOND_BACKPACK;
	public static BackpackItem NETHERITE_BACKPACK;
	public static PickupUpgradeItem PICKUP_UPGRADE;
	public static PickupUpgradeItem ADVANCED_PICKUP_UPGRADE;
	public static FilterUpgradeItem FILTER_UPGRADE;
	public static FilterUpgradeItem ADVANCED_FILTER_UPGRADE;
	public static MagnetUpgradeItem MAGNET_UPGRADE;
	public static MagnetUpgradeItem ADVANCED_MAGNET_UPGRADE;
	public static FeedingUpgradeItem FEEDING_UPGRADE;
	public static FeedingUpgradeItem ADVANCED_FEEDING_UPGRADE;
	public static CompactingUpgradeItem COMPACTING_UPGRADE;
	public static CompactingUpgradeItem ADVANCED_COMPACTING_UPGRADE;
	public static VoidUpgradeItem VOID_UPGRADE;
	public static VoidUpgradeItem ADVANCED_VOID_UPGRADE;
	public static RestockUpgradeItem RESTOCK_UPGRADE;
	public static RestockUpgradeItem ADVANCED_RESTOCK_UPGRADE;
	public static DepositUpgradeItem DEPOSIT_UPGRADE;
	public static DepositUpgradeItem ADVANCED_DEPOSIT_UPGRADE;
	public static RefillUpgradeItem REFILL_UPGRADE;
	public static InceptionUpgradeItem INCEPTION_UPGRADE;
	public static EverlastingUpgradeItem EVERLASTING_UPGRADE;
	public static SmeltingUpgradeItem SMELTING_UPGRADE;
	public static AutoSmeltingUpgradeItem AUTO_SMELTING_UPGRADE;
	public static SmokingUpgradeItem SMOKING_UPGRADE;
	public static AutoSmokingUpgradeItem AUTO_SMOKING_UPGRADE;
	public static BlastingUpgradeItem BLASTING_UPGRADE;
	public static AutoBlastingUpgradeItem AUTO_BLASTING_UPGRADE;
	public static CraftingUpgradeItem CRAFTING_UPGRADE;
	public static StonecutterUpgradeItem STONECUTTER_UPGRADE;
	public static StackUpgradeItem STACK_UPGRADE_TIER_1;
	public static StackUpgradeItem STACK_UPGRADE_TIER_2;
	public static StackUpgradeItem STACK_UPGRADE_TIER_3;
	public static StackUpgradeItem STACK_UPGRADE_TIER_4;
	public static JukeboxUpgradeItem JUKEBOX_UPGRADE;
	public static ToolSwapperUpgradeItem TOOL_SWAPPER_UPGRADE;
	public static ToolSwapperUpgradeItem ADVANCED_TOOL_SWAPPER_UPGRADE;
	public static TankUpgradeItem TANK_UPGRADE;
	public static BatteryUpgradeItem BATTERY_UPGRADE;
	public static PumpUpgradeItem PUMP_UPGRADE;
	public static PumpUpgradeItem ADVANCED_PUMP_UPGRADE;
	public static XpPumpUpgradeItem XP_PUMP_UPGRADE;

	public static ItemBase UPGRADE_BASE;

	public static ContainerType<BackpackContainer> BACKPACK_CONTAINER_TYPE;

	public static ContainerType<SettingsContainer> SETTINGS_CONTAINER_TYPE;

	public static EntityType<EverlastingBackpackItemEntity> EVERLASTING_BACKPACK_ITEM_ENTITY;


	public static void registerContent() {
		BACKPACK = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "backpack"), new BackpackItem(() -> Config.COMMON.leatherBackpack.inventorySlotCount, () -> Config.COMMON.leatherBackpack.upgradeSlotCount, ModBlocks.BACKPACK));
		IRON_BACKPACK = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "iron_backpack"), new BackpackItem(() -> Config.COMMON.ironBackpack.inventorySlotCount, () -> Config.COMMON.ironBackpack.upgradeSlotCount, ModBlocks.IRON_BACKPACK));
		GOLD_BACKPACK = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "gold_backpack"), new BackpackItem(() -> Config.COMMON.goldBackpack.inventorySlotCount, () -> Config.COMMON.goldBackpack.upgradeSlotCount, ModBlocks.GOLD_BACKPACK));
		DIAMOND_BACKPACK = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "diamond_backpack"), new BackpackItem(() -> Config.COMMON.diamondBackpack.inventorySlotCount, () -> Config.COMMON.diamondBackpack.upgradeSlotCount, ModBlocks.DIAMOND_BACKPACK));
		NETHERITE_BACKPACK = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "netherite_backpack"), new BackpackItem(() -> Config.COMMON.netheriteBackpack.inventorySlotCount, () -> Config.COMMON.netheriteBackpack.upgradeSlotCount, ModBlocks.NETHERITE_BACKPACK));
		PICKUP_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "pickup_upgrade"), new PickupUpgradeItem(() -> Config.COMMON.pickupUpgrade.filterSlots));
		ADVANCED_PICKUP_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "advanced_pickup_upgrade"), new PickupUpgradeItem(() -> Config.COMMON.advancedPickupUpgrade.filterSlots));
		FILTER_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "filter_upgrade"), new FilterUpgradeItem(() -> Config.COMMON.filterUpgrade.filterSlots));
		ADVANCED_FILTER_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "advanced_filter_upgrade"), new FilterUpgradeItem(() -> Config.COMMON.advancedFilterUpgrade.filterSlots));
		MAGNET_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "magnet_upgrade"), new MagnetUpgradeItem(() -> Config.COMMON.magnetUpgrade.magnetRange, () -> Config.COMMON.magnetUpgrade.filterSlots));
		ADVANCED_MAGNET_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "advanced_magnet_upgrade"), new MagnetUpgradeItem(() -> Config.COMMON.advancedMagnetUpgrade.magnetRange, () -> Config.COMMON.advancedMagnetUpgrade.filterSlots));
		FEEDING_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "feeding_upgrade"), new FeedingUpgradeItem(() -> Config.COMMON.feedingUpgrade.filterSlots));
		ADVANCED_FEEDING_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "advanced_feeding_upgrade"), new FeedingUpgradeItem(() -> Config.COMMON.advancedFeedingUpgrade.filterSlots));
		COMPACTING_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "compacting_upgrade"), new CompactingUpgradeItem(false, () -> Config.COMMON.compactingUpgrade.filterSlots));
		ADVANCED_COMPACTING_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "advanced_compacting_upgrade"), new CompactingUpgradeItem(true, () -> Config.COMMON.advancedCompactingUpgrade.filterSlots));
		VOID_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "void_upgrade"), new VoidUpgradeItem(() -> Config.COMMON.voidUpgrade.filterSlots));
		ADVANCED_VOID_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "advanced_void_upgrade"), new VoidUpgradeItem(() -> Config.COMMON.advancedVoidUpgrade.filterSlots));
		RESTOCK_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "restock_upgrade"), new RestockUpgradeItem(() -> Config.COMMON.restockUpgrade.filterSlots));
		ADVANCED_RESTOCK_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "advanced_restock_upgrade"), new RestockUpgradeItem(() -> Config.COMMON.advancedRestockUpgrade.filterSlots));
		DEPOSIT_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "deposit_upgrade"), new DepositUpgradeItem(() -> Config.COMMON.depositUpgrade.filterSlots));
		ADVANCED_DEPOSIT_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "advanced_deposit_upgrade"), new DepositUpgradeItem(() -> Config.COMMON.advancedDepositUpgrade.filterSlots));
		REFILL_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "refill_upgrade"), new RefillUpgradeItem());
		INCEPTION_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "inception_upgrade"), new InceptionUpgradeItem());
		EVERLASTING_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "everlasting_upgrade"), new EverlastingUpgradeItem());
		SMELTING_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "smelting_upgrade"), new SmeltingUpgradeItem());
		AUTO_SMELTING_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "auto_smelting_upgrade"), new AutoSmeltingUpgradeItem());
		SMOKING_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "smoking_upgrade"), new SmokingUpgradeItem());
		AUTO_SMOKING_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "auto_smoking_upgrade"), new AutoSmokingUpgradeItem());
		BLASTING_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "blasting_upgrade"), new BlastingUpgradeItem());
		AUTO_BLASTING_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "auto_blasting_upgrade"), new AutoBlastingUpgradeItem());
		CRAFTING_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "crafting_upgrade"), new CraftingUpgradeItem());
		STONECUTTER_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "stonecutter_upgrade"), new StonecutterUpgradeItem());
		STACK_UPGRADE_TIER_1 = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "stack_upgrade_tier_1"), new StackUpgradeItem(2));
		STACK_UPGRADE_TIER_2 = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "stack_upgrade_tier_2"), new StackUpgradeItem(4));
		STACK_UPGRADE_TIER_3 = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "stack_upgrade_tier_3"), new StackUpgradeItem(8));
		STACK_UPGRADE_TIER_4 = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "stack_upgrade_tier_4"), new StackUpgradeItem(16));
		JUKEBOX_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "jukebox_upgrade"), new JukeboxUpgradeItem());
		TOOL_SWAPPER_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "tool_swapper_upgrade"), new ToolSwapperUpgradeItem(false, false));
		ADVANCED_TOOL_SWAPPER_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "advanced_tool_swapper_upgrade"), new ToolSwapperUpgradeItem(true, true));
		TANK_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "tank_upgrade"), new TankUpgradeItem());
		BATTERY_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "battery_upgrade"), new BatteryUpgradeItem());
		PUMP_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "pump_upgrade"), new PumpUpgradeItem(false, false));
		ADVANCED_PUMP_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "advanced_pump_upgrade"), new PumpUpgradeItem(true, true));
		XP_PUMP_UPGRADE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "xp_pump_upgrade"), new XpPumpUpgradeItem());
		UPGRADE_BASE = Registry.register(Registry.ITEM, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "upgrade_base"), new ItemBase(new Item.Properties().stacksTo(16)));
		BACKPACK_CONTAINER_TYPE = Registry.register(Registry.MENU, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "backpack"), new ContainerType<>(BackpackContainer::fromOpenData));
		SETTINGS_CONTAINER_TYPE = Registry.register(Registry.MENU, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "settings"), new ContainerType<>(SettingsContainer::fromOpenData));
		EVERLASTING_BACKPACK_ITEM_ENTITY = Registry.register(Registry.ENTITY_TYPE, new ResourceLocation(SophisticatedBackpacks.MOD_ID, "everlasting_backpack_item"), EntityType.Builder.<EverlastingBackpackItemEntity>create(EverlastingBackpackItemEntity::new, EntityClassification.MISC).size(0.25F, 0.25F).build("everlasting_backpack_item"));
	}


	private static final UpgradeContainerType<PickupUpgradeWrapper, ContentsFilteredUpgradeContainer<PickupUpgradeWrapper>> PICKUP_BASIC_TYPE = new UpgradeContainerType<>(ContentsFilteredUpgradeContainer::new);
	private static final UpgradeContainerType<PickupUpgradeWrapper, ContentsFilteredUpgradeContainer<PickupUpgradeWrapper>> PICKUP_ADVANCED_TYPE = new UpgradeContainerType<>(ContentsFilteredUpgradeContainer::new);
	private static final UpgradeContainerType<MagnetUpgradeWrapper, MagnetUpgradeContainer> MAGNET_BASIC_TYPE = new UpgradeContainerType<>(MagnetUpgradeContainer::new);
	private static final UpgradeContainerType<MagnetUpgradeWrapper, MagnetUpgradeContainer> MAGNET_ADVANCED_TYPE = new UpgradeContainerType<>(MagnetUpgradeContainer::new);
	private static final UpgradeContainerType<FeedingUpgradeWrapper, FeedingUpgradeContainer> FEEDING_TYPE = new UpgradeContainerType<>(FeedingUpgradeContainer::new);
	private static final UpgradeContainerType<FeedingUpgradeWrapper, FeedingUpgradeContainer> ADVANCED_FEEDING_TYPE = new UpgradeContainerType<>(FeedingUpgradeContainer::new);
	private static final UpgradeContainerType<CompactingUpgradeWrapper, CompactingUpgradeContainer> COMPACTING_TYPE = new UpgradeContainerType<>(CompactingUpgradeContainer::new);
	private static final UpgradeContainerType<CompactingUpgradeWrapper, CompactingUpgradeContainer> ADVANCED_COMPACTING_TYPE = new UpgradeContainerType<>(CompactingUpgradeContainer::new);
	private static final UpgradeContainerType<VoidUpgradeWrapper, VoidUpgradeContainer> VOID_TYPE = new UpgradeContainerType<>(VoidUpgradeContainer::new);
	private static final UpgradeContainerType<VoidUpgradeWrapper, VoidUpgradeContainer> ADVANCED_VOID_TYPE = new UpgradeContainerType<>(VoidUpgradeContainer::new);
	private static final UpgradeContainerType<RestockUpgradeWrapper, ContentsFilteredUpgradeContainer<RestockUpgradeWrapper>> RESTOCK_TYPE = new UpgradeContainerType<>(ContentsFilteredUpgradeContainer::new);
	private static final UpgradeContainerType<RestockUpgradeWrapper, ContentsFilteredUpgradeContainer<RestockUpgradeWrapper>> ADVANCED_RESTOCK_TYPE = new UpgradeContainerType<>(ContentsFilteredUpgradeContainer::new);
	private static final UpgradeContainerType<DepositUpgradeWrapper, DepositUpgradeContainer> DEPOSIT_TYPE = new UpgradeContainerType<>(DepositUpgradeContainer::new);
	private static final UpgradeContainerType<DepositUpgradeWrapper, DepositUpgradeContainer> ADVANCED_DEPOSIT_TYPE = new UpgradeContainerType<>(DepositUpgradeContainer::new);
	private static final UpgradeContainerType<RefillUpgradeWrapper, FilteredUpgradeContainer<RefillUpgradeWrapper>> REFILL_TYPE = new UpgradeContainerType<>(FilteredUpgradeContainer::new);
	private static final UpgradeContainerType<CookingUpgradeWrapper.SmeltingUpgradeWrapper, CookingUpgradeContainer<FurnaceRecipe, CookingUpgradeWrapper.SmeltingUpgradeWrapper>> SMELTING_TYPE = new UpgradeContainerType<>(CookingUpgradeContainer::new);
	private static final UpgradeContainerType<AutoCookingUpgradeWrapper.AutoSmeltingUpgradeWrapper, AutoCookingUpgradeContainer<FurnaceRecipe, AutoCookingUpgradeWrapper.AutoSmeltingUpgradeWrapper>> AUTO_SMELTING_TYPE = new UpgradeContainerType<>(AutoCookingUpgradeContainer::new);
	private static final UpgradeContainerType<CookingUpgradeWrapper.SmokingUpgradeWrapper, CookingUpgradeContainer<SmokingRecipe, CookingUpgradeWrapper.SmokingUpgradeWrapper>> SMOKING_TYPE = new UpgradeContainerType<>(CookingUpgradeContainer::new);
	private static final UpgradeContainerType<AutoCookingUpgradeWrapper.AutoSmokingUpgradeWrapper, AutoCookingUpgradeContainer<SmokingRecipe, AutoCookingUpgradeWrapper.AutoSmokingUpgradeWrapper>> AUTO_SMOKING_TYPE = new UpgradeContainerType<>(AutoCookingUpgradeContainer::new);
	private static final UpgradeContainerType<CookingUpgradeWrapper.BlastingUpgradeWrapper, CookingUpgradeContainer<BlastingRecipe, CookingUpgradeWrapper.BlastingUpgradeWrapper>> BLASTING_TYPE = new UpgradeContainerType<>(CookingUpgradeContainer::new);
	private static final UpgradeContainerType<AutoCookingUpgradeWrapper.AutoBlastingUpgradeWrapper, AutoCookingUpgradeContainer<BlastingRecipe, AutoCookingUpgradeWrapper.AutoBlastingUpgradeWrapper>> AUTO_BLASTING_TYPE = new UpgradeContainerType<>(AutoCookingUpgradeContainer::new);
	private static final UpgradeContainerType<CraftingUpgradeWrapper, CraftingUpgradeContainer> CRAFTING_TYPE = new UpgradeContainerType<>(CraftingUpgradeContainer::new);
	private static final UpgradeContainerType<InceptionUpgradeWrapper, InceptionUpgradeContainer> INCEPTION_TYPE = new UpgradeContainerType<>(InceptionUpgradeContainer::new);
	private static final UpgradeContainerType<StonecutterUpgradeWrapper, StonecutterUpgradeContainer> STONECUTTER_TYPE = new UpgradeContainerType<>(StonecutterUpgradeContainer::new);
	private static final UpgradeContainerType<JukeboxUpgradeItem.Wrapper, JukeboxUpgradeContainer> JUKEBOX_TYPE = new UpgradeContainerType<>(JukeboxUpgradeContainer::new);
	private static final UpgradeContainerType<ToolSwapperUpgradeWrapper, ToolSwapperUpgradeContainer> TOOL_SWAPPER_TYPE = new UpgradeContainerType<>(ToolSwapperUpgradeContainer::new);
	private static final UpgradeContainerType<TankUpgradeWrapper, TankUpgradeContainer> TANK_TYPE = new UpgradeContainerType<>(TankUpgradeContainer::new);
	private static final UpgradeContainerType<BatteryUpgradeWrapper, BatteryUpgradeContainer> BATTERY_TYPE = new UpgradeContainerType<>(BatteryUpgradeContainer::new);
	private static final UpgradeContainerType<PumpUpgradeWrapper, PumpUpgradeContainer> PUMP_TYPE = new UpgradeContainerType<>(PumpUpgradeContainer::new);
	private static final UpgradeContainerType<PumpUpgradeWrapper, PumpUpgradeContainer> ADVANCED_PUMP_TYPE = new UpgradeContainerType<>(PumpUpgradeContainer::new);
	private static final UpgradeContainerType<XpPumpUpgradeWrapper, XpPumpUpgradeContainer> XP_PUMP_TYPE = new UpgradeContainerType<>(XpPumpUpgradeContainer::new);

	public static void registerContainers() {
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(PICKUP_UPGRADE), PICKUP_BASIC_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(ADVANCED_PICKUP_UPGRADE), PICKUP_ADVANCED_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(FILTER_UPGRADE), FilterUpgradeContainer.BASIC_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(ADVANCED_FILTER_UPGRADE), FilterUpgradeContainer.ADVANCED_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(MAGNET_UPGRADE), MAGNET_BASIC_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(ADVANCED_MAGNET_UPGRADE), MAGNET_ADVANCED_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(FEEDING_UPGRADE), FEEDING_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(ADVANCED_FEEDING_UPGRADE), ADVANCED_FEEDING_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(COMPACTING_UPGRADE), COMPACTING_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(ADVANCED_COMPACTING_UPGRADE), ADVANCED_COMPACTING_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(VOID_UPGRADE), VOID_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(ADVANCED_VOID_UPGRADE), ADVANCED_VOID_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(RESTOCK_UPGRADE), RESTOCK_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(ADVANCED_RESTOCK_UPGRADE), ADVANCED_RESTOCK_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(DEPOSIT_UPGRADE), DEPOSIT_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(ADVANCED_DEPOSIT_UPGRADE), ADVANCED_DEPOSIT_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(REFILL_UPGRADE), REFILL_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(SMELTING_UPGRADE), SMELTING_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(AUTO_SMELTING_UPGRADE), AUTO_SMELTING_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(SMOKING_UPGRADE), SMOKING_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(AUTO_SMOKING_UPGRADE), AUTO_SMOKING_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(BLASTING_UPGRADE), BLASTING_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(AUTO_BLASTING_UPGRADE), AUTO_BLASTING_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(CRAFTING_UPGRADE), CRAFTING_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(INCEPTION_UPGRADE), INCEPTION_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(STONECUTTER_UPGRADE), STONECUTTER_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(JUKEBOX_UPGRADE), JUKEBOX_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(ADVANCED_TOOL_SWAPPER_UPGRADE), TOOL_SWAPPER_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(TANK_UPGRADE), TANK_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(BATTERY_UPGRADE), BATTERY_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(PUMP_UPGRADE), PUMP_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(ADVANCED_PUMP_UPGRADE), ADVANCED_PUMP_TYPE);
		UpgradeContainerRegistry.register(Registry.ITEM.getKey(XP_PUMP_UPGRADE), XP_PUMP_TYPE);

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			ScreenManager.register(BACKPACK_CONTAINER_TYPE, BackpackScreen::constructScreen);
			ScreenManager.register(SETTINGS_CONTAINER_TYPE, SettingsScreen::constructScreen);

			UpgradeGuiManager.registerTab(PICKUP_BASIC_TYPE, PickupUpgradeTab.Basic::new);
			UpgradeGuiManager.registerTab(PICKUP_ADVANCED_TYPE, PickupUpgradeTab.Advanced::new);
			UpgradeGuiManager.registerTab(FilterUpgradeContainer.BASIC_TYPE, FilterUpgradeTab.Basic::new);
			UpgradeGuiManager.registerTab(FilterUpgradeContainer.ADVANCED_TYPE, FilterUpgradeTab.Advanced::new);
			UpgradeGuiManager.registerTab(MAGNET_BASIC_TYPE, MagnetUpgradeTab.Basic::new);
			UpgradeGuiManager.registerTab(MAGNET_ADVANCED_TYPE, MagnetUpgradeTab.Advanced::new);
			UpgradeGuiManager.registerTab(FEEDING_TYPE, FeedingUpgradeTab.Basic::new);
			UpgradeGuiManager.registerTab(ADVANCED_FEEDING_TYPE, FeedingUpgradeTab.Advanced::new);
			UpgradeGuiManager.registerTab(COMPACTING_TYPE, CompactingUpgradeTab.Basic::new);
			UpgradeGuiManager.registerTab(ADVANCED_COMPACTING_TYPE, CompactingUpgradeTab.Advanced::new);
			UpgradeGuiManager.registerTab(VOID_TYPE, VoidUpgradeTab.Basic::new);
			UpgradeGuiManager.registerTab(ADVANCED_VOID_TYPE, VoidUpgradeTab.Advanced::new);
			UpgradeGuiManager.registerTab(RESTOCK_TYPE, RestockUpgradeTab.Basic::new);
			UpgradeGuiManager.registerTab(ADVANCED_RESTOCK_TYPE, RestockUpgradeTab.Advanced::new);
			UpgradeGuiManager.registerTab(DEPOSIT_TYPE, DepositUpgradeTab.Basic::new);
			UpgradeGuiManager.registerTab(ADVANCED_DEPOSIT_TYPE, DepositUpgradeTab.Advanced::new);
			UpgradeGuiManager.registerTab(REFILL_TYPE, RefillUpgradeTab::new);
			UpgradeGuiManager.registerTab(SMELTING_TYPE, CookingUpgradeTab.SmeltingUpgradeTab::new);
			UpgradeGuiManager.registerTab(AUTO_SMELTING_TYPE, AutoCookingUpgradeTab.AutoSmeltingUpgradeTab::new);
			UpgradeGuiManager.registerTab(SMOKING_TYPE, CookingUpgradeTab.SmokingUpgradeTab::new);
			UpgradeGuiManager.registerTab(AUTO_SMOKING_TYPE, AutoCookingUpgradeTab.AutoSmokingUpgradeTab::new);
			UpgradeGuiManager.registerTab(BLASTING_TYPE, CookingUpgradeTab.BlastingUpgradeTab::new);
			UpgradeGuiManager.registerTab(AUTO_BLASTING_TYPE, AutoCookingUpgradeTab.AutoBlastingUpgradeTab::new);
			UpgradeGuiManager.registerTab(CRAFTING_TYPE, CraftingUpgradeTab::new);
			UpgradeGuiManager.registerTab(INCEPTION_TYPE, InceptionUpgradeTab::new);
			UpgradeGuiManager.registerTab(STONECUTTER_TYPE, StonecutterUpgradeTab::new);
			UpgradeGuiManager.registerTab(JUKEBOX_TYPE, JukeboxUpgradeTab::new);
			UpgradeGuiManager.registerTab(TOOL_SWAPPER_TYPE, ToolSwapperUpgradeTab::new);
			UpgradeGuiManager.registerTab(TANK_TYPE, TankUpgradeTab::new);
			UpgradeGuiManager.registerTab(BATTERY_TYPE, BatteryUpgradeTab::new);
			UpgradeGuiManager.registerInventoryPart(TANK_TYPE, TankInventoryPart::new);
			UpgradeGuiManager.registerInventoryPart(BATTERY_TYPE, BatteryInventoryPart::new);
			UpgradeGuiManager.registerTab(PUMP_TYPE, PumpUpgradeTab.Basic::new);
			UpgradeGuiManager.registerTab(ADVANCED_PUMP_TYPE, PumpUpgradeTab.Advanced::new);
			UpgradeGuiManager.registerTab(XP_PUMP_TYPE, XpPumpUpgradeTab::new);
		});
	}

	public static void registerRecipeSerializers() {

		evt.getRegistry().register(BackpackUpgradeRecipe.SERIALIZER.setRegistryName(SophisticatedBackpacks.MOD_ID, "backpack_upgrade"));
		evt.getRegistry().register(SmithingBackpackUpgradeRecipe.SERIALIZER.setRegistryName(SophisticatedBackpacks.MOD_ID, "smithing_backpack_upgrade"));
		evt.getRegistry().register(UpgradeNextTierRecipe.SERIALIZER.setRegistryName(SophisticatedBackpacks.MOD_ID, "upgrade_next_tier"));
		evt.getRegistry().register(BackpackDyeRecipe.SERIALIZER.setRegistryName(SophisticatedBackpacks.MOD_ID, "backpack_dye"));
		evt.getRegistry().register(UpgradeClearRecipe.SERIALIZER.setRegistryName(SophisticatedBackpacks.MOD_ID, "upgrade_clear"));
	}

	public static void registerDispenseBehavior() {
		DispenserBlock.registerBehavior(BACKPACK.get(), new BackpackDispenseBehavior());
		DispenserBlock.registerBehavior(IRON_BACKPACK.get(), new BackpackDispenseBehavior());
		DispenserBlock.registerBehavior(GOLD_BACKPACK.get(), new BackpackDispenseBehavior());
		DispenserBlock.registerBehavior(DIAMOND_BACKPACK.get(), new BackpackDispenseBehavior());
		DispenserBlock.registerBehavior(NETHERITE_BACKPACK.get(), new BackpackDispenseBehavior());
	}

	private static class BackpackDispenseBehavior extends OptionalDispenseBehavior {
		@Override
		protected ItemStack execute(IBlockSource source, ItemStack stack) {
			setSuccess(false);
			Item item = stack.getItem();
			if (item instanceof BackpackItem) {
				Direction dispenserDirection = source.getBlockState().get(DispenserBlock.FACING);
				BlockPos blockpos = source.getPos().relative(dispenserDirection);
				Direction against = source.getLevel().isEmptyBlock(blockpos.below()) ? dispenserDirection.getOpposite() : Direction.UP;

				setSuccess(((BackpackItem) item).tryPlace(null, dispenserDirection.getAxis() == Direction.Axis.Y ? Direction.NORTH : dispenserDirection.getOpposite(), new DirectionalPlaceContext(source.getLevel(), blockpos, dispenserDirection, stack, against)).consumesAction());
			}

			return stack;
		}
	}
}
