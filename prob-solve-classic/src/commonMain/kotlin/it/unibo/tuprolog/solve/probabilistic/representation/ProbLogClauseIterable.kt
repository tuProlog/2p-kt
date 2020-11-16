package it.unibo.tuprolog.solve.probabilistic.representation

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.theory.Theory

internal class ProbLogClauseIterable(private val theory: Theory): Iterable<Clause> {
    override fun iterator(): Iterator<Clause> {
        return TheoryIterator(theory.iterator())
    }

    private class TheoryIterator(private val iterator: Iterator<Clause>): Iterator<Clause> {
        override fun hasNext(): Boolean {
            return iterator.hasNext()
        }

        override fun next(): Clause {
            val next = iterator.next()
            return ProbLogClause(next, Numeric.of(1.0))
        }
    }
}
