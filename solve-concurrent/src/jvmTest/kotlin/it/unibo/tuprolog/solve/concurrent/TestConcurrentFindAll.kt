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

interface TestConcurrentFindAll<T : WithAssertingEquals> : FromSequence<T>, SolverFactory {

    val errorSignature: Signature

    fun testFindXInDiffValues() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = findall("X", ("X" eq 1) or ("X" eq 2), "S")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("S" to listOf(1, 2)))

            expected.assertingEquals(solutions)
        }
    }

    fun testFindSumResult() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = findall("+"("X", "Y"), "X" eq 1, "S")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("S" to listOf(1 + "Y")))

            expected.assertingEquals(solutions)
        }
    }

    fun testFindXinFail() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = findall("X", fail, "L")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("L" to emptyList))

            expected.assertingEquals(solutions)
        }
    }

    fun testFindXinSameXValues() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = findall("X", ("X" eq 1) or ("X" eq 1), "S")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("S" to listOf(1, 1)))

            expected.assertingEquals(solutions)
        }
    }

    fun testResultListIsCorrect() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = findall("X", ("X" eq 2) or ("X" eq 1), listOf(1, 2))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testFindXtoDoubleAssignment() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = findall("X", ("X" eq 1) or ("X" eq 2), listOf("X", "Y"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to 1, "Y" to 2))

            expected.assertingEquals(solutions)
        }
    }

    fun testFindXinGoal() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = findall("X", "Goal", "S")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(
                query.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("findall", 3),
                        varOf("Goal"),
                        index = 1
                    )
                )
            )

            expected.assertingEquals(solutions)
        }
    }

    fun testFindXinNumber() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = findall("X", 4, "S")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(
                query.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("findall", 3),
                        TypeError.Expected.CALLABLE,
                        numOf(4),
                        index = 1
                    )
                )
            )

            expected.assertingEquals(solutions)
        }
    }

    fun testFindXinCall() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = findall("X", call(1), "S")
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
}
