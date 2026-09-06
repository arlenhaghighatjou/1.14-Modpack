package net.p3pp3rf1y.sophisticatedbackpacks.common;

import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapperLookup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resources.IFutureReloadListener;
import net.p3pp3rf1y.sophisticatedbackpacks.util.LazyOptional;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;

import java.util.List;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IAttackEntityResponseUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IBlockClickResponseUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackSettingsManager;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModFluids;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModParticles;
import net.p3pp3rf1y.sophisticatedbackpacks.network.PacketHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.network.SyncPlayerSettingsMessage;
import net.p3pp3rf1y.sophisticatedbackpacks.registry.RegistryLoader;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.jukebox.ServerBackpackSoundHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.util.InventoryHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedbackpacks.util.RandHelper;

import net.lax1dude.eaglercraft.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CommonProxy {
	private final RegistryLoader registryLoader = new RegistryLoader();
	private final PlayerInventoryProvider playerInventoryProvider = new PlayerInventoryProvider();

	public void registerClientHandlers() {
		//noop on the server side
	}

	public void registerHandlers() {
		ModItems.registerHandlers();
		ModBlocks.registerHandlers();
		ModFluids.registerHandlers();
		ModParticles.registerParticles();
	}

	private static final int BACKPACK_COUNT_CHECK_COOLDOWN = 40;
	private long nextBackpackCountCheck = 0;

	public void onWorldTick(World world) {
		if (world.isRemote || Boolean.FALSE.equals(Config.COMMON.nerfsConfig.tooManyBackpacksSlowness) || nextBackpackCountCheck > world.getGameTime()) {
			return;
		}
		nextBackpackCountCheck = world.getGameTime() + BACKPACK_COUNT_CHECK_COOLDOWN;

		world.getPlayers().forEach(player -> {
			AtomicInteger numberOfBackpacks = new AtomicInteger(0);
			SophisticatedBackpacks.PROXY.getPlayerInventoryProvider().runOnBackpacks(player, (backpack, handlerName, identifier, slot) -> {
				numberOfBackpacks.incrementAndGet();
				return false;
			});
			int maxNumberOfBackpacks = Config.COMMON.nerfsConfig.maxNumberOfBackpacks;
			if (numberOfBackpacks.get() > maxNumberOfBackpacks) {
				int numberOfSlownessLevels = Math.min(10, (int) Math.ceil((numberOfBackpacks.get() - maxNumberOfBackpacks) * Config.COMMON.nerfsConfig.slownessLevelsPerAdditionalBackpack));
				player.addPotionEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, BACKPACK_COUNT_CHECK_COOLDOWN * 2, numberOfSlownessLevels - 1, false, false));
			}
		});
	}

	public PlayerInventoryProvider getPlayerInventoryProvider() {
		return playerInventoryProvider;
	}

	public void onPlayerChangedDimension(ServerPlayerEntity player) {
		PacketHandler.sendToClient(player, new SyncPlayerSettingsMessage(BackpackSettingsManager.getPlayerBackpackSettingsTag(player)));
	}

	public void onPlayerLoggedIn(ServerPlayerEntity player) {
		PacketHandler.sendToClient(player, new SyncPlayerSettingsMessage(BackpackSettingsManager.getPlayerBackpackSettingsTag(player)));
	}

	public void addReloadListeners(List<IFutureReloadListener> listeners) {
		listeners.add(registryLoader);
	}

	public boolean onCauldronInteract(PlayerEntity player, Hand hand, World world, BlockPos pos) {
		ItemStack backpack = player.getHeldItem(hand);
		if (!(backpack.getItem() instanceof BackpackItem)) {
			return false;
		}

		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block != Blocks.CAULDRON) {
			return false;
		}
		int level = state.get(CauldronBlock.LEVEL);

		LazyOptional<IBackpackWrapper> backpackWrapperCapability = BackpackWrapperLookup.get(backpack);
		if (level == 0 || backpackWrapperCapability.map(this::hasDefaultColor).orElse(true)) {
			return false;
		}

		if (!world.isRemote) {
			backpackWrapperCapability.ifPresent(w -> {
				w.setColors(BackpackWrapper.DEFAULT_CLOTH_COLOR, BackpackWrapper.DEFAULT_BORDER_COLOR);
				((CauldronBlock) block).setWaterLevel(world, pos, state, level - 1);
			});
		}

		return true;
	}

	private boolean hasDefaultColor(IBackpackWrapper wrapper) {
		return wrapper.getBorderColor() == BackpackWrapper.DEFAULT_BORDER_COLOR && wrapper.getClothColor() == BackpackWrapper.DEFAULT_CLOTH_COLOR;
	}

	public void onBlockClick(PlayerEntity player, BlockPos pos) {
		if (player.world.isRemote) {
			return;
		}
		playerInventoryProvider.runOnBackpacks(player, (backpack, inventoryHandlerName, identifier, slot) -> BackpackWrapperLookup.get(backpack)
				.map(wrapper -> {
					for (IBlockClickResponseUpgrade upgrade : wrapper.getUpgradeHandler().getWrappersThatImplement(IBlockClickResponseUpgrade.class)) {
						if (upgrade.onBlockClick(player, pos)) {
							return true;
						}
					}
					return false;
				}).orElse(false));
	}

	public void onAttackEntity(PlayerEntity player) {
		if (player.world.isRemote) {
			return;
		}
		playerInventoryProvider.runOnBackpacks(player, (backpack, inventoryHandlerName, identifier, slot) -> BackpackWrapperLookup.get(backpack)
				.map(wrapper -> {
					for (IAttackEntityResponseUpgrade upgrade : wrapper.getUpgradeHandler().getWrappersThatImplement(IAttackEntityResponseUpgrade.class)) {
						if (upgrade.onAttackEntity(player)) {
							return true;
						}
					}
					return false;
				}).orElse(false));
	}

	public void onLivingSpecialSpawn(LivingEntity entity) {
		if (entity instanceof MonsterEntity) {
			MonsterEntity monster = (MonsterEntity) entity;
			if (monster.getItemStackFromSlot(EquipmentSlotType.CHEST).isEmpty()) {
				EntityBackpackAdditionHandler.addBackpack(monster);
			}
		}
	}

	public void onEntityMobGriefing(Entity entity) {
		if (entity instanceof CreeperEntity) {
			EntityBackpackAdditionHandler.removeBeneficialEffects((CreeperEntity) entity);
		}
	}

	public void onEntityLeaveWorld(Entity entity) {
		if (!(entity instanceof MonsterEntity)) {
			return;
		}
		EntityBackpackAdditionHandler.removeBackpackUuid((MonsterEntity) entity);
	}

	public boolean onItemPickup(PlayerEntity player, ItemEntity itemEntity) {
		if (itemEntity.getItem().isEmpty()) {
			return false;
		}

		AtomicReference<ItemStack> remainingStackSimulated = new AtomicReference<>(itemEntity.getItem().copy());
		World world = player.world;
		playerInventoryProvider.runOnBackpacks(player, (backpack, inventoryHandlerName, identifier, slot) -> BackpackWrapperLookup.get(backpack)
				.map(wrapper -> {
					remainingStackSimulated.set(InventoryHelper.runPickupOnBackpack(world, remainingStackSimulated.get(), wrapper, true));
					return remainingStackSimulated.get().isEmpty();
				}).orElse(false));
		if (remainingStackSimulated.get().isEmpty()) {
			ItemStack remainingStack = itemEntity.getItem().copy();
			playerInventoryProvider.runOnBackpacks(player, (backpack, inventoryHandlerName, identifier, slot) -> BackpackWrapperLookup.get(backpack)
					.map(wrapper -> InventoryHelper.runPickupOnBackpack(world, player, remainingStack, wrapper, false).isEmpty()).orElse(false)
			);
			if (!itemEntity.isSilent()) {
				Random rand = itemEntity.world.rand;
				itemEntity.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, (RandHelper.getRandomMinusOneToOne(rand) * 0.7F + 1.0F) * 2.0F);
			}
			itemEntity.setItem(ItemStack.EMPTY);
			return true;
		}
		return false;
	}

}
