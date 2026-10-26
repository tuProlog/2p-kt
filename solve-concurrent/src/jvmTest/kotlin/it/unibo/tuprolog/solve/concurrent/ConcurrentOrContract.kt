package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentOrContract() =
    concurrentContract(
        disabled =
            setOf(
                "or/cut-false-or-true", // todo implement specific primitive ! (cut) for concurrent solver
                "or/cut-call", // todo implement specific primitive ! (cut) for concurrent solver
                "or/cut-assigned-value", // todo implement specific primitive ! (cut) for concurrent solver
            ),
    ) {
        case("or/true-or-false") { true or fail yields { listOf(it.yes(), it.no()) } }
        case("or/cut-false-or-true") { "!" and fail or true yields { listOf(it.no()) } }
        case("or/cut-call") { "!" or call(3) yields { listOf(it.yes()) } }
        case("or/cut-assigned-value") { ("X" eq 1 and "!") or ("X" eq 2) yields { listOf(it.yes("X" to 1)) } }

        scenario("or/or-double-assignment") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = ("X" eq 1) or ("X" eq 2)
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        sequenceOf(
                            query.yes("X" to 1),
                            query.yes("X" to 2),
                        ),
                    )
                expected.assertingEquals(solutions)
            }
        }
    }
