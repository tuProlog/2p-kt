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

fun concurrentCallContract() =
    concurrentContract {
        case("call/call-cut") { call("!") yields { listOf(it.yes()) } }
        case("call/call-fail") { call(fail) yields { listOf(it.no()) } }
        case("call/call-fail-x") { call(fail and "X") yields { listOf(it.no()) } }
        case("call/call-fail-call") { call(fail and call(1)) yields { listOf(it.no()) } }

        scenario("call/call-write-x") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = call(write(3) and "X")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            InstantiationError.forGoal(
                                DummyInstances.executionContext,
                                errorSignature,
                                varOf("X"),
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("call/call-write-call") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = call(write(3) and call(1))
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

        scenario("call/call-x") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = call("X")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            InstantiationError.forGoal(
                                DummyInstances.executionContext,
                                errorSignature,
                                varOf("X"),
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("call/call-one") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = call(1)
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

        scenario("call/call-fail-one") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = call(fail and 1)
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            TypeError.forGoal(
                                DummyInstances.executionContext,
                                errorSignature,
                                TypeError.Expected.CALLABLE,
                                // solver returns 1
                                fail and 1,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("call/call-write-one") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = call(write(3) and 1)
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            TypeError.forGoal(
                                DummyInstances.executionContext,
                                errorSignature,
                                TypeError.Expected.CALLABLE,
                                // solver returns 1
                                write(3) and 1,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("call/call-true") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = call(1 or true)
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            TypeError.forGoal(
                                DummyInstances.executionContext,
                                errorSignature,
                                TypeError.Expected.CALLABLE,
                                // solver returns 1
                                (1 or true),
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }
    }

private val errorSignature = Signature("ensure_executable", 1)
