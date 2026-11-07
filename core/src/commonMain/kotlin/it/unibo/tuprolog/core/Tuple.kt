package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.Terms.TUPLE_FUNCTOR
import it.unibo.tuprolog.core.impl.LazyTupleImpl
import it.unibo.tuprolog.core.impl.TupleImpl
import it.unibo.tuprolog.utils.cursor
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.collections.List as KtList

/**
 * A [Recursive] structure with functor `, ` and [arity] `2`, i.e. the standard Prolog conjunction `(A, B)`.
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
         * [Clause]'s body goals into a single [Term]. A [Sequence], or an [Iterable] other than a [KtList], is
         * consumed lazily: only as many items as needed to tell those cases apart are pulled upon wrapping.
         */
        @JvmStatic
        @JsName("wrapIfNeeded")
        @JvmOverloads
        fun wrapIfNeeded(
            vararg terms: Term,
            ifEmpty: () -> Term = { Truth.TRUE },
        ): Term = wrapIfNeeded(terms.toList(), ifEmpty)

        /** @see wrapIfNeeded */
        @JvmStatic
        @JsName("wrapIterableIfNeeded")
        @JvmOverloads
        fun wrapIfNeeded(
            terms: Iterable<Term>,
            ifEmpty: () -> Term = { Truth.TRUE },
        ): Term =
            when {
                terms !is KtList<Term> -> wrapIfNeeded(terms.asSequence(), ifEmpty)
                terms.isEmpty() -> ifEmpty()
                terms.size == 1 -> terms[0]
                else -> of(terms)
            }

        /** @see wrapIfNeeded */
        @JvmStatic
        @JsName("wrapSequenceIfNeeded")
        @JvmOverloads
        fun wrapIfNeeded(
            terms: Sequence<Term>,
            ifEmpty: () -> Term = { Truth.TRUE },
        ): Term {
            val cursor = terms.cursor()
            return when {
                cursor.isOver -> ifEmpty()
                cursor.next.isOver -> cursor.current!!
                else -> LazyTupleImpl(cursor)
            }
        }

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
         * Creates a right-nested [Tuple] out of [terms]: eagerly if [terms] is a [KtList], lazily (as [of] does
         * for a [Sequence]) otherwise.
         * @throws IllegalArgumentException if [terms] has fewer than 2 elements
         */
        @JvmStatic
        @JsName("ofIterable")
        fun of(terms: Iterable<Term>): Tuple =
            when (terms) {
                is KtList<Term> -> of(terms)
                else -> of(terms.asSequence())
            }

        /**
         * Creates a right-nested [Tuple] out of [terms], lazily: only the first 2 items are pulled out of [terms]
         * upon construction, while the others are pulled (once) as the tuple is traversed. Changes to the source
         * of [terms] after construction may hence be reflected by the tuple.
         * @throws IllegalArgumentException if [terms] has fewer than 2 elements
         */
        @JvmStatic
        @JsName("ofSequence")
        fun of(terms: Sequence<Term>): Tuple {
            val cursor = terms.cursor()
            require(!cursor.isOver && !cursor.next.isOver) {
                "Tuples require at least 2 terms"
            }
            return LazyTupleImpl(cursor)
        }

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
            val i = terms.listIterator(terms.size)
            var right: Term = i.previous()
            while (i.hasPrevious()) {
                right = TupleImpl(i.previous(), right)
            }
            return right.castToTuple()
        }
    }
}
