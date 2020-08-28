package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.*

internal class TupleUnfolder(tuple: Tuple) : Iterator<Term> {

    private var current: Term? = tuple

    override fun hasNext(): Boolean = current != null

    override fun next(): Term {
        return when (val x = current) {
            is Tuple -> {
                current = x.right
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