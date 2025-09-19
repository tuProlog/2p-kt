package it.unibo.tuprolog.solve.problog.lib.knowledge.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.problog.lib.exception.ClauseMappingException

/**
 * This implements a [ClauseMapper] that handle "evidence" predicates.
 * Those predicates define probabilistic evidences, which in our case are defined as logic
 * terms that we have certainty of being true.
 *
 * Those are different from probabilistic facts with probability of 1.0. The difference is that
 * evidence predicates are not part of the logical theory itself, but are instead used to compute
 * the "conditional probability" of probabilistic queries.
 *
 * @author Jason Dellaluce
 */
internal object EvidenceClauseMapper : ClauseMapper {
    override fun isCompatible(clause: Clause): Boolean =
        clause is Rule && clause.head.functor == ProblogLib.EVIDENCE_PREDICATE

    override fun apply(clause: Clause): List<Clause> {
        if (clause !is Rule) {
            throw ClauseMappingException("Clause is not an instance of Rule: $clause")
        }

        if (clause.head.arity > 2) {
            throw TuPrologException("Invalid evidence predicate: ${clause.head}")
        } else if (clause.head.arity == 2 && clause.head[1] !is Truth) {
            throw TuPrologException("Invalid evidence predicate: ${clause.head}")
        }

        // Adjust implicit evidence predicates
        var headTerm = clause.head
        if (headTerm.arity == 1) {
            headTerm = Struct.of(headTerm.functor, headTerm[0], Truth.TRUE)
        }

        return listOf(
            if (clause is Fact) {
                Fact.of(headTerm)
            } else {
                Rule.of(
                    headTerm,
                    clause.body.withBodyExplanation(Var.anonymous()),
                )
            },
        )
    }
}
