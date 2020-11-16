package it.unibo.tuprolog.solve.probabilistic.representation

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term

internal class ProbLogClause(private val prologClause: Clause, private val probability: Term):
    ProbabilisticClause,
    Clause by
        if (prologClause.head == null
                || prologClause.head is Struct && prologClause.head!!.functor == ProbLogLibrary.PROB_FUNCTOR) {
            prologClause
        } else {
            Clause.of(
                Struct.of(ProbLogLibrary.PROB_FUNCTOR, probability, prologClause.head!!),
                prologClause.body
            )
        } {
    override fun toPrologClause(): Clause {
        return prologClause
    }

    override fun toProbability(): Term {
        return probability
    }
}