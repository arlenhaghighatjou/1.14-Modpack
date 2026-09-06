package net.p3pp3rf1y.sophisticatedbackpacks.util.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModRegistry<T> {
	private final Registry<? super T> registry;
	private final String modId;
	private final List<RegistryObject<? extends T>> entries = new ArrayList<>();
	private boolean registered = false;

	public ModRegistry(Registry<? super T> registry, String modId) {
		this.registry = registry;
		this.modId = modId;
	}

	public <I extends T> RegistryObject<I> register(String name, Supplier<I> factory) {
		RegistryObject<I> entry = new RegistryObject<>(new ResourceLocation(modId, name), factory);
		entries.add(entry);
		return entry;
	}

	public void register() {
		if (registered) {
			return;
		}
		registered = true;
		for (RegistryObject<? extends T> entry : entries) {
			Registry.register(registry, entry.getId(), entry.create());
		}
	}
}
