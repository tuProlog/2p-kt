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
                            rule { "a"("X") impliedBy ("b"("X") and "c"("X")) },
                            fact { "b"(1) },
                            fact { "c"(1) }
                    )
            )

            val solutions = solver.solve("a"("X")).take(2).toList()

            assertTrue { solutions.size == 1 }

            solutions[0].let {
                assertTrue { it is Solution.Yes }
                assertTrue { it.query == structOf("a", varOf("X")) }
                assertTrue { it.solvedQuery == structOf("a", numOf(1)) }
            }
        }
    }

}