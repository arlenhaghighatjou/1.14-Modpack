package net.p3pp3rf1y.sophisticatedbackpacks.upgrades.jukebox;

import com.mojang.brigadier.StringReader;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.registry.Registry;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModParticles;

public class JukeboxUpgradeNoteParticleData extends ParticleType<JukeboxUpgradeNoteParticleData> implements IParticleData {
	public JukeboxUpgradeNoteParticleData() {
		super(false, DESERIALIZER);
	}

	@Override
	public JukeboxUpgradeNoteParticleData getType() {
		return ModParticles.JUKEBOX_NOTE.get();
	}

	@Override
	public void write(PacketBuffer pBuffer) {
		//noop
	}

	@Override
	public String getParameters() {
		//noinspection ConstantConditions
		return Registry.PARTICLE_TYPE.getKey(ModParticles.JUKEBOX_NOTE.get()).toString();
	}

	public static final IDeserializer<JukeboxUpgradeNoteParticleData> DESERIALIZER = new IDeserializer<JukeboxUpgradeNoteParticleData>() {
		@Override
		public JukeboxUpgradeNoteParticleData deserialize(ParticleType<JukeboxUpgradeNoteParticleData> pParticleType, StringReader pReader) {
			return (JukeboxUpgradeNoteParticleData) pParticleType;
		}

		@Override
		public JukeboxUpgradeNoteParticleData read(ParticleType<JukeboxUpgradeNoteParticleData> pParticleType, PacketBuffer pBuffer) {
			return (JukeboxUpgradeNoteParticleData) pParticleType;
		}
	};
}
