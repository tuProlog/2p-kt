package it.unibo.tuprolog.core.visitors

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.EmptySet
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor

abstract class AbstractTermVisitor<T> : TermVisitor<T> {
    protected abstract fun <X : Term> join(term: X, f1: (X) -> T, vararg fs: (X) -> T): T

    override fun visitAtom(term: Atom): T = join(term, this::visitStruct, this::visitConstant)

    override fun visitEmptySet(term: EmptySet): T = join(term, this::visitSet, this::visitEmpty)

    override fun visitEmptyList(term: EmptyList): T = join(term, this::visitList, this::visitEmpty)
}
