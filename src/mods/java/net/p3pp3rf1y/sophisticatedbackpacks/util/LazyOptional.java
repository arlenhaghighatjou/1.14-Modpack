package net.p3pp3rf1y.sophisticatedbackpacks.util;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A value that is only built when something actually asks for it. Ported from the capability
 * system's holder so the backpack wrappers keep their lazy construction.
 */
public class LazyOptional<T> {
	private static final LazyOptional<Void> EMPTY = new LazyOptional<>(null);

	@Nullable
	private final Supplier<T> supplier;
	private T value;
	private boolean resolved;

	private LazyOptional(@Nullable Supplier<T> supplier) {
		this.supplier = supplier;
	}

	public static <T> LazyOptional<T> of(@Nullable Supplier<T> supplier) {
		return supplier == null ? empty() : new LazyOptional<>(supplier);
	}

	@SuppressWarnings("unchecked")
	public static <T> LazyOptional<T> empty() {
		return (LazyOptional<T>) EMPTY;
	}

	@SuppressWarnings("unchecked")
	public <X> LazyOptional<X> cast() {
		return (LazyOptional<X>) this;
	}

	@Nullable
	private T resolve() {
		if (supplier == null) {
			return null;
		}

		if (!resolved) {
			value = supplier.get();
			resolved = true;
		}

		return value;
	}

	public boolean isPresent() {
		return resolve() != null;
	}

	public void ifPresent(Consumer<? super T> consumer) {
		T resolved = resolve();
		if (resolved != null) {
			consumer.accept(resolved);
		}
	}

	public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
		T resolved = resolve();
		return resolved != null ? Optional.ofNullable(mapper.apply(resolved)) : Optional.empty();
	}

	public T orElse(T other) {
		T resolved = resolve();
		return resolved != null ? resolved : other;
	}

	public T orElseGet(Supplier<? extends T> other) {
		T resolved = resolve();
		return resolved != null ? resolved : other.get();
	}

	public Optional<T> resolveOptional() {
		return Optional.ofNullable(resolve());
	}
}
