//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.math.LongMath;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

@Beta
@GwtCompatible
public final class Streams {
    private Streams() {
    }

    public static <T> Stream<T> stream(Iterable<T> iterable) {
        return iterable instanceof Collection ? ((Collection) iterable).stream() : StreamSupport.stream(iterable.spliterator(), false);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static <T> Stream<T> stream(Collection<T> collection) {
        return collection.stream();
    }

    public static <T> Stream<T> stream(Iterator<T> iterator) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
    }

    public static <T> Stream<T> stream(Optional<T> optional) {
        return optional.isPresent() ? Stream.of(optional.get()) : Stream.of();
    }

    public static <T> Stream<T> stream(java.util.Optional<T> optional) {
        return optional.isPresent() ? Stream.of(optional.get()) : Stream.of();
    }

    @SafeVarargs
    public static <T> Stream<T> concat(Stream<? extends T>... streams) {
        boolean isParallel = false;
        int characteristics = 336;
        long estimatedSize = 0L;
        ImmutableList.Builder<Spliterator<? extends T>> splitrsBuilder = new ImmutableList.Builder(streams.length);
        Stream[] var6 = streams;
        int var7 = streams.length;

        for (int var8 = 0; var8 < var7; ++var8) {
            Stream<? extends T> stream = var6[var8];
            isParallel |= stream.isParallel();
            Spliterator<? extends T> splitr = stream.spliterator();
            splitrsBuilder.add(splitr);
            characteristics &= splitr.characteristics();
            estimatedSize = LongMath.saturatedAdd(estimatedSize, splitr.estimateSize());
        }

        return (Stream<T>) StreamSupport.stream(CollectSpliterators.flatMap(splitrsBuilder.build().spliterator(), (splitrx) -> {
            return splitrx;
        }, characteristics, estimatedSize), isParallel);
    }

    public static IntStream concat(IntStream... streams) {
        return Stream.of(streams).flatMapToInt((stream) -> {
            return stream;
        });
    }

    public static LongStream concat(LongStream... streams) {
        return Stream.of(streams).flatMapToLong((stream) -> {
            return stream;
        });
    }

    public static DoubleStream concat(DoubleStream... streams) {
        return Stream.of(streams).flatMapToDouble((stream) -> {
            return stream;
        });
    }

    public static IntStream stream(OptionalInt optional) {
        return optional.isPresent() ? IntStream.of(optional.getAsInt()) : IntStream.empty();
    }

    public static LongStream stream(OptionalLong optional) {
        return optional.isPresent() ? LongStream.of(optional.getAsLong()) : LongStream.empty();
    }

    public static DoubleStream stream(OptionalDouble optional) {
        return optional.isPresent() ? DoubleStream.of(optional.getAsDouble()) : DoubleStream.empty();
    }

    public static <T> java.util.Optional<T> findLast(Stream<T> stream) {
        class OptionalState<T> {
            boolean set = false;
            T value = null;

            OptionalState() {
            }

            void set(@Nullable T value) {
                this.set = true;
                this.value = value;
            }

            T get() {
                Preconditions.checkState(this.set);
                return this.value;
            }
        }

        OptionalState<T> state = new OptionalState();
        Deque<Spliterator<T>> splits = new ArrayDeque();
        splits.addLast(stream.spliterator());

        while (true) {
            while (true) {
                Spliterator spliterator;
                do {
                    if (splits.isEmpty()) {
                        return java.util.Optional.empty();
                    }

                    spliterator = (Spliterator) splits.removeLast();
                } while (spliterator.getExactSizeIfKnown() == 0L);

                Spliterator prefix;
                if (spliterator.hasCharacteristics(16384)) {
                    while (true) {
                        prefix = spliterator.trySplit();
                        if (prefix == null || prefix.getExactSizeIfKnown() == 0L) {
                            break;
                        }

                        if (spliterator.getExactSizeIfKnown() == 0L) {
                            spliterator = prefix;
                            break;
                        }
                    }

                    return java.util.Optional.of(state.get());
                }

                prefix = spliterator.trySplit();
                if (prefix != null && prefix.getExactSizeIfKnown() != 0L) {
                    splits.addLast(prefix);
                    splits.addLast(spliterator);
                } else {
                    if (state.set) {
                        return java.util.Optional.of(state.get());
                    }
                }
            }
        }
    }

    public static OptionalInt findLast(IntStream stream) {
        java.util.Optional<Integer> boxedLast = findLast(stream.boxed());
        return boxedLast.isPresent() ? OptionalInt.of((Integer) boxedLast.get()) : OptionalInt.empty();
    }

    public static OptionalLong findLast(LongStream stream) {
        java.util.Optional<Long> boxedLast = findLast(stream.boxed());
        return boxedLast.isPresent() ? OptionalLong.of((Long) boxedLast.get()) : OptionalLong.empty();
    }

    public static OptionalDouble findLast(DoubleStream stream) {
        java.util.Optional<Double> boxedLast = findLast(stream.boxed());
        return boxedLast.isPresent() ? OptionalDouble.of((Double) boxedLast.get()) : OptionalDouble.empty();
    }

    public static <A, B, R> Stream<R> zip(Stream<A> streamA, Stream<B> streamB, final BiFunction<? super A, ? super B, R> function) {
        Preconditions.checkNotNull(streamA);
        Preconditions.checkNotNull(streamB);
        Preconditions.checkNotNull(function);
        boolean isParallel = streamA.isParallel() || streamB.isParallel();
        Spliterator<A> splitrA = streamA.spliterator();
        Spliterator<B> splitrB = streamB.spliterator();
        int characteristics = splitrA.characteristics() & splitrB.characteristics() & 80;
        final Iterator<A> itrA = Spliterators.iterator(splitrA);
        final Iterator<B> itrB = Spliterators.iterator(splitrB);
        return StreamSupport.stream(java.util.Spliterators.spliteratorUnknownSize(new java.util.Iterator<R>() {
            public boolean hasNext() {
                return itrA.hasNext() && itrB.hasNext();
            }

            public R next() {
                if (!hasNext()) throw new java.util.NoSuchElementException();
                return function.apply(itrA.next(), itrB.next());
            }
        }, characteristics), isParallel);
    }

    public static <T, R> Stream<R> mapWithIndex(Stream<T> stream, final FunctionWithIndex<? super T, ? extends R> function) {
        Preconditions.checkNotNull(stream);
        Preconditions.checkNotNull(function);
        boolean isParallel = stream.isParallel();
        Spliterator<T> fromSpliterator = stream.spliterator();
        if (!fromSpliterator.hasCharacteristics(16384)) {
            final Iterator<T> fromIterator = Spliterators.iterator(fromSpliterator);
            return StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(
                            new Iterator<R>() {
                                long index = 0L;

                                public boolean hasNext() {
                                    return fromIterator.hasNext();
                                }

                                public R next() {
                                    if (!hasNext()) throw new java.util.NoSuchElementException();
                                    return function.apply(fromIterator.next(), (long) (this.index++));
                                }
                            },
                            fromSpliterator.characteristics() & 80
                    ),
                    isParallel
            );
        } else {
            class Splitr extends MapWithIndexSpliterator<Spliterator<T>, R, Splitr> implements Consumer<T> {
                T holder;

                Splitr(Spliterator<T> splitr, long index) {
                    super(splitr, index);
                }

                public void accept(@Nullable T t) {
                    this.holder = t;
                }

                public boolean tryAdvance(Consumer<? super R> action) {
                    if (this.fromSpliterator.tryAdvance(this)) {
                        try {
                            action.accept(function.apply(this.holder, (long) (this.index++)));
                            return true;
                        } finally {
                            this.holder = null;
                        }
                    } else {
                        return false;
                    }
                }

                Splitr createSplit(Spliterator<T> from, long i) {
                    return new Splitr(from, i);
                }
            }

            return StreamSupport.stream(new Splitr(fromSpliterator, 0L), isParallel);
        }
    }

    public static <R> Stream<R> mapWithIndex(IntStream stream, final IntFunctionWithIndex<R> function) {
        Preconditions.checkNotNull(stream);
        Preconditions.checkNotNull(function);
        boolean isParallel = stream.isParallel();
        Spliterator.OfInt fromSpliterator = stream.spliterator();
        if (!fromSpliterator.hasCharacteristics(16384)) {
            final PrimitiveIterator.OfInt fromIterator = Spliterators.iterator(fromSpliterator);
            return StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(
                            new Iterator<R>() {
                                long index = 0L;

                                public boolean hasNext() {
                                    return fromIterator.hasNext();
                                }

                                public R next() {
                                    if (!hasNext()) throw new java.util.NoSuchElementException();
                                    return function.apply(fromIterator.nextInt(), (long) (this.index++));
                                }
                            },
                            fromSpliterator.characteristics() & 80
                    ),
                    isParallel
            );
        } else {
            class Splitr extends MapWithIndexSpliterator<Spliterator.OfInt, R, Splitr> implements IntConsumer, Spliterator<R> {
                int holder;

                Splitr(Spliterator.OfInt splitr, long index) {
                    super(splitr, index);
                }

                public void accept(int t) {
                    this.holder = t;
                }

                public boolean tryAdvance(Consumer<? super R> action) {
                    if (((Spliterator.OfInt) this.fromSpliterator).tryAdvance(this)) {
                        action.accept(function.apply(this.holder, (long) (this.index++)));
                        return true;
                    } else {
                        return false;
                    }
                }

                Splitr createSplit(Spliterator.OfInt from, long i) {
                    return new Splitr(from, i);
                }
            }

            return StreamSupport.stream(new Splitr(fromSpliterator, 0L), isParallel);
        }
    }

    public static <R> Stream<R> mapWithIndex(LongStream stream, final LongFunctionWithIndex<R> function) {
        Preconditions.checkNotNull(stream);
        Preconditions.checkNotNull(function);
        boolean isParallel = stream.isParallel();
        Spliterator.OfLong fromSpliterator = stream.spliterator();
        if (!fromSpliterator.hasCharacteristics(16384)) {
            final PrimitiveIterator.OfLong fromIterator = Spliterators.iterator(fromSpliterator);
            return StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(
                            new Iterator<R>() {
                                long index = 0L;

                                public boolean hasNext() {
                                    return fromIterator.hasNext();
                                }

                                public R next() {
                                    if (!hasNext()) throw new java.util.NoSuchElementException();
                                    return function.apply(fromIterator.nextLong(), (long) (this.index++));
                                }
                            },
                            fromSpliterator.characteristics() & 80
                    ),
                    isParallel
            );
        } else {
            class Splitr extends MapWithIndexSpliterator<Spliterator.OfLong, R, Splitr> implements LongConsumer, Spliterator<R> {
                long holder;

                Splitr(Spliterator.OfLong splitr, long index) {
                    super(splitr, index);
                }

                public void accept(long t) {
                    this.holder = t;
                }

                public boolean tryAdvance(Consumer<? super R> action) {
                    if (((Spliterator.OfLong) this.fromSpliterator).tryAdvance(this)) {
                        action.accept(function.apply(this.holder, (long) (this.index++)));
                        return true;
                    } else {
                        return false;
                    }
                }

                Splitr createSplit(Spliterator.OfLong from, long i) {
                    return new Splitr(from, i);
                }
            }

            return StreamSupport.stream(new Splitr(fromSpliterator, 0L), isParallel);
        }
    }

    public static <R> Stream<R> mapWithIndex(DoubleStream stream, final DoubleFunctionWithIndex<R> function) {
        Preconditions.checkNotNull(stream);
        Preconditions.checkNotNull(function);
        boolean isParallel = stream.isParallel();
        Spliterator.OfDouble fromSpliterator = stream.spliterator();
        if (!fromSpliterator.hasCharacteristics(16384)) {
            final PrimitiveIterator.OfDouble fromIterator = Spliterators.iterator(fromSpliterator);
            return StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(
                            new Iterator<R>() {
                                long index = 0L;

                                public boolean hasNext() {
                                    return fromIterator.hasNext();
                                }

                                public R next() {
                                    if (!hasNext()) throw new java.util.NoSuchElementException();
                                    return function.apply(fromIterator.nextDouble(), (long) (this.index++));
                                }
                            },
                            fromSpliterator.characteristics() & 80
                    ),
                    isParallel
            );
        } else {
            class Splitr extends MapWithIndexSpliterator<Spliterator.OfDouble, R, Splitr> implements DoubleConsumer, Spliterator<R> {
                double holder;

                Splitr(Spliterator.OfDouble splitr, long index) {
                    super(splitr, index);
                }

                public void accept(double t) {
                    this.holder = t;
                }

                public boolean tryAdvance(Consumer<? super R> action) {
                    if (((Spliterator.OfDouble) this.fromSpliterator).tryAdvance(this)) {
                        action.accept(function.apply(this.holder, (long) (this.index++)));
                        return true;
                    } else {
                        return false;
                    }
                }

                Splitr createSplit(Spliterator.OfDouble from, long i) {
                    return new Splitr(from, i);
                }
            }

            return StreamSupport.stream(new Splitr(fromSpliterator, 0L), isParallel);
        }
    }

    @Beta
    public interface DoubleFunctionWithIndex<R> {
        R apply(double var1, long var3);
    }

    @Beta
    public interface LongFunctionWithIndex<R> {
        R apply(long var1, long var3);
    }

    @Beta
    public interface IntFunctionWithIndex<R> {
        R apply(int var1, long var2);
    }

    @Beta
    public interface FunctionWithIndex<T, R> {
        R apply(T var1, long var2);
    }

    private abstract static class MapWithIndexSpliterator<F extends Spliterator<?>, R, S extends MapWithIndexSpliterator<F, R, S>> implements Spliterator<R> {
        final F fromSpliterator;
        long index;

        MapWithIndexSpliterator(F fromSpliterator, long index) {
            this.fromSpliterator = fromSpliterator;
            this.index = index;
        }

        abstract S createSplit(F var1, long var2);

        public S trySplit() {
            F split = (F) this.fromSpliterator.trySplit();
            if (split == null) {
                return null;
            } else {
                S result = this.createSplit(split, this.index);
                this.index += split.getExactSizeIfKnown();
                return result;
            }
        }

        public long estimateSize() {
            return this.fromSpliterator.estimateSize();
        }

        public int characteristics() {
            return this.fromSpliterator.characteristics() & 16464;
        }
    }
}
