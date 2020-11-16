package it.unibo.tuprolog.solve.probabilistic.representation

import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Term

internal class ProbLogDirective(private val prologClause: Directive, private val probability: Term):
    ProbabilisticClause,
    Directive by prologClause {
    override fun toPrologClause(): Directive {
        return prologClause
    }

    override fun toProbability(): Term {
        return probability
    }
}