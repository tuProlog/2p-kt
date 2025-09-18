package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.no

interface TestConcurrentRepeat<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    fun testRepeat() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = (repeat and (cut and fail))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }
}
