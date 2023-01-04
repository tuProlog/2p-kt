package it.unibo.tuprolog.datalog.visitors

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var

object HeadVariablesOutsideNonNegatedLiterals : AbstractClauseVisitor<Set<Var>>() {
    override fun reduce(results: Sequence<Set<Var>>): Set<Var> =
        buildSet {
            for (vars in results.filter { it.isNotEmpty() }) {
                addAll(vars)
            }
        }

    override fun defaultValue(term: Term): Set<Var> = emptySet()

    override fun visitClause(term: Clause): Set<Var> {
        val positiveVariables = reduce(dispatchBody(term))
        val headVariables = reduce(dispatchHead(term))
        return headVariables.asSequence().filter { it !in positiveVariables }.toSet()
    }

    override fun visitVar(term: Var): Set<Var> = setOf(term)

    override fun visitNegatedLiteral(literal: Struct): Set<Var> = emptySet()
}
