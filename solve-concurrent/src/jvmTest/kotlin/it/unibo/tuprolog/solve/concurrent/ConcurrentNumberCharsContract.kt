package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentNumberCharsContract() =
    concurrentContract {
        case("number-chars/number-chars-list-is-var") {
            number_chars(33, "L") yields {
                listOf(it.yes("L" to logicListOf("3", "3")))
            }
        }

        case("number-chars/number-chars-ok") { number_chars(33, logicListOf("3", "3")) yields { listOf(it.yes()) } }

        case("number-chars/number-chars-num-is-var") {
            number_chars("X", logicListOf("3", "3")) yields {
                listOf(it.yes("X" to 33))
            }
        }

        case("number-chars/number-chars-num-negative-is-var") {
            number_chars("X", logicListOf("-", "2", "5")) yields {
                listOf(it.yes("X" to intOf(-25)))
            }
        }

        case("number-chars/number-chars-space") {
            number_chars("X", logicListOf("\n", "3")) yields {
                listOf(it.yes("X" to 3))
            }
        }

        case("number-chars/number-chars-decimal-number") {
            number_chars("X", logicListOf("4", ".", "2")) yields {
                listOf(it.yes("X" to 4.2))
            }
        }

        scenario("number-chars/number-chars-complete-case") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()
                // val query = number_chars("X", listOf("3", ".", "3", "E", "+", "0"))
                val query = number_chars(X, logicListOf("3", ".", "9"))
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected = fromSequence(query.yes("X" to 3.9))

                expected.assertingEquals(solutions)
            }
        }

        scenario("number-chars/number-chars-instantiation-error") {
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
