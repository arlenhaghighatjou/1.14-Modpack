package net.p3pp3rf1y.sophisticatedbackpacks.util;

@FunctionalInterface
public interface TriConsumer<A, B, C> {
	void accept(A a, B b, C c);
}
