package net.minecraft.util;

public interface IObjectIntIterable<T> extends Iterable<T> {

    T getByValue(int value);
}
