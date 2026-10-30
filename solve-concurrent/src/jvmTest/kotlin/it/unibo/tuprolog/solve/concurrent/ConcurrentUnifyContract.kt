package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentUnifyContract() =
    concurrentContract {
        case("unify/number-unify") { 1 eq 1 yields { listOf(it.yes()) } }
        case("unify/number-x-unify") { "X" eq 1 yields { listOf(it.yes("X" to 1)) } }
        case("unify/xy-unify") { "X" eq "Y" yields { listOf(it.yes("X" to "Y")) } }

        case("unify/double-unify") {
            (("X" eq "Y") and ("X" eq "abc")) yields {
                listOf(it.yes("X" to "abc", "Y" to "abc"))
            }
        }

        case("unify/f-def-unify") {
            "f"("X", "def") eq "f"("def", "Y") yields {
                listOf(it.yes("X" to "def", "Y" to "def"))
            }
        }

        case("unify/diff-number-unify") { 1 eq 2 yields { listOf(it.no()) } }
        case("unify/dec-number-unify") { 1 eq realOf(1.0) yields { listOf(it.no()) } }
        case("unify/g-unify-fx") { ("g"("X")) eq ("f"("a"("X"))) yields { listOf(it.no()) } }
        case("unify/f-unify") { ("f"("X", 1)) eq ("f"("a"("X"))) yields { listOf(it.no()) } }

        case("unify/f-multiple-term-unify") {
            ("f"("X", "Y", "X")) eq ("f"("a"("X"), "a"("Y"), "Y", 2)) yields {
                listOf(it.no())
            }
        }

        scenario("unify/multiple-term-unify") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = ("f"("A", "B", "C")) eq ("f"("g"("B", "B"), "g"("C", "C"), "g"("D", "D")))
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.yes(
                            "A" to "g"("g"("g"("D", "D"), "g"("D", "D")), "g"("g"("D", "D"), "g"("D", "D"))),
                            "B" to "g"("g"("D", "D"), "g"("D", "D")),
                            "C" to "g"("D", "D"),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }
    }
