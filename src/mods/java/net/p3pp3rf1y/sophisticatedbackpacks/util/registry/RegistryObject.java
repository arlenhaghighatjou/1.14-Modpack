package net.p3pp3rf1y.sophisticatedbackpacks.util.registry;

import net.minecraft.util.ResourceLocation;

import java.util.Optional;
import java.util.function.Supplier;

public class RegistryObject<T> implements Supplier<T> {
	private final ResourceLocation name;
	private final Supplier<T> factory;
	private T value;

	RegistryObject(ResourceLocation name, Supplier<T> factory) {
		this.name = name;
		this.factory = factory;
	}

	public ResourceLocation getId() {
		return name;
	}

	T create() {
		if (value == null) {
			value = factory.get();
		}
		return value;
	}

	@Override
	public T get() {
		if (value == null) {
			throw new IllegalStateException("Registry entry " + name + " is not registered yet");
		}
		return value;
	}

	public boolean isPresent() {
		return value != null;
	}

	public Optional<T> resolve() {
		return Optional.ofNullable(value);
	}
}
