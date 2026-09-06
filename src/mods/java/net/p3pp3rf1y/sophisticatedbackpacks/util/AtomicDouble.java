package net.p3pp3rf1y.sophisticatedbackpacks.util;

public class AtomicDouble {
	private double value;

	public AtomicDouble(double value) {
		this.value = value;
	}

	public double get() {
		return value;
	}

	public void set(double value) {
		this.value = value;
	}

	public double addAndGet(double delta) {
		value += delta;
		return value;
	}
}
