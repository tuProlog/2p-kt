package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.Terms.TUPLE_FUNCTOR
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
        ): Term = TermFactory.default.wrapAsTupleIfNeeded(terms = terms, ifEmpty)

        @JvmStatic
        @JsName("wrapIterableIfNeeded")
        @JvmOverloads
        fun wrapIfNeeded(
            terms: Iterable<Term>,
            ifEmpty: () -> Term = { Truth.TRUE },
        ): Term = TermFactory.default.wrapAsTupleIfNeeded(terms, ifEmpty)

        @JvmStatic
        @JsName("wrapSequenceIfNeeded")
        @JvmOverloads
        fun wrapIfNeeded(
            terms: Sequence<Term>,
            ifEmpty: () -> Term = { Truth.TRUE },
        ): Term = TermFactory.default.wrapAsTupleIfNeeded(terms, ifEmpty)

        @JvmStatic
        @JsName("of")
        fun of(
            first: Term,
            second: Term,
        ): Tuple = TermFactory.default.tupleOf(first, second)

        @JvmStatic
        @JsName("ofMany")
        fun of(
            first: Term,
            second: Term,
            vararg others: Term,
        ): Tuple = TermFactory.default.tupleOf(first, second, *others)

        @JvmStatic
        @JsName("ofIterable")
        fun of(terms: Iterable<Term>): Tuple = TermFactory.default.tupleOf(terms)

        @JvmStatic
        @JsName("ofList")
        fun of(terms: KtList<Term>): Tuple = TermFactory.default.tupleOf(terms)
    }
}
