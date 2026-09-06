package net.p3pp3rf1y.sophisticatedbackpacks.upgrades.jukebox;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class JukeboxUpgradeNoteParticle extends SpriteTexturedParticle {
	protected JukeboxUpgradeNoteParticle(World level, double x, double y, double z) {
		super(level, x, y, z, 0.0D, 0.0D, 0.0D);
		motionX *= 0.01F;
		motionY *= 0.05F;
		motionZ *= 0.01F;
		motionY += 0.01D;
		double color = level.rand.nextDouble();
		particleRed = Math.max(0.0F, MathHelper.sin(((float) color + 0.0F) * ((float) Math.PI * 2F)) * 0.65F + 0.35F);
		particleGreen = Math.max(0.0F, MathHelper.sin(((float) color + 0.33333334F) * ((float) Math.PI * 2F)) * 0.65F + 0.35F);
		particleBlue = Math.max(0.0F, MathHelper.sin(((float) color + 0.6666667F) * ((float) Math.PI * 2F)) * 0.65F + 0.35F);
		particleScale *= 1.5F;
		maxAge = 20;
	}

	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public float getScale(float pScaleFactor) {
		return particleScale * MathHelper.clamp((age + pScaleFactor) / maxAge * 32.0F, 0.0F, 1.0F);
	}

	@Override
	public void tick() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		if (age++ >= maxAge) {
			setExpired();
		} else {
			move(motionX, motionY, motionZ);
			if (posY == prevPosY) {
				motionX *= 1.1D;
				motionZ *= 1.1D;
			}
			if (onGround) {
				motionX *= 0.7F;
				motionZ *= 0.7F;
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<JukeboxUpgradeNoteParticleData> {
		private final IAnimatedSprite spriteSet;

		public Factory(IAnimatedSprite spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Nullable
		@Override
		public Particle makeParticle(JukeboxUpgradeNoteParticleData type, World level, double x, double y, double z, double pXSpeed, double pYSpeed, double pZSpeed) {
			JukeboxUpgradeNoteParticle particle = new JukeboxUpgradeNoteParticle(level, x, y, z);
			particle.selectSpriteRandomly(spriteSet);
			return particle;
		}
	}
}
