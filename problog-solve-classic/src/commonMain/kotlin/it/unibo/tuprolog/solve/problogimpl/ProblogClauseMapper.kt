package it.unibo.tuprolog.solve.problogimpl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.problogimpl.stdlib.DefaultBuiltins
import kotlin.collections.List

abstract class ProblogClauseMapper {
    private var clauseIndex = 1

    protected fun mapClause(clause: Clause): List<Clause> {
        return when (clause) {
            is Rule -> mapRule(clause)
            else -> listOf(clause)
        }
    }

    private fun mapRule(rule: Rule): List<Clause> {
        if (rule.head.functor == DefaultBuiltins.PROB_FUNCTOR) {
            if (rule.head.arity != 2) {
                throw TuPrologException("Invalid probabilistic fact: ${rule.head}")
            }

            /* Extract probability */
            val probTerm = rule.head[0]
            if (probTerm !is Numeric) {
                throw TuPrologException("Variable probabilities are not supported: ${rule.head}")
            }
            var probability = probTerm.decimalValue.toDouble()

            /* Extract predicate */
            val predicateTerm = rule.head[1]
            val predicate = if (predicateTerm is Struct) {
                predicateTerm
            } else {
                Struct.of(predicateTerm.toString())
            }

            /* This is a negated probabilistic fact */
            if (rule.body is Truth) {
                /* Map "negated" facts */
                if (!rule.body.isTrue) {
                    probability = 1.0 - probability
                }
                return listOf(ProblogFact(clauseIndex++, probability, predicate))
            }

            /* This is a probabilistic clause */
            return listOf(ProblogRule(clauseIndex++, probability, predicate, rule.body))
        }

        return listOf(rule)
    }
}
