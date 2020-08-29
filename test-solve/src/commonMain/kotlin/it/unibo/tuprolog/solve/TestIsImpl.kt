package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError

internal class TestIsImpl(private val solverFactory: SolverFactory) : TestIs {
    override fun testIsResult() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "Result" `is` numOf(3) + 11.0
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("Result" to 14.0)),
                solutions
            )
        }
    }

    override fun testIsXY() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ("X" `=` numOf(1) + 2) and ("Y" `is` "X" * 3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to numOf(1)+2, "Y" to 9)),
                solutions
            )
        }
    }

    override fun testIsFoo() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "foo" `is` 77
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }

    override fun testIsNNumber() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = 77 `is` "N"
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        InstantiationError.forGoal(
                            DummyInstances.executionContext,
                            Signature("is", 2),
                            varOf("N")
                        )
                    )
                ),
                solutions
            )
        }
    }

    override fun testIsNumberFoo() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = 77 `is` "foo"
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        TypeError.forGoal(
                            DummyInstances.executionContext,
                            Signature("is", 2),
                            TypeError.Expected.EVALUABLE,
                            atomOf("foo")

                        )
                    )
                ),
                solutions
            )
        }
    }

    override fun testIsXFloat() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "X" `is` float(3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to 3.0)),
                solutions
            )
        }
    }
}