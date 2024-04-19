package it.unibo.tuprolog.core

import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import kotlin.collections.List as KtList

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
        const val FUNCTOR = Terms.BLOCK_FUNCTOR

        const val EMPTY_FUNCTOR = Terms.EMPTY_BLOCK_FUNCTOR

        @JvmStatic
        @JsName("empty")
        fun empty(): EmptyBlock = TermFactory.default.emptyBlock()

        @JvmStatic
        @JsName("of")
        fun of(vararg terms: Term): Block = TermFactory.default.blockOf(*terms)

        @JvmStatic
        @JsName("ofList")
        fun of(terms: KtList<Term>): Block = TermFactory.default.blockOf(terms)

        @JvmStatic
        @JsName("ofIterable")
        fun of(terms: Iterable<Term>): Block = TermFactory.default.blockOf(terms)

        @JvmStatic
        @JsName("ofSequence")
        fun of(terms: Sequence<Term>): Block = TermFactory.default.blockOf(terms)
    }
}
