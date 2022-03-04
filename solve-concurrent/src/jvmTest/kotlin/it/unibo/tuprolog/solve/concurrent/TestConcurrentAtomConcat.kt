package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentAtomConcat<T : WithAssertingEquals> : FromSequence<T>, SolverFactory {

    fun testAtomConcatThirdIsVar() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = atom_concat("test", "concat", "X")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to atomOf("testconcat")))

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomConcatFails() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = atom_concat("test", "concat", "test")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomConcatSecondIsVar() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = atom_concat("test", "X", "testTest")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to atomOf("Test")))

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomConcatFirstIsVar() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = atom_concat("X", "query", "testquery")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to atomOf("test")))

            expected.assertingEquals(solutions)
        }
    }
}
