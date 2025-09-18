package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.LazyConsWithExplicitLast
import it.unibo.tuprolog.core.impl.LazyConsWithImplicitLast
import it.unibo.tuprolog.utils.Cursor
import it.unibo.tuprolog.utils.cursor
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
        fun empty(): List = Empty.list()

        @JvmStatic
        @JsName("of")
        fun of(vararg items: Term): List = from(items.toList(), empty())

        @JvmStatic
        @JsName("ofIterable")
        fun of(items: Iterable<Term>): List =
            when (items) {
                is KtList<Term> -> from(items, empty())
                else -> from(items.cursor(), empty())
            }

        @JvmStatic
        @JsName("ofList")
        fun of(items: KtList<Term>): List = from(items, empty())

        @JvmStatic
        @JsName("ofSequence")
        fun of(items: Sequence<Term>): List = from(items.cursor(), empty())

        @JvmStatic
        @JsName("from")
        fun from(
            vararg items: Term,
            last: Term?,
        ): List = from(items.toList(), last)

        @JvmStatic
        @JsName("fromNullTerminated")
        fun from(vararg items: Term): List = from(items.cursor(), null)

        @JvmStatic
        @JsName("fromIterable")
        fun from(
            items: Iterable<Term>,
            last: Term?,
        ): List =
            when (items) {
                is KtList<Term> -> from(items, last)
                else -> from(items.cursor(), last)
            }

        @JvmStatic
        @JsName("fromIterableNullTerminated")
        fun from(items: Iterable<Term>): List = from(items, null)

        @JvmStatic
        @JsName("fromSequence")
        fun from(
            items: Sequence<Term>,
            last: Term?,
        ): List = from(items.cursor(), last)

        @JvmStatic
        @JsName("fromSequenceNullTerminated")
        fun from(items: Sequence<Term>): List = from(items.cursor(), null)

        @JvmStatic
        @JsName("fromList")
        fun from(
            items: KtList<Term>,
            last: Term?,
        ): List {
            if (items.isEmpty()) {
                return (last ?: empty()).asList()
                    ?: throw IllegalArgumentException(
                        "Cannot create a list out of the provided arguments: $items, $last",
                    )
            }
            val i = items.asReversed().iterator()
            var right =
                if (last == null) {
                    i.next()
                    items.last()
                } else {
                    last
                }
            while (i.hasNext()) {
                right = Cons.of(i.next(), right)
            }
            return right.castToList()
        }

        @JvmStatic
        @JsName("fromListNullTerminated")
        fun from(items: KtList<Term>): List = from(items, null)

        @JvmStatic
        @JsName("fromCursor")
        fun from(
            items: Cursor<out Term>,
            last: Term?,
        ): List =
            when {
                items.isOver ->
                    (last ?: empty()).asList()
                        ?: throw IllegalArgumentException(
                            "Cannot create a list out of the provided arguments: $items, $last",
                        )
                last == null -> LazyConsWithImplicitLast(items)
                else -> LazyConsWithExplicitLast(items, last)
            }

        @JvmStatic
        @JsName("fromCursorNullTerminated")
        fun from(items: Cursor<out Term>): List = from(items, null)
    }
}
