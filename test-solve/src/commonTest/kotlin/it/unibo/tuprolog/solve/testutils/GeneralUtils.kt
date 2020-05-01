package it.unibo.tuprolog.solve.testutils

fun <T, U, R> Sequence<T>.product(other: Sequence<U>, combinator: (T, U) -> R): Sequence<R> =
    flatMap { x ->
        other.map { y -> combinator(x, y) }
    }

fun <T, U> Sequence<T>.product(other: Sequence<U>): Sequence<Pair<T, U>> =
    product(other, ::Pair)

fun <T, R> Sequence<T>.squared(combinator: (T, T) -> R): Sequence<R> =
    product(this, combinator)

fun <T> Sequence<T>.squared(): Sequence<Pair<T, T>> =
    product(this)