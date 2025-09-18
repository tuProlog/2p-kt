package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Tuple

internal class TupleUnfolder(
    tuple: Tuple,
) : Iterator<Term> {
    private var current: Term? = tuple

    override fun hasNext(): Boolean = current != null

    private val tupleUnfolderVisitor =
        object : TermVisitor<Term> {
            override fun visitTuple(term: Tuple): Term {
                current = term.right
                return term
            }

            override fun defaultValue(term: Term): Term {
                current = null
                return term
            }
        }

    override fun next(): Term = current?.accept(tupleUnfolderVisitor) ?: throw NoSuchElementException()
}
