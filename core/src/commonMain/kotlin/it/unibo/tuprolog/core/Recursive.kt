package it.unibo.tuprolog.core

import kotlin.collections.List
import kotlin.js.JsName

/**
 * Base type for [Struct]s that conventionally represent a (possibly improper) sequence of [Term]s folded
 * into nested binary structures: [List] (functor `.`), [Tuple] (functor `, `), and [Block] (functor `{}`).
 * [Recursive] exposes that sequence uniformly, regardless of which folding convention the concrete sub-type
 * uses, via [unfold]/[unfoldedSequence]/[toList]/[toArray].
 *
 * A [Recursive] term may be *lazy*: lists, tuples and blocks built out of a [Sequence] (or of an [Iterable] other
 * than a [List]) pull their items out of it only as they are traversed. Laziness survives wrapping (e.g. `[x | L]`
 * over a lazy `L`), substitution and fresh copies, while [size] and [isGround] are computed once, on first access.
 * When built out of children whose metadata is already known, instead, [size] and [isGround] are known upon
 * construction, without any traversal. Equality, comparison, hashing, substitution and fresh copies of
 * right-nested terms of any length use a bounded amount of native stack, and substitution reuses the longest
 * suffix it leaves unchanged.
 */
interface Recursive : Struct {
    override val isRecursive: Boolean
        get() = true

    override fun asRecursive(): Recursive = this

    /** The elements of this structure, unfolded lazily, in order. Same as [unfold]. */
    @JsName("unfoldedSequence")
    val unfoldedSequence: Sequence<Term>

    /** The elements of this structure, unfolded eagerly into a random-access [List], computed once. */
    @JsName("unfoldedList")
    val unfoldedList: List<Term>

    /** The elements of this structure, unfolded eagerly into an [Array]. */
    @JsName("unfoldedArray")
    val unfoldedArray: Array<Term>

    /** The number of elements in this structure, once unfolded: computed once, if not known upon construction. */
    @JsName("size")
    val size: Int

    /** Alias for [unfoldedSequence], exposed as an [Iterable]. */
    @JsName("items")
    val items: Iterable<Term>

    /** Eagerly unfolds this structure's elements into an [Array]. Same as [unfoldedArray]. */
    @JsName("toArray")
    fun toArray(): Array<Term>

    /** Eagerly unfolds this structure's elements into a [List]. Same as [unfoldedList]. */
    @JsName("toList")
    fun toList(): List<Term>

    /** Lazily unfolds this structure's elements into a [Sequence]. Same as [unfoldedSequence]. */
    fun toSequence(): Sequence<Term>

    /** Lazily unfolds this structure's elements. Same as [unfoldedSequence]. */
    @JsName("unfold")
    fun unfold(): Sequence<Term>

    override fun freshCopy(): Recursive

    override fun freshCopy(scope: Scope): Recursive
}
