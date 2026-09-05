package com.mojang.datafixers.types;

import com.google.common.collect.ImmutableMap;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public interface DynamicOps<T> {
    T empty();

    default T emptyMap() {
        return this.createMap(ImmutableMap.of());
    }

    default T emptyList() {
        return this.createList(Stream.empty());
    }

    Type<?> getType(T var1);

    Optional<Number> getNumberValue(T var1);

    default Number getNumberValue(T input, Number defaultValue) {
        return (Number) this.getNumberValue(input).orElse(defaultValue);
    }

    T createNumeric(Number var1);

    default T createByte(byte value) {
        return this.createNumeric(value);
    }

    default T createShort(short value) {
        return this.createNumeric(value);
    }

    default T createInt(int value) {
        return this.createNumeric(value);
    }

    default T createLong(long value) {
        return this.createNumeric(value);
    }

    default T createFloat(float value) {
        return this.createNumeric(value);
    }

    default T createDouble(double value) {
        return this.createNumeric(value);
    }

    default T createBoolean(boolean value) {
        return this.createByte((byte) (value ? 1 : 0));
    }

    Optional<String> getStringValue(T var1);

    T createString(String var1);

    T mergeInto(T var1, T var2);

    T mergeInto(T var1, T var2, T var3);

    T merge(T var1, T var2);

    Optional<Map<T, T>> getMapValues(T var1);

    T createMap(Map<T, T> var1);

    Optional<Stream<T>> getStream(T var1);

    T createList(Stream<T> var1);

    default Optional<ByteBuffer> getByteBuffer(T input) {
        return this.getStream(input).flatMap((stream) -> {
            List<T> list = (List) stream.collect(Collectors.toList());
            if (!list.stream().allMatch((element) -> {
                return this.getNumberValue(element).isPresent();
            })) {
                return Optional.empty();
            } else {
                ByteBuffer buffer = ByteBuffer.wrap(new byte[list.size()]);

                for (int i = 0; i < list.size(); ++i) {
                    buffer.put(i, ((Number) this.getNumberValue(list.get(i)).get()).byteValue());
                }

                return Optional.of(buffer);
            }
        });
    }

    default T createByteList(ByteBuffer input) {
        int[] i = new int[]{0};
        return this.createList(Stream.generate(() -> {
            int var10005 = i[0];
            int var10002 = i[0];
            i[0] = var10005 + 1;
            return this.createByte(input.get(var10002));
        }).limit((long) input.capacity()));
    }

    default Optional<IntStream> getIntStream(T input) {
        return this.getStream(input).flatMap((stream) -> {
            List<T> list = (List) stream.collect(Collectors.toList());
            return list.stream().allMatch((element) -> {
                return this.getNumberValue(element).isPresent();
            }) ? Optional.of(list.stream().mapToInt((element) -> {
                return ((Number) this.getNumberValue(element).get()).intValue();
            })) : Optional.empty();
        });
    }

    default T createIntList(IntStream input) {
        return this.createList(input.mapToObj(this::createInt));
    }

    default Optional<LongStream> getLongStream(T input) {
        return this.getStream(input).flatMap((stream) -> {
            List<T> list = (List) stream.collect(Collectors.toList());
            return list.stream().allMatch((element) -> {
                return this.getNumberValue(element).isPresent();
            }) ? Optional.of(list.stream().mapToLong((element) -> {
                return ((Number) this.getNumberValue(element).get()).longValue();
            })) : Optional.empty();
        });
    }

    default T createLongList(LongStream input) {
        return this.createList(input.mapToObj(this::createLong));
    }

    T remove(T var1, String var2);

    default Optional<T> get(T input, String key) {
        return this.getGeneric(input, this.createString(key));
    }

    default Optional<T> getGeneric(T input, T key) {
        return this.getMapValues(input).flatMap((map) -> {
            return Optional.ofNullable(map.get(key));
        });
    }

    default T set(T input, String key, T value) {
        return this.mergeInto(input, this.createString(key), value);
    }

    default T update(T input, String key, Function<T, T> function) {
        return this.get(input, key).map((value) -> {
            return this.set(input, key, function.apply(value));
        }).orElse(input);
    }

    default T updateGeneric(T input, T key, Function<T, T> function) {
        return this.getGeneric(input, key).map((value) -> {
            return this.mergeInto(input, key, function.apply(value));
        }).orElse(input);
    }
}
