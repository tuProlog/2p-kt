package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentIfThenContract() =
    concurrentContract(
        disabled =
            setOf(
                "if-then/if-then-x-or", // todo implement specific rule -> (arrow) for concurrent solver
            ),
    ) {
        case("if-then/if-then-true") { "->"(true, true) yields { listOf(it.yes()) } }
        case("if-then/if-then-fail") { "->"(true, fail) yields { listOf(it.no()) } }
        case("if-then/if-then-fail-true") { "->"(fail, true) yields { listOf(it.no()) } }
        case("if-then/if-then-xto-one") { "->"(true, "X" eq 1) yields { listOf(it.yes("X" to 1)) } }
        case("if-then/if-then-x-or") { "->"(("X" eq 1) or ("X" eq 2), true) yields { listOf(it.yes("X" to 1)) } }

        scenario("if-then/if-then-or-with-double-sub") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = "->"(true, ("X" eq 1) or ("X" eq 2))
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
    }
