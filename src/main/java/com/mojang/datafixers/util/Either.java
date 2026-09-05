package com.mojang.datafixers.util;

import java.util.Optional;
import java.util.function.Function;

public class Either<L, R> {
    private final L left;
    private final R right;
    private final boolean isLeft;

    private Either(L left, R right, boolean isLeft) {
        this.left = left;
        this.right = right;
        this.isLeft = isLeft;
    }

    public static <L, R> Either<L, R> left(L value) {
        return new Either<>(value, null, true);
    }

    public static <L, R> Either<L, R> right(R value) {
        return new Either<>(null, value, false);
    }

    public <T> T map(Function<? super L, ? extends T> var1, Function<? super R, ? extends T> var2) {
        return isLeft ? var1.apply(left) : var2.apply(right);
    }

    public <T> Either<T, R> mapLeft(Function<? super L, ? extends T> mapper) {
        return isLeft ? left(mapper.apply(left)) : right(right);
    }

    public <T> Either<L, T> mapRight(Function<? super R, ? extends T> mapper) {
        return isLeft ? left(left) : right(mapper.apply(right));
    }

    public <T> Either<T, R> flatMap(Function<? super L, ? extends Either<T, R>> mapper) {
        return isLeft ? mapper.apply(left) : right(right);
    }

    public Optional<R> right() {
        return isLeft ? Optional.empty() : Optional.of(right);
    }

    public boolean isLeft() {
        return isLeft;
    }

    public boolean isRight() {
        return !isLeft;
    }

    public Optional<L> left() {
        return isLeft ? Optional.of(left) : Optional.empty();
    }

    public boolean isSuccess() {
        return isRight();
    }

    public Optional<L> getLeft() {
        return isLeft ? Optional.of(left) : Optional.empty();
    }

    public Optional<R> getRight() {
        return isLeft ? Optional.empty() : Optional.of(right);
    }

    public boolean ifRight(Object o) {
        return isRight();
    }

    public boolean ifLeft(Object o) {
        return isLeft;
    }
}