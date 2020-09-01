package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import kotlin.collections.listOf as ktListOf

internal class TestAssertAImpl(private val solverFactory: SolverFactory) : TestAssertA{

    override fun testAssertAClause() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = (asserta(("bar"("X") `if` "X")) and clause{("bar"("B") and "X")})
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.yes("B" to call("X"))),
                    solutions
            )
        }
    }

    override fun testAssertAAny() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = asserta(`_`)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.halt(
                            InstantiationError.forGoal(
                                    DummyInstances.executionContext,
                                    Signature("asserta", 3),
                                    varOf("_")
                            )
                    )
                    ),
                    solutions
            )
        }
    }

    override fun testAssertANumber() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = asserta(4)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.halt(
                            TypeError.forGoal(
                                    DummyInstances.executionContext,
                                    Signature("asserta", 3),
                                    TypeError.Expected.CALLABLE,
                                    numOf(4)
                            )
                    )
                    ),
                    solutions
            )
        }
    }

    override fun testAssertAFooNumber() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = asserta(("foo" `if` 4))
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.halt(
                            TypeError.forGoal(
                                    DummyInstances.executionContext,
                                    Signature("asserta", 3),
                                    TypeError.Expected.CALLABLE,
                                    numOf(4)
                            )
                    )
                    ),
                    solutions
            )
        }
    }

    override fun testAssertAAtomTrue() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = asserta(atom(`_`) `if` true)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.no() //TODO permission_error
                    ),
                    solutions
            )
        }
    }
}