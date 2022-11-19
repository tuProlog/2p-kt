@file:JvmName("IterUtils")

package it.unibo.tuprolog.utils

import kotlin.jvm.JvmName

fun <T> merge(comparator: Comparator<T>, iterables: Iterable<Iterable<T>>): Sequence<T> {
    return sequence {
        val pipeline = iterables.asSequence().map { it.cursor() }.filterNot { it.isOver }.toMutableList()
        while (pipeline.isNotEmpty()) {
            val (minIndex, minValue) = pipeline.asSequence().map { it.current!! }.indexed().minWithOrNull(
                Comparator { a, b -> comparator.compare(a.value, b.value) }
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

fun <T> merge(iterables: Iterable<Iterable<T>>, comparator: (T, T) -> Int): Sequence<T> {
    return merge(Comparator(comparator), iterables)
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

fun <T> Sequence<T>.longIndexed(): Sequence<LongIndexed<T>> =
    zip(LongRange(0, Long.MAX_VALUE).asSequence()) { it, i ->
        LongIndexed.of(i, it)
    }

fun <T> Sequence<T>.indexed(): Sequence<IntIndexed<T>> =
    zip(IntRange(0, Int.MAX_VALUE).asSequence()) { it, i ->
        IntIndexed.of(i, it)
    }

fun <T> interleave(iterables: Iterable<Iterable<T>>): Sequence<T> =
    sequence {
        val pipeline = iterables.asSequence()
            .map { it.iterator() }
            .filter { it.hasNext() }
            .toList()
        var nNonEmpty = pipeline.size
        while (nNonEmpty > 0) {
            nNonEmpty = 0
            for (iter in pipeline) {
                if (iter.hasNext()) {
                    nNonEmpty++
                    yield(iter.next())
                }
            }
        }
    }

fun <T> interleave(vararg iterables: Iterable<T>): Sequence<T> =
    interleave(iterables.asIterable())

fun <T> interleave(iterables: Sequence<Iterable<T>>): Sequence<T> =
    interleave(iterables.asIterable())

fun <T> interleaveSequences(vararg iterables: Sequence<T>): Sequence<T> =
    interleave(sequenceOf(*iterables).map { it.asIterable() }.asIterable())

fun <T> interleaveSequences(iterables: Sequence<Sequence<T>>): Sequence<T> =
    interleave(iterables.map { it.asIterable() }.asIterable())

fun <T> interleaveSequences(iterables: Iterable<Sequence<T>>): Sequence<T> =
    interleave(iterables.map { it.asIterable() })

fun <T> Sequence<T>.subsequences(): Sequence<Sequence<T>> {
    return sequence {
        var maxSize = 1
        var actualSize = 0
        while (true) {
            val sublist = this@subsequences.take(maxSize).toList()
            yield(sublist.asSequence())
            if (actualSize >= sublist.size) {
                break
            }
            maxSize++
            actualSize = sublist.size
        }
    }
}

fun <T> itemWiseEquals(iterable1: Iterable<T>, iterable2: Iterable<T>, comparator: (T, T) -> Boolean): Boolean {
    val i = iterable1.iterator()
    val j = iterable2.iterator()
    while (i.hasNext() && j.hasNext()) {
        val a = i.next()
        val b = j.next()
        if (!comparator(a, b)) {
            return false
        }
    }
    return i.hasNext() == j.hasNext()
}

fun <T> itemWiseEquals(iterable1: Iterable<T>, iterable2: Iterable<T>): Boolean =
    itemWiseEquals(iterable1, iterable2) { a, b ->
        a == b
    }

fun <T> itemWiseEquals(sequence1: Sequence<T>, sequence2: Sequence<T>, comparator: (T, T) -> Boolean): Boolean {
    return itemWiseEquals(sequence1.asIterable(), sequence2.asIterable(), comparator)
}

fun <T> itemWiseEquals(sequence1: Sequence<T>, sequence2: Sequence<T>): Boolean {
    return itemWiseEquals(sequence1.asIterable(), sequence2.asIterable())
}

fun <T> itemWiseHashCode(vararg items: T): Int {
    return itemWiseHashCode(items.asIterable())
}

fun <T> itemWiseHashCode(iterable: Iterable<T>): Int {
    var hash = 13
    val i = iterable.iterator()
    while (i.hasNext()) {
        hash = 31 * hash + (i.next()?.hashCode() ?: 0)
    }
    return hash
}

fun <T> itemWiseHashCode(sequence: Sequence<T>): Int {
    return itemWiseHashCode(sequence.asIterable())
}

fun <T> Iterable<T>.subsequences(): Sequence<Sequence<T>> {
    return asSequence().subsequences()
}

fun <T> subsequences(vararg items: T): Sequence<Sequence<T>> {
    return sequenceOf(*items).subsequences()
}

fun <T> Sequence<T>.buffered(): Sequence<T> {
    return this.toList().asSequence()
}

fun <T> Sequence<T>.cached(): Sequence<T> {
    return this.cursor().asSequence()
}

fun <T> Sequence<T>.skipIndex(index: Int): Sequence<T> {
    require(index >= 0)
    return sequence {
        var i = 0
        val iter = iterator()
        while (i < index && iter.hasNext()) {
            yield(iter.next())
            i++
        }
        if (iter.hasNext()) iter.next()
        while (iter.hasNext()) {
            yield(iter.next())
        }
    }
}

fun <T> permutations(vararg items: T): Sequence<List<T>> =
    items.toList().permutations()

fun <T> Iterable<T>.permutations(): Sequence<List<T>> =
    toList().permutations()

fun <T> Sequence<T>.permutations(): Sequence<List<T>> =
    toList().permutations()

fun <T> List<T>.permutations(): Sequence<List<T>> =
    when (size) {
        0, 1 -> sequenceOf(this)
        2 -> sequenceOf(this, asReversed())
        else -> {
            asSequence().indexed().flatMap { (i, head) ->
                this@permutations.asSequence()
                    .skipIndex(i)
                    .toList()
                    .permutations()
                    .map { listOf(head) + it }
            }
        }
    }

fun <T> Sequence<T>.insertAt(index: Int, item: T, vararg items: T): Sequence<T> = sequence {
    for ((i, x) in withIndex().asIterable()) {
        if (i == index) {
            yield(item)
            yieldAll(items.asIterable())
        }
        yield(x)
    }
}

fun <T> Sequence<T>.dropLast(): Sequence<T> = sequence {
    val i = iterator()
    while (i.hasNext()) {
        val current = i.next()
        if (i.hasNext()) {
            yield(current)
        }
    }
}
