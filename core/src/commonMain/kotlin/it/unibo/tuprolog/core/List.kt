package it.unibo.tuprolog.core

import kotlin.collections.List as KtList

interface List : Struct {

    override val isList: Boolean
        get() = true

    val unfoldedSequence: Sequence<Term>

    val unfoldedList: KtList<Term>

    val unfoldedArray: Array<Term>

    fun toArray(): Array<Term> =
            if (unfoldedArray.last() is EmptyList) {
                unfoldedArray.sliceArray(0 until unfoldedArray.lastIndex)
            } else {
                unfoldedArray
            }

    fun toList(): KtList<Term> =
            if (unfoldedList.last() is EmptyList) {
                unfoldedList.slice(0 until unfoldedArray.lastIndex)
            } else {
                unfoldedList
            }

    fun toSequence(): Sequence<Term> = toList().asSequence()

    override fun freshCopy(): List = super.freshCopy() as List

    override fun freshCopy(scope: Scope): List =
            if (isGround) {
                this
            } else {
                scope.listOf(argsSequence.map { it.freshCopy(scope) }.takeWhile { it !is EmptyList }.asIterable())
            }

    companion object {

        fun from(items: Iterable<Term>, last: Term? = null): List = from(items.toList(), last)

        fun from(items: Sequence<Term>, last: Term? = null): List = from(items.toList(), last)

        fun from(items: KtList<Term>, last: Term? = null): List {
            if (items.isEmpty() && last !is EmptyList && last !== null) {
                throw IllegalArgumentException(
                        "Input list for method List.from(kotlin.collection.List, Term?) cannot be empty if the last item is `$last`"
                )

            }

            val finalItem = if (last === null) Empty.list() else last
            return items.foldRight(finalItem) { head, tail -> Couple.of(head, tail) } as List
        }

        fun of(vararg items: Term): List = from(items.toList(), Empty.list())

        fun of(items: Iterable<Term>): List = from(items.toList(), Empty.list())

        fun empty(): List = Empty.list()
    }
}
