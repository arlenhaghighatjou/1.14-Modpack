package net.p3pp3rf1y.sophisticatedbackpacks.api;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.lax1dude.eaglercraft.Random;
import java.util.function.UnaryOperator;

public interface IUpgradeRenderer<T extends IUpgradeRenderData> {
	void render(World world, Random rand, UnaryOperator<Vec3d> getPositionFromOffset, T upgradeRenderData);
}
