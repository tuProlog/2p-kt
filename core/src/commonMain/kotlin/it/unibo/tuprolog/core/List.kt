package it.unibo.tuprolog.core

import kotlin.collections.List as KtList

interface List : Struct {

    override val isList: Boolean
        get() = true

    val unfoldedSequence: Sequence<Term>

    val unfoldedList: KtList<Term>

    val unfoldedArray: Array<Term>

    val size: Int
        get() = when {
            unfoldedList.last() is EmptyList -> unfoldedList.size - 1
            else -> unfoldedList.size
        }

    fun toArray(): Array<Term> =
            when {
                unfoldedArray.last() is EmptyList -> unfoldedArray.sliceArray(0 until unfoldedArray.lastIndex)
                else -> unfoldedArray
            }

    fun toList(): KtList<Term> =
            when {
                unfoldedList.last() is EmptyList -> unfoldedList.slice(0 until unfoldedArray.lastIndex)
                else -> unfoldedList
            }

    fun toSequence(): Sequence<Term> = toList().asSequence()

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

        fun empty(): List = Empty.list()

        fun of(vararg items: Term): List = from(items.toList(), empty())

        fun of(items: Iterable<Term>): List = from(items.toList(), empty())

        fun from(items: Iterable<Term>, last: Term? = null): List = from(items.toList(), last)

        fun from(items: Sequence<Term>, last: Term? = null): List = from(items.toList(), last)

        fun from(items: KtList<Term>, last: Term? = null): List {
            require(items.isNotEmpty() || last is EmptyList || last === null) {
                "Input list for method List.from(kotlin.collection.List, Term?) cannot be empty if the last item is `$last`"
            }

            val finalItem = last ?: empty()
            return items.foldRight(finalItem) { head, tail -> Couple.of(head, tail) } as List
        }
    }
}
