package it.unibo.tuprolog.solve.probabilistic.representation

import it.unibo.tuprolog.core.*

internal class ProbLogFact(private val prologFact: Fact, private val probability: Term):
        ProbabilisticClause,
        Fact by
            if (prologFact.head.functor == ProbLogLibrary.PROB_FUNCTOR) {
                prologFact
            } else {
                if (probability is Numeric && !prologFact.body.isTrue) {
                    val negatedProbability = Numeric.of(1.0 - probability.decimalValue.toDouble())
                    Fact.of(Struct.of(ProbLogLibrary.PROB_FUNCTOR, negatedProbability, prologFact.head))
                } else {
                    Fact.of(Struct.of(ProbLogLibrary.PROB_FUNCTOR, probability, prologFact.head))
                }
            }
        {
    override val isConstant: Boolean get() = true

    override fun toProbability(): Term {
        return probability
    }

    override fun toPrologClause(): Clause {
        return prologFact
    }
}