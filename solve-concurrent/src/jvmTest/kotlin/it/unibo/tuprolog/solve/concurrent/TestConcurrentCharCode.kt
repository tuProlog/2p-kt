package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentCharCode<T : WithAssertingEquals> : FromSequence<T>, SolverFactory {

    fun testCharCodeSecondIsVar() {
        prolog {
            val solver = solverWithDefaultBuiltins()
            val query = char_code("a", "X")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to 97))

            expected.assertingEquals(solutions)
        }
    }

    fun testCharCodeFirstIsVar() {
        prolog {
            val solver = solverWithDefaultBuiltins()
            val query = char_code("X", intOf(97))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to "a"))

            expected.assertingEquals(solutions)
        }
    }

    fun testCharCodeTypeError() {
        prolog {
            val solver = solverWithDefaultBuiltins()
            val query = atom_length("X", 4)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(
                query.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("atom_length", 2),
                        varOf("X"),
                        index = 0
                    )
                )
            )

            expected.assertingEquals(solutions)

            // assertSolutionEquals(
            //     kotlin.collections.listOf(
            //         query.halt(
            //             InstantiationError.forArgument(
            //                 DummyInstances.executionContext,
            //                 Signature("atom_length", 2),
            //                 varOf("X"),
            //                 index = 0
            //             )
            //         )
            //     ),
            //     solutions
            // )
        }
    }

    fun testCharCodeFails() {
        prolog {
            val solver = solverWithDefaultBuiltins()
            val query = char_code("g", intOf(105))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }
}
