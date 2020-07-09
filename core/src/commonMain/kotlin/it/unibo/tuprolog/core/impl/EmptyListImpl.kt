package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Term

internal object EmptyListImpl : AtomImpl(Empty.EMPTY_LIST_FUNCTOR), EmptyList {

    override val unfoldedList: List<Term> = listOf(this)
    override val unfoldedSequence: Sequence<Term> = sequenceOf(this)
    override val unfoldedArray: Array<Term> = arrayOf(this)

    override fun toString(): String = value

    override val last: Term
        get() = this
}