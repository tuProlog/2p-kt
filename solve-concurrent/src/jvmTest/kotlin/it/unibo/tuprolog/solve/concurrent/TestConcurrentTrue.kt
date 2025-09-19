package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.yes

interface TestConcurrentTrue<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    fun testTrue() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()
            val query = atomOf("true")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(sequenceOf(query.yes()))

            expected.assertingEquals(solutions)
        }
    }
}
