package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.SolverTest
import it.unibo.tuprolog.solve.yes
import kotlin.test.Test
import kotlin.test.assertEquals

interface TestConcurrentTrue<T> : FromSequence<T>, SolverFactory {

    fun testTrue() {
        prolog {
            val solver = solverWithDefaultBuiltins()
            val query = atomOf("true")

            assertEquals(
                fromSequence(solver.solve(query,mediumDuration)),
                fromSequence(sequenceOf(query.yes()))
            )

        }
    }
}

class TestConcurrentTrueImpl : TestConcurrentTrue<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {

    @Test
    override fun testTrue() = super.testTrue()

}
