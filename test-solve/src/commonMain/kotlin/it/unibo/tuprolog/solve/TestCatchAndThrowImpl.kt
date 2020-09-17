package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.error.SystemError

internal class TestCatchAndThrowImpl(private val solverFactory: SolverFactory) : TestCatchAndThrow {
    override fun testCatchThrow() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = (catch(true, "C", write(atomOf("something"))) and "throw"("blabla"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        SystemError.forUncaughtException(
                            DummyInstances.executionContext,
                            atomOf("blabla")

                        )
                    )
                ),
                solutions
            )
        }
    }

    override fun testCatchFail() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = catch("number_chars"("A", "L"), "error"("instantiation_error" and `_`), fail)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.no()
                ),
                solutions
            )
        }
    }
}
