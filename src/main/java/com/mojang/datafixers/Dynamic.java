package com.mojang.datafixers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.types.DynamicOps;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Dynamic<T> extends DynamicLike<T> {
    private final T value;

    public Dynamic(DynamicOps<T> ops) {
        this(ops, ops.empty());
    }

    public Dynamic(DynamicOps<T> ops, @Nullable T value) {
        super(ops);
        this.value = value == null ? ops.empty() : value;
    }

    public static <S, T> T convert(DynamicOps<S> inOps, DynamicOps<T> outOps, S input) {
        return null;
    }

    public T getValue() {
        return this.value;
    }

    public Dynamic<T> map(Function<? super T, ? extends T> function) {
        return new Dynamic(this.ops, function.apply(this.value));
    }

    public <U> Dynamic<U> castTyped(DynamicOps<U> ops) {
        if (!Objects.equals(this.ops, ops)) {
            throw new IllegalStateException("Dynamic type doesn't match");
        } else {
            return (Dynamic<U>) this;
        }
    }

    public <U> U cast(DynamicOps<U> ops) {
        return this.castTyped(ops).getValue();
    }

    public Dynamic<T> merge(Dynamic<?> value) {
        return this.map((v) -> {
            return this.ops.mergeInto(v, value.cast(this.ops));
        });
    }

    public Dynamic<T> merge(Dynamic<?> key, Dynamic<?> value) {
        return this.map((v) -> {
            return this.ops.mergeInto(v, key.cast(this.ops), value.cast(this.ops));
        });
    }

    public Optional<Map<Dynamic<T>, Dynamic<T>>> getMapValues() {
        return this.ops.getMapValues(this.value).map((map) -> {
            Builder<Dynamic<T>, Dynamic<T>> builder = ImmutableMap.builder();
            Iterator var3 = map.entrySet().iterator();

            while (var3.hasNext()) {
                Entry<T, T> entry = (Entry) var3.next();
                builder.put(new Dynamic(this.ops, entry.getKey()), new Dynamic(this.ops, entry.getValue()));
            }

            return builder.build();
        });
    }

    public Optional<Number> asNumber() {
        return this.ops.getNumberValue(this.value);
    }

    public Optional<String> asString() {
        return this.ops.getStringValue(this.value);
    }

    public Optional<Stream<Dynamic<T>>> asStreamOpt() {
        return this.ops.getStream(this.value).map((s) -> {
            return s.map((e) -> {
                return new Dynamic(this.ops, e);
            });
        });
    }

    public Optional<ByteBuffer> asByteBufferOpt() {
        return this.ops.getByteBuffer(this.value);
    }

    public Optional<IntStream> asIntStreamOpt() {
        return this.ops.getIntStream(this.value);
    }

    public Optional<LongStream> asLongStreamOpt() {
        return this.ops.getLongStream(this.value);
    }

    public OptionalDynamic<T> get(String key) {
        return new OptionalDynamic(this.ops, this.ops.get(this.value, key).map((v) -> {
            return new Dynamic(this.ops, v);
        }));
    }

    public Optional<T> getGeneric(T key) {
        return this.ops.getGeneric(this.value, key);
    }

    public Dynamic<T> remove(String key) {
        return this.map((v) -> {
            return this.ops.remove(v, key);
        });
    }

    public Dynamic<T> set(String key, Dynamic<?> value) {
        return this.map((v) -> {
            return this.ops.set(v, key, value.cast(this.ops));
        });
    }

    public Dynamic<T> update(String key, Function<Dynamic<?>, Dynamic<?>> function) {
        return this.map((v) -> {
            return this.ops.update(v, key, (value) -> {
                return (T) ((Dynamic) function.apply(new Dynamic(this.ops, value))).cast(this.ops);
            });
        });
    }

    public Dynamic<T> updateGeneric(T key, Function<T, T> function) {
        return this.map((v) -> {
            return this.ops.updateGeneric(v, key, function);
        });
    }

    public Optional<T> getElement(String key) {
        return this.getElementGeneric(this.ops.createString(key));
    }

    public Optional<T> getElementGeneric(T key) {
        return this.ops.getMapValues(this.value).flatMap((m) -> {
            return Optional.ofNullable(m.get(key));
        });
    }

    public <U> Optional<List<U>> asListOpt(Function<Dynamic<T>, U> deserializer) {
        return this.asStreamOpt().map((stream) -> {
            return (List) stream.map(deserializer).collect(Collectors.toList());
        });
    }

    public <K, V> Optional<Map<K, V>> asMapOpt(Function<Dynamic<T>, K> keyDeserializer, Function<Dynamic<T>, V> valueDeserializer) {
        return this.ops.getMapValues(this.value).map((map) -> {
            Builder<K, V> builder = ImmutableMap.builder();
            Iterator var5 = map.entrySet().iterator();

            while (var5.hasNext()) {
                Entry<T, T> entry = (Entry) var5.next();
                builder.put((K) keyDeserializer.apply(new Dynamic(this.ops, entry.getKey())), (V) valueDeserializer.apply(new Dynamic(this.ops, entry.getValue())));
            }

            return builder.build();
        });
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Dynamic<?> dynamic = (Dynamic) o;
            return Objects.equals(this.ops, dynamic.ops) && Objects.equals(this.value, dynamic.value);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.ops, this.value});
    }

    public String toString() {
        return String.format("%s[%s]", this.ops, this.value);
    }

    public <R> Dynamic<R> convert(DynamicOps<R> outOps) {
        return new Dynamic(outOps, convert(this.ops, outOps, this.value));
    }
}
