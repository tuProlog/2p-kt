package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.EmptySet
import it.unibo.tuprolog.core.Set
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Tuple

internal class SetUnfolder(set: Set) : Iterator<Term> {

    private var current: Term? = set

    private var setUnfolded = false

    override fun hasNext(): Boolean = current != null

    private val tupleUnfolderVisitor = object : TermVisitor<Term> {
        override fun visitTuple(term: Tuple): Term {
            current = term.right
            return term
        }

        override fun defaultValue(term: Term): Term {
            current = null
            return term
        }
    }

    private val setUnfolderVisitor = object : TermVisitor<Term> {
        override fun visitEmptySet(term: EmptySet): Term {
            current = null
            return term
        }

        override fun visitSet(term: Set): Term {
            current = term[0]
            setUnfolded = true
            return term
        }

        override fun defaultValue(term: Term): Term {
            current = null
            return term
        }
    }

    override fun next(): Term =
        current?.accept(if (setUnfolded) tupleUnfolderVisitor else setUnfolderVisitor) ?: throw NoSuchElementException()
}
