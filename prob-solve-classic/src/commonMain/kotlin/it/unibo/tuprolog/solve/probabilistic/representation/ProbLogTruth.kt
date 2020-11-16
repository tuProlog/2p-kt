package it.unibo.tuprolog.solve.probabilistic.representation

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth

internal class ProbLogTruth(private val prologTerm: Truth): ProbabilisticTerm, Truth by prologTerm {
    override val isConstant: Boolean get() = true

    override fun toProbability(): Term {
        return Numeric.of(if (prologTerm.isTrue) 1.0 else 0.0)
    }

    override fun toPrologTerm(): Term {
        return prologTerm
    }
}