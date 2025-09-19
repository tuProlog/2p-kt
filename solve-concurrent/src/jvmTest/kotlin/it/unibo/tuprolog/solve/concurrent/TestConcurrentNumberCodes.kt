package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.yes

interface TestConcurrentNumberCodes<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    fun testNumberCodesListIsVar() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = number_codes(33, "L")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("L" to logicListOf(51, 51)))

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberCodesNumIsDecimal() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = number_codes(33.1, "L")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("L" to logicListOf(51, 51, 46, 49)))

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberCodesListIsVar2() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = number_codes(9921.1, "L")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("L" to logicListOf(57, 57, 50, 49, 46, 49)))

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberCodesOk() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = number_codes(33, logicListOf(51, 51))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberCodesCompleteTest() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = number_codes(34, logicListOf(51, 52))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberCodesNegativeNumber() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = number_codes("X", logicListOf(45, 51, 46, 56))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to -3.8))

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberCodesChar() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = number_codes("a", "L")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected =
                fromSequence(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("number_codes", 2),
                            TypeError.Expected.NUMBER,
                            atomOf("a"),
                            index = 0,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)
        }
    }
}
