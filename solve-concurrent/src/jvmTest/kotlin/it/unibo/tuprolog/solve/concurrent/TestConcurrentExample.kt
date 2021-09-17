package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.SolverTest
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.yes
import kotlin.test.Test

class TestConcurrentExample : SolverTest, SolverFactory by ConcurrentSolverFactory {
    @Test
    fun testNameHere() {
        prolog {
            val solver = solverWithDefaultBuiltins()
            val query = truthOf(true)

            val solutions = solver.solve(query).take(1).toList() // removing the take(1) prevents the termination of the test
            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )
        }
    }
}
