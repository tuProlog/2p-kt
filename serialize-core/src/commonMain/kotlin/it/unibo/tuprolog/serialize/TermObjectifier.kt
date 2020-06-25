package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor

interface TermObjectifier<T> : Objectifier<Term, T>, TermVisitor<T> {
    override fun objectify(value: Term): T = visit(value)
}