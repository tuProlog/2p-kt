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

interface TestConcurrentBagOf<T : WithAssertingEquals> : FromSequence<T>, SolverFactory {

    fun testBagXInDifferentValues() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = bagof("X", ("X" `=` 1) or ("X" `=` 2), "S")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("S" to listOf(1, 2)))

            expected.assertingEquals(solutions)
        }
    }

    fun testBagOfFindX() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = bagof("X", ("X" `=` 1) or ("X" `=` 2), "X")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to listOf(1, 2)))

            expected.assertingEquals(solutions)
        }
    }

    fun testBagOfYXZ() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = bagof("X", ("X" `=` "Y") or ("X" `=` "Z"), "L")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("L" to listOf("Y", "Z")))

            expected.assertingEquals(solutions)
        }
    }

    fun testBagOfFail() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = bagof("X", fail, "L")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testBagOfSameAsFindall() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = bagof("X", "Y" `^` (("X" `=` 1) or ("Y" `=` 2)), "S")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("S" to listOf(1)))

            expected.assertingEquals(solutions)
        }
    }

    fun testBagOfInstanceError() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = bagof("X", "G", "S")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(
                query.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("bagof", 3),
                        varOf("G"),
                        index = 1
                    )
                )
            )

            expected.assertingEquals(solutions)
        }
    }

    fun testBagOfTypeError() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = bagof("X", 1, "S")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(
                query.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("bagof", 3),
                        TypeError.Expected.CALLABLE,
                        numOf(1),
                        index = 1
                    )
                )
            )

            expected.assertingEquals(solutions)
        }
    }
}
