package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentNotUnify<T : WithAssertingEquals> : FromSequence<T>, SolverFactory {

    fun testNumberNotUnify() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = 1 notEqualsTo 1
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testNumberXNotUnify() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = "X" notEqualsTo 1
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testXYNotUnify() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = "X" notEqualsTo "Y"
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testDoubleNotUnify() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = (("X" notEqualsTo "Y") and ("X" notEqualsTo "abc"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testFDefNotUnify() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = "f"("X", "def") notEqualsTo ("f"("def", "Y"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testDiffNumberNotUnify() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = 1 notEqualsTo 2
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testDecNumberNotUnify() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = intOf(1) notEqualsTo realOf(1.0)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testGNotUnifyFX() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = ("g"("X")) notEqualsTo ("f"("a"("X")))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testFNotUnify() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = ("f"("X", 1)) notEqualsTo ("f"("a"("X")))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testFMultipleTermNotUnify() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = ("f"("X", "Y", "X")) notEqualsTo ("f"("a"("X"), "a"("Y"), "Y", 2))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }
}
