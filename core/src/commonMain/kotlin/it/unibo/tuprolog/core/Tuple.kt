package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.Terms.TUPLE_FUNCTOR
import it.unibo.tuprolog.core.impl.TupleImpl
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.collections.List as KtList

interface Tuple : Recursive {
    override val isTuple: Boolean
        get() = true

    override val functor: String
        get() = TUPLE_FUNCTOR

    override val arity: Int
        get() = 2

    @JsName("left")
    val left: Term

    @JsName("right")
    val right: Term

    override fun asTuple(): Tuple = this

    override fun toArray(): Array<Term> = unfoldedArray

    override fun toList(): KtList<Term> = unfoldedList

    override fun toSequence(): Sequence<Term> = unfoldedSequence

    override fun freshCopy(): Tuple

    override fun freshCopy(scope: Scope): Tuple

    companion object {
        const val FUNCTOR = TUPLE_FUNCTOR

        @JvmStatic
        @JsName("wrapIfNeeded")
        @JvmOverloads
        fun wrapIfNeeded(
            vararg terms: Term,
            ifEmpty: () -> Term = { Truth.TRUE },
        ): Term = wrapIfNeeded(terms.asIterable(), ifEmpty)

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

        @JvmStatic
        @JsName("wrapSequenceIfNeeded")
        @JvmOverloads
        fun wrapIfNeeded(
            terms: Sequence<Term>,
            ifEmpty: () -> Term = { Truth.TRUE },
        ): Term = wrapIfNeeded(terms.asIterable(), ifEmpty)

        @JvmStatic
        @JsName("of")
        fun of(
            left: Term,
            right: Term,
        ): Tuple = TupleImpl(left, right)

        @JvmStatic
        @JsName("ofMany")
        fun of(
            first: Term,
            second: Term,
            vararg others: Term,
        ): Tuple = of(listOf(first, second, *others))

        @JvmStatic
        @JsName("ofIterable")
        fun of(terms: Iterable<Term>): Tuple = of(terms.toList())

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
