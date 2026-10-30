package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentAtomLengthContract() =
    concurrentContract {
        case("atom-length/atom-length-no-var") { atom_length("test", 4) yields { listOf(it.yes()) } }
        case("atom-length/atom-length-second-is-var") { atom_length("test", X) yields { listOf(it.yes("X" to 4)) } }

        scenario("atom-length/atom-length-first-is-var") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = char_code("X", "a")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            TypeError.forArgument(
                                DummyInstances.executionContext,
                                Signature("char_code", 2),
                                TypeError.Expected.INTEGER,
                                atomOf("a"),
                                index = 1,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        case("atom-length/atom-length-second-is-var2") {
            atom_length("testLength", X) yields {
                listOf(it.yes("X" to 10))
            }
        }

        case("atom-length/atom-length-fail") { atom_length("test", 5) yields { listOf(it.no()) } }
    }
