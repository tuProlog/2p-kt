package it.unibo.tuprolog.core

import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import kotlin.collections.List as KtList

interface List : Recursive {
    override val isList: Boolean
        get() = true

    @JsName("isWellFormed")
    val isWellFormed: Boolean

    @JsName("last")
    val last: Term

    /**
     * Estimated length of this list.
     * This property is part of the [List] interface to enable efficiency tweaks.
     * It is NOT intended for external usage
     *
     * DO NOT assume this returns the correct length of the current list.
     */
    @JsName("estimatedLength")
    val estimatedLength: Int

    override val unfoldedSequence: Sequence<Term>

    override val unfoldedList: KtList<Term>

    override val unfoldedArray: Array<Term>

    override val size: Int
        get() =
            unfoldedSequence.count().let {
                if (isWellFormed) {
                    it - 1
                } else {
                    it
                }
            }

    override fun freshCopy(): List

    override fun freshCopy(scope: Scope): List

    override fun asList(): List = this

    companion object {
        const val CONS_FUNCTOR = Terms.CONS_FUNCTOR

        const val EMPTY_LIST_FUNCTOR = Terms.EMPTY_LIST_FUNCTOR

        @JvmStatic
        @JsName("empty")
        fun empty(): List = TermFactory.default.emptyLogicList()

        @JvmStatic
        @JsName("of")
        fun of(vararg items: Term): List = TermFactory.default.logicListOf(items = items)

        @JvmStatic
        @JsName("ofIterable")
        fun of(items: Iterable<Term>): List = TermFactory.default.logicListOf(items)

        @JvmStatic
        @JsName("ofList")
        fun of(items: KtList<Term>): List = TermFactory.default.logicListOf(items)

        @JvmStatic
        @JsName("ofSequence")
        fun of(items: Sequence<Term>): List = TermFactory.default.logicListOf(items)

        @JvmStatic
        @JsName("from")
        fun from(
            vararg items: Term,
            tail: Term?,
        ): List = TermFactory.default.logicListFrom(items = items, tail = tail)

        @JvmStatic
        @JsName("fromNullTerminated")
        fun from(vararg items: Term): List = TermFactory.default.logicListFrom(items = items)

        @JvmStatic
        @JsName("fromIterable")
        fun from(
            items: Iterable<Term>,
            tail: Term?,
        ): List = TermFactory.default.logicListFrom(items, tail)

        @JvmStatic
        @JsName("fromIterableNullTerminated")
        fun from(items: Iterable<Term>): List = TermFactory.default.logicListFrom(items)

        @JvmStatic
        @JsName("fromSequence")
        fun from(
            items: Sequence<Term>,
            tail: Term?,
        ): List = TermFactory.default.logicListFrom(items, tail)

        @JvmStatic
        @JsName("fromSequenceNullTerminated")
        fun from(items: Sequence<Term>): List = TermFactory.default.logicListFrom(items)

        @JvmStatic
        @JsName("fromList")
        fun from(
            items: KtList<Term>,
            tail: Term?,
        ): List = TermFactory.default.logicListFrom(items, tail)

        @JvmStatic
        @JsName("fromListNullTerminated")
        fun from(items: KtList<Term>): List = TermFactory.default.logicListFrom(items)
    }
}
