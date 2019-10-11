package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SolverTestPrototype(solverFactory: SolverFactory) : SolverFactory by solverFactory {

    fun testConjunction() {
        prolog {
            val solver = solverOf(
                    staticKB = theoryOf(
                            rule { "a" impliedBy ("b" and "c") },
                            fact { "b" },
                            fact { "c" }
                    )
            )

            val solutions = solver.solve(atomOf("a")).take(2).toList()

            assertTrue { solutions.size == 1 }

            solutions[0].let {
                assertTrue { it is Solution.Yes }
                assertEquals(atomOf("a"), it.query)
                assertEquals(atomOf("a"), it.solvedQuery)
            }
        }
    }

    fun testConjunctionWithUnification() {
        prolog {
            val solver = solverOf(
                    staticKB = theoryOf(
                            rule { "a"("X") impliedBy ("b"("X") and "c"("X")) },
                            fact { "b"(1) },
                            fact { "c"(1) }
                    )
            )

            val solutions = solver.solve("a"("N")).take(2).toList()

            assertTrue { solutions.size == 1 }

            solutions[0].let {
                assertTrue { it is Solution.Yes }
                assertEquals("a"("N"), it.query)
                assertEquals("a"(1), it.solvedQuery)
            }
        }
    }

}