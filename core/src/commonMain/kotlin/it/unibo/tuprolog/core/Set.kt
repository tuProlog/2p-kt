package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.SetImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import kotlin.collections.List as KtList

interface Set : Collection {

    override val isSet: Boolean
        get() = true

    override val isEmptySet: Boolean
        get() = arity == 0

    override val functor: String
        get() = FUNCTOR

    override fun toArray(): Array<Term> = unfoldedArray

    override fun toList(): KtList<Term> = unfoldedList

    override fun toSequence(): Sequence<Term> = unfoldedSequence

    override fun freshCopy(): Set

    override fun freshCopy(scope: Scope): Set

    companion object {

        const val FUNCTOR = Terms.SET_FUNCTOR

        const val EMPTY_FUNCTOR = Terms.EMPTY_SET_FUNCTOR

        @JvmStatic
        @JsName("empty")
        fun empty(): EmptySet = EmptySet()

        @JvmStatic
        @JsName("of")
        fun of(vararg terms: Term): Set = of(terms.toList())

        @JvmStatic
        @JsName("ofList")
        fun of(terms: KtList<Term>): Set =
            when {
                terms.isEmpty() -> empty()
                terms.size == 1 -> SetImpl(terms.single())
                else -> SetImpl(Tuple.of(terms))
            }

        @JvmStatic
        @JsName("ofIterable")
        fun of(terms: Iterable<Term>): Set = of(terms.toList())

        @JvmStatic
        @JsName("ofSequence")
        fun of(terms: Sequence<Term>): Set = of(terms.toList())
    }
}
