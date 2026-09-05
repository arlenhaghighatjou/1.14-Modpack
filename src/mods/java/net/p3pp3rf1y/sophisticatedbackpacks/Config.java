package net.p3pp3rf1y.sophisticatedbackpacks;

import net.minecraft.util.registry.Registry;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraft.util.ResourceLocation;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SortButtonsPosition;
import net.p3pp3rf1y.sophisticatedbackpacks.util.RegistryHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@SuppressWarnings("java:S1192") //don't complain about repeated config names if two upgrades happen to have the same setting
public class Config {
	private static final String SETTINGS = " Settings";

	private Config() {}

	public static final Client CLIENT = new Client();
	public static final Common COMMON = new Common();

	public static class Client {
		public SortButtonsPosition sortButtonsPosition;
		public boolean playButtonSound;

		Client() {
			sortButtonsPosition = SortButtonsPosition.TITLE_LINE_RIGHT;
			playButtonSound = true;
		}
	}

	public static class Common {
		public final EnabledItems enabledItems;
		public final DisallowedItems disallowedItems;
		public final BackpackConfig leatherBackpack;
		public final BackpackConfig ironBackpack;
		public final BackpackConfig goldBackpack;
		public final BackpackConfig diamondBackpack;
		public final BackpackConfig netheriteBackpack;
		public final FilteredUpgradeConfig compactingUpgrade;
		public final FilteredUpgradeConfig advancedCompactingUpgrade;
		public final FilteredUpgradeConfig depositUpgrade;
		public final FilteredUpgradeConfig advancedDepositUpgrade;
		public final FilteredUpgradeConfig feedingUpgrade;
		public final FilteredUpgradeConfig advancedFeedingUpgrade;
		public final FilteredUpgradeConfig filterUpgrade;
		public final FilteredUpgradeConfig advancedFilterUpgrade;
		public final MagnetUpgradeConfig magnetUpgrade;
		public final MagnetUpgradeConfig advancedMagnetUpgrade;
		public final FilteredUpgradeConfig pickupUpgrade;
		public final FilteredUpgradeConfig advancedPickupUpgrade;
		public final FilteredUpgradeConfig refillUpgrade;
		public final FilteredUpgradeConfig restockUpgrade;
		public final FilteredUpgradeConfig advancedRestockUpgrade;
		public final FilteredUpgradeConfig voidUpgrade;
		public final FilteredUpgradeConfig advancedVoidUpgrade;
		public final CookingUpgradeConfig smeltingUpgrade;
		public final CookingUpgradeConfig smokingUpgrade;
		public final CookingUpgradeConfig blastingUpgrade;
		public final AutoCookingUpgradeConfig autoSmeltingUpgrade;
		public final AutoCookingUpgradeConfig autoSmokingUpgrade;
		public final AutoCookingUpgradeConfig autoBlastingUpgrade;
		public final InceptionUpgradeConfig inceptionUpgrade;
		public final EntityBackpackAdditionsConfig entityBackpackAdditions;
		public boolean chestLootEnabled;
		public boolean itemFluidHandlerEnabled;
		public final ToolSwapperUpgradeConfig toolSwapperUpgrade;
		public final TankUpgradeConfig tankUpgrade;
		public final BatteryUpgradeConfig batteryUpgrade;
		public final StackUpgradeConfig stackUpgrade;
		public final PumpUpgradeConfig pumpUpgrade;
		public final XpPumpUpgradeConfig xpPumpUpgrade;
		public final NerfsConfig nerfsConfig;

		Common() {

			enabledItems = new EnabledItems();
			disallowedItems = new DisallowedItems();

			leatherBackpack = new BackpackConfig("Leather", 27, 1);
			ironBackpack = new BackpackConfig("Iron", 54, 2);
			goldBackpack = new BackpackConfig("Gold", 81, 3);
			diamondBackpack = new BackpackConfig("Diamond", 108, 5);
			netheriteBackpack = new BackpackConfig("Netherite", 120, 7);

			compactingUpgrade = new FilteredUpgradeConfig("Compacting Upgrade", "compactingUpgrade", 9, 3);
			advancedCompactingUpgrade = new FilteredUpgradeConfig("Advanced Compacting Upgrade", "advancedCompactingUpgrade", 16, 4);
			depositUpgrade = new FilteredUpgradeConfig("Deposit Upgrade", "depositUpgrade", 9, 3);
			advancedDepositUpgrade = new FilteredUpgradeConfig("Advanced Deposit Upgrade", "advancedDepositUpgrade", 16, 4);
			feedingUpgrade = new FilteredUpgradeConfig("Feeding Upgrade", "feedingUpgrade", 9, 3);
			advancedFeedingUpgrade = new FilteredUpgradeConfig("Advanced Feeding Upgrade", "advancedFeedingUpgrade", 16, 4);
			filterUpgrade = new FilteredUpgradeConfig("Filter Upgrade", "filterUpgrade", 9, 3);
			advancedFilterUpgrade = new FilteredUpgradeConfig("Advanced Filter Upgrade", "advancedFilterUpgrade", 16, 4);
			magnetUpgrade = new MagnetUpgradeConfig("Magnet Upgrade", "magnetUpgrade", 9, 3, 3);
			advancedMagnetUpgrade = new MagnetUpgradeConfig("Advanced Magnet Upgrade", "advancedMagnetUpgrade", 16, 4, 5);
			pickupUpgrade = new FilteredUpgradeConfig("Pickup Upgrade", "pickupUpgrade", 9, 3);
			advancedPickupUpgrade = new FilteredUpgradeConfig("Advanced Pickup Upgrade", "advancedPickupUpgrade", 16, 4);
			refillUpgrade = new FilteredUpgradeConfig("Refill Upgrade", "refillUpgrade", 6, 3);
			restockUpgrade = new FilteredUpgradeConfig("Restock Upgrade", "restockUpgrade", 9, 3);
			advancedRestockUpgrade = new FilteredUpgradeConfig("Advanced Restock Upgrade", "advancedRestockUpgrade", 16, 4);
			voidUpgrade = new FilteredUpgradeConfig("Void Upgrade", "voidUpgrade", 9, 3);
			advancedVoidUpgrade = new FilteredUpgradeConfig("Advanced Void Upgrade", "advancedVoidUpgrade", 16, 4);
			stackUpgrade = new StackUpgradeConfig();
			smeltingUpgrade = CookingUpgradeConfig.getInstance("Smelting Upgrade", "smeltingUpgrade");
			smokingUpgrade = CookingUpgradeConfig.getInstance("Smoking Upgrade", "smokingUpgrade");
			blastingUpgrade = CookingUpgradeConfig.getInstance("Blasting Upgrade", "blastingUpgrade");
			autoSmeltingUpgrade = new AutoCookingUpgradeConfig("Auto-Smelting Upgrade", "autoSmeltingUpgrade");
			autoSmokingUpgrade = new AutoCookingUpgradeConfig("Auto-Smoking Upgrade", "autoSmokingUpgrade");
			autoBlastingUpgrade = new AutoCookingUpgradeConfig("Auto-Blasting Upgrade", "autoBlastingUpgrade");
			inceptionUpgrade = new InceptionUpgradeConfig();
			toolSwapperUpgrade = new ToolSwapperUpgradeConfig();
			tankUpgrade = new TankUpgradeConfig();
			batteryUpgrade = new BatteryUpgradeConfig();
			pumpUpgrade = new PumpUpgradeConfig();
			xpPumpUpgrade = new XpPumpUpgradeConfig();
			entityBackpackAdditions = new EntityBackpackAdditionsConfig();
			nerfsConfig = new NerfsConfig();

			chestLootEnabled = true;
			itemFluidHandlerEnabled = true;

		}

		public static class NerfsConfig {
			public boolean tooManyBackpacksSlowness;
			public int maxNumberOfBackpacks;
			public double slownessLevelsPerAdditionalBackpack;

			public NerfsConfig() {
				tooManyBackpacksSlowness = false;
				maxNumberOfBackpacks = 3;
				slownessLevelsPerAdditionalBackpack = 1;
			}
		}

		public static class XpPumpUpgradeConfig {
			public int maxXpPointsPerMending;
			public boolean mendingOn;

			public XpPumpUpgradeConfig() {
				mendingOn = true;
				maxXpPointsPerMending = 5;
			}
		}

		public static class PumpUpgradeConfig {
			public int maxInputOutput;
			public double stackMultiplierRatio;
			public int filterSlots;

			public PumpUpgradeConfig() {
				filterSlots = 4;
				maxInputOutput = 20;
				stackMultiplierRatio = 1D;
			}
		}

		public static class EntityBackpackAdditionsConfig {
			private static final String REGISTRY_NAME_MATCHER = "([a-z1-9_.-]+:[a-z1-9_/.-]+)";
			private static final String ENTITY_LOOT_MATCHER = "([a-z1-9_.-]+:[a-z1-9_/.-]+)\\|(null|[a-z1-9_.-]+:[a-z1-9/_.-]+)";
			public double chance;
			public boolean addLoot;
			public boolean buffWithPotionEffects;
			public boolean buffHealth;
			public boolean equipWithArmor;
			public boolean playJukebox;
			public double backpackDropChance;
			public double lootingChanceIncreasePerLevel;
			public List<? extends String> entityLootTableList;
			public List<? extends String> discBlockList;
			@Nullable
			private Map<EntityType<?>, ResourceLocation> entityLootTables = null;

			public EntityBackpackAdditionsConfig() {
				chance = 0.01;
				addLoot = true;
				buffWithPotionEffects = true;
				buffHealth = true;
				equipWithArmor = true;
				entityLootTableList = getDefaultEntityLootTableList();
				discBlockList = getDefaultDiscBlockList();
				playJukebox = true;
				backpackDropChance = 0.085;
				lootingChanceIncreasePerLevel = 0.01;
			}

			public Optional<ResourceLocation> getLootTableName(EntityType<?> entityType) {
				if (entityLootTables == null) {
					initEntityLootTables();
				}
				return Optional.ofNullable(entityLootTables.get(entityType));
			}

			public boolean canWearBackpack(EntityType<?> entityType) {
				if (entityLootTables == null) {
					initEntityLootTables();
				}
				return entityLootTables.containsKey(entityType);
			}

			private void initEntityLootTables() {
				entityLootTables = new HashMap<>();
				for (String mapping : entityLootTableList) {
					String[] entityLoot = mapping.split("\\|");
					if (entityLoot.length < 2) {
						continue;
					}
					String entityRegistryName = entityLoot[0];
					String lootTableName = entityLoot[1];

					EntityType<?> entityType = Registry.ENTITY_TYPE.getOrDefault(new ResourceLocation(entityRegistryName));
					if (entityType != null) {
						entityLootTables.put(entityType, lootTableName.equals("null") ? null : new ResourceLocation(lootTableName));
					}
				}
			}

			private List<String> getDefaultDiscBlockList() {
				List<String> ret = new ArrayList<>();
				ret.add("botania:record_gaia_1");
				ret.add("botania:record_gaia_2");
				return ret;
			}

			private List<String> getDefaultEntityLootTableList() {
				return getDefaultEntityLootMapping().entrySet().stream().map(e -> Registry.ENTITY_TYPE.getKey(e.getKey()) + "|" + e.getValue()).collect(Collectors.toList());
			}

			private Map<EntityType<?>, ResourceLocation> getDefaultEntityLootMapping() {
				Map<EntityType<?>, ResourceLocation> mapping = new LinkedHashMap<>();
				mapping.put(EntityType.CREEPER, LootTables.CHESTS_DESERT_PYRAMID);
				mapping.put(EntityType.DROWNED, LootTables.CHESTS_SHIPWRECK_TREASURE);
				mapping.put(EntityType.ENDERMAN, LootTables.CHESTS_END_CITY_TREASURE);
				mapping.put(EntityType.EVOKER, LootTables.CHESTS_WOODLAND_MANSION);
				mapping.put(EntityType.HUSK, LootTables.CHESTS_DESERT_PYRAMID);
				mapping.put(EntityType.PILLAGER, LootTables.CHESTS_PILLAGER_OUTPOST);
				mapping.put(EntityType.SKELETON, LootTables.CHESTS_SIMPLE_DUNGEON);
				mapping.put(EntityType.STRAY, LootTables.CHESTS_IGLOO_CHEST);
				mapping.put(EntityType.VEX, LootTables.CHESTS_WOODLAND_MANSION);
				mapping.put(EntityType.VINDICATOR, LootTables.CHESTS_WOODLAND_MANSION);
				mapping.put(EntityType.WITCH, LootTables.CHESTS_BURIED_TREASURE);
				mapping.put(EntityType.WITHER_SKELETON, LootTables.CHESTS_NETHER_BRIDGE);
				mapping.put(EntityType.ZOMBIE, LootTables.CHESTS_SIMPLE_DUNGEON);
				mapping.put(EntityType.ZOMBIE_VILLAGER, LootTables.CHESTS_VILLAGE_VILLAGE_ARMORER);
				mapping.put(EntityType.ZOMBIE_PIGMAN, LootTables.CHESTS_NETHER_BRIDGE);
				return mapping;
			}
		}

		public static class ToolSwapperUpgradeConfig {
			public int slotsInRow;

			protected ToolSwapperUpgradeConfig() {
				slotsInRow = 4;
			}
		}

		public static class TankUpgradeConfig {
			public int capacityPerSlotRow;
			public double stackMultiplierRatio;
			public int autoFillDrainContainerCooldown;
			public int maxInputOutput;

			protected TankUpgradeConfig() {
				capacityPerSlotRow = 4000;
				stackMultiplierRatio = 1D;
				autoFillDrainContainerCooldown = 20;
				maxInputOutput = 20;
			}
		}

		public static class BatteryUpgradeConfig {
			public int energyPerSlotRow;
			public double stackMultiplierRatio;
			public int maxInputOutput;

			protected BatteryUpgradeConfig() {
				energyPerSlotRow = 10000;
				stackMultiplierRatio = 1D;
				maxInputOutput = 20;
			}
		}

		public static class InceptionUpgradeConfig {
			public boolean upgradesUseInventoriesOfBackpacksInBackpack;
			public boolean upgradesInContainedBackpacksAreFunctional;

			public InceptionUpgradeConfig() {
				upgradesUseInventoriesOfBackpacksInBackpack = true;
				upgradesInContainedBackpacksAreFunctional = true;
			}
		}

		public static class AutoCookingUpgradeConfig extends CookingUpgradeConfig {
			public int inputFilterSlots;
			public int inputFilterSlotsInRow;
			public int fuelFilterSlots;
			public int fuelFilterSlotsInRow;

			public AutoCookingUpgradeConfig(String upgradeName, String path) {
				super(upgradeName, path);
				inputFilterSlots = 8;
				inputFilterSlotsInRow = 4;
				fuelFilterSlots = 4;
				fuelFilterSlotsInRow = 4;
			}
		}

		public static class CookingUpgradeConfig {
			public double cookingSpeedMultiplier;
			public double fuelEfficiencyMultiplier;

			protected CookingUpgradeConfig(final String upgradeName, String path) {
				cookingSpeedMultiplier = 1.0D;
				fuelEfficiencyMultiplier = 1.0D;
			}

			public static CookingUpgradeConfig getInstance(final String upgradeName, String path) {
				CookingUpgradeConfig instance = new CookingUpgradeConfig(upgradeName, path);
				return instance;
			}
		}

		public static class MagnetUpgradeConfig extends FilteredUpgradeConfigBase {
			public int magnetRange;

			public MagnetUpgradeConfig(String name, String path, int defaultFilterSlots, int defaultSlotsInRow, int defaultMagnetRange) {
				super(name, path, defaultFilterSlots, defaultSlotsInRow);
				magnetRange = defaultMagnetRange;
			}
		}

		public static class FilteredUpgradeConfig extends FilteredUpgradeConfigBase {
			public FilteredUpgradeConfig(String name, String path, int defaultFilterSlots, int defaultSlotsInRow) {
				super(name, path, defaultFilterSlots, defaultSlotsInRow);
			}
		}

		public static class FilteredUpgradeConfigBase {
			public int filterSlots;
			public int slotsInRow;

			protected FilteredUpgradeConfigBase(String name, String path, int defaultFilterSlots, int defaultSlotsInRow) {
				filterSlots = defaultFilterSlots;
				slotsInRow = defaultSlotsInRow;
			}
		}

		public static class BackpackConfig {
			public int inventorySlotCount;
			public int upgradeSlotCount;

			public BackpackConfig(String backpackPrefix, int inventorySlotCountDefault, int upgradeSlotCountDefault) {
				inventorySlotCount = inventorySlotCountDefault;
				upgradeSlotCount = upgradeSlotCountDefault;
			}
		}

		public static class EnabledItems {
			private List<String> itemsEnableList;
			private final Map<String, Boolean> enabledMap = new ConcurrentHashMap<>();

			EnabledItems() {
				itemsEnableList = new ArrayList<>();
			}

			public boolean isItemEnabled(Item item) {
				return RegistryHelper.getRegistryName(item).map(rn -> isItemEnabled(rn.getPath())).orElse(false);
			}

			public boolean isItemEnabled(String itemRegistryName) {
				if (enabledMap.isEmpty()) {
					loadEnabledMap();
				}
				return enabledMap.computeIfAbsent(itemRegistryName, irn -> {
					addEnabledItemToConfig(itemRegistryName);
					return true;
				});
			}

			private void addEnabledItemToConfig(String itemRegistryName) {
				itemsEnableList.add(itemRegistryName + ":true");
			}

			private void loadEnabledMap() {
				for (String itemEnabled : itemsEnableList) {
					String[] data = itemEnabled.split(":");
					if (data.length == 2) {
						enabledMap.put(data[0], Boolean.valueOf(data[1]));
					} else {
						SophisticatedBackpacks.LOGGER.error("Wrong data for enabledItems - expected name:true/false when {} was provided", itemEnabled);
					}
				}
			}
		}

		public static class DisallowedItems {
			private List<String> disallowedItemsList;
			private boolean setInitialized = false;
			private Set<Item> disallowedItemsSet = null;

			DisallowedItems() {
				disallowedItemsList = new ArrayList<>();
			}

			public boolean isItemDisallowed(Item item) {
				if (!setInitialized) {
					loadDisallowedSet();
				}
				return disallowedItemsSet.contains(item);
			}

			private void loadDisallowedSet() {
				disallowedItemsSet = new HashSet<>();

				for (String disallowedItemName : disallowedItemsList) {
					ResourceLocation registryName = new ResourceLocation(disallowedItemName);
					if (Registry.ITEM.keySet().contains(registryName)) {
						disallowedItemsSet.add(Registry.ITEM.getOrDefault(registryName));
					}
				}
			}
		}

		public static class StackUpgradeConfig {
			private List<String> nonStackableItemsList;
			@Nullable
			private Set<Item> nonStackableItems = null;
			public StackUpgradeConfig() {
				nonStackableItemsList = new ArrayList<>();
			}

			public boolean canItemStack(Item item) {
				if (nonStackableItems == null) {
					nonStackableItems = new HashSet<>();
					nonStackableItemsList.forEach(name -> {
						ResourceLocation registryName = new ResourceLocation(name);
						if (Registry.ITEM.keySet().contains(registryName)) {
							nonStackableItems.add(Registry.ITEM.getOrDefault(registryName));
						} else {
							SophisticatedBackpacks.LOGGER.error("Item {} is set to be disabled in config, but it does not exist in item registry", name);
						}
					});
				}
				return !nonStackableItems.contains(item);
			}
		}
	}
}
