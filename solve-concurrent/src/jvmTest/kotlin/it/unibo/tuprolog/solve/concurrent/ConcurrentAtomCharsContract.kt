@file:Suppress("LongMethod") // a case table

package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentAtomCharsContract() =
    concurrentContract {
        case("atom-chars/atom-chars-test-first-is-var") {
            atom_chars("X", logicListOf("t", "e", "s", "t")) yields {
                listOf(it.yes("X" to "test"))
            }
        }

        case("atom-chars/atom-chars-test-yes") {
            atom_chars("test", logicListOf("t", "e", "s", "t")) yields {
                listOf(it.yes())
            }
        }

        case("atom-chars/atom-chars-test-one-char-is-var") {
            atom_chars("test", logicListOf("t", "e", "s", "T")) yields {
                listOf(it.yes(("T" to "t")))
            }
        }

        case("atom-chars/atom-chars-test-failure") {
            atom_chars("test1", logicListOf("t", "e", "s", "T")) yields {
                listOf(it.no())
            }
        }

        case("atom-chars/atom-chars-test-empty") {
            atom_chars("", "L") yields {
                listOf(it.yes("L" to emptyLogicList))
            }
        }

        case("atom-chars/atom-chars-test-list-head") {
            atom_chars("ac", logicListOf("a", "C")) yields {
                listOf(it.yes("C" to "c"))
            }
        }

        scenario("atom-chars/atom-chars-test-istantation-error-check") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()
                val query = atom_chars("A", "L")

                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            InstantiationError.forArgument(
                                DummyInstances.executionContext,
                                Signature("atom_chars", 2),
                                varOf("A"),
                                index = 0,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("atom-chars/atom-chars-test-type-error-check") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()
                val query = atom_chars("A", "iso")

                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            TypeError.forArgument(
                                DummyInstances.executionContext,
                                Signature("atom_chars", 2),
                                TypeError.Expected.LIST,
                                atomOf("iso"),
                                index = 1,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }
    }
