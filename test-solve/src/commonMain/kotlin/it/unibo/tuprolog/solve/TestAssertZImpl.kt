package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import kotlin.collections.listOf as ktListOf

internal class TestAssertZImpl(private val solverFactory: SolverFactory) : TestAssertZ{

    override fun testAssertZClause() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = assertz("foo"("X") `if` "X" then call("X"))
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.yes()),
                    solutions
            )
        }
    }

    override fun testAssertZAny() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = assertz(`_`)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.halt(
                            InstantiationError.forGoal(
                                    DummyInstances.executionContext,
                                    Signature("assertz", 3),
                                    varOf("_")
                            )
                    )
                    ),
                    solutions
            )
        }
    }

    override fun testAssertZNumber() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = assertz(4)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.halt(
                            TypeError.forGoal(
                                    DummyInstances.executionContext,
                                    Signature("assertz", 3),
                                    TypeError.Expected.CALLABLE,
                                    numOf(4)
                            )
                    )
                    ),
                    solutions
            )
        }
    }

    override fun testAssertZFooNumber() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = assertz(("foo" `if` 4))
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.halt(
                            TypeError.forGoal(
                                    DummyInstances.executionContext,
                                    Signature("assertz", 3),
                                    TypeError.Expected.CALLABLE,
                                    numOf(4)
                            )
                    )
                    ),
                    solutions
            )
        }
    }

    override fun testAssertZAtomTrue() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = assertz(atom(`_`) `if` true)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.no() //TODO permission_error
                    ),
                    solutions
            )
        }
    }
}