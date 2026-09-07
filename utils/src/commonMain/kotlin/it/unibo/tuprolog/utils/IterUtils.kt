@file:JvmName("IterUtils")

package it.unibo.tuprolog.utils

import kotlin.jvm.JvmName

/**
 * Performs a k-way merge of the given [iterables], each of which is assumed to already be sorted according
 * to [comparator], producing a single [Sequence] that yields all their elements in the order induced by
 * [comparator] (like the "merge" step of a merge-sort, generalized to more than two inputs).
 *
 * This is lazy (elements are pulled from the inputs only as the resulting sequence is consumed) and cheaper
 * than concatenating the [iterables] and sorting the result, since it exploits the fact that each input is
 * already ordered. It is used, for instance, by the Rete-based clause indexes in the `:theory` module to
 * merge per-index-bucket clause sequences (each already sorted by insertion order) back into a single
 * globally-ordered sequence:
 * ```kotlin
 * fun merge(iterable: Iterable<Sequence<SituatedIndexedClause>>): Sequence<SituatedIndexedClause> =
 *     mergeSequences(iterable) { c1, c2 -> ... }
 * ```
 * If any element supplied by [iterables] is not consistent with [comparator]'s ordering (i.e. the inputs are
 * not actually sorted), the result is unspecified.
 */
fun <T> merge(
    comparator: Comparator<T>,
    iterables: Iterable<Iterable<T>>,
): Sequence<T> =
    sequence {
        val pipeline =
            iterables
                .asSequence()
                .map { it.cursor() }
                .filterNot { it.isOver }
                .toMutableList()
        while (pipeline.isNotEmpty()) {
            val (minIndex, minValue) =
                pipeline.asSequence().map { it.current!! }.indexed().minWithOrNull(
                    Comparator { a, b -> comparator.compare(a.value, b.value) },
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

/**
 * Same as [merge], with [comparator] expressed as a plain comparison lambda instead of a [Comparator].
 */
fun <T> merge(
    iterables: Iterable<Iterable<T>>,
    comparator: (T, T) -> Int,
): Sequence<T> = merge(Comparator(comparator), iterables)

/**
 * Same as [merge], taking the [iterables] to merge as varargs instead of an [Iterable].
 */
fun <T> merge(
    vararg iterables: Iterable<T>,
    comparator: (T, T) -> Int,
): Sequence<T> = merge(Comparator(comparator), *iterables)

/**
 * Same as [merge], taking the [iterables] to merge as varargs instead of an [Iterable].
 */
fun <T> merge(
    comparator: Comparator<T>,
    vararg iterables: Iterable<T>,
): Sequence<T> = merge(comparator, listOf(*iterables))

/**
 * Same as [merge], taking the [iterables] to merge as a [Sequence] of [Iterable]s.
 */
fun <T> merge(
    iterables: Sequence<Iterable<T>>,
    comparator: (T, T) -> Int,
): Sequence<T> = merge(Comparator(comparator), iterables)

/**
 * Same as [merge], taking the [iterables] to merge as a [Sequence] of [Iterable]s.
 */
fun <T> merge(
    comparator: Comparator<T>,
    iterables: Sequence<Iterable<T>>,
): Sequence<T> = merge(comparator, iterables.asIterable())

/**
 * Same as [merge], but each of the [iterables] to merge is itself a [Sequence] rather than an [Iterable].
 */
fun <T> mergeSequences(
    iterables: Iterable<Sequence<T>>,
    comparator: (T, T) -> Int,
): Sequence<T> = merge(Comparator(comparator), iterables.map { it.asIterable() })

/**
 * Same as [merge], but each of the [iterables] to merge is itself a [Sequence] rather than an [Iterable].
 */
fun <T> mergeSequences(
    comparator: Comparator<T>,
    iterables: Iterable<Sequence<T>>,
): Sequence<T> = merge(comparator, iterables.map { it.asIterable() })

/**
 * Same as [merge], but both the outer collection and each of the [iterables] to merge are [Sequence]s.
 */
fun <T> mergeSequences(
    iterables: Sequence<Sequence<T>>,
    comparator: (T, T) -> Int,
): Sequence<T> = merge(Comparator(comparator), iterables.map { it.asIterable() }.asIterable())

/**
 * Same as [merge], but both the outer collection and each of the [iterables] to merge are [Sequence]s.
 */
fun <T> mergeSequences(
    comparator: Comparator<T>,
    iterables: Sequence<Sequence<T>>,
): Sequence<T> = merge(comparator, iterables.map { it.asIterable() }.asIterable())

/**
 * Same as [merge], taking the [Sequence]s to merge as varargs.
 */
fun <T> mergeSequences(
    vararg iterables: Sequence<T>,
    comparator: (T, T) -> Int,
): Sequence<T> = merge(Comparator(comparator), iterables.map { it.asIterable() })

/**
 * Same as [merge], taking the [Sequence]s to merge as varargs.
 */
fun <T> mergeSequences(
    comparator: Comparator<T>,
    vararg iterables: Sequence<T>,
): Sequence<T> = merge(comparator, iterables.map { it.asIterable() })

/**
 * Returns the lazy Cartesian product of this sequence and [other], combining each pair of elements via
 * [combinator]. Equivalent to a nested `for (x in this) for (y in other) yield(combinator(x, y))`, except
 * lazy: the result has `this.count() * other.count()` elements, but [other] must be safely re-iterable
 * (e.g. backed by a [List], not a one-shot generator), since it is traversed once per element of this
 * sequence.
 */
fun <T, U, R> Sequence<T>.product(
    other: Sequence<U>,
    combinator: (T, U) -> R,
): Sequence<R> =
    flatMap { x ->
        other.map { y -> combinator(x, y) }
    }

/**
 * Same as [product], pairing up elements into [Pair]s instead of combining them via a custom function.
 */
fun <T, U> Sequence<T>.product(other: Sequence<U>): Sequence<Pair<T, U>> = product(other, ::Pair)

/**
 * Returns the lazy Cartesian product of this sequence with itself (see [product]), combining each pair of
 * elements (including a value with itself) via [combinator].
 */
fun <T, R> Sequence<T>.squared(combinator: (T, T) -> R): Sequence<R> = product(this, combinator)

/**
 * Same as [squared], pairing up elements into [Pair]s instead of combining them via a custom function.
 */
fun <T> Sequence<T>.squared(): Sequence<Pair<T, T>> = product(this)

/**
 * Pairs each element of this sequence with its 0-based position, as a [LongIndexed] value, lazily. Use this
 * rather than [indexed] when the sequence may contain more than [Int.MAX_VALUE] elements.
 */
fun <T> Sequence<T>.longIndexed(): Sequence<LongIndexed<T>> =
    zip(LongRange(0, Long.MAX_VALUE).asSequence()) { it, i ->
        LongIndexed.of(i, it)
    }

/**
 * Pairs each element of this sequence with its 0-based position, as an [IntIndexed] value, lazily.
 * ```kotlin
 * for ((index, value) in sequence.indexed()) { ... }
 * ```
 */
fun <T> Sequence<T>.indexed(): Sequence<IntIndexed<T>> =
    zip(IntRange(0, Int.MAX_VALUE).asSequence()) { it, i ->
        IntIndexed.of(i, it)
    }

/**
 * Lazily interleaves the elements of the given [iterables], round-robin style: the first element of every
 * iterable, then the second element of every (still non-exhausted) iterable, and so on, until all of them
 * are exhausted. Unlike [merge], this does not require the inputs to be sorted, and does not reorder
 * elements based on their value, only on their position within their originating iterable.
 */
fun <T> interleave(iterables: Iterable<Iterable<T>>): Sequence<T> =
    sequence {
        val pipeline =
            iterables
                .asSequence()
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

/**
 * Same as [interleave], taking the iterables to interleave as varargs.
 */
fun <T> interleave(vararg iterables: Iterable<T>): Sequence<T> = interleave(iterables.asIterable())

/**
 * Same as [interleave], taking the outer collection of iterables as a [Sequence].
 */
fun <T> interleave(iterables: Sequence<Iterable<T>>): Sequence<T> = interleave(iterables.asIterable())

/**
 * Same as [interleave], taking the [Sequence]s to interleave as varargs.
 */
fun <T> interleaveSequences(vararg iterables: Sequence<T>): Sequence<T> =
    interleave(sequenceOf(*iterables).map { it.asIterable() }.asIterable())

/**
 * Same as [interleave], where both the outer collection and each of the interleaved elements are [Sequence]s.
 */
fun <T> interleaveSequences(iterables: Sequence<Sequence<T>>): Sequence<T> =
    interleave(
        iterables
            .map {
                it.asIterable()
            }.asIterable(),
    )

/**
 * Same as [interleave], where each of the interleaved elements is a [Sequence] rather than an [Iterable].
 */
fun <T> interleaveSequences(iterables: Iterable<Sequence<T>>): Sequence<T> =
    interleave(
        iterables.map {
            it.asIterable()
        },
    )

/**
 * Lazily generates every prefix (i.e. leading subsequence) of this sequence, from the singleton sequence
 * containing only the first element up to the full sequence, one element longer than the previous at each
 * step; stops as soon as a prefix turns out shorter than requested (i.e. once this sequence is exhausted).
 * Note that, despite the name, this yields growing *prefixes*, not every possible (non-contiguous)
 * subsequence.
 */
fun <T> Sequence<T>.subsequences(): Sequence<Sequence<T>> =
    sequence {
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

/**
 * Compares [iterable1] and [iterable2] element-by-element, in iteration order, via [comparator], returning
 * `true` if and only if both have the same number of elements and every corresponding pair of elements
 * satisfies [comparator]. Useful to compare two iterables for equality without materializing them into
 * [List]s first, or using a notion of "equality" other than [Any.equals] (e.g. structural equality of terms
 * that ignores variable naming).
 */
fun <T> itemWiseEquals(
    iterable1: Iterable<T>,
    iterable2: Iterable<T>,
    comparator: (T, T) -> Boolean,
): Boolean {
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

/**
 * Same as [itemWiseEquals], using [Any.equals] (`==`) as the element comparator.
 */
fun <T> itemWiseEquals(
    iterable1: Iterable<T>,
    iterable2: Iterable<T>,
): Boolean =
    itemWiseEquals(iterable1, iterable2) { a, b ->
        a == b
    }

/**
 * Same as [itemWiseEquals], for two [Sequence]s rather than [Iterable]s.
 */
fun <T> itemWiseEquals(
    sequence1: Sequence<T>,
    sequence2: Sequence<T>,
    comparator: (T, T) -> Boolean,
): Boolean = itemWiseEquals(sequence1.asIterable(), sequence2.asIterable(), comparator)

/**
 * Same as [itemWiseEquals], for two [Sequence]s rather than [Iterable]s, using [Any.equals] (`==`) as the
 * element comparator.
 */
fun <T> itemWiseEquals(
    sequence1: Sequence<T>,
    sequence2: Sequence<T>,
): Boolean = itemWiseEquals(sequence1.asIterable(), sequence2.asIterable())

/**
 * Computes a hash code out of the given [items], consistent with [itemWiseEquals]: two iterables that are
 * [itemWiseEquals] (under `==`) always produce the same [itemWiseHashCode], much like [List.hashCode] does
 * for lists, but usable on any [Iterable]/[Sequence]. Handy when implementing [Any.hashCode] for a custom
 * collection-like type that should be compared item-wise (e.g. structurally-shared term lists) rather than
 * by reference.
 */
fun <T> itemWiseHashCode(vararg items: T): Int = itemWiseHashCode(items.asIterable())

/**
 * Same as [itemWiseHashCode], taking the items as an [Iterable].
 */
fun <T> itemWiseHashCode(iterable: Iterable<T>): Int {
    var hash = 13
    val i = iterable.iterator()
    while (i.hasNext()) {
        hash = 31 * hash + (i.next()?.hashCode() ?: 0)
    }
    return hash
}

/**
 * Same as [itemWiseHashCode], taking the items as a [Sequence].
 */
fun <T> itemWiseHashCode(sequence: Sequence<T>): Int = itemWiseHashCode(sequence.asIterable())

/**
 * Same as [Sequence.subsequences], but starting from an [Iterable].
 */
fun <T> Iterable<T>.subsequences(): Sequence<Sequence<T>> = asSequence().subsequences()

/**
 * Same as [Sequence.subsequences], taking the [items] to generate prefixes of as varargs.
 */
fun <T> subsequences(vararg items: T): Sequence<Sequence<T>> = sequenceOf(*items).subsequences()

/**
 * Eagerly materializes this sequence into a [List] and returns it as a (now safely re-iterable) [Sequence].
 * Use this to consume a one-shot/expensive-to-recompute sequence more than once without recomputing it
 * every time, at the cost of holding all of its elements in memory at once; see also [cached] for a lazier
 * alternative that memoizes elements only as they are first traversed.
 */
fun <T> Sequence<T>.buffered(): Sequence<T> = this.toList().asSequence()

/**
 * Returns a [Sequence] view that lazily memoizes the elements of this sequence as they are first traversed
 * (via [Cursor.asSequence]), so that traversing the result more than once does not recompute already-seen
 * elements. Unlike [buffered], this does not eagerly consume the whole sequence upfront.
 */
fun <T> Sequence<T>.cached(): Sequence<T> = this.cursor().asSequence()

/**
 * Returns a new sequence containing all the elements of this sequence except the one at the given [index]
 * (0-based). If [index] is beyond the end of this sequence, the returned sequence is equal to this one.
 * @throws IllegalArgumentException if [index] is negative
 */
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

/**
 * Lazily generates every permutation of the given [items], as [List]s, in an unspecified order. The number
 * of permutations generated is `items.size` factorial, so this quickly becomes expensive for anything but
 * small inputs.
 */
fun <T> permutations(vararg items: T): Sequence<List<T>> = items.toList().permutations()

/**
 * Same as [List.permutations], starting from an [Iterable] rather than a [List].
 */
fun <T> Iterable<T>.permutations(): Sequence<List<T>> = toList().permutations()

/**
 * Same as [List.permutations], starting from a [Sequence] rather than a [List].
 */
fun <T> Sequence<T>.permutations(): Sequence<List<T>> = toList().permutations()

/**
 * Lazily generates every permutation of the elements of this list, as [List]s, in an unspecified order,
 * via the standard recursive "pick each element as head, permute the rest" algorithm. The number of
 * permutations generated is `size` factorial (e.g. 720 for a 6-element list), so this quickly becomes
 * expensive for anything but small inputs.
 */
fun <T> List<T>.permutations(): Sequence<List<T>> =
    when (size) {
        0, 1 -> sequenceOf(this)
        2 -> sequenceOf(this, asReversed())
        else -> {
            asSequence().indexed().flatMap { (i, head) ->
                this@permutations
                    .asSequence()
                    .skipIndex(i)
                    .toList()
                    .permutations()
                    .map { listOf(head) + it }
            }
        }
    }

/**
 * Returns a new sequence equal to this one, except that [item] (followed by any [items]) is inserted right
 * before the element that was originally at position [index] (0-based); if [index] is (at or) beyond the
 * end of this sequence, [item] and [items] are appended at the end.
 */
fun <T> Sequence<T>.insertAt(
    index: Int,
    item: T,
    vararg items: T,
): Sequence<T> =
    sequence {
        for ((i, x) in withIndex().asIterable()) {
            if (i == index) {
                yield(item)
                yieldAll(items.asIterable())
            }
            yield(x)
        }
    }

/**
 * Returns a new sequence containing all the elements of this sequence except the last one. Returns an
 * empty sequence if this sequence has zero or one elements.
 */
fun <T> Sequence<T>.dropLast(): Sequence<T> =
    sequence {
        val i = iterator()
        while (i.hasNext()) {
            val current = i.next()
            if (i.hasNext()) {
                yield(current)
            }
        }
    }

/**
 * Asserts that none of the elements of this array is `null`, returning it as an [Array] of the non-nullable
 * element type [T] (which, for a reference-typed array, is the very same array instance, unsafely cast).
 * @throws IllegalArgumentException if any element of this array is `null`, naming its index in the message
 */
fun <T> Array<T?>.assertItemsAreNotNull(): Array<T> {
    for ((index, value) in this.withIndex()) {
        if (value == null) {
            throw IllegalArgumentException("Item at index $index is null")
        }
    }
    @Suppress("UNCHECKED_CAST")
    return this as Array<T>
}

/**
 * Asserts that none of the elements of this list is `null`, returning it as a [List] of the non-nullable
 * element type [T].
 * @throws IllegalArgumentException if any element of this list is `null`, naming its index in the message
 */
fun <T> List<T?>.assertItemsAreNotNull(): List<T> {
    for ((index, value) in this.withIndex()) {
        if (value == null) {
            throw IllegalArgumentException("Item at index $index is null")
        }
    }
    @Suppress("UNCHECKED_CAST")
    return this as List<T>
}

/**
 * Lazily asserts that none of the elements of this sequence is `null`, yielding a [Sequence] of the
 * non-nullable element type [T]; each element is checked only as it is traversed.
 * @throws IllegalArgumentException when a `null` element is reached, naming its index in the message
 */
fun <T> Sequence<T?>.assertItemsAreNotNull(): Sequence<T> =
    indexed().map {
        when (val value = it.value) {
            null -> throw IllegalArgumentException("Item at index ${it.index} is null")
            else -> value
        }
    }

/**
 * Same as [Sequence.assertItemsAreNotNull], starting from an [Iterable] rather than a [Sequence].
 * @throws IllegalArgumentException when a `null` element is reached, naming its index in the message
 */
fun <T> Iterable<T?>.assertItemsAreNotNull(): Iterable<T> = asSequence().assertItemsAreNotNull().asIterable()
