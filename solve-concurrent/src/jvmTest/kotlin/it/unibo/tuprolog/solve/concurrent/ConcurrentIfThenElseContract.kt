package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentIfThenElseContract() =
    concurrentContract(
        disabled =
            setOf(
                // nondeterministic order, this test may pass or fail unpredictably. It's the Schrödinger's test
                "if-then-else/if-or-else-true",
            ),
    ) {
        case("if-then-else/if-true-else-fail") { ";"("->"(true, true), fail) yields { listOf(it.yes()) } }
        case("if-then-else/if-fail-else-true") { ";"("->"(fail, true), true) yields { listOf(it.yes()) } }
        case("if-then-else/if-true-then-else-fail") { ";"("->"(true, fail), fail) yields { listOf(it.no()) } }
        case("if-then-else/if-fail-else-fail") { ";"("->"(fail, true), fail) yields { listOf(it.no()) } }

        case("if-then-else/if-x-true-else-x") {
            ";"("->"(true, ("X" eq 1)), "X" eq 2) yields {
                listOf(it.yes("X" to 1))
            }
        }

        case("if-then-else/if-fail-else-x") {
            ";"("->"(fail, ("X" eq 1)), "X" eq 2) yields {
                listOf(it.yes("X" to 2))
            }
        }

        scenario("if-then-else/if-then-else-or-with-double-sub") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = ";"("->"(true, ("X" eq 1) or ("X" eq 2)), true)
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        sequenceOf(
                            query.yes("X" to 1),
                            query.yes("X" to 2),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        case("if-then-else/if-or-else-true") {
            ";"("->"(("X" eq 1) or ("X" eq 2), true), true) yields {
                listOf(it.yes("X" to 1))
            }
        }
    }
