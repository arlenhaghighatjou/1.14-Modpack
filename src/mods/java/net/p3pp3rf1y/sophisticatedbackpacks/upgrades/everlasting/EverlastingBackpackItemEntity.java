package net.p3pp3rf1y.sophisticatedbackpacks.upgrades.everlasting;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

@SuppressWarnings("java:S2160") //no need to override equals, the default implementation is good
public class EverlastingBackpackItemEntity extends ItemEntity {
	private boolean wasFloatingUp = false;

	public EverlastingBackpackItemEntity(EntityType<? extends ItemEntity> type, World world) {
		super(type, world);
	}

	@Override
	public void tick() {
		if (!world.isRemote) {
			double d0 = posX + 0.5F - rand.nextFloat();
			double d1 = posY + rand.nextFloat() * 0.5F;
			double d2 = posZ + 0.5F - rand.nextFloat();
			ServerWorld serverWorld = (ServerWorld) world;
			if (rand.nextInt(20) == 0) {
				serverWorld.spawnParticle(ParticleTypes.HAPPY_VILLAGER, d0, d1, d2, 0, 0, 0.1D, 0, 1f);
			}
		}
		if (!hasNoGravity()) {
			if (isInWater() || isInLava()) {
				setMotion(getMotion().x, 0.06D, getMotion().z);
				wasFloatingUp = true;
			} else if (wasFloatingUp) {
				setNoGravity(true);
				setMotion(Vec3d.ZERO);
			}
		}
		super.tick();
	}

	@Override
	public boolean isInWater() {
		return posY < 1 || super.isInWater();
	}

	@Override
	public boolean isImmuneToExplosions() {
		return true;
	}

	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		return true;
	}

	@Override
	protected void outOfWorld() {
		//do nothing as the only thing that vanilla does here is remove entity from world, but it can't for this
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return new net.minecraft.network.play.server.SSpawnObjectPacket(this);
	}

}
