package it.unibo.tuprolog.solve.problogimpl

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.theory.Theory


class ProblogTheory(private val prologTheory: Theory):
    Theory by Theory.of(ProblogClauseIterable(prologTheory))
{
    private class ProblogClauseIterable(private val theory: Theory): Iterable<Clause> {

        override fun iterator(): Iterator<Clause> {
            return TheoryIterator(theory.iterator())
        }

        private class TheoryIterator(
            private val iterator: Iterator<Clause>,
        ): ProblogClauseMapper(), Iterator<Clause> {
            private val mappedList: MutableList<Clause> = mutableListOf()

            override fun hasNext(): Boolean {
                return mappedList.size > 0 || iterator.hasNext()
            }

            override fun next(): Clause {
                if (mappedList.size == 0) {
                    mappedList.addAll(mapClause(iterator.next()))
                }
                return mappedList.removeFirst()
            }

        }

    }

}