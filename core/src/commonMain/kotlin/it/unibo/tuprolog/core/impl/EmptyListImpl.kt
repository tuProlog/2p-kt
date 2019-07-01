package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Term

internal object EmptyListImpl : AtomImpl(Empty.EMPTY_LIST_FUNCTOR), EmptyList {

    private val unfoldedList: List<Term> = emptyList()
    private val unfoldedSequence: Sequence<Term> = emptySequence()
    private val unfoldedArray: Array<Term> = emptyArray()

    override fun toArray(): Array<Term> = unfoldedArray

    override fun toSequence(): Sequence<Term> = unfoldedSequence

    override fun toList(): List<Term> = unfoldedList

    override fun toString(): String = value
}