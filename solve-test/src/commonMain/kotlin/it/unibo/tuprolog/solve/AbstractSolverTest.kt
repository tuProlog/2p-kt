package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

abstract class AbstractSolverTest : SolverFactory {

    @Test
    open fun testConjunction() {
        prolog {
            val solver = solverOf(
                    staticKB = theoryOf(
                            rule { "a" `if` ("b" and "c") },
                            fact { "b" },
                            fact { "c" }
                    )
            )

            val solutions = solver.solveGoal { "a" }.take(2).toList()
            assertTrue { solutions.isNotEmpty() }
            println(solutions)
        }
    }

}