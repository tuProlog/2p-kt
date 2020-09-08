package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError

internal class TestIsImpl(private val solverFactory: SolverFactory) : TestIs {
    override fun testIsResult() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "Result" `is` (numOf(3) + realOf(11.0))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("Result" to realOf(14.0))),
                solutions
            )
        }
    }

    override fun testIsXY() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ("X" `=` (intOf(1) + intOf(2))) and ("Y" `is` ("X" * intOf(3)))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to intOf(1) + intOf(2), "Y" to intOf(9))),
                solutions
            )
        }
    }

    override fun testIsFoo() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "foo" `is` intOf(77)
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

            val query = intOf(77) `is` "N"
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("is", 2),
                            varOf("N"),
                            index = 1
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

            val query = intOf(77) `is` "foo"
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("is", 2),
                            TypeError.Expected.EVALUABLE,
                            atomOf("foo"),
                            index = 1
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

            val query = "X" `is` float(intOf(3))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to realOf(3.0))),
                solutions
            )
        }
    }
}