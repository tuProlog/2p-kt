package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentCharCodeContract() =
    concurrentContract {
        case("char-code/char-code-second-is-var") { char_code("a", "X") yields { listOf(it.yes("X" to 97)) } }
        case("char-code/char-code-first-is-var") { char_code("X", intOf(97)) yields { listOf(it.yes("X" to "a")) } }

        scenario("char-code/char-code-type-error") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()
                val query = atom_length("X", 4)
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            InstantiationError.forArgument(
                                DummyInstances.executionContext,
                                Signature("atom_length", 2),
                                varOf("X"),
                                index = 0,
                            ),
                        ),
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

        case("char-code/char-code-fails") { char_code("g", intOf(105)) yields { listOf(it.no()) } }
    }
