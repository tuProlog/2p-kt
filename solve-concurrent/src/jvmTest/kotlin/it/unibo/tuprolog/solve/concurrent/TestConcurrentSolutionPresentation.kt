package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolverFactory
import kotlin.test.assertEquals
import kotlin.test.assertTrue

interface TestConcurrentSolutionPresentation<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    fun testSolutionWithDandlingVars() {
        logicProgramming {
            val theory =
                theoryOf(
                    fact { "append"("seq"(X), X) },
                )
            val solver = solverOf(staticKb = theory)
            val query = "append"(A, B)
            val sol = solver.solveOnce(query)
            assertTrue { sol is Solution.Yes }
            assertTrue { setOf(A, B).all { it in sol.substitution.keys } }
            assertTrue { sol.substitution[A]!! matches "seq"(`_`) }
            assertEquals("X", (sol.substitution[B] as? Var)?.name)
        }
    }
}
