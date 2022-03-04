package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.yes

interface TestConcurrentNumberCodes<T : WithAssertingEquals> : FromSequence<T>, SolverFactory {

    fun testNumberCodesListIsVar() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = number_codes(33, "L")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("L" to listOf(51, 51)))

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberCodesNumIsDecimal() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = number_codes(33.1, "L")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("L" to listOf(51, 51, 46, 49)))

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberCodesListIsVar2() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = number_codes(9921.1, "L")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("L" to listOf(57, 57, 50, 49, 46, 49)))

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberCodesOk() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = number_codes(33, listOf(51, 51))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberCodesCompleteTest() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = number_codes(34, listOf(51, 52))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberCodesNegativeNumber() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = number_codes("X", listOf(45, 51, 46, 56))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to -3.8))

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberCodesChar() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = number_codes("a", "L")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(
                query.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("number_codes", 2),
                        TypeError.Expected.NUMBER,
                        atomOf("a"),
                        index = 0
                    )
                )
            )

            expected.assertingEquals(solutions)
        }
    }
}
