package it.unibo.tuprolog.utils

import it.unibo.tuprolog.utils.impl.EmptyCursor
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * An immutable, persistent, singly-linked view over a (possibly lazily produced) sequence of elements of
 * type [T], akin to a Lisp-style `(head . tail)` cons cell.
 *
 * A [Cursor] differs from Kotlin's [Iterator] in two important ways: it is immutable (advancing to [next]
 * yields a *new* [Cursor], leaving the original one untouched and still usable), and it can be shared/reused
 * from multiple call sites without any of them interfering with the others' traversal position. This makes
 * it a good building block for lazily-constructed, structurally-shared data (e.g. Prolog lists, see
 * `it.unibo.tuprolog.core.List.from(Cursor, Term?)` in the `:core` module, which wraps a [Cursor] of `Term`s
 * without eagerly materializing them), where a plain [Iterator] would be unsuitable because it can only be
 * consumed once and does not support branching.
 *
 * Use [Cursor.of] (or the [toCursor]/[cursor] extension functions) to obtain a [Cursor] out of an
 * [Iterator], [Iterable], [Sequence], [Array] or [Collection]; use [Cursor.empty] to get the canonical empty
 * cursor. A finished cursor (i.e. one for which [isOver] is `true`) has no [current] element and calling
 * [next] on it throws.
 * ```kotlin
 * var cursor: Cursor<out T> = listOf(1, 2, 3).cursor()
 * while (!cursor.isOver) {
 *     println(cursor.current)
 *     cursor = cursor.next
 * }
 * ```
 * @param T is the type of the elements traversed by this cursor
 */
interface Cursor<T> {
    /**
     * The cursor pointing to the element following [current], or, if this cursor [isOver], a cursor that is
     * also [isOver].
     * @throws NoSuchElementException if this cursor is already [isOver] (there is no "next" of "over")
     */
    @JsName("next")
    val next: Cursor<out T>

    /**
     * The element this cursor currently points at, or `null` if this cursor [isOver].
     */
    @JsName("current")
    val current: T?

    /**
     * Whether this cursor still has, at least, one more element to yield after [current] (i.e. whether
     * [next] is not [isOver]). Always `false` once the cursor [isOver].
     */
    @JsName("hasNext")
    val hasNext: Boolean

    /**
     * Whether this cursor has been fully traversed, i.e. it has no [current] element left to yield.
     */
    @JsName("isOver")
    val isOver: Boolean

    /**
     * Whether this cursor lazily pulls elements from an underlying [Iterator] (as, e.g., the ones created via
     * [Cursor.of] or [toCursor]) rather than exposing an already fully-computed chain.
     */
    @JsName("isLazy")
    val isLazy: Boolean

    /**
     * Lazily transforms every element yielded by this cursor via [mapper], returning a new cursor of the
     * mapped elements. The returned cursor mirrors the laziness of the original one: [mapper] is invoked
     * on each element only when it is actually traversed.
     */
    @JsName("map")
    fun <R> map(mapper: (T) -> R): Cursor<out R>

    /**
     * Returns a (one-shot) [Iterator] traversing the elements of this cursor, from [current] onwards.
     */
    @JsName("iterator")
    fun iterator(): Iterator<T>

    /**
     * Returns an [Iterable] view of this cursor; each call to [Iterable.iterator] yields a fresh traversal
     * starting again from [current].
     */
    @JsName("asIterable")
    fun asIterable(): Iterable<T> = Iterable { iterator() }

    /**
     * Returns a [Sequence] view of this cursor, equivalent to `asIterable().asSequence()`.
     */
    @JsName("asSequence")
    fun asSequence(): Sequence<T> = Sequence { iterator() }

    companion object {
        /**
         * Wraps [iterator] into a lazy [Cursor], pulling elements from it as the cursor is traversed.
         * @see toCursor
         */
        @JvmStatic
        @JsName("of")
        fun <T> of(iterator: Iterator<T>): Cursor<out T> = iterator.toCursor()

        /**
         * Wraps [sequence] into a lazy [Cursor], equivalent to `sequence.cursor()`.
         * @see cursor
         */
        @JvmStatic
        @JsName("ofSequence")
        fun <T> of(sequence: Sequence<T>): Cursor<out T> = sequence.cursor()

        /**
         * Returns the canonical, already-[isOver] empty cursor, i.e. one with no elements to yield.
         */
        @JvmStatic
        @JsName("empty")
        fun <T> empty(): Cursor<out T> = EmptyCursor
    }
}
