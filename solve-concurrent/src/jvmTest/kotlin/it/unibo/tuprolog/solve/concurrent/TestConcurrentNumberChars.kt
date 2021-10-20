package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.yes

interface TestConcurrentNumberChars<T : WithAssertingEquals> : FromSequence<T>, SolverFactory {

    fun testNumberCharsListIsVar() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = number_chars(33, "L")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("L" to listOf("3", "3")))

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberCharsOK() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = number_chars(33, listOf("3", "3"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberCharsNumIsVar() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = number_chars("X", listOf("3", "3"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to 33))

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberCharsNumNegativeIsVar() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = number_chars("X", listOf("-", "2", "5"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to intOf(-25)))

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberCharsSpace() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = number_chars("X", listOf("\n", "3"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to 3))

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberCharsDecimalNumber() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = number_chars("X", listOf("4", ".", "2"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to 4.2))

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberCharsCompleteCase() {
        prolog {
            val solver = solverWithDefaultBuiltins()
            // val query = number_chars("X", listOf("3", ".", "3", "E", "+", "0"))
            val query = number_chars(X, listOf("3", ".", "9"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to 3.9))

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberCharsInstantiationError() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = number_chars("X", "L")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(
                query.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("number_chars", 2),
                        varOf("X"),
                        index = 0
                    )
                )
            )

            expected.assertingEquals(solutions)
        }
    }
}
