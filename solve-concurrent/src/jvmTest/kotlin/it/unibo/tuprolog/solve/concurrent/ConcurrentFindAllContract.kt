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

fun concurrentFindAllContract() =
    concurrentContract(
        disabled =
            setOf(
                // nondeterministic order, this test may pass or fail unpredictably. It's the Schrödinger's test
                "find-all/result-list-is-correct",
            ),
    ) {
        case("find-all/find-x-in-diff-values") {
            findall("X", ("X" eq 1) or ("X" eq 2), "S") yields {
                listOf(it.yes("S" to logicListOf(1, 2)))
            }
        }

        case("find-all/find-sum-result") {
            findall("+"("X", "Y"), "X" eq 1, "S") yields {
                listOf(it.yes("S" to logicListOf(1 + "Y")))
            }
        }

        case("find-all/find-xin-fail") { findall("X", fail, "L") yields { listOf(it.yes("L" to emptyLogicList)) } }

        case("find-all/find-xin-same-x-values") {
            findall("X", ("X" eq 1) or ("X" eq 1), "S") yields {
                listOf(it.yes("S" to logicListOf(1, 1)))
            }
        }

        case("find-all/result-list-is-correct") {
            findall("X", ("X" eq 2) or ("X" eq 1), logicListOf(1, 2)) yields {
                listOf(it.no())
            }
        }

        case("find-all/find-xto-double-assignment") {
            findall("X", ("X" eq 1) or ("X" eq 2), logicListOf("X", "Y")) yields {
                listOf(it.yes("X" to 1, "Y" to 2))
            }
        }

        scenario("find-all/find-xin-goal") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = findall("X", "Goal", "S")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            InstantiationError.forArgument(
                                DummyInstances.executionContext,
                                Signature("findall", 3),
                                varOf("Goal"),
                                index = 1,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("find-all/find-xin-number") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = findall("X", 4, "S")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            TypeError.forArgument(
                                DummyInstances.executionContext,
                                Signature("findall", 3),
                                TypeError.Expected.CALLABLE,
                                numOf(4),
                                index = 1,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("find-all/find-xin-call") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = findall("X", call(1), "S")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            TypeError.forGoal(
                                DummyInstances.executionContext,
                                errorSignature,
                                TypeError.Expected.CALLABLE,
                                numOf(1),
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }
    }

private val errorSignature = Signature("ensure_executable", 1)
