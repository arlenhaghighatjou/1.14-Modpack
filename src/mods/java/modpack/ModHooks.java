package modpack;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.common.EntityBackpackAdditionHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.jukebox.ServerBackpackSoundHandler;

import java.util.List;

public class ModHooks {
	private ModHooks() {}

	public static void onWorldTickEnd(World world) {
		SophisticatedBackpacks.PROXY.onWorldTick(world);
		ServerBackpackSoundHandler.tick(world);
	}

	public static void onPlayerLoggedIn(ServerPlayerEntity player) {
		SophisticatedBackpacks.PROXY.onPlayerLoggedIn(player);
	}

	public static void onPlayerChangedDimension(ServerPlayerEntity player) {
		SophisticatedBackpacks.PROXY.onPlayerChangedDimension(player);
	}

	public static void addReloadListeners(List<IFutureReloadListener> listeners) {
		SophisticatedBackpacks.PROXY.addReloadListeners(listeners);
	}

	public static boolean onRightClickBlock(PlayerEntity player, Hand hand, World world, BlockPos pos) {
		return net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlock.playerInteract(player, world, pos)
				|| SophisticatedBackpacks.PROXY.onCauldronInteract(player, hand, world, pos);
	}

	public static void onBlockClick(PlayerEntity player, BlockPos pos) {
		SophisticatedBackpacks.PROXY.onBlockClick(player, pos);
	}

	public static void onAttackEntity(PlayerEntity player) {
		SophisticatedBackpacks.PROXY.onAttackEntity(player);
	}

	public static void onSpecialSpawn(LivingEntity entity) {
		SophisticatedBackpacks.PROXY.onLivingSpecialSpawn(entity);
	}

	public static void onLivingDrops(LivingEntity entity, DamageSource source, int lootingLevel) {
		EntityBackpackAdditionHandler.handleBackpackDrop(entity, source, lootingLevel);
	}

	public static void onMobGriefing(Entity entity) {
		SophisticatedBackpacks.PROXY.onEntityMobGriefing(entity);
	}

	public static void onEntityLeaveWorld(Entity entity) {
		SophisticatedBackpacks.PROXY.onEntityLeaveWorld(entity);
	}

	public static boolean onItemPickup(PlayerEntity player, ItemEntity itemEntity) {
		return SophisticatedBackpacks.PROXY.onItemPickup(player, itemEntity);
	}

	public static Entity replaceEntity(Entity entity) {
		if (!(entity instanceof ItemEntity)) {
			return entity;
		}
		ItemEntity itemEntity = (ItemEntity) entity;
		ItemStack stack = itemEntity.getItem();
		if (!(stack.getItem() instanceof BackpackItem) || !((BackpackItem) stack.getItem()).hasCustomEntity(stack)) {
			return entity;
		}
		Entity replacement = ((BackpackItem) stack.getItem()).createEntity(entity.world, itemEntity, stack);
		return replacement == null ? entity : replacement;
	}

	public static void onLivingUpdate(LivingEntity entity) {
		EntityBackpackAdditionHandler.onLivingUpdate(entity);
	}
}
