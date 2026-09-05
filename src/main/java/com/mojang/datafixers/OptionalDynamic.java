package com.mojang.datafixers;

import com.mojang.datafixers.types.DynamicOps;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public final class OptionalDynamic<T> extends DynamicLike<T> {
    private final Optional<Dynamic<T>> delegate;

    public OptionalDynamic(DynamicOps<T> ops, Optional<Dynamic<T>> delegate) {
        super(ops);
        this.delegate = delegate;
    }

    public Optional<Dynamic<T>> get() {
        return this.delegate;
    }

    public <U> Optional<U> map(Function<? super Dynamic<T>, ? extends U> mapper) {
        return this.delegate.map(mapper);
    }

    public <U> Optional<U> flatMap(Function<? super Dynamic<T>, Optional<U>> mapper) {
        return this.delegate.flatMap(mapper);
    }

    public Optional<Number> asNumber() {
        return this.flatMap(DynamicLike::asNumber);
    }

    public Optional<String> asString() {
        return this.flatMap(DynamicLike::asString);
    }

    public Optional<Stream<Dynamic<T>>> asStreamOpt() {
        return this.flatMap(DynamicLike::asStreamOpt);
    }

    public Optional<ByteBuffer> asByteBufferOpt() {
        return this.flatMap(DynamicLike::asByteBufferOpt);
    }

    public Optional<IntStream> asIntStreamOpt() {
        return this.flatMap(DynamicLike::asIntStreamOpt);
    }

    public Optional<LongStream> asLongStreamOpt() {
        return this.flatMap(DynamicLike::asLongStreamOpt);
    }

    public OptionalDynamic<T> get(String key) {
        return new OptionalDynamic(this.ops, this.flatMap((k) -> {
            return k.get(key).get();
        }));
    }

    public Optional<T> getGeneric(T key) {
        return this.flatMap((v) -> {
            return v.getGeneric(key);
        });
    }

    public Optional<T> getElement(String key) {
        return this.flatMap((v) -> {
            return v.getElement(key);
        });
    }

    public Optional<T> getElementGeneric(T key) {
        return this.flatMap((v) -> {
            return v.getElementGeneric(key);
        });
    }

    public <U> Optional<List<U>> asListOpt(Function<Dynamic<T>, U> deserializer) {
        return this.flatMap((t) -> {
            return t.asListOpt(deserializer);
        });
    }

    public <K, V> Optional<Map<K, V>> asMapOpt(Function<Dynamic<T>, K> keyDeserializer, Function<Dynamic<T>, V> valueDeserializer) {
        return this.flatMap((input) -> {
            return input.asMapOpt(keyDeserializer, valueDeserializer);
        });
    }

    public Dynamic<T> orElseEmptyMap() {
        return (Dynamic) this.delegate.orElseGet(this::emptyMap);
    }

    public Dynamic<T> orElseEmptyList() {
        return (Dynamic) this.delegate.orElseGet(this::emptyList);
    }
}
