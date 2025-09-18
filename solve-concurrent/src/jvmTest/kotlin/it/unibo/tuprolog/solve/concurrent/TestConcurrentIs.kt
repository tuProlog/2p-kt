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

interface TestConcurrentIs<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    fun testIsResult() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = "Result" `is` (numOf(3) + realOf(11.0))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("Result" to realOf(14.0)))

            expected.assertingEquals(solutions)
        }
    }

    fun testIsXY() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = ("X" eq (intOf(1) + intOf(2))) and ("Y" `is` ("X" * intOf(3)))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to intOf(1) + intOf(2), "Y" to intOf(9)))

            expected.assertingEquals(solutions)
        }
    }

    fun testIsFoo() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = "foo" `is` intOf(77)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testIsNNumber() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = intOf(77) `is` "N"
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected =
                fromSequence(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("is", 2),
                            varOf("N"),
                            index = 1,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)
        }
    }

    fun testIsNumberFoo() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = intOf(77) `is` "foo"
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected =
                fromSequence(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("is", 2),
                            TypeError.Expected.EVALUABLE,
                            atomOf("foo"),
                            index = 1,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)
        }
    }

    fun testIsXFloat() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = "X" `is` float(intOf(3))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to realOf(3.0)))

            expected.assertingEquals(solutions)
        }
    }
}
