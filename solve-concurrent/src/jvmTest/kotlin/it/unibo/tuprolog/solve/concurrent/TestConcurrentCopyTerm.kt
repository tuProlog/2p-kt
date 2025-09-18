package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentCopyTerm<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    fun testCopyXNum() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = copy_term(X, 3)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testCopyAnyA() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = copy_term(`_`, "a")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testCopySum() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = copy_term(atomOf("a") + X, X + "b")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes(X to "a"))

            expected.assertingEquals(solutions)
        }
    }

    fun testCopyAnyAny() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = copy_term(`_`, `_`)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testCopyTripleSum() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = copy_term(X + X + Y, A + B + B)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes(A to B))

            expected.assertingEquals(solutions)
        }
    }

    fun testCopyAA() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = copy_term("a", "a")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testCopyAB() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = copy_term("a", "b")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testCopyF() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = copy_term("f"("a"), "f"(X))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes(X to "a"))

            expected.assertingEquals(solutions)
        }
    }

    fun testDoubleCopy() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = copy_term(atom("a") + X, X + "b") and copy_term(atom("a") + X, X + "b")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }
}
