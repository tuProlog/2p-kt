package it.unibo.tuprolog.solve.probabilistic.representation

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.solve.probabilistic.ClassicProbabilisticSolverFactory
import it.unibo.tuprolog.theory.Theory

internal class ProbLogTheory(private val prologTheory: Theory): ProbabilisticTheory, Theory by
    Theory.of(ProbLogClauseIterable(prologTheory)) {
    override fun toPrologTheory(): Theory {
        return Theory.of(ProbLogRevertClauseIterable(prologTheory))
    }

    private class ProbLogRevertClauseIterable(private val theory: Theory): Iterable<Clause> {
        override fun iterator(): Iterator<Clause> {
            return TheoryIterator(theory.iterator())
        }

        private class TheoryIterator(private val iterator: Iterator<Clause>): Iterator<Clause> {
            override fun hasNext(): Boolean {
                return iterator.hasNext()
            }

            override fun next(): Clause {
                val next = iterator.next()
                return ProbLogRepresentationFactory.from(next).toPrologClause()
            }
        }
    }
}