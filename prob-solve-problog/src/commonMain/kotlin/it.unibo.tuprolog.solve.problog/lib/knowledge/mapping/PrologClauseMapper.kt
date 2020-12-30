package it.unibo.tuprolog.solve.problog.lib.knowledge.mapping

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.problog.lib.exception.ClauseMappingException
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbTerm
import it.unibo.tuprolog.solve.problog.lib.rule.Prob

/** [ClauseMapper] implementation that handled classic Prolog clauses, that does not contain
 * any probabilistic information and is represented as a pure-logic clause. */
internal object PrologClauseMapper : ClauseMapper {

    override fun isCompatible(clause: Clause): Boolean {
        return clause is Rule
    }

    override fun apply(clause: Clause): List<Clause> {
        if (clause !is Rule) {
            throw ClauseMappingException("Clause is not an instance of Rule: $clause")
        }

        /* Map facts and truths differently */
        if (clause.body is Truth) {
            return listOf(
                Rule.of(
                    clause.head.wrapInBinaryHeadPredicate(
                        BinaryDecisionDiagram.Terminal<ProbTerm>(true).toTerm()
                    ),
                    clause.body
                )
            )
        }

        val ddVar = Var.of("${ProblogLib.DD_VAR_NAME}_Res")
        return listOf(
            Rule.of(
                Struct.of(Prob.FUNCTOR, ddVar, clause.head),
                clause.body.wrapInBinaryPredicateRecursive(ddVar)
            )
        )
    }
}
