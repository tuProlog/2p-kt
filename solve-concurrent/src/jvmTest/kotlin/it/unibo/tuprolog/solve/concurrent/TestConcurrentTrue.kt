package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.SolverTest
import it.unibo.tuprolog.solve.yes
import kotlin.test.Test

class TestConcurrentTrue : SolverTest, SolverFactory by ConcurrentSolverFactory {
    @Test
    fun testTrue() {
        prolog {
            val solver = solverWithDefaultBuiltins()
            val query = atomOf("true")

            val solutions = MultiSet(solver.solve(query,mediumDuration))

            assertUnorderedSolutionEquals(
                MultiSet(query.yes()),
                solutions
            )
        }
    }
}
