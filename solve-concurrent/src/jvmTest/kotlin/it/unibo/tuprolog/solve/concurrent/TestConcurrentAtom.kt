package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentAtom<T : WithAssertingEquals> : FromSequence<T>, SolverFactory {

    fun testAtomAtom() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = atom("atom")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomString() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = atom("string")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomAofB() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = atom("a"("b"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomVar() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = atom("Var")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomEmptyList() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = atom(emptyList)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomNum() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = atom(6)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomNumDec() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = atom(3.3)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }
}
