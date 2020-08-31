package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.EmptySet
import it.unibo.tuprolog.core.Set
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple

internal class SetUnfolder(set: Set) : Iterator<Term> {

    private var current: Term? = set
    private var setUnfolded = false

    override fun hasNext(): Boolean = current != null

    override fun next(): Term {
        return if (setUnfolded) {
            when (val x = current) {
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
        } else {
            when (val x = current) {
                is EmptySet -> {
                    current = null
                    x
                }
                is Set -> {
                    current = x[0]
                    setUnfolded = true
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

}