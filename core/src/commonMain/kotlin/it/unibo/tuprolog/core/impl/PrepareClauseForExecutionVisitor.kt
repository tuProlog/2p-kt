package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermFactory
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Terms
import it.unibo.tuprolog.core.Var

internal class PrepareClauseForExecutionVisitor(
    private val unifier: Substitution.Unifier,
    private val termFactory: TermFactory,
) : TermVisitor<Term> {
    override fun defaultValue(term: Term) = term

    override fun visitStruct(term: Struct): Term =
        when {
            term.functor in Terms.NOTABLE_FUNCTORS_FOR_CLAUSES && term.arity == 2 ->
                termFactory.structOf(term.functor, term.argsSequence.map { arg -> arg.accept(this) })
            else -> term
        }

    override fun visitClause(term: Clause): Term = termFactory.clauseOf(term.head, term.body.accept(this))

    override fun visitVar(term: Var): Term =
        when (term) {
            in unifier -> unifier[term]!!.accept(this)
            else -> termFactory.structOf("call", term)
        }
}
