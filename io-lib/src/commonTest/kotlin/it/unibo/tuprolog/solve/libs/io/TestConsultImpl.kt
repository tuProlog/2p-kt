package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.assertHasPredicateInAPI
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.libs.io.primitives.Consult
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestConsultImpl(private val solverFactory: SolverFactory) : TestConsult {

    private fun testConsultWithCorrectTheory(url: Url) {
        prolog {
            val canaryTheory = theoryOf(factOf("canary"))
            val solver = solverFactory.solverWithDefaultBuiltins(
                Libraries.of(IOLib),
                staticKb = canaryTheory
            )
            assertEquals(canaryTheory, solver.staticKb)
            val query = consult(url.toString())
            val solutions = solver.solve(query).toList()
            try {
                assertTrue { solutions.size == 1 }
                assertTrue { solutions.single() is Solution.Yes }
            } catch (e: AssertionError) {
                throw AssertionError("Unexpected solutions: $solutions")
            }
            val expectedTheory = canaryTheory + ExampleTheories.PARENTS
            assertTrue { expectedTheory.equals(solver.staticKb, useVarCompleteName = false) }
        }
    }

    override fun testApi() {
        val solver = solverFactory.solverWithDefaultBuiltins(
            Libraries.of(IOLib)
        )
        assertEquals(IOLib, solver.libraries[IOLib.alias])
        solver.assertHasPredicateInAPI(Consult)
    }

    override fun testConsultWorksLocally() {
        testConsultWithCorrectTheory(findResource("Parents.pl"))
    }

    override fun testConsultWorksRemotely() {
        testConsultWithCorrectTheory(Url.of(ExampleUrls.PARENTS))
    }
}
