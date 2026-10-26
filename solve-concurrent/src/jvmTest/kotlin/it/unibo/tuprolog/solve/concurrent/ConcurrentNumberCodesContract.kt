package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentNumberCodesContract() =
    concurrentContract {
        case("number-codes/number-codes-list-is-var") {
            number_codes(33, "L") yields {
                listOf(it.yes("L" to logicListOf(51, 51)))
            }
        }

        case("number-codes/number-codes-num-is-decimal") {
            number_codes(33.1, "L") yields {
                listOf(it.yes("L" to logicListOf(51, 51, 46, 49)))
            }
        }

        case("number-codes/number-codes-list-is-var2") {
            number_codes(9921.1, "L") yields {
                listOf(it.yes("L" to logicListOf(57, 57, 50, 49, 46, 49)))
            }
        }

        case("number-codes/number-codes-ok") { number_codes(33, logicListOf(51, 51)) yields { listOf(it.yes()) } }

        case("number-codes/number-codes-complete-test") {
            number_codes(34, logicListOf(51, 52)) yields {
                listOf(it.yes())
            }
        }

        case("number-codes/number-codes-negative-number") {
            number_codes("X", logicListOf(45, 51, 46, 56)) yields {
                listOf(it.yes("X" to -3.8))
            }
        }

        scenario("number-codes/number-codes-char") {
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
