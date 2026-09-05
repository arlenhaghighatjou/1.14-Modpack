package com.mojang.datafixers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.types.DynamicOps;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public abstract class DynamicLike<T> {
    protected final DynamicOps<T> ops;

    public DynamicLike(DynamicOps<T> ops) {
        this.ops = ops;
    }

    public DynamicOps<T> getOps() {
        return this.ops;
    }

    public abstract Optional<Number> asNumber();

    public abstract Optional<String> asString();

    public abstract Optional<Stream<Dynamic<T>>> asStreamOpt();

    public abstract Optional<ByteBuffer> asByteBufferOpt();

    public abstract Optional<IntStream> asIntStreamOpt();

    public abstract Optional<LongStream> asLongStreamOpt();

    public abstract OptionalDynamic<T> get(String var1);

    public abstract Optional<T> getGeneric(T var1);

    public abstract Optional<T> getElement(String var1);

    public abstract Optional<T> getElementGeneric(T var1);

    public abstract <U> Optional<List<U>> asListOpt(Function<Dynamic<T>, U> var1);

    public abstract <K, V> Optional<Map<K, V>> asMapOpt(Function<Dynamic<T>, K> var1, Function<Dynamic<T>, V> var2);

    public Number asNumber(Number defaultValue) {
        return (Number) this.asNumber().orElse(defaultValue);
    }

    public int asInt(int defaultValue) {
        return this.asNumber(defaultValue).intValue();
    }

    public long asLong(long defaultValue) {
        return this.asNumber(defaultValue).longValue();
    }

    public float asFloat(float defaultValue) {
        return this.asNumber(defaultValue).floatValue();
    }

    public double asDouble(double defaultValue) {
        return this.asNumber(defaultValue).doubleValue();
    }

    public byte asByte(byte defaultValue) {
        return this.asNumber(defaultValue).byteValue();
    }

    public short asShort(short defaultValue) {
        return this.asNumber(defaultValue).shortValue();
    }

    public boolean asBoolean(boolean defaultValue) {
        return this.asNumber(defaultValue ? 1 : 0).intValue() != 0;
    }

    public String asString(String defaultValue) {
        return (String) this.asString().orElse(defaultValue);
    }

    public Stream<Dynamic<T>> asStream() {
        return (Stream) this.asStreamOpt().orElseGet(Stream::empty);
    }

    public ByteBuffer asByteBuffer() {
        return (ByteBuffer) this.asByteBufferOpt().orElseGet(() -> {
            return ByteBuffer.wrap(new byte[0]);
        });
    }

    public IntStream asIntStream() {
        return (IntStream) this.asIntStreamOpt().orElseGet(IntStream::empty);
    }

    public LongStream asLongStream() {
        return (LongStream) this.asLongStreamOpt().orElseGet(LongStream::empty);
    }

    public <U> List<U> asList(Function<Dynamic<T>, U> deserializer) {
        return (List) this.asListOpt(deserializer).orElseGet(ImmutableList::of);
    }

    public <K, V> Map<K, V> asMap(Function<Dynamic<T>, K> keyDeserializer, Function<Dynamic<T>, V> valueDeserializer) {
        return (Map) this.asMapOpt(keyDeserializer, valueDeserializer).orElseGet(ImmutableMap::of);
    }

    public T getElement(String key, T defaultValue) {
        return this.getElement(key).orElse(defaultValue);
    }

    public T getElementGeneric(T key, T defaultValue) {
        return this.getElementGeneric(key).orElse(defaultValue);
    }

    public Dynamic<T> emptyList() {
        return new Dynamic(this.ops, this.ops.emptyList());
    }

    public Dynamic<T> emptyMap() {
        return new Dynamic(this.ops, this.ops.emptyMap());
    }

    public Dynamic<T> createNumeric(Number i) {
        return new Dynamic(this.ops, this.ops.createNumeric(i));
    }

    public Dynamic<T> createByte(byte value) {
        return new Dynamic(this.ops, this.ops.createByte(value));
    }

    public Dynamic<T> createShort(short value) {
        return new Dynamic(this.ops, this.ops.createShort(value));
    }

    public Dynamic<T> createInt(int value) {
        return new Dynamic(this.ops, this.ops.createInt(value));
    }

    public Dynamic<T> createLong(long value) {
        return new Dynamic(this.ops, this.ops.createLong(value));
    }

    public Dynamic<T> createFloat(float value) {
        return new Dynamic(this.ops, this.ops.createFloat(value));
    }

    public Dynamic<T> createDouble(double value) {
        return new Dynamic(this.ops, this.ops.createDouble(value));
    }

    public Dynamic<T> createBoolean(boolean value) {
        return new Dynamic(this.ops, this.ops.createBoolean(value));
    }

    public Dynamic<T> createString(String value) {
        return new Dynamic(this.ops, this.ops.createString(value));
    }

    public Dynamic<T> createList(Stream<? extends Dynamic<?>> input) {
        return new Dynamic(this.ops, this.ops.createList(input.map((element) -> {
            return element.cast(this.ops);
        })));
    }

    public Dynamic<T> createMap(Map<? extends Dynamic<?>, ? extends Dynamic<?>> map) {
        Builder<T, T> builder = ImmutableMap.builder();
        Iterator var3 = map.entrySet().iterator();

        while (var3.hasNext()) {
            Entry<? extends Dynamic<?>, ? extends Dynamic<?>> entry = (Entry) var3.next();
            builder.put((T) ((Dynamic) entry.getKey()).cast(this.ops), (T) ((Dynamic) entry.getValue()).cast(this.ops));
        }

        return new Dynamic(this.ops, this.ops.createMap(builder.build()));
    }

    public Dynamic<?> createByteList(ByteBuffer input) {
        return new Dynamic(this.ops, this.ops.createByteList(input));
    }

    public Dynamic<?> createIntList(IntStream input) {
        return new Dynamic(this.ops, this.ops.createIntList(input));
    }

    public Dynamic<?> createLongList(LongStream input) {
        return new Dynamic(this.ops, this.ops.createLongList(input));
    }
}
