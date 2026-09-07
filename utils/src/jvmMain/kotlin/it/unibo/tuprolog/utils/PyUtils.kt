@file:JvmName("PyUtils")

package it.unibo.tuprolog.utils

import it.unibo.tuprolog.utils.impl.IterableWrapper
import it.unibo.tuprolog.utils.impl.IteratorWrapper
import it.unibo.tuprolog.utils.impl.SequenceWrapper
import kotlin.jvm.JvmName

// JVM-only helpers that expose a plain Kotlin Iterator/Iterable/Sequence as their Kotlin
// MutableIterator/MutableIterable counterparts, for interop with JVM host APIs (e.g. embedding this
// library from Java, or from a JVM-hosted scripting language such as Jython/GraalPy, from which the name
// "PyUtils" originates) that specifically require a java.util.Iterator/java.lang.Iterable reference and
// are unaware of Kotlin's read-only collection interfaces.
//
// None of these actually support removal unless the wrapped source itself does: `iterator` delegates
// MutableIterator.remove to the wrapped Iterator if it happens to already be a MutableIterator, or throws
// UnsupportedOperationException otherwise; Sequence-backed iterables (via the `iterable` overload taking a
// Sequence) never support removal, since a fresh, non-mutable Iterator is produced by Sequence.iterator
// every time.

/**
 * Wraps [iterator] as a [MutableIterator], usable from JVM code expecting one.
 * @throws UnsupportedOperationException from [MutableIterator.remove], if [iterator] is not itself mutable
 */
fun <T> iterator(iterator: Iterator<T>): MutableIterator<T> = IteratorWrapper(iterator)

/**
 * Wraps [iterable] as a [MutableIterable], usable from JVM code expecting one; each call to
 * [MutableIterable.iterator] wraps a fresh `iterable.iterator()` via [iterator].
 * @throws UnsupportedOperationException from a returned iterator's [MutableIterator.remove], if [iterable]'s
 * own iterators are not themselves mutable
 */
fun <T> iterable(iterable: Iterable<T>): MutableIterable<T> = IterableWrapper(iterable)

/**
 * Wraps [sequence] as a [MutableIterable], usable from JVM code expecting one; each call to
 * [MutableIterable.iterator] wraps a fresh `sequence.iterator()`.
 * @throws UnsupportedOperationException from a returned iterator's [MutableIterator.remove], since a
 * [Sequence]'s iterator never supports removal
 */
fun <T> iterable(sequence: Sequence<T>): MutableIterable<T> = SequenceWrapper(sequence)
