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

interface TestConcurrentAtomChars<T : WithAssertingEquals> : FromSequence<T>, SolverFactory {

    fun atomCharsTestFirstIsVar() {
        prolog {
            val solver = solverWithDefaultBuiltins()
            val query = atom_chars("X", listOf("t", "e", "s", "t"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to "test"))

            expected.assertingEquals(solutions)
        }
    }

    fun atomCharsTestYes() {
        prolog {
            val solver = solverWithDefaultBuiltins()
            val query = atom_chars("test", listOf("t", "e", "s", "t"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun atomCharsTestOneCharIsVar() {
        prolog {
            val solver = solverWithDefaultBuiltins()
            val query = atom_chars("test", listOf("t", "e", "s", "T"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes(("T" to "t")))

            expected.assertingEquals(solutions)
        }
    }

    fun atomCharsTestFailure() {
        prolog {
            val solver = solverWithDefaultBuiltins()
            val query = atom_chars("test1", listOf("t", "e", "s", "T"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun atomCharsTestEmpty() {
        prolog {
            val solver = solverWithDefaultBuiltins()
            val query = atom_chars("", "L")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("L" to emptyList))

            expected.assertingEquals(solutions)
        }
    }

    fun atomCharsTestListHead() {
        prolog {
            val solver = solverWithDefaultBuiltins()
            val query = atom_chars("ac", listOf("a", "C"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("C" to "c"))

            expected.assertingEquals(solutions)
        }
    }

    fun atomCharsTestIstantationErrorCheck() {
        prolog {
            val solver = solverWithDefaultBuiltins()
            val query = atom_chars("A", "L")

            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(
                query.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("atom_chars", 2),
                        varOf("A"),
                        index = 0
                    )
                )
            )

            expected.assertingEquals(solutions)
        }
    }

    fun atomCharsTestTypeErrorCheck() {
        prolog {
            val solver = solverWithDefaultBuiltins()
            val query = atom_chars("A", "iso")

            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(
                query.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("atom_chars", 2),
                        TypeError.Expected.LIST,
                        atomOf("iso"),
                        index = 1
                    )
                )
            )

            expected.assertingEquals(solutions)
        }
    }
}
