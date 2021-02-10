package it.unibo.tuprolog.core

import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import kotlin.collections.List as KtList

interface List : Collection {

    override val isList: Boolean
        get() = true

    @JsName("isWellFormed")
    val isWellFormed: Boolean

    @JsName("last")
    val last: Term

    override val unfoldedSequence: Sequence<Term>

    override val unfoldedList: KtList<Term>

    override val unfoldedArray: Array<Term>

    override val size: Int
        get() = unfoldedSequence.count().let {
            if (isWellFormed) {
                it - 1
            } else {
                it
            }
        }

    override fun freshCopy(): List

    override fun freshCopy(scope: Scope): List

    companion object {

        const val CONS_FUNCTOR = Terms.CONS_FUNCTOR

        const val EMPTY_LIST_FUNCTOR = Terms.EMPTY_LIST_FUNCTOR

        @JvmStatic
        @JsName("empty")
        fun empty(): List = Empty.list()

        @JvmStatic
        @JsName("of")
        fun of(vararg items: Term): List = from(items.toList(), empty())

        @JvmStatic
        @JsName("ofIterable")
        fun of(items: Iterable<Term>): List = from(items.toList(), empty())

        @JvmStatic
        @JsName("ofSequence")
        fun of(items: Sequence<Term>): List = from(items.toList(), empty())

        @JvmStatic
        @JsName("from")
        fun from(vararg items: Term, last: Term?): List =
            from(items.toList(), last)

        @JvmStatic
        @JsName("fromNullTerminated")
        fun from(vararg items: Term): List =
            from(items.toList(), null)

        @JvmStatic
        @JsName("fromIterable")
        fun from(items: Iterable<Term>, last: Term?): List =
            from(items.toList(), last)

        @JvmStatic
        @JsName("fromIterableNullTerminated")
        fun from(items: Iterable<Term>): List =
            from(items, null)

        @JvmStatic
        @JsName("fromSequence")
        fun from(items: Sequence<Term>, last: Term?): List =
            from(items.toList(), last)

        @JvmStatic
        @JsName("fromSequenceNullTerminated")
        fun from(items: Sequence<Term>): List =
            from(items.toList(), null)

        @JvmStatic
        @JsName("fromList")
        fun from(items: KtList<Term>, last: Term?): List {
            require(items.isNotEmpty() || last is EmptyList || last === null) {
                "Input list for method List.from(kotlin.collection.List, Term?) cannot be empty if the last item is `$last`"
            }

            val finalItem = last ?: empty()
            return items.foldRight(finalItem) { head, tail -> Cons.of(head, tail) } as List
        }

        @JvmStatic
        @JsName("fromListNullTerminated")
        fun from(items: KtList<Term>): List =
            from(items, null)
    }
}
