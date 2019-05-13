package it.unibo.tuprolog.core

import kotlin.collections.List

internal object EmptyListImpl : AtomImpl(Empty.EMPTY_LIST_FUNCTOR), EmptyList {

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

    override fun toString(): String {
        return value
    }
}