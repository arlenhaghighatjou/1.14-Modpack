package vectorwing.farmersdelight.registry;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import vectorwing.farmersdelight.FarmersDelight;

public class ModParticleTypes
{
	public static BasicParticleType STAR_PARTICLE;

	public static void registerParticleTypes()
	{
		STAR_PARTICLE = Registry.register(Registry.PARTICLE_TYPE, new ResourceLocation(FarmersDelight.MODID, "star"), new BasicParticleType(true));
	}
}
