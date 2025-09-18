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

interface TestConcurrentBagOf<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    fun testBagXInDifferentValues() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = bagof("X", ("X" eq 1) or ("X" eq 2), "S")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("S" to logicListOf(1, 2)))

            expected.assertingEquals(solutions)
        }
    }

    fun testBagOfFindX() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = bagof("X", ("X" eq 1) or ("X" eq 2), "X")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to logicListOf(1, 2)))

            expected.assertingEquals(solutions)
        }
    }

    fun testBagOfYXZ() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = bagof("X", ("X" eq "Y") or ("X" eq "Z"), "L")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("L" to logicListOf("Y", "Z")))

            expected.assertingEquals(solutions)
        }
    }

    fun testBagOfFail() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = bagof("X", fail, "L")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testBagOfSameAsFindall() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = bagof("X", "Y" sup (("X" eq 1) or ("Y" eq 2)), "S")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("S" to logicListOf(1)))

            expected.assertingEquals(solutions)
        }
    }

    fun testBagOfInstanceError() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = bagof("X", "G", "S")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected =
                fromSequence(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("bagof", 3),
                            varOf("G"),
                            index = 1,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)
        }
    }

    fun testBagOfTypeError() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = bagof("X", 1, "S")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected =
                fromSequence(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("bagof", 3),
                            TypeError.Expected.CALLABLE,
                            numOf(1),
                            index = 1,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)
        }
    }
}
