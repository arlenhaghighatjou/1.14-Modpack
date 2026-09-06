package net.p3pp3rf1y.sophisticatedbackpacks.init;

import net.minecraft.fluid.Fluid;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.fluid.XpFluid;
import net.p3pp3rf1y.sophisticatedbackpacks.util.registry.ModRegistry;
import net.p3pp3rf1y.sophisticatedbackpacks.util.registry.RegistryObject;

public class ModFluids {
	private ModFluids() {}

	public static final ResourceLocation EXPERIENCE_TAG_NAME = new ResourceLocation("forge:experience");

	public static final ModRegistry<Fluid> FLUIDS = new ModRegistry<>(Registry.FLUID, SophisticatedBackpacks.MOD_ID);

	public static final RegistryObject<XpFluid> XP_STILL = FLUIDS.register("xp_still", () -> new XpFluid(true));
	public static final RegistryObject<XpFluid> XP_FLOWING = FLUIDS.register("xp_flowing", () -> new XpFluid(false));

	private static Tag<Fluid> experienceTag = new Tag<>(EXPERIENCE_TAG_NAME);

	public static Tag<Fluid> getExperienceTag() {
		return experienceTag;
	}

	public static void registerHandlers() {
		FLUIDS.register();
		experienceTag = Tag.Builder.<Fluid>create().add(XP_STILL.get(), XP_FLOWING.get()).build(EXPERIENCE_TAG_NAME);
	}
}
