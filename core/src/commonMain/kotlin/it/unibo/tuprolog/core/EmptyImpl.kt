package it.unibo.tuprolog.core

import kotlin.collections.List

internal object EmptyImpl : AtomImpl(Empty.EMPTY_LIST_FUNCTOR), Empty {
    private val unfoldedList: List<Term> = emptyList()
    private val unfoldedSequence: Sequence<Term> = emptySequence()
    private val unfoldedArray: Array<Term> = emptyArray()

    override fun toArray(): Array<Term> {
        return unfoldedArray
    }

    override fun toSequence(): Sequence<Term> {
        return unfoldedSequence
    }

    override fun toList(): List<Term> {
        return unfoldedList
    }
}