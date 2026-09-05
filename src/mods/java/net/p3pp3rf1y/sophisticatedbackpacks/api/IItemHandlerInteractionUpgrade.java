package net.p3pp3rf1y.sophisticatedbackpacks.api;

import net.minecraft.entity.player.PlayerEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.util.inventory.IItemHandler;

public interface IItemHandlerInteractionUpgrade {
	void onHandlerInteract(IItemHandler itemHandler, PlayerEntity player);
}
