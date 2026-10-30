@file:Suppress("LongMethod") // a case table

package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentArgContract() =
    concurrentContract {
        case("arg/arg-from-foo") { arg(1, "foo"("a", "b"), "a") yields { listOf(it.yes()) } }
        case("arg/arg-from-foo-x") { arg(1, "foo"("a", "b"), "X") yields { listOf(it.yes("X" to "a")) } }
        case("arg/arg-from-foo2") { arg(1, "foo"("X", "b"), "a") yields { listOf(it.yes("X" to "a")) } }

        case("arg/arg-from-foo-in-f") {
            arg(2, "foo"("a", "f"("X", "b"), "c"), "f"("a", "Y")) yields {
                listOf(it.yes("X" to "a", "Y" to "b"))
            }
        }

        case("arg/arg-from-foo-y") { arg(1, "foo"("X", "b"), "Y") yields { listOf(it.yes("Y" to "X")) } }
        case("arg/arg-from-foo-in-second-term") { arg(1, "foo"("a", "b"), "b") yields { listOf(it.no()) } }
        case("arg/arg-from-foo-in-foo") { arg(1, "foo"("a", "b"), "foo") yields { listOf(it.no()) } }
        case("arg/arg-number-from-foo") { arg(3, "foo"(3, 4), "N") yields { listOf(it.no()) } }
        case("arg/arg-x-from-foo") { arg("X", "foo"("a", "b"), "a") yields { listOf(it.yes("X" to 1)) } }

        scenario("arg/arg-number-from-x") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = arg(1, "X", "a")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            InstantiationError.forArgument(
                                DummyInstances.executionContext,
                                Signature("arg", 3),
                                varOf("X"),
                                index = 1,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("arg/arg-from-atom") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = arg(0, "atom", "A")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            TypeError.forArgument(
                                DummyInstances.executionContext,
                                Signature("arg", 3),
                                TypeError.Expected.COMPOUND,
                                atomOf("atom"),
                                index = 1,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("arg/arg-from-number") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = arg(0, 3, "A")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            TypeError.forArgument(
                                DummyInstances.executionContext,
                                Signature("arg", 3),
                                TypeError.Expected.COMPOUND,
                                numOf(3),
                                index = 1,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("arg/negative-arg-from-foo") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = arg(intOf(-3), "foo"("a", "b"), "A")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            DomainError.forArgument(
                                DummyInstances.executionContext,
                                Signature("arg", 3),
                                DomainError.Expected.NOT_LESS_THAN_ZERO,
                                numOf(-3),
                                index = 0,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("arg/arg-a-from-foo") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = arg("a", "foo"("a", "b"), "X")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            TypeError.forArgument(
                                DummyInstances.executionContext,
                                Signature("arg", 3),
                                TypeError.Expected.INTEGER,
                                atomOf("a"),
                                index = 0,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }
    }
