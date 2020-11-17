package it.unibo.tuprolog.solve.probabilistic.representation

import it.unibo.tuprolog.core.*

internal class ProbLogRule(private val prologClause: Rule, private val probability: Term):
    ProbabilisticClause,
    Rule by
    if (prologClause.head.functor == ProbLogLibrary.PROB_FUNCTOR) {
        val prob = prologClause.head[0]
        if (prob is Numeric
                && prob.decimalValue.toDouble() < 1.0
                && prologClause.body is Truth
                && !prologClause.isTrue) {
            val negatedProbability = Numeric.of(1.0 - prob.decimalValue.toDouble())
            Fact.of(Struct.of(ProbLogLibrary.PROB_FUNCTOR, negatedProbability, prologClause.head[1]))
        } else {
            prologClause
        }
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