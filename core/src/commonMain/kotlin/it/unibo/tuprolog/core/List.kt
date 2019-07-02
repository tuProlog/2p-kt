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

        fun from(items: KtList<Term>, last: Term? = null): List = when {
            last === null -> {
                val tail = Couple.of(items[items.lastIndex - 1], items[items.lastIndex])
                items.slice(0 until items.size - 2).foldRight(tail) { h, t -> Couple.of(h, t) }
            }
            else -> items.foldRight<Term, List>(Empty.list()) { h, t -> Couple.of(h, t) }
        }

        fun from(items: Iterable<Term>, last: Term? = null): List = from(items.toList(), last)

        fun from(items: Sequence<Term>, last: Term? = null): List = from(items.toList(), last)

        fun of(vararg items: Term): List = from(items.toList(), Empty.list())

        fun of(items: Iterable<Term>): List = from(items.toList(), Empty.list())
    }
}
