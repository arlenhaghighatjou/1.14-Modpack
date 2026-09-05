package net.p3pp3rf1y.sophisticatedbackpacks.common.gui;

import net.minecraft.util.registry.Registry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IBackpackUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IUpgradeWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UpgradeContainerRegistry {
	private UpgradeContainerRegistry() {}

	private static final Map<ResourceLocation, UpgradeContainerType<? extends IUpgradeWrapper, ? extends UpgradeContainerBase<?, ?>>> UPGRADE_CONTAINERS = new HashMap<>();

	public static void register(ResourceLocation upgradeName, UpgradeContainerType<? extends IUpgradeWrapper, ? extends UpgradeContainerBase<?, ?>> containerFactory) {
		UPGRADE_CONTAINERS.put(upgradeName, containerFactory);
	}

	public static <W extends IUpgradeWrapper, C extends UpgradeContainerBase<W, C>> Optional<UpgradeContainerBase<W, C>> instantiateContainer(PlayerEntity player, int windowId, W wrapper) {
		ResourceLocation upgradeName = Registry.ITEM.getKey(wrapper.getUpgradeStack().getItem());
		if (!(wrapper.getUpgradeStack().getItem() instanceof IBackpackUpgradeItem<?>) || wrapper.hideSettingsTab() || !UPGRADE_CONTAINERS.containsKey(upgradeName)) {
			return Optional.empty();
		}
		//noinspection unchecked,ConstantConditions
		return Optional.of((UpgradeContainerBase<W, C>) getContainerType(upgradeName).create(player, windowId, wrapper));
	}

	private static <W extends IUpgradeWrapper, C extends UpgradeContainerBase<W, C>> UpgradeContainerType<W, C> getContainerType(ResourceLocation upgradeName) {
		//noinspection unchecked
		return (UpgradeContainerType<W, C>) UPGRADE_CONTAINERS.get(upgradeName);
	}
}
