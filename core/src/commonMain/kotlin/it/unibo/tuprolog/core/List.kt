package it.unibo.tuprolog.core

import kotlin.collections.List as KtList

interface List : Struct {

    override val isList: Boolean
        get() = true

    fun toArray(): Array<Term>

    fun toList(): KtList<Term>

    fun toSequence(): Sequence<Term>

    companion object {

        fun empty(): List = Empty.list()

        fun from(items: KtList<Term>, last: Term? = null): List =
                items.foldRight(
                        if (last === null) Empty.list() else Couple.last(last)
                ) { head, tail -> Couple.of(head, tail) }

        fun from(items: Iterable<Term>, last: Term? = null): List = from(items.toList(), last)

        fun from(items: Sequence<Term>, last: Term? = null): List = from(items.toList(), last)

        fun of(vararg items: Term): List = from(items.toList())

        fun of(items: Iterable<Term>): List = from(items.toList())
    }
}
