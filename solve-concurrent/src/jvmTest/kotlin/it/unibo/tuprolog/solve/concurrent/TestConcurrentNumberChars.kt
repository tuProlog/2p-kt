package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.yes

interface TestConcurrentNumberChars<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    fun testNumberCharsListIsVar() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = number_chars(33, "L")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("L" to logicListOf("3", "3")))

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberCharsOK() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = number_chars(33, logicListOf("3", "3"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberCharsNumIsVar() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = number_chars("X", logicListOf("3", "3"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to 33))

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberCharsNumNegativeIsVar() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = number_chars("X", logicListOf("-", "2", "5"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to intOf(-25)))

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberCharsSpace() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = number_chars("X", logicListOf("\n", "3"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to 3))

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberCharsDecimalNumber() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = number_chars("X", logicListOf("4", ".", "2"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to 4.2))

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberCharsCompleteCase() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()
            // val query = number_chars("X", listOf("3", ".", "3", "E", "+", "0"))
            val query = number_chars(X, logicListOf("3", ".", "9"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to 3.9))

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberCharsInstantiationError() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = number_chars("X", "L")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected =
                fromSequence(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("number_chars", 2),
                            varOf("X"),
                            index = 0,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)
        }
    }
}
