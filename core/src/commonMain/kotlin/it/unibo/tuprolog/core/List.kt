package it.unibo.tuprolog.core

import kotlin.jvm.JvmField
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.collections.List as KtList

interface List : Collection {

    override val isList: Boolean
        get() = true

    override val unfoldedSequence: Sequence<Term>

    override val unfoldedList: KtList<Term>

    override val unfoldedArray: Array<Term>

    override val size: Int
        get() = when {
            unfoldedList.last() is EmptyList -> unfoldedList.size - 1
            else -> unfoldedList.size
        }

    override fun toArray(): Array<Term> =
        when {
            unfoldedArray.last() is EmptyList -> unfoldedArray.sliceArray(0 until unfoldedArray.lastIndex)
            else -> unfoldedArray
        }

    override fun toList(): KtList<Term> =
        when {
            unfoldedList.last() is EmptyList -> unfoldedList.slice(0 until unfoldedArray.lastIndex)
            else -> unfoldedList
        }

    override fun toSequence(): Sequence<Term> = toList().asSequence()

    override fun freshCopy(): List = super.freshCopy() as List

    override fun freshCopy(scope: Scope): List =
        when {
            isGround -> this
            else -> {
                val cloned = unfoldedList.map { it.freshCopy(scope) }
                scope.listFrom(cloned.subList(0, cloned.lastIndex), cloned.last())
            }
        }

    companion object {

        @JvmStatic
        fun empty(): List = Empty.list()

        @JvmStatic
        fun of(vararg items: Term): List = from(items.toList(), empty())

        @JvmStatic
        fun of(items: Iterable<Term>): List = from(items.toList(), empty())

        @JvmStatic
        fun from(items: Iterable<Term>, last: Term?): List =
            from(items.toList(), last)

        @JvmStatic
        fun from(items: Iterable<Term>): List =
            from(items, null)

        @JvmStatic
        fun from(items: Sequence<Term>, last: Term?): List =
            from(items.toList(), last)

        @JvmStatic
        fun from(items: Sequence<Term>): List =
            from(items.toList(), null)

        @JvmStatic
        fun from(items: KtList<Term>, last: Term?): List {
            require(items.isNotEmpty() || last is EmptyList || last === null) {
                "Input list for method List.from(kotlin.collection.List, Term?) cannot be empty if the last item is `$last`"
            }

            val finalItem = last ?: empty()
            return items.foldRight(finalItem) { head, tail -> Cons.of(head, tail) } as List
        }

        @JvmStatic
        fun from(items: KtList<Term>): List =
            from(items, null)
    }
}
