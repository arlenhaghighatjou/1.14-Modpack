//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@GwtCompatible
final class CollectSpliterators {
    private CollectSpliterators() {
    }

    static <T> Spliterator<T> indexed(int size, int extraCharacteristics, IntFunction<T> function) {
        return indexed(size, extraCharacteristics, function, (Comparator) null);
    }

    static <T> Spliterator<T> indexed(int size, final int extraCharacteristics, IntFunction<T> function, final Comparator<? super T> comparator) {
        if (comparator != null) {
            Preconditions.checkArgument((extraCharacteristics & 4) != 0);
        }

        class WithCharacteristics implements Spliterator<T> {
            private final Spliterator<T> delegate;

            WithCharacteristics(Spliterator<T> delegate) {
                this.delegate = delegate;
            }

            public boolean tryAdvance(Consumer<? super T> action) {
                return this.delegate.tryAdvance(action);
            }

            public void forEachRemaining(Consumer<? super T> action) {
                this.delegate.forEachRemaining(action);
            }

            @Nullable
            public Spliterator<T> trySplit() {
                Spliterator<T> split = this.delegate.trySplit();
                return split == null ? null : new WithCharacteristics(split);
            }

            public long estimateSize() {
                return this.delegate.estimateSize();
            }

            public int characteristics() {
                return this.delegate.characteristics() | extraCharacteristics;
            }

            public Comparator<? super T> getComparator() {
                if (this.hasCharacteristics(4)) {
                    return comparator;
                } else {
                    throw new IllegalStateException();
                }
            }
        }

        return new WithCharacteristics(IntStream.range(0, size).mapToObj(function).spliterator());
    }

    static <F, T> Spliterator<T> map(final Spliterator<F> fromSpliterator, final Function<? super F, ? extends T> function) {
        Preconditions.checkNotNull(fromSpliterator);
        Preconditions.checkNotNull(function);
        return new Spliterator<T>() {
            public boolean tryAdvance(Consumer<? super T> action) {
                return fromSpliterator.tryAdvance((fromElement) -> {
                    action.accept(function.apply(fromElement));
                });
            }

            public void forEachRemaining(Consumer<? super T> action) {
                fromSpliterator.forEachRemaining((fromElement) -> {
                    action.accept(function.apply(fromElement));
                });
            }

            public Spliterator<T> trySplit() {
                Spliterator<F> fromSplit = fromSpliterator.trySplit();
                return fromSplit != null ? CollectSpliterators.map(fromSplit, function) : null;
            }

            public long estimateSize() {
                return fromSpliterator.estimateSize();
            }

            public int characteristics() {
                return fromSpliterator.characteristics() & -262;
            }
        };
    }

    static <T> Spliterator<T> filter(final Spliterator<T> fromSpliterator, final Predicate<? super T> predicate) {
        Preconditions.checkNotNull(fromSpliterator);
        Preconditions.checkNotNull(predicate);

        class Splitr implements Spliterator<T>, Consumer<T> {
            T holder = null;

            Splitr() {
            }

            public void accept(T t) {
                this.holder = t;
            }

            public boolean tryAdvance(Consumer<? super T> action) {
                while (true) {
                    if (fromSpliterator.tryAdvance(this)) {
                        boolean var2;
                        try {
                            if (!predicate.test(this.holder)) {
                                continue;
                            }

                            action.accept(this.holder);
                            var2 = true;
                        } finally {
                            this.holder = null;
                        }

                        return var2;
                    }

                    return false;
                }
            }

            public Spliterator<T> trySplit() {
                Spliterator<T> fromSplit = fromSpliterator.trySplit();
                return fromSplit == null ? null : CollectSpliterators.filter(fromSplit, predicate);
            }

            public long estimateSize() {
                return fromSpliterator.estimateSize() / 2L;
            }

            public Comparator<? super T> getComparator() {
                return fromSpliterator.getComparator();
            }

            public int characteristics() {
                return fromSpliterator.characteristics() & 277;
            }
        }

        return new Splitr();
    }

    static <F, T> Spliterator<T> flatMap(Spliterator<F> fromSpliterator, final Function<? super F, Spliterator<T>> function, int topCharacteristics, long topSize) {
        Preconditions.checkArgument((topCharacteristics & 16384) == 0, "flatMap does not support SUBSIZED characteristic");
        Preconditions.checkArgument((topCharacteristics & 4) == 0, "flatMap does not support SORTED characteristic");
        Preconditions.checkNotNull(fromSpliterator);
        Preconditions.checkNotNull(function);

        class FlatMapSpliterator implements Spliterator<T> {
            final Spliterator<F> from;
            final int characteristics;
            @Nullable
            Spliterator<T> prefix;
            long estimatedSize;

            FlatMapSpliterator(Spliterator<T> prefix, Spliterator<F> from, int characteristics, long estimatedSize) {
                this.prefix = prefix;
                this.from = from;
                this.characteristics = characteristics;
                this.estimatedSize = estimatedSize;
            }

            public boolean tryAdvance(Consumer<? super T> action) {
                do {
                    if (this.prefix != null && this.prefix.tryAdvance(action)) {
                        if (this.estimatedSize != Long.MAX_VALUE) {
                            --this.estimatedSize;
                        }

                        return true;
                    }

                    this.prefix = null;
                } while (this.from.tryAdvance((fromElement) -> {
                    this.prefix = (Spliterator) function.apply(fromElement);
                }));

                return false;
            }

            public void forEachRemaining(Consumer<? super T> action) {
                if (this.prefix != null) {
                    this.prefix.forEachRemaining(action);
                    this.prefix = null;
                }

                this.from.forEachRemaining((fromElement) -> {
                    ((Spliterator) function.apply(fromElement)).forEachRemaining(action);
                });
                this.estimatedSize = 0L;
            }

            public Spliterator<T> trySplit() {
                Spliterator<F> fromSplit = this.from.trySplit();
                if (fromSplit != null) {
                    int splitCharacteristics = this.characteristics & -65;
                    long estSplitSize = this.estimateSize();
                    if (estSplitSize < Long.MAX_VALUE) {
                        estSplitSize /= 2L;
                        this.estimatedSize -= estSplitSize;
                    }

                    Spliterator<T> resultx = new FlatMapSpliterator(this.prefix, fromSplit, splitCharacteristics, estSplitSize);
                    this.prefix = null;
                    return resultx;
                } else if (this.prefix != null) {
                    Spliterator<T> result = this.prefix;
                    this.prefix = null;
                    return result;
                } else {
                    return null;
                }
            }

            public long estimateSize() {
                if (this.prefix != null) {
                    this.estimatedSize = Math.max(this.estimatedSize, this.prefix.estimateSize());
                }

                return Math.max(this.estimatedSize, 0L);
            }

            public int characteristics() {
                return this.characteristics;
            }
        }

        return new FlatMapSpliterator((Spliterator) null, fromSpliterator, topCharacteristics, topSize);
    }
}
