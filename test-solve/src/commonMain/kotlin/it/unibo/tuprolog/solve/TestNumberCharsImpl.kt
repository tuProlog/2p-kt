package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.error.InstantiationError

class TestNumberCharsImpl(private val solverFactory: SolverFactory) : TestNumberChars {
    override fun testNumberCharsListIsVar() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number_chars(33, "L")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("L" to listOf("3", "3"))),
                solutions
            )
        }
    }

    override fun testNumberCharsOK() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number_chars(33, listOf("3", "3"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions
            )
        }
    }

    override fun testNumberCharsNumIsVar() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number_chars("X", listOf("3", "3"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to 33)),
                solutions
            )
        }
    }

    override fun testNumberCharsNumNegativeIsVar() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number_chars("X", listOf("-", "2", "5"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to intOf(-25))),
                solutions
            )
        }
    }

    override fun testNumberCharsSpace() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number_chars("X", listOf("\n", "3"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to 3)),
                solutions
            )
        }
    }

    override fun testNumberCharsDecimalNumber() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number_chars("X", listOf("4", ".", "2"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to 4.2)),
                solutions
            )
        }
    }

    override fun testNumberCharsCompleteCase() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            // val query = number_chars("X", listOf("3", ".", "3", "E", "+", "0"))
            val query = number_chars(X, listOf("3", ".", "9"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to 3.9)),
                solutions
            )
        }
    }

    override fun testNumberCharsInstationErrror() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number_chars("X", "L")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("number_chars", 2),
                            varOf("X"),
                            index = 0
                        )
                    )
                ),
                solutions
            )
        }
    }
}
