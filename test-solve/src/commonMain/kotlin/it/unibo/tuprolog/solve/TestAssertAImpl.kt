package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import kotlin.collections.listOf as ktListOf

internal class TestAssertAImpl(private val solverFactory: SolverFactory) : TestAssertA {

    override fun testAssertAClause() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = (asserta(("bar"("X") `if` "X")) and clause { ("bar"("B") and "X") })
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes("B" to call("X"))),
                solutions
            )
        }
    }

    override fun testAssertAAny() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val underscore = `_`

            val query = asserta(underscore)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("asserta", 1),
                            underscore,
                            0
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

            val query = asserta(4)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
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

            val query = asserta(("foo" `if` 4))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
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

            val query = asserta(atom(`_`) `if` true)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.no() //TODO permission_error
                ),
                solutions
            )
        }
    }
}