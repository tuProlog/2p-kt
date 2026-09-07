package it.unibo.tuprolog.datalog.visitors

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var

/**
 * A [AbstractClauseVisitor] that collects the [Var]s occurring in a clause's head which do *not* also occur
 * in one of its non-negated body literals — i.e. the offending variables that would make
 * [it.unibo.tuprolog.datalog.allHeadVariablesInNonNegatedLiterals] `false`. Variables inside negated body
 * literals ([ClauseVisitor.visitNegatedLiteral]) are deliberately ignored when collecting "positive"
 * variables, since a negated goal does not bind anything.
 *
 * `clause.accept(HeadVariablesOutsideNonNegatedLiterals)` returns the empty set for a well-formed clause,
 * and the set of unsafe head variables otherwise.
 */
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
