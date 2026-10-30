package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

fun concurrentBigListContract() =
    concurrentContract {
        scenario("big-list/big-list-generation", runs = 1) {
            logicProgramming {
                val theory =
                    theoryOf(
                        fact { "biglist"(0, logicListOf(0)) },
                        rule {
                            "biglist"(N, consOf(N, X)).impliedBy(
                                N greaterThan 0,
                                M `is` (N - 1),
                                "biglist"(M, X),
                            )
                        },
                    )

                val solver = solverWithDefaultBuiltins(staticKb = theory)

                val query = "biglist"(BigListOptions.SIZE, L)
                val solutions = fromSequence(solver.solve(query, BIG_LIST_TIMEOUT))
                val expected =
                    fromSequence(
                        sequenceOf(
                            query.yes(
                                L to logicListOf((0..BigListOptions.SIZE).reversed().map { Integer.of(it) }),
                            ),
                            query.no(),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }
    }

// the long duration the concurrent suite used for big lists (4 * 2 * 6 s)
private const val BIG_LIST_TIMEOUT = 48_000L
