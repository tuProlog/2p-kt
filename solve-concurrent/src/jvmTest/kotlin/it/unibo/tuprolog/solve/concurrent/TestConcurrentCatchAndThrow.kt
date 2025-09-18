package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no

interface TestConcurrentCatchAndThrow<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    fun testCatchThrow() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = (catch(true, C, write("something")) and `throw`("blabla"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected =
                fromSequence(
                    query.halt(
                        SystemError.forUncaughtException(
                            DummyInstances.executionContext,
                            atomOf("blabla"),
                        ),
                    ),
                )

            expected.assertingEquals(solutions)
        }
    }

    fun testCatchFail() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = catch(number_chars("A", "L"), "error"("instantiation_error", `_`), fail)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }
}
