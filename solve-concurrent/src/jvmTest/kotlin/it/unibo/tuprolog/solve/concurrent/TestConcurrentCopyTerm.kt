package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentCopyTerm<T : WithAssertingEquals> : FromSequence<T>, SolverFactory {

    fun testCopyXNum() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = copy_term(X, 3)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testCopyAnyA() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = copy_term(`_`, "a")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testCopySum() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = copy_term(atomOf("a") + X, X + "b")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes(X to "a"))

            expected.assertingEquals(solutions)
        }
    }

    fun testCopyAnyAny() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = copy_term(`_`, `_`)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testCopyTripleSum() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = copy_term(X + X + Y, A + B + B)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes(A to B))

            expected.assertingEquals(solutions)
        }
    }

    fun testCopyAA() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = copy_term("a", "a")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testCopyAB() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = copy_term("a", "b")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testCopyF() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = copy_term("f"("a"), "f"(X))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes(X to "a"))

            expected.assertingEquals(solutions)
        }
    }

    fun testDoubleCopy() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = copy_term(atom("a") + X, X + "b") and copy_term(atom("a") + X, X + "b")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }
}
