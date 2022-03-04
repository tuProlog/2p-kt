package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentAtomCodes<T : WithAssertingEquals> : FromSequence<T>, SolverFactory {

    fun testAtomCodesSecondIsVar1() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = atom_codes("abc", "X")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to listOf(97, 98, 99)))

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomCodesSecondIsVar() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = atom_codes("test", "X")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to listOf(116, 101, 115, 116)))

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomCodesFirstIsVar() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = atom_codes("X", listOf(97, 98, 99))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to "abc"))

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomCodesNoVar() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = atom_codes("test", listOf(116, 101, 115, 116))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomCodesFail() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = atom_codes("test", listOf(112, 101, 115, 116))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }
}
