package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentNotProvable<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    val errorSignature: Signature

    fun testNPTrue() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = "not"(true)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testNPCut() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = "not"("!")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testNPCutFail() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = "not"(("!" and fail))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testOrNotCutFail() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = ((("X" eq 1) or ("X" eq 2)) and "not"(("!" and fail)))
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

    fun testNPEquals() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = "not"(4 eq 5)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testNPNum() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = "not"(3)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected =
                fromSequence(
                    query.halt(
                        TypeError.forGoal(
                            DummyInstances.executionContext,
                            errorSignature,
                            TypeError.Expected.CALLABLE,
                            numOf(3),
                        ),
                    ),
                )

            expected.assertingEquals(solutions)
        }
    }

    fun testNPX() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = "not"("X")
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
}
