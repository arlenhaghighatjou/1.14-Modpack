package net.minecraft.util;

import java.util.Map;

public interface DynamicOps<T> {
    T createMap(Map<Object, Object> collect);

    T createString(String string);
}