package it.unibo.tuprolog.solve.problog.lib.knowledge.mapping

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.problog.lib.exception.ClauseMappingException

internal object EvidenceClauseMapper : ClauseMapper {
    override fun isCompatible(clause: Clause): Boolean {
        return clause is Rule && clause.head.functor == ProblogLib.EVIDENCE_PREDICATE
    }

    override fun apply(clause: Clause): List<Clause> {
        if (clause !is Rule) {
            throw ClauseMappingException("Clause is not an instance of Rule: $clause")
        }

        if (clause.head.arity > 2) {
            throw TuPrologException("Invalid evidence predicate: ${clause.head}")
        } else if (clause.head.arity == 2 && clause.head[1] !is Truth) {
            throw TuPrologException("Invalid evidence predicate: ${clause.head}")
        }

        /* Adjust implicit evidence predicates */
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
                    clause.body.wrapInBinaryPredicateRecursive(Var.anonymous())
                )
            }
        )
    }
}
