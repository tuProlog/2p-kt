package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.TrackVariables
import it.unibo.tuprolog.solve.flags.invoke
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TestSubstitutionsImpl(private val solverFactory: SolverFactory) : TestSubstitutions {

    private class Inspect(
        private val inspections: MutableList<Set<String>>
    ) : UnaryPredicate.Predicative<ExecutionContext>("inspect") {
        override fun Solve.Request<ExecutionContext>.compute(first: Term): Boolean {
            inspections += first.variables.distinct()
                .map { context.substitution.getOriginal(it) }
                .filterNotNull()
                .map { it.name }
                .toSet()
            return true
        }
    }

    override fun interestingVariablesAreNotObliterated() {
        testVariablesTracking(false)
    }

    private fun testVariablesTracking(tracking: Boolean) {
        val inspections = mutableListOf<Set<String>>()
        logicProgramming {
            val inspect = Inspect(inspections)
            val solver = solverFactory.solverOf(
                staticKb = TestingClauseTheories.callsWithVariablesAndInspectorTheory("p", inspect.functor),
                libraries = runtimeOf("default", inspect),
                flags = FlagStore.EMPTY + TrackVariables { if (tracking) ON else OFF }
            )
            val query = "p"(A, B, C)
            solver.solveOnce(query)
        }
        assertEquals(setOf("A", "B", "C"), inspections[0])
        assertEquals(setOf("D"), inspections[1])
        assertEquals(setOf("E"), inspections[2])
        assertEquals(setOf("A", "B", "C", "E") + if (tracking) setOf("D") else emptySet(), inspections[3])
    }

    override fun interestingVariablesAreProperlyTracked() {
        testVariablesTracking(true)
    }

    override fun uninterestingVariablesAreObliterated() {
        logicProgramming {
            val solver = solverFactory.solverOf(staticKb = TestingClauseTheories.callsWithVariablesTheory)
            val query = "a"(X)
            val solution = solver.solveOnce(query)
            assertIs<Solution.Yes>(solution)
            assertEquals(1, solution.substitution.size)
            assertEquals(intOf(1), solution.substitution[X])
        }
    }
}
