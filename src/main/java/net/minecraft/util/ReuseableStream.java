package net.minecraft.util;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Stream;

public class ReuseableStream<T> {
    private static final ReuseableStream<?> EMPTY = new ReuseableStream<>(Stream.empty());
    private final List<T> cachedValues = Lists.newArrayList();

    public ReuseableStream(Stream<T> p_i49816_1_) {
        p_i49816_1_.forEach(this.cachedValues::add);
    }

    public Stream<T> createStream() {
        return this.cachedValues.stream();
    }

    @SuppressWarnings("unchecked")
    public static <T> ReuseableStream<T> empty() {
        return (ReuseableStream<T>) EMPTY;
    }
}
