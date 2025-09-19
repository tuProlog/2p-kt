package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.dsl.theory.logicProgramming
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestSolutionPresentationImpl(
    private val solverFactory: SolverFactory,
) : TestSolutionPresentation {
    override fun testSolutionWithDandlingVars() {
        logicProgramming {
            val theory =
                theoryOf(
                    fact { "append"("seq"(X), X) },
                )
            val solver = solverFactory.solverOf(staticKb = theory)
            val query = "append"(A, B)
            val sol = solver.solveOnce(query)
            assertTrue { sol is Solution.Yes }
            assertTrue { setOf(A, B).all { it in sol.substitution.keys } }
            assertTrue { sol.substitution[A]!! matches "seq"(`_`) }
            assertEquals("X", (sol.substitution[B] as? Var)?.name)
        }
    }
}
