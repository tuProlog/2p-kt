package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Terms

internal class ClauseBodyWellFormedVisitor : TermVisitor<Boolean> {
    override fun defaultValue(term: Term): Boolean = true

    override fun visitNumeric(term: Numeric): Boolean = false

    override fun visitStruct(term: Struct): Boolean =
        when {
            term.functor in Terms.NOTABLE_FUNCTORS_FOR_CLAUSES && term.arity == 2 ->
                term.argsSequence
                    .map { arg -> arg.accept(this) }
                    .reduce(Boolean::and)
            else -> true
        }
}
