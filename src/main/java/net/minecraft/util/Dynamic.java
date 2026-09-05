package net.minecraft.util;

import java.util.Optional;

public class Dynamic<T> {
    private final T value;

    public Dynamic(T value) {
        this.value = value;
    }

    public Dynamic<T> get(String key) {
        return this;
    }

    public String asString(String defaultValue) {
        if (value instanceof String) {
            return (String) value;
        }
        return defaultValue;
    }

    public int asInt(int defaultValue) {
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return defaultValue;
    }

    public T getValue() {
        return value;
    }

    public <U> Dynamic<U> convert(DynamicOps<U> ops) {
        return new Dynamic<U>(null);
    }

    public Dynamic<T> orElseEmptyMap() {
        return this;
    }
}