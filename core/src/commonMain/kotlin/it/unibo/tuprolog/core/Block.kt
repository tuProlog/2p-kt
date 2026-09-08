package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.BlockImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import kotlin.collections.List as KtList

/**
 * A [Recursive] structure with functor `{}`, representing Prolog's curly-braced term (`{Goal}`), most
 * commonly seen in the body of DCG (Definite Clause Grammar) rules. A [Block] wrapping zero terms is an
 * [EmptyBlock] (the atom `{}`); one wrapping a single term stores it directly; two or more are folded, right
 * to left, using [Tuple] as the inner structure — the same folding convention used by [List] and [Tuple]
 * themselves, so [Recursive] operations like [unfold]/[toList] work identically across all three.
 */
interface Block : Recursive {
    override val isBlock: Boolean
        get() = true

    override val isEmptyBlock: Boolean
        get() = arity == 0

    override val functor: String
        get() = FUNCTOR

    override fun toArray(): Array<Term> = unfoldedArray

    override fun toList(): KtList<Term> = unfoldedList

    override fun toSequence(): Sequence<Term> = unfoldedSequence

    override fun freshCopy(): Block

    override fun freshCopy(scope: Scope): Block

    override fun asBlock(): Block = this

    companion object {
        /** The canonical block functor: `{}` */
        const val FUNCTOR = Terms.BLOCK_FUNCTOR

        /** The functor of an [EmptyBlock], coincidentally equal to [FUNCTOR] itself (i.e. the atom `{}`). */
        const val EMPTY_FUNCTOR = Terms.EMPTY_BLOCK_FUNCTOR

        /** Creates a new, empty [Block], i.e. the `{}` atom. */
        @JvmStatic
        @JsName("empty")
        fun empty(): EmptyBlock = EmptyBlock()

        /** Creates a [Block] wrapping the given [terms]. */
        @JvmStatic
        @JsName("of")
        fun of(vararg terms: Term): Block = of(terms.toList())

        /** Creates a [Block] wrapping the given [terms]. */
        @JvmStatic
        @JsName("ofList")
        fun of(terms: KtList<Term>): Block =
            when {
                terms.isEmpty() -> empty()
                terms.size == 1 -> BlockImpl(terms.single())
                else -> BlockImpl(Tuple.of(terms))
            }

        /** Creates a [Block] wrapping the given [terms]. */
        @JvmStatic
        @JsName("ofIterable")
        fun of(terms: Iterable<Term>): Block = of(terms.toList())

        /** Creates a [Block] wrapping the given [terms]. */
        @JvmStatic
        @JsName("ofSequence")
        fun of(terms: Sequence<Term>): Block = of(terms.toList())
    }
}
