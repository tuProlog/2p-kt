package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.exception.error.TypeError.Expected.EVALUABLE

internal class TestArithImpl(
    private val solverFactory: SolverFactory,
) : TestArith {
    override fun testArithDiff() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = 0 arithNeq 1
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )

            query = 1.0 arithNeq 1
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )

            query = (numOf(3) * 2) arithNeq (numOf(7) - 1)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )

            query = "N" arithNeq 5
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("=\\=", 2),
                            varOf("N"),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )

            query = "floot"(1) arithNeq 5
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("=\\=", 2),
                            EVALUABLE,
                            "floot"(1),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testArithEq() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = 0 arithEq 1
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )

            query = 1.0 arithEq 1
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )

            query = (numOf(3) * 2) arithEq (numOf(7) - 1)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )

            query = "N" arithEq 5
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("=:=", 2),
                            varOf("N"),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )

            query = "floot"(1) arithEq 5
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("=:=", 2),
                            EVALUABLE,
                            "floot"(1),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )

            query = 0.333 arithEq (numOf(1) / 3)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testArithGreaterThan() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = 0 greaterThan 1
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )

            query = 1.0 greaterThan 1
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )

            query = (numOf(3) * 2) greaterThan (numOf(7) - 1)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )

            query = "X" greaterThan 5
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature(">", 2),
                            varOf("X"),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )

            query = (2 + "floot"(1)) greaterThan 5
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature(">", 2),
                            EVALUABLE,
                            "floot"(1),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testArithGreaterThanEq() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = 0 greaterThanOrEqualsTo 1
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )

            query = 1.0 greaterThanOrEqualsTo 1
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )

            query = (numOf(3) * 2) greaterThanOrEqualsTo (numOf(7) - 1)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )

            query = "X" greaterThanOrEqualsTo 5
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature(">=", 2),
                            varOf("X"),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )

            query = (2 + "floot"(1)) greaterThanOrEqualsTo 5
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature(">=", 2),
                            EVALUABLE,
                            "floot"(1),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testArithLessThan() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = 0 lowerThan 1
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )

            query = 1.0 lowerThan 1
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )

            query = (numOf(3) * 2) lowerThan (numOf(7) - 1)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )

            query = "X" lowerThan 5
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("<", 2),
                            varOf("X"),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )

            query = (2 + "floot"(1)) lowerThan 5
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("<", 2),
                            EVALUABLE,
                            "floot"(1),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testArithLessThanEq() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = 0 lowerThanOrEqualsTo 1
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )

            query = 1.0 lowerThanOrEqualsTo 1
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )

            query = (numOf(3) * 2) lowerThanOrEqualsTo (numOf(7) - 1)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )

            query = "X" lowerThanOrEqualsTo 5
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("=<", 2),
                            varOf("X"),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )

            query = (2 + "floot"(1)) lowerThanOrEqualsTo 5
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("=<", 2),
                            EVALUABLE,
                            "floot"(1),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }
}
