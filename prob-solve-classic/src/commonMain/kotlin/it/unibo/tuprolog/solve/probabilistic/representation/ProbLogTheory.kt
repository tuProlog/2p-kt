package it.unibo.tuprolog.solve.probabilistic.representation

import it.unibo.tuprolog.theory.Theory

internal class ProbLogTheory(private val prologTheory: Theory): ProbabilisticTheory, Theory by
    Theory.of(ProbLogClauseIterable(prologTheory)) {
    override fun toPrologTheory(): Theory {
        return prologTheory
    }
}