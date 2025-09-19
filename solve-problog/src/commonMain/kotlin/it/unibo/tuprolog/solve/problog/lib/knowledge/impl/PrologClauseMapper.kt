package it.unibo.tuprolog.solve.problog.lib.knowledge.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.problog.lib.exception.ClauseMappingException
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanation

/** [ClauseMapper] implementation that handled classic Prolog clauses, that does not contain
 * any probabilistic information and is represented as a pure-logic clause.
 * Simple logic predicates are mapped in the probabilistic domain as Problog terms with
 * probability of 1.0.
 *
 * @author Jason Dellaluce */
internal object PrologClauseMapper : ClauseMapper {
    override fun isCompatible(clause: Clause): Boolean = clause is Rule

    override fun apply(clause: Clause): List<Clause> {
        if (clause !is Rule) {
            throw ClauseMappingException("Clause is not an instance of Rule: $clause")
        }

        // Map facts and truths differently
        if (clause.body is Truth) {
            return listOf(
                Rule.of(
                    clause.head.withExplanation(ProbExplanation.TRUE.toTerm()),
                    clause.body,
                ),
            )
        }

        val explanationVar = Var.of("${ProblogLib.EXPLANATION_VAR_NAME}_Res")
        return listOf(
            Rule.of(
                clause.head.withExplanation(explanationVar),
                clause.body.withBodyExplanation(explanationVar),
            ),
        )
    }
}
