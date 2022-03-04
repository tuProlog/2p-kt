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

interface TestConcurrentCall<T : WithAssertingEquals> : FromSequence<T>, SolverFactory {

    val errorSignature: Signature

    fun testCallCut() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = call("!")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testCallFail() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = call(fail)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testCallFailX() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = call(fail and "X")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testCallFailCall() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = call(fail and call(1))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testCallWriteX() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = call(write(3) and "X")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(
                query.halt(
                    InstantiationError.forGoal(
                        DummyInstances.executionContext,
                        errorSignature,
                        varOf("X")
                    )
                )
            )

            expected.assertingEquals(solutions)
        }
    }

    fun testCallWriteCall() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = call(write(3) and call(1))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(
                query.halt(
                    TypeError.forGoal(
                        DummyInstances.executionContext,
                        errorSignature,
                        TypeError.Expected.CALLABLE,
                        numOf(1)
                    )
                )
            )

            expected.assertingEquals(solutions)
        }
    }

    fun testCallX() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = call("X")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(
                query.halt(
                    InstantiationError.forGoal(
                        DummyInstances.executionContext,
                        errorSignature,
                        varOf("X")
                    )
                )
            )

            expected.assertingEquals(solutions)
        }
    }

    fun testCallOne() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = call(1)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(
                query.halt(
                    TypeError.forGoal(
                        DummyInstances.executionContext,
                        errorSignature,
                        TypeError.Expected.CALLABLE,
                        numOf(1)
                    )
                )
            )

            expected.assertingEquals(solutions)
        }
    }

    fun testCallFailOne() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = call(fail and 1)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(
                query.halt(
                    TypeError.forGoal(
                        DummyInstances.executionContext,
                        errorSignature,
                        TypeError.Expected.CALLABLE,
                        fail and 1 // solver returns 1
                    )
                )
            )

            expected.assertingEquals(solutions)
        }
    }

    fun testCallWriteOne() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = call(write(3) and 1)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(
                query.halt(
                    TypeError.forGoal(
                        DummyInstances.executionContext,
                        errorSignature,
                        TypeError.Expected.CALLABLE,
                        write(3) and 1 // solver returns 1
                    )
                )
            )

            expected.assertingEquals(solutions)
        }
    }

    fun testCallTrue() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = call(1 or true)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(
                query.halt(
                    TypeError.forGoal(
                        DummyInstances.executionContext,
                        errorSignature,
                        TypeError.Expected.CALLABLE,
                        (1 or true) // solver returns 1
                    )
                )
            )

            expected.assertingEquals(solutions)
        }
    }
}
