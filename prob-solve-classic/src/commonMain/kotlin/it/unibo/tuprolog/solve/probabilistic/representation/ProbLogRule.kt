package it.unibo.tuprolog.solve.probabilistic.representation

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term

internal class ProbLogRule(private val prologClause: Rule, private val probability: Term):
    ProbabilisticClause,
    Rule by
    if (prologClause.head.functor == ProbLogLibrary.PROB_FUNCTOR) {
        prologClause
    } else {
        Rule.of(
            Struct.of(ProbLogLibrary.PROB_FUNCTOR, probability, prologClause.head),
            prologClause.body
        )
    } {
    override fun toPrologClause(): Rule {
        return Rule.of(this.head[1] as Struct, this.body)
    }

    override fun toProbability(): Term {
        return this.head[0]
    }
}