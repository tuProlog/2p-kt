package it.unibo.tuprolog.solve.probabilistic.representation

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term

internal class ProbLogTerm(private val prologTerm: Term, private val probability: Term):
    ProbabilisticTerm,
    Struct by
        if (prologTerm is Struct && prologTerm.functor == ProbLogLibrary.PROB_FUNCTOR) {
            prologTerm
        } else {
            Struct.of(ProbLogLibrary.PROB_FUNCTOR, probability, prologTerm)
        }
    {

    override fun toProbability(): Term {
        return this[0]
    }

    override fun toPrologTerm(): Term {
        return this[1]
    }
}