package it.unibo.tuprolog.examples

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.classicWithDefaultBuiltins

fun main() {
    prolog {
        val solver =  Solver.classicWithDefaultBuiltins(
            staticKb = theoryOf(
                fact { "user"("giovanni") },
                fact { "user"("lorenzo") },
                rule { "user"(`_`) impliedBy fail() }
            )
        )
        val query = "user"("X") and "write"("hello: ") and "write"("X") and "nl"
        solver.solve(query).forEach {
            when (it) {
                is Solution.No -> println("no.\n")
                is Solution.Yes -> {
                    println("yes: ${it.solvedQuery}")
                    for (assignment in it.substitution) {
                        println("\t${assignment.key} / ${assignment.value}")
                    }
                    println()
                }
            }
        }
    }
}