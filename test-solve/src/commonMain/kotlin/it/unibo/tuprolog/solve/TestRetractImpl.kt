package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.error.TypeError

internal class TestRetractImpl(private val solverFactory: SolverFactory) : TestRetract {
    override fun testRetractNumIfX() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = retract((4 `if` "X"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        TypeError.forGoal(
                            DummyInstances.executionContext,
                            Signature("retract", 2),
                            TypeError.Expected.CALLABLE,
                            numOf(4)

                        )
                    )
                ),
                solutions
            )
        }
    }

    override fun testRetractAtomEmptyList() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = retract((atom(`_`) `if` "X" `==` emptyList))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.no()), //Permission_error
                solutions
            )
        }
    }
}