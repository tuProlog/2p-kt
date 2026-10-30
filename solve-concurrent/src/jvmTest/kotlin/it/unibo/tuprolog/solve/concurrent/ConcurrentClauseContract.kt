@file:Suppress("LongMethod") // a case table

package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.PermissionError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentClauseContract() =
    concurrentContract {
        case("clause/clause-x-body") { clause("x", "Body") yields { listOf(it.no()) } }

        scenario("clause/clause-any-b") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = clause(`_`, "B")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            InstantiationError.forArgument(
                                DummyInstances.executionContext,
                                Signature("clause", 2),
                                `_`,
                                index = 0,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)

                // assertSolutionEquals(
                //     kotlin.collections.listOf(
                //         query.halt(
                //             InstantiationError.forArgument(
                //                 DummyInstances.executionContext,
                //                 Signature("clause", 2),
                //                 `_`,
                //                 index = 0
                //             )
                //         )
                //     ),
                //     solutions
                // )
            }
        }

        scenario("clause/clause-num-b") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = clause(4, "B")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            TypeError.forArgument(
                                DummyInstances.executionContext,
                                Signature("clause", 2),
                                TypeError.Expected.CALLABLE,
                                numOf(4),
                                index = 0,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)

                // assertSolutionEquals(
                //     kotlin.collections.listOf(
                //         query.halt(
                //             TypeError.forArgument(
                //                 DummyInstances.executionContext,
                //                 Signature("clause", 2),
                //                 TypeError.Expected.CALLABLE,
                //                 numOf(4),
                //                 index = 0
                //             )
                //         )
                //     ),
                //     solutions
                // )
            }
        }

        scenario("clause/clause-f-any-num") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = clause("f"(`_`), 5)
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            TypeError.forArgument(
                                DummyInstances.executionContext,
                                Signature("clause", 2),
                                TypeError.Expected.CALLABLE,
                                numOf(5),
                                index = 1,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)

                // assertSolutionEquals(
                //     kotlin.collections.listOf(
                //         query.halt(
                //             TypeError.forArgument(
                //                 DummyInstances.executionContext,
                //                 Signature("clause", 2),
                //                 TypeError.Expected.CALLABLE,
                //                 numOf(5),
                //                 index = 1
                //             )
                //         )
                //     ),
                //     solutions
                // )
            }
        }

        scenario("clause/clause-atom-body") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = clause(atom(`_`), "Body")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            PermissionError.of(
                                DummyInstances.executionContext,
                                Signature("clause", 2),
                                PermissionError.Operation.ACCESS,
                                PermissionError.Permission.PRIVATE_PROCEDURE,
                                "atom" / 1,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)

                // assertSolutionEquals(
                //     kotlin.collections.listOf(
                //         query.halt(
                //             PermissionError.of(
                //                 DummyInstances.executionContext,
                //                 Signature("clause", 2),
                //                 PermissionError.Operation.ACCESS,
                //                 PermissionError.Permission.PRIVATE_PROCEDURE,
                //                 "atom" / 1
                //             )
                //         )
                //     ),
                //     solutions
                // )
            }
        }

        scenario("clause/clause-variables") {
            logicProgramming {
                val solver =
                    solverWithDefaultBuiltins(
                        staticKb =
                            theoryOf(
                                rule { "f"(X) impliedBy "g"(X) },
                            ),
                    )

                var query = clause("f"(A), B)
                var solutions = fromSequence(solver.solve(query, mediumDuration))
                var expected = fromSequence(query.yes(B to "g"(A)))

                expected.assertingEquals(solutions)

                query = clause("f"(1), Z)
                solutions = fromSequence(solver.solve(query, mediumDuration))
                expected = fromSequence(query.yes(Z to "g"(1)))

                expected.assertingEquals(solutions)
            }
        }
    }
