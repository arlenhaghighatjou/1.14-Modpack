package net.p3pp3rf1y.sophisticatedbackpacks.common.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IUpgradeWrapper;

public class UpgradeContainerType<W extends IUpgradeWrapper, C extends UpgradeContainerBase<W, C>> {
	private final IFactory<W, C> factory;

	public UpgradeContainerType(IFactory<W, C> factory) {
		this.factory = factory;
	}

	public C create(PlayerEntity player, int windowId, W wrapper) {
		return factory.create(player, windowId, wrapper, this);
	}

	public interface IFactory<W extends IUpgradeWrapper, C extends UpgradeContainerBase<W, C>> {
		C create(PlayerEntity player, int windowId, W upgrade, UpgradeContainerType<W, C> type);
	}
}
