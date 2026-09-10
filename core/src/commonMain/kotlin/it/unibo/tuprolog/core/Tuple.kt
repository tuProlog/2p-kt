package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.Terms.TUPLE_FUNCTOR
import it.unibo.tuprolog.core.impl.TupleImpl
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.collections.List as KtList

/**
 * A [Recursive] structure with functor `, ` and [arity] `2`, i.e. the classic Prolog conjunction `(A, B)`.
 * [Tuple]s of more than two terms are represented by right-nesting: `(A, B, C)` is `[left]=A`,
 * `[right]=(B, C)`. This is the same functor Prolog uses for clause bodies with multiple goals — that is why
 * [Clause.body] and [Rule.of] fold multi-goal bodies into a [Tuple] under the hood.
 *
 * Since a two-argument [Struct] with functor `, ` is structurally identical to a [Tuple], [Struct.of] and
 * [wrapIfNeeded] automatically return a [Tuple] whenever that shape arises; there is normally no need to
 * distinguish a plain `, `/2 [Struct] from a [Tuple] by hand.
 */
interface Tuple : Recursive {
    override val isTuple: Boolean
        get() = true

    override val functor: String
        get() = TUPLE_FUNCTOR

    override val arity: Int
        get() = 2

    /** The first (leftmost) element of this [Tuple]. */
    @JsName("left")
    val left: Term

    /** The rest of this [Tuple]: either the last element, or a nested [Tuple] holding the remaining elements. */
    @JsName("right")
    val right: Term

    override fun asTuple(): Tuple = this

    override fun toArray(): Array<Term> = unfoldedArray

    override fun toList(): KtList<Term> = unfoldedList

    override fun toSequence(): Sequence<Term> = unfoldedSequence

    override fun freshCopy(): Tuple

    override fun freshCopy(scope: Scope): Tuple

    companion object {
        /** The canonical tuple functor: `, ` */
        const val FUNCTOR = TUPLE_FUNCTOR

        /**
         * Wraps [terms] into a single [Term]: returns [ifEmpty]'s result if [terms] is empty, the sole
         * element if [terms] has exactly one, or a [Tuple] otherwise. Used, in particular, to fold a
         * [Clause]'s body goals into a single [Term].
         */
        @JvmStatic
        @JsName("wrapIfNeeded")
        @JvmOverloads
        fun wrapIfNeeded(
            vararg terms: Term,
            ifEmpty: () -> Term = { Truth.TRUE },
        ): Term = wrapIfNeeded(terms.asIterable(), ifEmpty)

        /** @see wrapIfNeeded */
        @JvmStatic
        @JsName("wrapIterableIfNeeded")
        @JvmOverloads
        fun wrapIfNeeded(
            terms: Iterable<Term>,
            ifEmpty: () -> Term = { Truth.TRUE },
        ): Term {
            val i = terms.iterator()
            if (!i.hasNext()) return ifEmpty()
            val first = i.next()
            if (!i.hasNext()) return first
            val items = mutableListOf(first)
            while (i.hasNext()) {
                items.add(i.next())
            }
            return of(items)
        }

        /** @see wrapIfNeeded */
        @JvmStatic
        @JsName("wrapSequenceIfNeeded")
        @JvmOverloads
        fun wrapIfNeeded(
            terms: Sequence<Term>,
            ifEmpty: () -> Term = { Truth.TRUE },
        ): Term = wrapIfNeeded(terms.asIterable(), ifEmpty)

        /** Creates a [Tuple] with [left] and [right] as its two direct arguments. */
        @JvmStatic
        @JsName("of")
        fun of(
            left: Term,
            right: Term,
        ): Tuple = TupleImpl(left, right)

        /** Creates a right-nested [Tuple] out of [first], [second], and [others]. */
        @JvmStatic
        @JsName("ofMany")
        fun of(
            first: Term,
            second: Term,
            vararg others: Term,
        ): Tuple = of(listOf(first, second, *others))

        /**
         * Creates a right-nested [Tuple] out of [terms].
         * @throws IllegalArgumentException if [terms] has fewer than 2 elements
         */
        @JvmStatic
        @JsName("ofIterable")
        fun of(terms: Iterable<Term>): Tuple = of(terms.toList())

        /**
         * Creates a right-nested [Tuple] out of [terms].
         * @throws IllegalArgumentException if [terms] has fewer than 2 elements
         */
        @JvmStatic
        @JsName("ofList")
        fun of(terms: KtList<Term>): Tuple {
            require(terms.size >= 2) {
                "Tuples require at least 2 terms"
            }
            return terms
                .slice(0 until terms.lastIndex)
                .foldRight(terms.last()) { l, r ->
                    TupleImpl(l, r)
                }.castToTuple()
        }
    }
}
