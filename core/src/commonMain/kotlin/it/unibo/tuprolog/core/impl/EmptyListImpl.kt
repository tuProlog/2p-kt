package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Term

internal object EmptyListImpl : AtomImpl(Empty.EMPTY_LIST_FUNCTOR), EmptyList {


    override fun toArray(): Array<Term> = arrayOf(this)

    override fun toSequence(): Sequence<Term> = sequenceOf(this)

    override fun toList(): List<Term> = listOf(this)

    override fun toString(): String = value
}