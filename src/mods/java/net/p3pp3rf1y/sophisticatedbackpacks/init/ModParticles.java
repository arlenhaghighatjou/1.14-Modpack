package net.p3pp3rf1y.sophisticatedbackpacks.init;

import net.minecraft.client.Minecraft;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.registry.Registry;
import net.p3pp3rf1y.sophisticatedbackpacks.util.registry.ModRegistry;
import net.p3pp3rf1y.sophisticatedbackpacks.util.registry.RegistryObject;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.jukebox.JukeboxUpgradeNoteParticle;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.jukebox.JukeboxUpgradeNoteParticleData;

public class ModParticles {
	private ModParticles() {}

	private static final ModRegistry<ParticleType<?>> PARTICLES = new ModRegistry<>(Registry.PARTICLE_TYPE, SophisticatedBackpacks.MOD_ID);

	public static final RegistryObject<JukeboxUpgradeNoteParticleData> JUKEBOX_NOTE = PARTICLES.register("jukebox_note", JukeboxUpgradeNoteParticleData::new);

	public static void registerParticles() {
		PARTICLES.register();
	}

	public static void registerFactories() {
		Minecraft.getInstance().particles.registerFactory(JUKEBOX_NOTE.get(), JukeboxUpgradeNoteParticle.Factory::new);
	}
}
