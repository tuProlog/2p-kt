package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Term

internal class ListUnfolder(list: List) : Iterator<Term> {

    private var current: Term? = list

    override fun hasNext(): Boolean = current != null

    override fun next(): Term {
        return when (val x = current) {
            is Cons -> {
                current = x.tail
                x
            }
            is EmptyList -> {
                current = null
                x
            }
            null -> throw NoSuchElementException()
            else -> {
                current = null
                x
            }
        }
    }

}