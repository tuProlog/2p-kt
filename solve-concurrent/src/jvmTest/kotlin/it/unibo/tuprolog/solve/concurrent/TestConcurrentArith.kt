package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentArith<T : WithAssertingEquals> : FromSequence<T>, SolverFactory {

    fun testArithDiff() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            var query = 0 `=!=` 1
            var solutions = fromSequence(solver.solve(query, mediumDuration))
            var expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = 1.0 `=!=` 1
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = (numOf(3) * 2) `=!=` (numOf(7) - 1)
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = "N" `=!=` 5
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(
                query.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("=\\=", 2),
                        varOf("N"),
                        index = 0
                    )
                )
            )

            expected.assertingEquals(solutions)

            // assertSolutionEquals(
            //     ktListOf(
            //         query.halt(
            //             InstantiationError.forArgument(
            //                 DummyInstances.executionContext,
            //                 Signature("=\\=", 2),
            //                 varOf("N"),
            //                 index = 0
            //             )
            //         )
            //     ),
            //     solutions
            // )

            query = "floot"(1) `=!=` 5
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(
                query.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("=\\=", 2),
                        TypeError.Expected.EVALUABLE,
                        "floot"(1),
                        index = 0
                    )
                )
            )

            expected.assertingEquals(solutions)

            // assertSolutionEquals(
            //     ktListOf(
            //         query.halt(
            //             TypeError.forArgument(
            //                 DummyInstances.executionContext,
            //                 Signature("=\\=", 2),
            //                 TypeError.Expected.EVALUABLE,
            //                 "floot"(1),
            //                 index = 0
            //             )
            //         )
            //     ),
            //     solutions
            // )
        }
    }

    fun testArithEq() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            var query = 0 `===` 1
            var solutions = fromSequence(solver.solve(query, mediumDuration))
            var expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = 1.0 `===` 1
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = (numOf(3) * 2) `===` (numOf(7) - 1)
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = "N" `===` 5
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(
                query.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("=:=", 2),
                        varOf("N"),
                        index = 0
                    )
                )
            )

            expected.assertingEquals(solutions)

            // assertSolutionEquals(
            //     ktListOf(
            //         query.halt(
            //             InstantiationError.forArgument(
            //                 DummyInstances.executionContext,
            //                 Signature("=:=", 2),
            //                 varOf("N"),
            //                 index = 0
            //             )
            //         )
            //     ),
            //     solutions
            // )

            query = "floot"(1) `===` 5
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(
                query.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("=:=", 2),
                        TypeError.Expected.EVALUABLE,
                        "floot"(1),
                        index = 0
                    )
                )
            )

            expected.assertingEquals(solutions)

            // assertSolutionEquals(
            //     ktListOf(
            //         query.halt(
            //             TypeError.forArgument(
            //                 DummyInstances.executionContext,
            //                 Signature("=:=", 2),
            //                 TypeError.Expected.EVALUABLE,
            //                 "floot"(1),
            //                 index = 0
            //             )
            //         )
            //     ),
            //     solutions
            // )

            query = 0.333 `===` (numOf(1) / 3)
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testArithGreaterThan() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            var query = 0 greaterThan 1
            var solutions = fromSequence(solver.solve(query, mediumDuration))
            var expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = 1.0 greaterThan 1
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = (numOf(3) * 2) greaterThan (numOf(7) - 1)
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = "X" greaterThan 5
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(
                query.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature(">", 2),
                        varOf("X"),
                        index = 0
                    )
                )
            )

            expected.assertingEquals(solutions)

            // assertSolutionEquals(
            //     ktListOf(
            //         query.halt(
            //             InstantiationError.forArgument(
            //                 DummyInstances.executionContext,
            //                 Signature(">", 2),
            //                 varOf("X"),
            //                 index = 0
            //             )
            //         )
            //     ),
            //     solutions
            // )

            query = (2 + "floot"(1)) greaterThan 5
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(
                query.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature(">", 2),
                        TypeError.Expected.EVALUABLE,
                        "floot"(1),
                        index = 0
                    )
                )
            )

            expected.assertingEquals(solutions)

            // assertSolutionEquals(
            //     ktListOf(
            //         query.halt(
            //             TypeError.forArgument(
            //                 DummyInstances.executionContext,
            //                 Signature(">", 2),
            //                 TypeError.Expected.EVALUABLE,
            //                 "floot"(1),
            //                 index = 0
            //             )
            //         )
            //     ),
            //     solutions
            // )
        }
    }

    fun testArithGreaterThanEq() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            var query = 0 greaterThanOrEqualsTo 1
            var solutions = fromSequence(solver.solve(query, mediumDuration))
            var expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = 1.0 greaterThanOrEqualsTo 1
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = (numOf(3) * 2) greaterThanOrEqualsTo (numOf(7) - 1)
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = "X" greaterThanOrEqualsTo 5
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(
                query.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature(">=", 2),
                        varOf("X"),
                        index = 0
                    )
                )
            )

            expected.assertingEquals(solutions)

            // assertSolutionEquals(
            //     ktListOf(
            //         query.halt(
            //             InstantiationError.forArgument(
            //                 DummyInstances.executionContext,
            //                 Signature(">=", 2),
            //                 varOf("X"),
            //                 index = 0
            //             )
            //         )
            //     ),
            //     solutions
            // )

            query = (2 + "floot"(1)) greaterThanOrEqualsTo 5
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(
                query.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature(">=", 2),
                        TypeError.Expected.EVALUABLE,
                        "floot"(1),
                        index = 0
                    )
                )
            )

            expected.assertingEquals(solutions)

            // assertSolutionEquals(
            //     ktListOf(
            //         query.halt(
            //             TypeError.forArgument(
            //                 DummyInstances.executionContext,
            //                 Signature(">=", 2),
            //                 TypeError.Expected.EVALUABLE,
            //                 "floot"(1),
            //                 index = 0
            //             )
            //         )
            //     ),
            //     solutions
            // )
        }
    }

    fun testArithLessThan() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            var query = 0 lowerThan 1
            var solutions = fromSequence(solver.solve(query, mediumDuration))
            var expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = 1.0 lowerThan 1
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = (numOf(3) * 2) lowerThan (numOf(7) - 1)
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = "X" lowerThan 5
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(
                query.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("<", 2),
                        varOf("X"),
                        index = 0
                    )
                )
            )

            expected.assertingEquals(solutions)

            // assertSolutionEquals(
            //     ktListOf(
            //         query.halt(
            //             InstantiationError.forArgument(
            //                 DummyInstances.executionContext,
            //                 Signature("<", 2),
            //                 varOf("X"),
            //                 index = 0
            //             )
            //         )
            //     ),
            //     solutions
            // )

            query = (2 + "floot"(1)) lowerThan 5
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(
                query.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("<", 2),
                        TypeError.Expected.EVALUABLE,
                        "floot"(1),
                        index = 0
                    )
                )
            )

            expected.assertingEquals(solutions)

            // assertSolutionEquals(
            //     ktListOf(
            //         query.halt(
            //             TypeError.forArgument(
            //                 DummyInstances.executionContext,
            //                 Signature("<", 2),
            //                 TypeError.Expected.EVALUABLE,
            //                 "floot"(1),
            //                 index = 0
            //             )
            //         )
            //     ),
            //     solutions
            // )
        }
    }

    fun testArithLessThanEq() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            var query = 0 lowerThanOrEqualsTo 1
            var solutions = fromSequence(solver.solve(query, mediumDuration))
            var expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = 1.0 lowerThanOrEqualsTo 1
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = (numOf(3) * 2) lowerThanOrEqualsTo (numOf(7) - 1)
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = "X" lowerThanOrEqualsTo 5
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(
                query.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("=<", 2),
                        varOf("X"),
                        index = 0
                    )
                )
            )

            expected.assertingEquals(solutions)

            // assertSolutionEquals(
            //     ktListOf(
            //         query.halt(
            //             InstantiationError.forArgument(
            //                 DummyInstances.executionContext,
            //                 Signature("=<", 2),
            //                 varOf("X"),
            //                 index = 0
            //             )
            //         )
            //     ),
            //     solutions
            // )

            query = (2 + "floot"(1)) lowerThanOrEqualsTo 5
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(
                query.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("=<", 2),
                        TypeError.Expected.EVALUABLE,
                        "floot"(1),
                        index = 0
                    )
                )
            )

            expected.assertingEquals(solutions)

            // assertSolutionEquals(
            //     ktListOf(
            //         query.halt(
            //             TypeError.forArgument(
            //                 DummyInstances.executionContext,
            //                 Signature("=<", 2),
            //                 TypeError.Expected.EVALUABLE,
            //                 "floot"(1),
            //                 index = 0
            //             )
            //         )
            //     ),
            //     solutions
            // )
        }
    }
}
