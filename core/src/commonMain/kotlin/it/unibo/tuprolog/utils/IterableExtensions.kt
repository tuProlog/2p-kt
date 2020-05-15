@file:JvmName("IterableExtensions")

package it.unibo.tuprolog.utils

import kotlin.jvm.JvmName

fun <T> merge(iterables: Iterable<Iterable<T>>, comparator: (T, T) -> Int): Sequence<T> {
    return merge(Comparator(comparator), iterables)
}

fun <T> merge(comparator: Comparator<T>, iterables: Iterable<Iterable<T>>): Sequence<T> {
    return sequence {
        val pipeline = iterables.asSequence().map { it.cursor() }.filterNot { it.isOver }.toMutableList()
        while (pipeline.isNotEmpty()) {
            val (minIndex, minValue) = pipeline.asSequence().map { it.current!! }.indexed().minWith(
                Comparator<IntIndexed<T>> { a, b -> comparator.compare(a.value, b.value) }
            )!!
            yield(minValue)
            pipeline[minIndex].next.let {
                if (it.isOver) {
                    pipeline.removeAt(minIndex)
                } else {
                    pipeline[minIndex] = it
                }
            }
        }
    }
}

fun <T> merge(vararg iterables: Iterable<T>, comparator: (T, T) -> Int): Sequence<T> {
    return merge(Comparator(comparator), *iterables)
}

fun <T> merge(comparator: Comparator<T>, vararg iterables: Iterable<T>): Sequence<T> {
    return merge(comparator, listOf(*iterables))
}

fun <T> merge(iterables: Sequence<Iterable<T>>, comparator: (T, T) -> Int): Sequence<T> {
    return merge(Comparator(comparator), iterables)
}

fun <T> merge(comparator: Comparator<T>, iterables: Sequence<Iterable<T>>): Sequence<T> {
    return merge(comparator, iterables.asIterable())
}

fun <T> mergeSequences(iterables: Iterable<Sequence<T>>, comparator: (T, T) -> Int): Sequence<T> {
    return merge(Comparator(comparator), iterables.map { it.asIterable() })
}

fun <T> mergeSequences(comparator: Comparator<T>, iterables: Iterable<Sequence<T>>): Sequence<T> {
    return merge(comparator, iterables.map { it.asIterable() })
}

fun <T> mergeSequences(iterables: Sequence<Sequence<T>>, comparator: (T, T) -> Int): Sequence<T> {
    return merge(Comparator(comparator), iterables.map { it.asIterable() }.asIterable())
}

fun <T> mergeSequences(comparator: Comparator<T>, iterables: Sequence<Sequence<T>>): Sequence<T> {
    return merge(comparator, iterables.map { it.asIterable() }.asIterable())
}

fun <T> mergeSequences(vararg iterables: Sequence<T>, comparator: (T, T) -> Int): Sequence<T> {
    return merge(Comparator(comparator), iterables.map { it.asIterable() })
}

fun <T> mergeSequences(comparator: Comparator<T>, vararg iterables: Sequence<T>): Sequence<T> {
    return merge(comparator, iterables.map { it.asIterable() })
}