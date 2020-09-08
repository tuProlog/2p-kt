package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.exception.error.TypeError.Expected.Companion.EVALUABLE
import kotlin.collections.listOf as ktListOf

internal class TestArithImpl(private val solverFactory: SolverFactory) : TestArith {

    override fun testArithDiff() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = 0 `=!=` 1
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )

            query = 1.0 `=!=` 1
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )

            query = (numOf(3) * 2) `=!=` (numOf(7) - 1)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )

            query = "N" `=!=` 5
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("=\\=", 2),
                            varOf("N"),
                            index = 0
                        )
                    )
                ),
                solutions
            )

            query = "floot"(1) `=!=` 5
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("=\\=", 2),
                            EVALUABLE,
                            "floot"(1),
                            index = 0
                        )
                    )
                ),
                solutions
            )
        }
    }

    override fun testArithEq() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = 0 `===` 1
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )

            query = 1.0 `===` 1
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )

            query = (numOf(3) * 2) `===` (numOf(7) - 1)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )

            query = "N" `===` 5
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("=:=", 2),
                            varOf("N"),
                            index = 0
                        )
                    )
                ),
                solutions
            )

            query = "floot"(1) `===` 5
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("=:=", 2),
                            EVALUABLE,
                            "floot"(1),
                            index = 0
                        )
                    )
                ),
                solutions
            )

            query = 0.333 `===` (numOf(1) / 3)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )
        }
    }

    override fun testArithGreaterThan() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = 0 greaterThan 1
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )

            query = 1.0 greaterThan 1
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )

            query = (numOf(3) * 2) greaterThan (numOf(7) - 1)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )

            query = "X" greaterThan 5
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature(">", 2),
                            varOf("X"),
                            index = 0
                        )
                    )
                ),
                solutions
            )

            query = (2 + "floot"(1)) greaterThan 5
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature(">", 2),
                            EVALUABLE,
                            "floot"(1),
                            index = 0
                        )
                    )
                ),
                solutions
            )
        }
    }

    override fun testArithGreaterThanEq() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = 0 greaterThanOrEqualsTo 1
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )

            query = 1.0 greaterThanOrEqualsTo 1
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )

            query = (numOf(3) * 2) greaterThanOrEqualsTo (numOf(7) - 1)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )

            query = "X" greaterThanOrEqualsTo 5
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature(">=", 2),
                            varOf("X"),
                            index = 0
                        )
                    )
                ),
                solutions
            )

            query = (2 + "floot"(1)) greaterThanOrEqualsTo 5
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature(">=", 2),
                            EVALUABLE,
                            "floot"(1),
                            index = 0
                        )
                    )
                ),
                solutions
            )
        }
    }

    override fun testArithLessThan() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = 0 lowerThan 1
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )

            query = 1.0 lowerThan 1
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )

            query = (numOf(3) * 2) lowerThan (numOf(7) - 1)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )

            query = "X" lowerThan 5
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("<", 2),
                            varOf("X"),
                            index = 0
                        )
                    )
                ),
                solutions
            )

            query = (2 + "floot"(1)) lowerThan 5
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("<", 2),
                            EVALUABLE,
                            "floot"(1),
                            index = 0
                        )
                    )
                ),
                solutions
            )
        }
    }

    override fun testArithLessThanEq() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = 0 lowerThanOrEqualsTo 1
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )

            query = 1.0 lowerThanOrEqualsTo 1
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )

            query = (numOf(3) * 2) lowerThanOrEqualsTo (numOf(7) - 1)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )

            query = "X" lowerThanOrEqualsTo 5
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("=<", 2),
                            varOf("X"),
                            index = 0
                        )
                    )
                ),
                solutions
            )

            query = (2 + "floot"(1)) lowerThanOrEqualsTo 5
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("=<", 2),
                            EVALUABLE,
                            "floot"(1),
                            index = 0
                        )
                    )
                ),
                solutions
            )
        }
    }
}